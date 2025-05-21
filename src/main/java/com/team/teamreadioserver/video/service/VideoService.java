package com.team.teamreadioserver.video.service;

import com.team.teamreadioserver.video.controller.VideoController;
import com.team.teamreadioserver.video.repository.VideoRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private static final Logger log = LoggerFactory.getLogger(VideoService.class);
    private final ModelMapper modelMapper;



}
