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
            if(account.isComuCreatedByEmail()) {
                sendMovieCreatedEmail(movie, account);
            }

            if(account.isComuCreatedByWeb()) {
                saveMovieCreatedNotification(movie, account);
            }
        });

    }

    private void saveMovieCreatedNotification(Movie movie, Account account) {
        Notification notification = new Notification();
        notification.setTitle(movie.getTitle());
        notification.setLink("/movie/" + movie.getEncodedPath());
        notification.setChecked(false);
        notification.setCreatedLocalDateTime(LocalDateTime.now());
        notification.setAccount(account);
        notification.setNotificationType(NotificationType.MOVIE_CREATED);
        notificationRepository.save(notification);
    }

    private void sendMovieCreatedEmail(Movie movie, Account account) {
        Context context = new Context();
        context.setVariable("link", "/movie/" + movie.getEncodedPath());
        context.setVariable("nickname", account.getNickname());
        context.setVariable("linkName", movie.getTitle());
        context.setVariable("message", "새로운 영화모임이 생겼습니다");
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject("영화어때, '" + movie.getTitle() + "' 영화모임이 생겼습니다.")
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }
}
