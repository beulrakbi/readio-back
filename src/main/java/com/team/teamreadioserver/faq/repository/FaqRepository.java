package com.team.teamreadioserver.faq.repository;

import com.team.teamreadioserver.faq.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaqRepository extends JpaRepository<Faq, Integer> {

}
