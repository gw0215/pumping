package com.pumping.domain.member.repository;

import com.pumping.domain.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmailAndDeletedFalse(@Param("email") String email);

    boolean existsByEmailAndDeletedFalse(@Param("email") String email);

}
