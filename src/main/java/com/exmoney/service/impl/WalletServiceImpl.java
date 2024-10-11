package com.exmoney.service.impl;

import com.exmoney.entity.User;
import com.exmoney.entity.UserWallet;
import com.exmoney.entity.Wallet;
import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.common.ResponseFactory;
import com.exmoney.payload.enumerate.ErrorCode;
import com.exmoney.payload.mapper.WalletMapper;
import com.exmoney.payload.request.wallet.WalletRequest;
import com.exmoney.repository.UserRepository;
import com.exmoney.repository.UserWalletRepository;
import com.exmoney.repository.WalletRepository;
import com.exmoney.service.CommonService;
import com.exmoney.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.exmoney.payload.enumerate.ErrorCode.*;
import static com.exmoney.util.Utils.getNow;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserWalletRepository userWalletRepository;
    private final UserRepository userRepository;
    private final CommonService commonService;
    private final WalletMapper walletMapper;
    private final ResponseFactory responseFactory;

    @Value("${exmoney.application.action_log.wallet_create}") //chu y
    private String actionWalletCreate;

    @Value("${exmoney.application.default.wallet_name}")
    private String defaultWalletName;
    @Value("${exmoney.application.default.wallet_description}")
    private String defaultWalletDescription;

    @Override
    @Transactional
    public void initDefaultWallet(String userId, Locale locale) {
//        if (!walletRepository.findByUserId(userId).isEmpty()) {
//            commonService.throwException(INTERNAL_SERVER_ERROR, locale, null);
//        }
        Wallet wallet = new Wallet();
        wallet.setCreatedAt(getNow());
        wallet.setName(defaultWalletName);
        wallet.setDescription(defaultWalletDescription);
        wallet.setOwnerUserId(userId);
        walletRepository.save(wallet);

        userWalletRepository.save(
                UserWallet.builder()
                        .userId(userId)
                        .walletId(wallet.getId())
                        .updatedAt(getNow())
                        .build()
        );
    }

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<Wallet>> create(WalletRequest request, Locale locale) {
        String userId = commonService.getCurrentUserId();
        if (walletRepository.findByNameOfUser(request.getName(), userId).isPresent()) {
            commonService.throwException(WALLET_NAME_ALREADY_EXISTED, locale, null, request.getName());
        }
        Wallet wallet = walletMapper.toEntity(request);
        wallet.setCreatedAt(getNow());
        wallet.setOwnerUserId(userId);
        wallet.setDefault(false);
        wallet = walletRepository.save(wallet);

        userWalletRepository.save(
                UserWallet.builder()
                        .userId(userId)
                        .walletId(wallet.getId())
                        .updatedAt(getNow())
                        .build()
        );
        return responseFactory.success(actionWalletCreate, wallet);
    }

    @Override
    public ResponseEntity<BaseResponse<Wallet>> detail(String walletId, Locale locale) {
        Optional<Wallet> wallet = walletRepository.findByIdAndUser(walletId, commonService.getCurrentUserId());
        if (wallet.isEmpty()) {
            commonService.throwException(WALLET_NOT_FOUND, locale, null);
        }

        Wallet walletObj = wallet.get();
        if (walletObj.isDefault()) {
            walletObj.setName(commonService.getMessageSrc(walletObj.getName(), locale));
            walletObj.setDescription(commonService.getMessageSrc(walletObj.getDescription(), locale));
        }

        return responseFactory.success(null, walletObj);
    }

    @Override
    public ResponseEntity<BaseResponse<List<Wallet>>> listByUser(boolean isOwner, Locale locale) {
        return responseFactory.success(
                null,
                walletRepository.findByUserId(commonService.getCurrentUserId(), isOwner)
                        .stream().peek(w -> {
                            if (w.isDefault()) {
                                w.setName(commonService.getMessageSrc(w.getName(), locale));
                                w.setDescription(commonService.getMessageSrc(w.getDescription(), locale));
                            }
                        }).toList()
        );
    }

    @Override
    public ResponseEntity<BaseResponse<Wallet>> addUser(String walletId, String userId, Locale locale) {
        commonService.findUserByIdOrThrow(userId, locale, null);
        if (walletRepository.findByIdAndUser(walletId, commonService.getCurrentUserId()).isEmpty()) {
            commonService.throwException(WALLET_NOT_FOUND, locale, null);
        }
        if (walletRepository.findByIdAndUser(walletId, userId).isPresent()) {
            commonService.throwException(WALLET_IN_USE_BY_USER, locale, null);
        }

        userWalletRepository.save(
          UserWallet.builder()
                  .userId(userId)
                  .walletId(walletId)
                  .updatedAt(getNow())
                  .build()
        );
        return responseFactory.success(null, null);
    }
}
