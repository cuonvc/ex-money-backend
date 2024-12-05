package com.exmoney.service.impl;

import com.exmoney.entity.User;
import com.exmoney.entity.UserWallet;
import com.exmoney.entity.Wallet;
import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.common.ResponseFactory;
import com.exmoney.payload.enumerate.ErrorCode;
import com.exmoney.payload.mapper.UserMapper;
import com.exmoney.payload.mapper.WalletMapper;
import com.exmoney.payload.request.wallet.WalletRequest;
import com.exmoney.payload.response.expense.ExpenseResponse;
import com.exmoney.payload.response.user.UserResponse;
import com.exmoney.payload.response.wallet.WalletResponse;
import com.exmoney.repository.ExpenseRepository;
import com.exmoney.repository.UserRepository;
import com.exmoney.repository.UserWalletRepository;
import com.exmoney.repository.WalletRepository;
import com.exmoney.service.CommonService;
import com.exmoney.service.ExpenseService;
import com.exmoney.service.WalletService;
import com.exmoney.util.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static com.exmoney.payload.enumerate.ErrorCode.*;
import static com.exmoney.util.Constant.Status.ACTIVE;
import static com.exmoney.util.Constant.Status.DELETED;
import static com.exmoney.util.Constant.WalletChangeUserAction.ADD;
import static com.exmoney.util.Constant.WalletChangeUserAction.REMOVE;
import static com.exmoney.util.Utils.getNow;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserWalletRepository userWalletRepository;
    private final CommonService commonService;
    private final WalletMapper walletMapper;
    private final ExpenseRepository expenseRepository;
    private final ResponseFactory responseFactory;
    private final UserMapper userMapper;

    @Value("${exmoney.application.action_log.wallet_create}") //chu y
    private String actionWalletCreate;
    @Value("${exmoney.application.action_log.wallet_add_user}")
    private String actionWalletAddUser;
    @Value("${exmoney.application.action_log.wallet_remove_user}")
    private String actionWalletRemoveUser;

    @Value("${exmoney.application.default.wallet_name}")
    private String defaultWalletName;
    @Value("${exmoney.application.default.wallet_description}")
    private String defaultWalletDescription;

    @Override
    @Transactional
    public void initDefaultWallet(Long userId, Locale locale) {
//        if (!walletRepository.findByUserId(userId).isEmpty()) {
//            commonService.throwException(INTERNAL_SERVER_ERROR, locale, null);
//        }
        Wallet wallet = new Wallet();
        wallet.setCreatedAt(getNow());
        wallet.setCreatedBy(0L);
        wallet.setName(defaultWalletName); //lưu là default.wallet_name luôn để có thể get dynamic
        wallet.setDescription(defaultWalletDescription);
        wallet.setOwnerUserId(userId);
        walletRepository.save(wallet);

        userWalletRepository.save(
                UserWallet.builder()
                        .userId(userId)
                        .walletId(wallet.getId())
                        .updatedAt(getNow())
                        .status(ACTIVE)
                        .build()
        );
    }

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<Wallet>> create(WalletRequest request, Locale locale) {
        Long userId = commonService.getCurrentUserId();
        if (walletRepository.findByNameOfUser(request.getName(), userId).isPresent()) {
            commonService.throwException(WALLET_NAME_ALREADY_EXISTED, locale, null, request.getName());
        }
        Wallet wallet = walletMapper.toEntity(request);
        wallet.setCreatedAt(getNow());
        wallet.setCreatedBy(userId);
        wallet.setOwnerUserId(userId);
        wallet.setIsDefault(false);
        wallet = walletRepository.save(wallet);

        userWalletRepository.save(
                UserWallet.builder()
                        .userId(userId)
                        .walletId(wallet.getId())
                        .updatedAt(getNow())
                        .status(ACTIVE)
                        .build()
        );
        return responseFactory.success(actionWalletCreate, wallet);
    }

    @Override
    public ResponseEntity<BaseResponse<WalletResponse>> detail(Long walletId, Locale locale) {
        Long currentUserId = commonService.getCurrentUserId();
        List<Wallet> wallets = walletRepository.findByUserId(currentUserId, false);
        if (walletId != null) {
            walletId = wallets.stream().filter(Wallet::getIsDefault).findFirst().get().getId();
        }
        final Long finalWalletId = walletId;
        Optional<Wallet> otpWallet = wallets.stream().filter(w -> w.getId().equals(finalWalletId)).findFirst();
        if (otpWallet.isEmpty()) {
            commonService.throwException(WALLET_NOT_FOUND, locale, null);
        }

        Wallet walletObj = otpWallet.get();
        if (walletObj.getIsDefault()) {
            walletObj.setName(commonService.getMessageSrc(walletObj.getName(), locale));
            walletObj.setDescription(commonService.getMessageSrc(walletObj.getDescription(), locale));
        }
        WalletResponse response = walletMapper.toResponse(walletObj);
        List<Map<Long, String>> walletMap = wallets.stream()
                .map(w -> Map.of(w.getId(), w.getName()))
                .toList();
        response.setOtherWallets(walletMap);

        List<ExpenseResponse> expenseResponses = expenseRepository.findAccessByUser(currentUserId, walletId)
                .stream().peek(e -> {
                    e.setWalletName(response.getName());
                    e.setDescription(commonService.getMessageSrc(e.getDescription(), locale));
                    e.setCategoryName(commonService.getMessageSrc(e.getCategoryName(), locale));
                })
                .toList();
        response.setExpenses(expenseResponses);

        return responseFactory.success(null, response);
    }

    @Override
    public ResponseEntity<BaseResponse<List<WalletResponse>>> listByUser(boolean isOwner, Locale locale) {
        Long userId = commonService.getCurrentUserId();
        List<WalletResponse> responseList = walletRepository.findByUserId(userId, isOwner)
                .stream().map(w -> toWalletResponse(w, userId, locale)).toList();

        return responseFactory.success(null, responseList);
    }

    private WalletResponse toWalletResponse(Wallet wallet, Long userId, Locale locale) {
        if (wallet.getIsDefault()) {
            wallet.setName(commonService.getMessageSrc(wallet.getName(), locale));
            wallet.setDescription(commonService.getMessageSrc(wallet.getDescription(), locale));
        }
        WalletResponse response = walletMapper.toResponse(wallet);
        List<UserResponse> memberList = userWalletRepository.findUserByWallet(wallet.getId())
                .stream().map(userMapper::entityToResponse)
                .toList();
        List<ExpenseResponse> expenseResponses = expenseRepository.findAccessByUser(userId, wallet.getId())
                .stream().peek(e -> {
                    e.setWalletName(response.getName());
                    e.setDescription(commonService.getMessageSrc(e.getDescription(), locale));
                    e.setCategoryName(commonService.getMessageSrc(e.getCategoryName(), locale));
                })
                .toList();
        response.setMembers(memberList);
        response.setExpenses(expenseResponses);
        return response;
    }

    @Override
    public ResponseEntity<BaseResponse<WalletResponse>> changeUser(String action, Long walletId, String userEmail, Locale locale) {
        Long ownerId = commonService.getCurrentUserId();
        User targetUser = commonService.findUserByEmailOrThrow(userEmail, locale, null);
        if (targetUser.getId().equals(ownerId)) {
            commonService.throwException(INTERNAL_SERVER_ERROR, locale, null);
        }
        if (walletRepository.findByIdAndOwner(walletId, ownerId).isEmpty()) {
            commonService.throwException(WALLET_NOT_FOUND, locale, null);
        }

        Optional<Wallet> wallet = walletRepository.findByIdAndUser(walletId, targetUser.getId());
        UserWallet userWallet = new UserWallet();
        String actionLog = "";
        Wallet toResponse = null;
        if (action.equals(ADD)) {
            if (wallet.isPresent()) {
                commonService.throwException(WALLET_IN_USE_BY_USER, locale, null);
            }
            //must empty
            userWallet = UserWallet.builder()
                    .userId(targetUser.getId())
                    .walletId(walletId)
                    .updatedAt(getNow())
                    .status(ACTIVE)
                    .build();
            actionLog = actionWalletAddUser;
            toResponse = walletRepository.findById(walletId).get();
        } else if (action.equals(REMOVE)) {
            if (wallet.isEmpty()) {
                commonService.throwException(WALLET_NOT_CONTAINS_USER, locale, null);
            }
            //must exist
            userWallet = userWalletRepository.findByUserAndWallet(targetUser.getId(), walletId);
            userWallet.setStatus(DELETED);
            actionLog = actionWalletRemoveUser;
        } else {
            commonService.throwException(INTERNAL_SERVER_ERROR, locale, null);
        }


        userWalletRepository.save(userWallet);

        WalletResponse response = toResponse != null
                ? toWalletResponse(toResponse, ownerId, locale)
                : new WalletResponse();
        return responseFactory.success(actionLog, response);
    }
}
