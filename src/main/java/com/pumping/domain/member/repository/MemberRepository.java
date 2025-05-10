package com.pumping.domain.member.repository;

import com.pumping.domain.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmailAndDeletedFalse(String email);

    boolean existsByEmailAndDeletedFalse(String email);

}
