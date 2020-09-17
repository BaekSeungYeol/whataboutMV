package com.whataboutmv.modules.event.event;

import com.whataboutmv.infra.config.AppProperties;
import com.whataboutmv.infra.mail.EmailMessage;
import com.whataboutmv.infra.mail.EmailService;
import com.whataboutmv.modules.account.Account;
import com.whataboutmv.modules.event.Enrollment;
import com.whataboutmv.modules.event.Event;
import com.whataboutmv.modules.movie.Movie;
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
public class EnrollmentEventListener {

    private final NotificationRepository notificationRepository;
    private final AppProperties appProperties;
    private final TemplateEngine templateEngine;
    private final EmailService emailService;

    @EventListener
    public void handleEnrollmentEvent(EnrollmentEvent enrollmentEvent) {
        Enrollment enrollment = enrollmentEvent.getEnrollment();
        Account account = enrollment.getAccount();
        Event event = enrollment.getEvent();
        Movie movie = event.getMovie();

        if (account.isComuCreatedByEmail()) {
            sendEmail(enrollmentEvent, account, event, movie);
        }

        if (account.isComuCreatedByWeb()) {
            createNotification(enrollmentEvent, account, event, movie);
        }
    }

    private void sendEmail(EnrollmentEvent enrollmentEvent, Account account, Event event, Movie movie) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/movie/" + movie.getEncodedPath() + "/events/" + event.getId());
        context.setVariable("linkName", movie.getTitle());
        context.setVariable("message", enrollmentEvent.getMessage());
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject("영화 어때, " + event.getTitle() + " 모임 참가 신청 결과입니다.")
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

    private void createNotification(EnrollmentEvent enrollmentEvent, Account account, Event event, Movie movie) {
        Notification notification = new Notification();
        notification.setTitle(movie.getTitle() + " / " + event.getTitle());
        notification.setLink("/movie/" + movie.getEncodedPath() + "/events/" + event.getId());
        notification.setChecked(false);
        notification.setCreatedDateTime(LocalDateTime.now());
        notification.setMessage(enrollmentEvent.getMessage());
        notification.setAccount(account);
        notification.setNotificationType(NotificationType.EVENT_ENROLLMENT);
        notificationRepository.save(notification);
    }

}

