package com.whataboutmv.infra.mail;


public interface EmailService {

    void sendEmail(EmailMessage emailMessage);
}
