package com.whataboutmv.modules.event.event;

import com.whataboutmv.modules.event.Enrollment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EnrollmentEvent {

    protected  final Enrollment enrollment;
    protected  final String message;
}
