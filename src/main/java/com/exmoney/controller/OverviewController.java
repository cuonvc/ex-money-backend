package com.exmoney.controller;

import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.response.overview.HomeOverviewResponse;
import com.exmoney.service.OverviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

import static com.exmoney.util.Constant.API_BASE_USER;

@RestController
@RequiredArgsConstructor
public class OverviewController {

    private final OverviewService overviewService;

    @GetMapping(API_BASE_USER + "/overview")
    public ResponseEntity<BaseResponse<HomeOverviewResponse>> getHomeOverview(@RequestParam(required = false) Integer month,
                                                                              @RequestParam(required = false) Integer year,
                                                                              @RequestParam Locale locale) {
        return overviewService.getHomeOverview(month, year, locale);
    }
}
