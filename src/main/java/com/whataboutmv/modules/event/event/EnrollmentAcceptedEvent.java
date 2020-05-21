package com.whataboutmv.modules.event.event;

import com.whataboutmv.modules.event.Enrollment;

public class EnrollmentAcceptedEvent extends EnrollmentEvent {

    public EnrollmentAcceptedEvent(Enrollment enrollment) {
        super(enrollment, "모임 참가 신청을 확인하였습니다. 모임에 참석하세요");
    }
}
