package com.team.teamreadioserver.notice.repository;

import com.team.teamreadioserver.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Integer> {
    List<Notice> findAllByOrderByNoticeCreateAtDesc();

    //    Optional<Notice> findByNoticeTitle(String noticeTitle); 제목을 완전하게 적어야 하는 문제가 있음
    List<Notice> findByNoticeTitleContainingIgnoreCase(String keyword); //제목을 정확하게 검색하지 않아도 검색 가능

}