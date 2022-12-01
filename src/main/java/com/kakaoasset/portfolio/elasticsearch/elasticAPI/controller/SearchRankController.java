package com.kakaoasset.portfolio.elasticsearch.elasticAPI.controller;

import com.kakaoasset.portfolio.elasticsearch.elasticAPI.service.SearchRankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * packageName    : com.kakaoasset.portfolio.elasticsearch.elasticAPI.controller
 * fileName       : SearchRankController
 * author         : Hwang
 * date           : 2022-11-14
 */

@RestController
@CrossOrigin(origins = "*")
public class SearchRankController {

    @Autowired
    private SearchRankService searchRankService;

    @RequestMapping(value = "/search/rank", method = RequestMethod.GET)
    public String searchRank(){

        String result = null;

        result = searchRankService.selectSearchRank();

        return result;
    }

}
