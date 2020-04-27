package com.whataboutmv.movie;

import com.whataboutmv.account.CurrentUser;
import com.whataboutmv.domain.Account;
import com.whataboutmv.domain.Movie;
import com.whataboutmv.movie.form.MovieForm;
import com.whataboutmv.movie.validator.MovieFormValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final ModelMapper modelMapper;
    private final MovieFormValidator movieFormValidator;

    @InitBinder("movieForm")
    public void movieFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(movieFormValidator);
    }


    @GetMapping("/new-movie")
    public String newMovieForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new MovieForm());
        return "movie/form";
    }

    @PostMapping("/new-movie")
    public String newStudySubmit(@CurrentUser Account account, @Valid MovieForm movieForm, Errors errors) {
        if(errors.hasErrors()) {
            return "movie/form";
        }

        Movie newMovie = movieService.createNewMovie(modelMapper.map(movieForm, Movie.class), account);
        return "redirect:/movie/" + URLEncoder.encode(newMovie.getPath(), StandardCharsets.UTF_8);
    }
}
