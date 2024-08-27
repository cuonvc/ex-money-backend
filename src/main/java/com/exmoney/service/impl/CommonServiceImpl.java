package com.exmoney.service.impl;

import com.exmoney.security.CustomUserDetail;
import com.exmoney.service.CommonService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CommonServiceImpl implements CommonService {

    @Override
    public CustomUserDetail getCurrentUser() {
        return (CustomUserDetail) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
    }

    @Override
    public String getCurrentUserId() {
        return getCurrentUser().getId();
    }
}
