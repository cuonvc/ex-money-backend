package com.exmoney.service;

import com.exmoney.entity.RefreshToken;
import com.exmoney.entity.User;

public interface TokenService {
    boolean validateAccessToken(String token);

    void initRefreshToken(User user);

    void clearToken(String userId);

    RefreshToken generateTokenObject(User user);
}
