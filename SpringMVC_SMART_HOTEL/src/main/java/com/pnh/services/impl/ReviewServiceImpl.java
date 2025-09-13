package com.pnh.services.impl;

import com.pnh.pojo.Reviews;
import com.pnh.repositories.ReviewRepository;
import com.pnh.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public Reviews getByReservationId(Long reservationId) {
        return this.reviewRepository.findByReservationId(reservationId);
    }

    @Override
    public Reviews addOrUpdate(Reviews review) {
        return this.reviewRepository.addOrUpdate(review);
    }
}


