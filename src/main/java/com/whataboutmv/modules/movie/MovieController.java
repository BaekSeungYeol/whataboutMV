package com.whataboutmv.modules.movie;

import com.whataboutmv.modules.account.CurrentUser;
import com.whataboutmv.modules.account.Account;
import com.whataboutmv.modules.movie.form.MovieForm;
import com.whataboutmv.modules.movie.validator.MovieFormValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final MovieRepository movieRepository;


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
    public String newStudySubmit(@CurrentUser Account account, @Valid MovieForm movieForm, Errors errors, Model model) {
        if(errors.hasErrors()) {
            model.addAttribute(account);
            return "movie/form";
        }

        Movie newMovie = movieService.createNewMovie(modelMapper.map(movieForm, Movie.class), account);
        return "redirect:/movie/" + URLEncoder.encode(newMovie.getPath(), StandardCharsets.UTF_8);
    }

    @GetMapping("/movie/{path}")
    public String viewMovie(@CurrentUser Account account, @PathVariable String path, Model model) {
        Movie movie = movieService.getMovie(path);
        
        model.addAttribute(account);
        model.addAttribute(movie);
        return "movie/view";
    }

    @GetMapping("/movie/{path}/members")
    public String viewMovieMembers(@CurrentUser Account account, @PathVariable String path, Model model) {
        model.addAttribute(account);
        model.addAttribute(movieRepository.findByPath(path));
        return "movie/members";
    }

    @GetMapping("/movie/{path}/join")
    public String joinMovie(@CurrentUser Account account, @PathVariable String path) {
        Movie movie = movieRepository.findMovieWithMembersByPath(path);
        movieService.addMember(movie,account);
        return "redirect:/movie/" + movie.getEncodedPath() + "/members";
    }

    @GetMapping("/movie/{path}/leave")
    public String leaveMovie(@CurrentUser Account account, @PathVariable String path) {
        Movie movie = movieRepository.findMovieWithMembersByPath(path);
        movieService.removeMember(movie,account);
        return "redirect:/movie/" + movie.getEncodedPath() + "/members";
    }

}
