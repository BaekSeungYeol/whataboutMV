package com.whataboutmv.modules.account;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountFactory {

    @Autowired AccountRepository accountRepository;

    public Account createAccount(String nickname) {
        Account winwarm = new Account();
        winwarm.setNickname(nickname);
        winwarm.setEmail(nickname + "@email.com");
        accountRepository.save(winwarm);
        return winwarm;
    }

}