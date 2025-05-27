package com.team.teamreadioserver.postReview.service;

import com.team.teamreadioserver.postReview.repository.PostReviewRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostReviewService {

    private final PostReviewRepository postReviewRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PostReviewService(PostReviewRepository postReviewRepository ,ModelMapper modelMapper) {
        this.postReviewRepository = postReviewRepository;
        this.modelMapper = modelMapper;
    }

}
