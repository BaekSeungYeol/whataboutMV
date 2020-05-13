package com.whataboutmv.event;

import com.whataboutmv.account.CurrentUser;
import com.whataboutmv.domain.Account;
import com.whataboutmv.domain.Enrollment;
import com.whataboutmv.domain.Event;
import com.whataboutmv.domain.Movie;
import com.whataboutmv.event.form.EventForm;
import com.whataboutmv.event.validator.EventValidator;
import com.whataboutmv.movie.MovieRepository;
import com.whataboutmv.movie.MovieService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/movie/{path}")
@RequiredArgsConstructor
public class EventController {

    private final MovieService movieService;
    private final EventService eventService;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;
    private final EventRepository eventRepository;
    private final MovieRepository movieRepository;


    @InitBinder("eventForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventValidator);
    }

    @GetMapping("/new-event")
    public String newEventForm(@CurrentUser Account account, @PathVariable String path, Model model) {
        Movie movie = movieService.getMovieToUpdateStatus(account, path);
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

    @GetMapping("/events/{id}")
    public String getEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable("id") Event event,
                           Model model) {
        model.addAttribute(account);
        model.addAttribute(event);
        model.addAttribute(movieRepository.findMovieWithManagersByPath(path));
        return "event/view";
    }

    @GetMapping("/events")
    public String viewMovieEvents(@CurrentUser Account account, @PathVariable String path, Model model) {

        Movie movie = movieService.getMovie(path);
        model.addAttribute(movie);
        model.addAttribute(account);

        List<Event> events = eventRepository.findByMovieOrderByStartDateTime(movie);
        List<Event> oldEvents = new ArrayList<>();
        List<Event> newEvents = new ArrayList<>();

        events.forEach(e -> {
            if(e.getEndDateTime().isBefore(LocalDateTime.now())) {
                oldEvents.add(e);
            }
            else {
                newEvents.add(e);
            }
        });

        model.addAttribute("newEvents", newEvents);
        model.addAttribute("oldEvents", oldEvents);

        return "/movie/events";
    }

    @GetMapping("/events/{id}/edit")
    public String updateEventForm(@CurrentUser Account account,
                                  @PathVariable String path, @PathVariable("id") Event event, Model model) {
        Movie movie = movieService.getMovieToUpdate(account,path);
        model.addAttribute(movie);
        model.addAttribute(account);
        model.addAttribute(event);
        model.addAttribute(modelMapper.map(event, EventForm.class));
        return "event/update-form";
    }

    @PostMapping("/events/{id}/edit")
    public String updateEventSubmit(@CurrentUser Account account, @PathVariable String path,
                                    @PathVariable("id") Event event, @Valid EventForm eventForm, Errors errors, Model model) {

        Movie movie = movieService.getMovieToUpdate(account,path);
        eventForm.setEventType(event.getEventType());
        eventValidator.validateUpdateForm(eventForm, event, errors);

        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(movie);
            model.addAttribute(event);
            return "event/update-form";
        }

        eventService.updateEvent(event, eventForm);
        return "redirect:/movie/" + movie.getEncodedPath() + "/events/" + event.getId();
    }

    @DeleteMapping("/events/{id}")
    public String cancelEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable("id") Event event) {
        Movie movie = movieService.getMovieToUpdateStatus(account,path);
        eventService.deleteEvent(event);
        return "redirect:/movie/" + movie.getEncodedPath() + "/events";
    }

    @PostMapping("/events/{id}/enroll")
    public String newEnrollment(@CurrentUser Account account, @PathVariable String path, @PathVariable("id") Event event) {
        Movie movie = movieService.getMovieToEnroll(path);
        eventService.newEnrollment(event,account);
        return "redirect:/movie/" + movie.getEncodedPath() + "/events/" + event.getId();
    }
    @PostMapping("/events/{id}/disenroll")
    public String cancelEnrollment(@CurrentUser Account account, @PathVariable String path, @PathVariable("id") Event event) {
        Movie movie = movieService.getMovieToEnroll(path);
        eventService.cancelEnrollment(event, account);
        return "redirect:/movie/" + movie.getEncodedPath() + "/events/" + event.getId();
    }

    @GetMapping("events/{eventId}/enrollments/{enrollmentId}/accept")
    public String acceptEnrollment(@CurrentUser Account account, @PathVariable String path,
                                   @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
        Movie movie = movieService.getMovieToUpdate(account,path);
        eventService.acceptEnrollment(event,enrollment);
        return "redirect:/movie/" + movie.getEncodedPath() + "/events/" + event.getId();
    }

    @GetMapping("events/{eventId}/enrollments/{enrollmentId}/reject")
    public String rejectEnrollment(@CurrentUser Account account, @PathVariable String path,
                                   @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
        Movie movie = movieService.getMovieToUpdate(account,path);
        eventService.rejectEnrollment(event,enrollment);
        return "redirect:/movie/" + movie.getEncodedPath() + "/events/" + event.getId();
    }
    @GetMapping("events/{eventId}/enrollments/{enrollmentId}/checkin")
    public String checkInEnrollment(@CurrentUser Account account, @PathVariable String path,
                                   @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
        Movie movie = movieService.getMovieToUpdate(account,path);
        eventService.checkInEnrollment(enrollment);
        return "redirect:/movie/" + movie.getEncodedPath() + "/events/" + event.getId();
    }
    @GetMapping("events/{eventId}/enrollments/{enrollmentId}/cancel-checkin")
    public String cancelEnrollment(@CurrentUser Account account, @PathVariable String path,
                                   @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
        Movie movie = movieService.getMovieToUpdate(account,path);
        eventService.cancelCheckInEnrollment(enrollment);
        return "redirect:/movie/" + movie.getEncodedPath() + "/events/" + event.getId();
    }

}
