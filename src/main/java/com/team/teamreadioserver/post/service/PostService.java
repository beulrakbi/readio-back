package com.team.teamreadioserver.post.service;

import com.team.teamreadioserver.common.common.Criteria;
import com.team.teamreadioserver.post.dto.PostImgDTO;
import com.team.teamreadioserver.post.dto.PostRequestDTO;
import com.team.teamreadioserver.post.dto.PostResponseDTO;
import com.team.teamreadioserver.post.dto.PostSummaryDTO;
import com.team.teamreadioserver.post.entity.Post;
import com.team.teamreadioserver.post.entity.PostImg;
import com.team.teamreadioserver.post.repository.PostImgRepository;
import com.team.teamreadioserver.post.repository.PostLikeRepository;
import com.team.teamreadioserver.post.repository.PostRepository;
import com.team.teamreadioserver.postReview.repository.PostReviewRepository;
import com.team.teamreadioserver.profile.dto.ProfileRequestDTO;
import com.team.teamreadioserver.profile.dto.ProfileResponseDTO;
import com.team.teamreadioserver.profile.entity.Profile;
import com.team.teamreadioserver.profile.entity.ProfileImg;
import com.team.teamreadioserver.profile.repository.ProfileImgRepository;
import com.team.teamreadioserver.profile.repository.ProfileRepository;
import com.team.teamreadioserver.report.entity.ReportedPost;
import com.team.teamreadioserver.report.repository.ReportedPostRepository;
import com.team.teamreadioserver.search.dto.BookDTO;
import com.team.teamreadioserver.search.entity.Book;
import com.team.teamreadioserver.search.repository.BookRepository;
import com.team.teamreadioserver.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostImgRepository postImgRepository;
    private final ProfileImgRepository profileImgRepository;
    private final ModelMapper modelMapper;
    private final ReportedPostRepository reportedPostRepository;
    private final ProfileRepository profileRepository;
    private final BookRepository bookRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostReviewRepository postReviewRepository;

    @Value("${image.image-url}")
    private String IMAGE_URL;

    @Value("http://localhost:8080/img/profile/")
    private String imgUrl;

    public Object getPostDetail(Integer postId) {

        Post post = postRepository.findById(postId).get();
        PostResponseDTO postResponseDTO = modelMapper.map(post, PostResponseDTO.class);

        Profile profile = post.getProfile();
        if (profile != null) {
            ProfileImg authorImgEntity = profileImgRepository.findByProfile(profile).orElse(null);
            String authorImageUrl = null;
            if (authorImgEntity != null) {
                authorImageUrl = imgUrl + authorImgEntity.getSaveName();
            }

            ProfileResponseDTO profileResponseDTO= ProfileResponseDTO.builder()
                    .profileId(profile.getProfileId()) // Profile 엔티티의 PK
                    .penName(profile.getPenName())     // Profile 엔티티의 penName
                    .biography(profile.getBiography()) // Profile 엔티티의 biography
                    .isPrivate(profile.getIsPrivate() != null ? profile.getIsPrivate().name() : null) // Enum 경우 .name()
                    .imageUrl(authorImageUrl)
                    .build();

            postResponseDTO.setProfileId(profileResponseDTO);
        }

        if (postResponseDTO.getPostCreatedDate() == null && post.getPostCreateDate() != null) {
            postResponseDTO.setPostCreatedDate(post.getPostCreateDate());
        }

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

    public int getAllUserPost(String userId)
    {
        Optional<Profile> profile = profileRepository.findByUser_UserId(userId);
        List<Post> foundPosts = postRepository.findByProfile(profile.get());
        return foundPosts.size();
    }

    public Object getAllUserPostWithPaging(String userId, Criteria cri)
    {
        int index = cri.getPageNum() - 1;
        int count = cri.getAmount();
        Pageable paging = PageRequest.of(index, count, Sort.by("postId").descending());

        Optional<Profile> profile = profileRepository.findByUser_UserId(userId);
        ProfileResponseDTO profileResponseDTO = modelMapper.map(profile.get(), ProfileResponseDTO.class);
        Page<Post> foundPostsWithPaging = postRepository.findByProfile(profile.get(), paging);
        List<Post> foundPosts = foundPostsWithPaging.getContent();
        List<PostResponseDTO> result = new ArrayList<>();
        for (Post post : foundPosts) {
            PostResponseDTO postResponseDTO = new PostResponseDTO();
            Book book = bookRepository.findByBookIsbn(post.getBookIsbn());
            if(book != null)
            {
                BookDTO bookDTO = new BookDTO(book);
                postResponseDTO.setBook(bookDTO);
            }

            PostImg img = postImgRepository.findByPost(post);
            if (img != null)
            {
                PostImgDTO postImgDTO = new PostImgDTO();
                postImgDTO.setImgId(img.getImgId());
                postImgDTO.setOriginalName(img.getOriginalName());
                postImgDTO.setSaveName(IMAGE_URL + img.getSavedName());
                postImgDTO.setPostId(post.getPostId());
                postResponseDTO.setPostImg(postImgDTO);
            }
            postResponseDTO.setPostId(post.getPostId());
            postResponseDTO.setProfileId(profileResponseDTO);
            postResponseDTO.setPostTitle(post.getPostTitle());
            postResponseDTO.setPostContent(post.getPostContent());
            postResponseDTO.setPostHidden(post.getPostHidden());
            postResponseDTO.setPostCreatedDate(post.getPostCreateDate());
            Long likes = postLikeRepository.countByPost(post);
            Long reviews = postReviewRepository.countByPostPostId(post.getPostId());
            postResponseDTO.setLikes(likes);
            postResponseDTO.setReviewCount(reviews);
            postResponseDTO.setBookIsbn(post.getBookIsbn());
            result.add(postResponseDTO);
        }
        return result;
    }

    @Transactional
    public Object CreatePost(PostRequestDTO postRequestDTO, List<MultipartFile> multipartFile, Profile userProfile) {

        System.out.println(postRequestDTO);
        System.out.println(multipartFile);

        Post newPost = modelMapper.map(postRequestDTO, Post.class);
        newPost.setPostHidden("N");

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

    @Transactional // 트랜잭션 처리를 위해 @Transactional 어노테이션 추가
    public Object incrementReportCount(int postId) {
        // postId로 Post 엔티티를 찾고, 없으면 예외 발생
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));

        // reportCount를 1 증가
        post.setPostReported(post.getPostReported() + 1);
        postRepository.save(post); // 변경된 엔티티 저장

        System.out.println("Post " + postId + "의 신고수가 " + post.getPostReported() + "로 증가되었습니다.");

        if (post.getPostReported() == 1) {
            ReportedPost reportedPost = new ReportedPost(post.getPostId(), post.getProfile().getUser().getUserId());
            reportedPostRepository.save(reportedPost);
        }
        else if (post.getPostReported() > 4)
        {
            post.hide2();
        }

        // 필요한 경우, 증가된 신고수를 반환하거나 간단한 성공 메시지를 반환
        return post.getPostReported(); // 또는 "신고 처리 완료" 같은 DTO 반환
    }

    //소혜
    public List<PostSummaryDTO> getMonthlyPostSummary(Long profileId, int year, int month) {
        List<Post> posts = postRepository.findByProfileIdAndYearAndMonth(profileId, year, month);

        Map<String, List<Post>> grouped = posts.stream()
                .collect(Collectors.groupingBy(p ->
                        p.getPostCreateDate()
                                .toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                                .toString()
                ));

        return grouped.entrySet().stream()
                .map(entry -> new PostSummaryDTO(
                        entry.getKey(),
                        entry.getValue().size(),
                        entry.getValue().stream().map(Post::getPostId).collect(Collectors.toList())
                ))
                .sorted(Comparator.comparing(PostSummaryDTO::getDate))
                .collect(Collectors.toList());
    }

}

