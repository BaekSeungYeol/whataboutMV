package com.whataboutmv.event;

import com.whataboutmv.account.CurrentUser;
import com.whataboutmv.domain.Account;
import com.whataboutmv.domain.Event;
import com.whataboutmv.domain.Movie;
import com.whataboutmv.event.form.EventForm;
import com.whataboutmv.movie.MovieService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/movie/{path}")
@RequiredArgsConstructor
public class EventController {

    private final MovieService movieService;
    private final ModelMapper modelMapper;
    private final EventService eventService;
    private final EventValidator eventValidator;

    @GetMapping("/new-event")
    public String newEventForm(@CurrentUser Account account, @PathVariable String path, Model model){
        Movie movie = movieService.getMovieToUpdateStatus(account,path);
        model.addAttribute(movie);
        model.addAttribute(account);
        model.addAttribute(new EventForm());
        return "event/form";
    }

    @PostMapping("/new-event")
    public String newEventSubmit(@CurrentUser Account account, @PathVariable String path,
                                 @Valid EventForm eventForm, Errors errors, Model model) {

        Movie movie = movieService.getMovieToUpdateStatus(account, path);
        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(movie);
            return "event/form";
        }

        Event event = eventService.createEvent(modelMapper.map(eventForm, Event.class), movie, account);
        return "redirect:/movie/" + movie.getEncodedPath() + "/events/" + event.getId();
    }
}
