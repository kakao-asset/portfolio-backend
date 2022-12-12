package com.kakaoasset.portfolio.repostiory;

import com.kakaoasset.portfolio.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByMemberId(Long id);
    Member findByKakaoId(String id);
    void deleteByMemberId(Long id);
}
