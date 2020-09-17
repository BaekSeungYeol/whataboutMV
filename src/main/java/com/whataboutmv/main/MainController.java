package com.whataboutmv.main;

import com.whataboutmv.modules.account.AccountRepository;
import com.whataboutmv.modules.account.CurrentUser;
import com.whataboutmv.modules.account.Account;
import com.whataboutmv.modules.event.EnrollmentRepository;
import com.whataboutmv.modules.movie.Movie;
import com.whataboutmv.modules.movie.MovieRepository;
import com.whataboutmv.modules.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final MovieRepository movieRepository;
    private final NotificationRepository notificationRepository;
    private final AccountRepository accountRepository;
    private final EnrollmentRepository enrollmentRepository;

    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model) {
        if (account != null) {
            Account accountLoaded = accountRepository.findAccountWithTagsAndZonesById(account.getId());
            model.addAttribute(accountLoaded);
            model.addAttribute("enrollmentList", enrollmentRepository.findByAccountAndAcceptedOrderByEnrolledAtDesc(accountLoaded, true));

            model.addAttribute("movieList", movieRepository.findByAccount(
                    accountLoaded.getTags(),
                    accountLoaded.getZones()));
            model.addAttribute("movieManagerOf",
                    movieRepository.findFirst5ByManagersContainingAndClosedOrderByPublishedDateTimeDesc(account, false));
            model.addAttribute("movieMemberOf",
                    movieRepository.findFirst5ByMembersContainingAndClosedOrderByPublishedDateTimeDesc(account, false));
            return "index-after-login";

        }
        model.addAttribute("movieList", movieRepository.findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(true,false));
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }


    @GetMapping("/search/movie")
    public String searchMovie( String keyword, Model model,
                               @PageableDefault(size = 9, sort="publishedDateTime", direction = Sort.Direction.DESC)
                                       Pageable pageable) {
        Page<Movie> movieList = movieRepository.findByKeyword(keyword,pageable);
        model.addAttribute("moviePage", movieList);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortProperty", pageable.getSort().toString().contains("publishedDateTime") ?  "publishedDateTime" : "memberCount");
        return "search";
    }
}
