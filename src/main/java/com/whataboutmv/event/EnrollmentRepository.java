package com.whataboutmv.event;

import com.whataboutmv.domain.Account;
import com.whataboutmv.domain.Enrollment;
import com.whataboutmv.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment,Long> {

    boolean existsByEventAndAccount(Event event, Account account);

    Enrollment findByEventAndAccount(Event event, Account account);
}
