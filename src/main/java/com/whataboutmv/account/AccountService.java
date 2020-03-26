package com.whataboutmv.account;

import com.whataboutmv.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;


    private Account saveNewAccount(@Valid SignUpForm signUpForm) {
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(signUpForm.getPassword()) // need Encoding
                .comuCreatedByWeb(true)
                .comuEnrollmentResultByWeb(true)
                .comuUpdatedByWeb(true)
                .build();

        return accountRepository.save(account);
    }

    private void snedSignUpConfirmEmail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("영화 어때, 회원 가입 인증");
        mailMessage.setText("/check-email-token=" + newAccount.getEmailCheckToken()
                + "&email=" + newAccount.getEmail());
        javaMailSender.send(mailMessage);
    }

    public void processNewAccount(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        newAccount.generateEmailCheckToken();
        snedSignUpConfirmEmail(newAccount);
    }
}
