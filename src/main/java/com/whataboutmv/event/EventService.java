package com.whataboutmv.event;

import com.whataboutmv.domain.Account;
import com.whataboutmv.domain.Event;
import com.whataboutmv.domain.Movie;
import com.whataboutmv.event.form.EventForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Transactional
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;

    public Event createEvent(Event event, Movie movie, Account account) {
        event.setCreatedBy(account);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setMovie(movie);
        return eventRepository.save(event);
    }

    public void updateEvent(Event event, EventForm eventForm) {

        modelMapper.map(eventForm, event);
    }
}
