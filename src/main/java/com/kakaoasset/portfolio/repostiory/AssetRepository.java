package com.kakaoasset.portfolio.repostiory;

import com.kakaoasset.portfolio.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    Optional<Asset> findByMemberId(Long id);
}
