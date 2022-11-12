package com.kakaoasset.portfolio.repostiory;

import com.kakaoasset.portfolio.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    public Member findByEmail(String email);
}
