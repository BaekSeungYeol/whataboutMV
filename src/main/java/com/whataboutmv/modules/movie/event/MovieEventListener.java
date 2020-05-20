package com.whataboutmv.modules.movie.event;

import com.whataboutmv.infra.config.AppProperties;
import com.whataboutmv.infra.mail.EmailMessage;
import com.whataboutmv.infra.mail.EmailService;
import com.whataboutmv.modules.account.Account;
import com.whataboutmv.modules.account.AccountPredicates;
import com.whataboutmv.modules.account.AccountRepository;
import com.whataboutmv.modules.movie.Movie;
import com.whataboutmv.modules.movie.MovieRepository;
import com.whataboutmv.modules.notification.Notification;
import com.whataboutmv.modules.notification.NotificationRepository;
import com.whataboutmv.modules.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class MovieEventListener {

    private final MovieRepository movieRepository;
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleMovieCreateEvent(MovieCreatedEvent movieCreatedEvent) {
        Movie movie = movieRepository.findMovieWithTagsAndZonesById(movieCreatedEvent.getMovie().getId());
        Iterable<Account> accounts = accountRepository.findAll(AccountPredicates.findByTagsAndZones(movie.getTags(), movie.getZones()));
        accounts.forEach(account -> {
            if (account.isComuCreatedByEmail()) {
                sendMovieCreatedEmail(movie, account, "새로운 영화모임이 생겼습니다",
                        "영화어때, '" + movie.getTitle() + "' 영화모임이 생겼습니다.");
            }

            if(account.isComuCreatedByWeb()) {
                createNotification(movie, account, movie.getShortDescription(), NotificationType.MOVIE_CREATED);
            }
        });

    }

    @EventListener
    public void handleMovieUpdateEvent(MovieUpdateEvent movieUpdateEvent) {
        Movie movie = movieRepository.findMovieWithManagersAndMembersById(movieUpdateEvent.getMovie().getId());
        Set<Account> accounts = new HashSet<>();
        accounts.addAll(movie.getManagers());
        accounts.addAll(movie.getMembers());

        accounts.forEach(account -> {
            if(account.isComuUpdatedByEmail()) {
                sendMovieCreatedEmail(movie, account, movieUpdateEvent.getMessage(),
                        "영화 어때, '" + movie.getTitle() + "' 영화 어때에 새소식이 있습니다.");
            }

            if(account.isComuUpdatedByWeb()) {
                createNotification(movie, account, movieUpdateEvent.getMessage(), NotificationType.MOVIE_UPDATED);
            }
        });
    }
    private void createNotification(Movie movie, Account account, String message, NotificationType notificationType ) {
        Notification notification = new Notification();
        notification.setTitle(movie.getTitle());
        notification.setLink("/movie/" + movie.getEncodedPath());
        notification.setChecked(false);
        notification.setCreatedDateTime(LocalDateTime.now());
        notification.setMessage(movie.getShortDescription());
        notification.setAccount(account);
        notification.setNotificationType(NotificationType.MOVIE_CREATED);
        notificationRepository.save(notification);
    }

    private void sendMovieCreatedEmail(Movie movie, Account account,String contextMessage, String emailSubject) {
        Context context = new Context();
        context.setVariable("link", "/movie/" + movie.getEncodedPath());
        context.setVariable("nickname", account.getNickname());
        context.setVariable("linkName", movie.getTitle());
        context.setVariable("message", contextMessage);
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject(emailSubject)
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }
}
