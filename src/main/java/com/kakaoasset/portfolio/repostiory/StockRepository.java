package com.kakaoasset.portfolio.repostiory;

import com.kakaoasset.portfolio.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface StockRepository extends JpaRepository<Stock, Long> {

    // SQL 일반 파라미터 쿼리, @Param 사용 O
    //@Query("select s from Stock s where s.stockCode=?1 and s.member.memberId=?2")
    Stock findByStockCodeAndMember_MemberId(String stockCode, Long id);

    List<Stock> findByMember_MemberId(Long id);
    void deleteByStockCodeAndMember_MemberId(String stockCode, Long id);

}
