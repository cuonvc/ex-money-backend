package com.exmoney.service;

import com.exmoney.security.CustomUserDetail;

public interface CommonService {
    CustomUserDetail getCurrentUser();
    String getCurrentUserId();
}
