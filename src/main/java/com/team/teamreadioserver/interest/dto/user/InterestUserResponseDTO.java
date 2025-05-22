package com.team.teamreadioserver.interest.dto.user;

import com.team.teamreadioserver.interest.enums.InterestStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterestUserResponseDTO {
    private String userId;
    private List<SimpleInterestDTO> categories;
    private List<SimpleInterestDTO> keywords;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SimpleInterestDTO {
        private Long id;
        private String name;
    }
}

