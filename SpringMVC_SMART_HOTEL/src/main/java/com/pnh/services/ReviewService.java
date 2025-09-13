package com.pnh.services;

import com.pnh.pojo.Reviews;

public interface ReviewService {
    Reviews getByReservationId(Long reservationId);
    Reviews addOrUpdate(Reviews review);
}


