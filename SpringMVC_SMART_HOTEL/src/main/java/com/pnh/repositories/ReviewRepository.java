package com.pnh.repositories;

import com.pnh.pojo.Reviews;

public interface ReviewRepository {
    Reviews findByReservationId(Long reservationId);
    Reviews addOrUpdate(Reviews review);
}


