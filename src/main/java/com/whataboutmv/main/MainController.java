package com.whataboutmv.main;

import com.whataboutmv.modules.account.CurrentUser;
import com.whataboutmv.modules.account.Account;
import com.whataboutmv.modules.movie.Movie;
import com.whataboutmv.modules.movie.MovieRepository;
import com.whataboutmv.modules.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final MovieRepository movieRepository;
    private final NotificationRepository notificationRepository;
    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model) {
        if(account != null) {
            model.addAttribute(account);
        }

        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }


    @GetMapping("/search/movie")
    public String searchMovie(String keyword, Model model) {
        //List<Movie> movieList = movieRepository.findByKeyword(keyword);
        // TODO
        return null;
    }
}
