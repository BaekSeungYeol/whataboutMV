package com.whataboutmv.event;

import com.whataboutmv.domain.Account;
import com.whataboutmv.domain.Event;
import com.whataboutmv.domain.Movie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Transactional
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public Event createEvent(Event event, Movie movie, Account account) {
        event.setCreatedBy(account);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setMovie(movie);
        return eventRepository.save(event);
    }
}
