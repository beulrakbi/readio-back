package com.team.teamreadioserver.notice.repository;

import com.team.teamreadioserver.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Integer> {

}
