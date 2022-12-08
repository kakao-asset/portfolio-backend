package com.kakaoasset.portfolio.controller;

import com.kakaoasset.portfolio.service.SectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * packageName    : com.kakaoasset.portfolio.elasticsearch.elasticAPI.controller
 * fileName       : SectorController
 * author         : Hwang
 * date           : 2022-11-14
 */

@RestController
@CrossOrigin(origins = "*")
public class SectorController {

    @Autowired
    private SectorService sectorService;

    @RequestMapping(value = "/sector", method = RequestMethod.GET)
    public String searchRank(@RequestParam String stock_sector){

        String result = null;

        result = sectorService.selectSectorStock(stock_sector);

        return result;
    }
}
