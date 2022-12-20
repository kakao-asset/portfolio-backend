package com.kakaoasset.portfolio.repostiory;

import com.kakaoasset.portfolio.entity.Trend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrendRepository extends JpaRepository<Trend, Integer> {
    List<Trend> findByMemberId(Long id);
}
