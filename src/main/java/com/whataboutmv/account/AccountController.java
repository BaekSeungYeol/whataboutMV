package com.whataboutmv.account;

import com.whataboutmv.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

        @GetMapping("/sign-up")
        public String signUpForm(Model model) {
            Account account = Account.builder()
                    .build();

            model.addAttribute(account);
            return "account/sign-up";
        }
}
