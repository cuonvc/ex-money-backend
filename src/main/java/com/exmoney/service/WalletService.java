package com.exmoney.service;

import com.exmoney.entity.Wallet;
import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.request.wallet.WalletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Locale;

public interface WalletService {
    void initDefaultWallet(String userId, Locale locale);
    ResponseEntity<BaseResponse<Wallet>> create(WalletRequest wallet, Locale locale);
    ResponseEntity<BaseResponse<Wallet>> detail(String walletId, Locale locale);
    ResponseEntity<BaseResponse<List<Wallet>>> listByUser(boolean isOwner, Locale locale);
    ResponseEntity<BaseResponse<Wallet>> addUser(String walletId, String userId, Locale locale);
}
