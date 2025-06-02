package com.team.teamreadioserver.follow.dto;

import com.team.teamreadioserver.follow.entity.Follow;
import com.team.teamreadioserver.profile.dto.ProfileResponseDTO;
import lombok.*;
import org.modelmapper.ModelMapper;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FollowResponseDTO {

    private int followId;
    private ProfileResponseDTO followingProfileId;
    private ProfileResponseDTO followerProfileId;

    public static FollowResponseDTO fromEntity(Follow follow, ModelMapper modelMapper) {
        return new FollowResponseDTO(
                follow.getFollowId(), // follow.getFollowId()는 int 반환
                modelMapper.map(follow.getFollower(), ProfileResponseDTO.class),
                modelMapper.map(follow.getFollowing(), ProfileResponseDTO.class)

        );
    }
}
