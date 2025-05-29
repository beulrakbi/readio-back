package com.team.teamreadioserver.post.service;

import com.team.teamreadioserver.post.dto.PostImgDTO;
import com.team.teamreadioserver.post.dto.PostRequestDTO;
import com.team.teamreadioserver.post.dto.PostResponseDTO;
import com.team.teamreadioserver.post.entity.Post;
import com.team.teamreadioserver.post.entity.PostImg;
import com.team.teamreadioserver.post.repository.PostImgRepository;
import com.team.teamreadioserver.post.repository.PostRepository;
import com.team.teamreadioserver.profile.entity.Profile;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostImgRepository postImgRepository;
    private final ModelMapper modelMapper;

    @Value("${image.image-url}")
    private String IMAGE_URL;

    public Object getPostDetail(Integer postId) {

        Post post = postRepository.findById(postId).get();
        PostResponseDTO postResponseDTO = modelMapper.map(post, PostResponseDTO.class);

        if (post.getPostImg() != null) {
            PostImg postImg = post.getPostImg();

            PostImgDTO postImgDTO = new PostImgDTO();
            postImgDTO.setImgId(postImg.getImgId());
            postImgDTO.setOriginalName(postImg.getOriginalName());
            postImgDTO.setSaveName(IMAGE_URL + postImg.getSavedName());
            postImgDTO.setPostId(postId);

            postResponseDTO.setPostImg(postImgDTO);
        }

        return postResponseDTO;
    }

    @Transactional
    public Object CreatePost(PostRequestDTO postRequestDTO, List<MultipartFile> multipartFile, Profile userProfile) {

        System.out.println(postRequestDTO);
        System.out.println(multipartFile);

        Post newPost = modelMapper.map(postRequestDTO, Post.class);

        if (userProfile != null) {
            newPost.setProfile(userProfile); // Post 엔티티에 setProfile(Profile profile) 메소드가 있다고 가정합니다.
        } else {
            System.err.println("주의: CreatePost 서비스에 userProfile이 null로 전달되었습니다. 컨트롤러에서 처리되었어야 합니다.");
        }

        Post savedPost = postRepository.save(newPost);

        ClassPathResource resource = new ClassPathResource("post");
        String staticPath;

        try {
            staticPath = resource.getFile().getAbsolutePath();
        } catch (IOException e) {
            File dir = new File("src/main/resources/static/img/post");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            staticPath = dir.getAbsolutePath();
        }

        List<PostImgDTO> files = new ArrayList<>();
        List<String> savedFiles = new ArrayList<>();

        try {
            int count = 0;
            for (MultipartFile file : multipartFile) {
                if (count >= 1) break;
                String originalFileName = file.getOriginalFilename();
                String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
                String savedFileName = UUID.randomUUID().toString().replace("-", "") + extension;

                String fileUrl = "post/" + savedFileName;
                files.add(new PostImgDTO(fileUrl, originalFileName));

                File targetFile = new File(staticPath + "/" + savedFileName);
                file.transferTo(targetFile);
                savedFiles.add(fileUrl);

                count++;
            }

            for (PostImgDTO postImgDTO : files) {
                PostImg postImg = new PostImg(savedPost, postImgDTO.getSaveName(), postImgDTO.getOriginalName());
                postImgRepository.save(postImg);
            }
        } catch (Exception e) {
            for (PostImgDTO file : files) {
                new File(staticPath + "/" + file.getSaveName()).delete();
            }
            e.printStackTrace();
        }


        return "포스트 등록 완료";
    }

    @Transactional
    public Object UpdatePost(PostRequestDTO postRequestDTO) {

        Post Updatepost = postRepository.findById(postRequestDTO.getPostId()).get();

        Updatepost.setPostTitle(postRequestDTO.getPostTitle());
        Updatepost.setPostContent(postRequestDTO.getPostContent());
        Updatepost.setBookIsbn(postRequestDTO.getBookIsbn());

        postRepository.save(Updatepost);

        return "포스트 수정 완료";
    }

    @Transactional
    public Object DeletePost(int postId) {

        Post post = postRepository.findById(postId).get();

        postRepository.delete(post);

        return "포스트 삭제 완료";
    }
}

