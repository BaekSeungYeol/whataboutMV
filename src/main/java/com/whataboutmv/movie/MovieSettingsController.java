package com.whataboutmv.movie;


import com.whataboutmv.account.CurrentUser;
import com.whataboutmv.domain.Account;
import com.whataboutmv.domain.Movie;
import com.whataboutmv.movie.form.MovieDescriptionForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/study/{path}/settings")
@RequiredArgsConstructor
public class MovieSettingsController {

    private final MovieRepository movieRepository;
    private final MovieService movieService;
    private final ModelMapper modelMapper;

    @GetMapping("/description")
    public String viewMovieSetting(@CurrentUser Account account, @PathVariable String path, Model model) {
        Movie movie = movieService.getMovieToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(movie);
        model.addAttribute(modelMapper.map(movie, MovieDescriptionForm.class));
        return "movie/settings/description";
    }

    @PostMapping("/description")
    public String updateStudyInfo(@CurrentUser Account account, @PathVariable String path, @Valid MovieDescriptionForm
                                  movieDescriptionForm, Errors errors, Model model, RedirectAttributes attributes) {
        Movie movie = movieService.getMovieToUpdate(account,path);

        if(errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(movie);
            return "movie/settings/description";
        }

        movieService.updateMovieDescription(movie, movieDescriptionForm);
        attributes.addFlashAttribute("message", "모임 소개를 수정했습니다.");
        return "redirect:/movie/" + getPath(path) + "/settings/description";
    }

    private String getPath(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }


}
