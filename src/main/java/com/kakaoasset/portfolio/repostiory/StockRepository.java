package com.kakaoasset.portfolio.repostiory;

import com.kakaoasset.portfolio.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface StockRepository extends JpaRepository<Stock, Long> {

    Stock findByStockNameAndMember_MemberId(String stockName, Long id);

    List<Stock> findByMember_MemberId(Long id);
    void deleteByStockNameAndMember_MemberId(String stockName, Long id);

}
