package com.kakaoasset.portfolio.repostiory;

import com.kakaoasset.portfolio.entity.Member;
import com.kakaoasset.portfolio.entity.Stock;
import com.kakaoasset.portfolio.entity.StockHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;



public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {
    List<StockHistory> findByMember_MemberId(Long id);
}
