package com.team.teamreadioserver;

import com.team.teamreadioserver.post.dto.PostImgDTO;
import com.team.teamreadioserver.post.dto.PostRequestDTO;
import com.team.teamreadioserver.post.entity.Post;
import com.team.teamreadioserver.post.repository.PostRepository;
import com.team.teamreadioserver.post.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
//import org.springframework.security.core.parameters.P;

@SpringBootTest
class TeamReadioServerApplicationTests {

    @Autowired
    private PostService postService;
    @Autowired
    private PostRepository postRepository;

    @Test
    void contextLoads() {
        PostRequestDTO postRequestDTO = new PostRequestDTO(
                null,
                "test",
                "test",
                "001"
        );


        postService.CreatePost(postRequestDTO, multipartFile);
    }

    @Test
    void contextLoads2() {
        PostRequestDTO postRequestDTO = new PostRequestDTO(
                2,
                "test1",
                "test1",
                "002"
        );

        postService.UpdatePost(postRequestDTO);
    }

}
