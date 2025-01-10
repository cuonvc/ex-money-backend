package com.exmoney.service;

import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.response.overview.HomeOverviewResponse;
import org.springframework.http.ResponseEntity;

import java.util.Locale;

public interface OverviewService {
    ResponseEntity<BaseResponse<HomeOverviewResponse>> getHomeOverview(Integer month, Integer year, Locale locale);
}
