package com.exmoney.service;

import com.exmoney.entity.Wallet;
import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.request.wallet.WalletRequest;
import com.exmoney.payload.request.wallet.WalletSettingRequest;
import com.exmoney.payload.response.wallet.WalletResponse;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

public interface WalletService {
    void initDefaultWallet(Long userId, Locale locale);
    ResponseEntity<BaseResponse<Wallet>> create(WalletRequest wallet, Locale locale);
    ResponseEntity<BaseResponse<WalletResponse>> detail(Long walletId, Locale locale);
    ResponseEntity<BaseResponse<List<WalletResponse>>> listByUser(boolean isOwner, Locale locale);
    ResponseEntity<BaseResponse<WalletResponse>> changeUser(String action, Long walletId, String userEmail, Locale locale);
    ResponseEntity<BaseResponse<WalletResponse>> setting(Long walletId, WalletSettingRequest request, Locale locale);

    String getDisplayWalletName(Wallet wallet, Long currentUserId, String ownerWallet, Locale locale);
}
