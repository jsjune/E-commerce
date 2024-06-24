package com.memberservice.repository;


import com.memberservice.entity.Member;
import com.memberservice.entity.UserRole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByEmailOrUsername(String email, String username);

    Optional<Member> findByUsername(String username);

    Optional<Member> findByIdAndRole(Long memberId, UserRole memberRole);
}
