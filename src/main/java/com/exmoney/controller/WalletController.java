package com.exmoney.controller;

import com.exmoney.entity.Wallet;
import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.request.wallet.WalletRequest;
import com.exmoney.payload.response.wallet.WalletResponse;
import com.exmoney.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

import static com.exmoney.util.Constant.API_BASE_USER;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping(API_BASE_USER + "/wallet")
    public ResponseEntity<BaseResponse<Wallet>> create(@RequestParam Locale locale,
                                                       @RequestBody WalletRequest request) {
        return walletService.create(request, locale);
    }

    @GetMapping(API_BASE_USER + "/wallet/detail")
    //nếu walletId = null -> get default wallet
    public ResponseEntity<BaseResponse<WalletResponse>> detail(@RequestParam Locale locale,
                                                               @RequestParam(required = false) Long id) {
        return walletService.detail(id, locale);
    }

    @GetMapping(API_BASE_USER + "/wallet/list")
    //Nếu is_owner = false -> get all ví có thể truy cập (không chỉ có ví khách)
    public ResponseEntity<BaseResponse<List<WalletResponse>>> listByUser(@RequestParam Locale locale,
                                                                         @RequestParam(name = "is_owner", defaultValue = "false") boolean isOwner) {
        return walletService.listByUser(isOwner, locale);
    }

    @PutMapping(API_BASE_USER + "/wallet/change_user")
    public ResponseEntity<BaseResponse<WalletResponse>> changeUser(@RequestParam Locale locale,
                                                           @RequestParam(name = "action") String action,
                                                           @RequestParam(name = "wallet_id") Long walletId,
                                                           @RequestParam(name = "user_email") String userEmail) {
        return walletService.changeUser(action, walletId, userEmail, locale);
    }
}
