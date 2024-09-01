package com.exmoney.service.impl;

import com.exmoney.entity.RefreshToken;
import com.exmoney.entity.User;
import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.common.ResponseFactory;
import com.exmoney.payload.dto.AccessTokenDto;
import com.exmoney.payload.mapper.TokenMapper;
import com.exmoney.payload.mapper.UserMapper;
import com.exmoney.payload.request.auth.LoginRequest;
import com.exmoney.payload.request.auth.PasswordChangeRequest;
import com.exmoney.payload.request.user.ProfileRequest;
import com.exmoney.payload.request.auth.RegRequest;
import com.exmoney.payload.response.user.PageResponseUsers;
import com.exmoney.payload.response.user.UserResponse;
import com.exmoney.repository.RefreshTokenRepository;
import com.exmoney.repository.UserRepository;
import com.exmoney.security.jwt.JwtTokenProvider;
import com.exmoney.service.CommonService;
import com.exmoney.service.TokenService;
import com.exmoney.service.UserService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.exmoney.payload.enumerate.ErrorCode.*;
import static com.exmoney.util.Utils.getNow;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private static final String AVATAR = "avatarUrl";
    private static final String COVER = "";

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository tokenRepository;
    private final TokenService tokenService;
    private final UserMapper userMapper;
    private final TokenMapper tokenMapper;
    private final ResponseFactory responseFactory;
    private final EntityManager entityManager;
    private final CommonService commonService;

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<UserResponse>> register(RegRequest request, Locale locale) {

        request.setEmail(request.getEmail().trim().toLowerCase());
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            commonService.throwException(EMAIL_EXISTED, locale);
        }

        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            commonService.throwException(PASSWORD_NOT_MATCHED, locale);
        }

        User user = userMapper.regRequestToEntity(request);
        user.setCreatedAt(getNow());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        entityManager.persist(user);
        tokenService.initRefreshToken(user);
        UserResponse response = userMapper.entityToResponse(user);
        return responseFactory.success(response);
    }

    @Override
    public ResponseEntity<BaseResponse<Object>> signIn(LoginRequest request, Locale locale) {
        request.setEmail(request.getEmail().trim().toLowerCase());

        User user = commonService.findUserByEmailOrThrow(request.getEmail(), locale);

        if (!validPassword(request.getPassword(), user.getPassword())) {
            commonService.throwException(PASSWORD_INCORRECT, locale);
        }

        AccessTokenDto accessTokenObj = jwtTokenProvider.generateToken(request.getEmail());
        RefreshToken refreshToken = tokenService.generateTokenObject(user);
        UserResponse userResponse = userMapper.entityToResponse(user);

        Object[] repsonse = {accessTokenObj, tokenMapper.mapToDto(refreshToken), userResponse};

        return responseFactory.success(commonService.getMessageSrc("sign_in.success", locale), repsonse);
    }

    @Override
    public ResponseEntity<BaseResponse<String>> signOut(Locale locale) {
        tokenService.clearToken(commonService.getCurrentUserId());
        return responseFactory.success(commonService.getMessageSrc("sign_out.success", locale));
    }

    @Override
    public ResponseEntity<BaseResponse<Object>> renewAccessToken(String refreshToken) {
        Optional<RefreshToken> refreshTokenOp = tokenRepository.findByToken(refreshToken);
        if (refreshTokenOp.isEmpty()) {
            commonService.throwException(INVALID_CREDENTIAL, Locale.forLanguageTag("us"));
        }

        RefreshToken rt = refreshTokenOp.get();
        if (rt.getExpireDate().compareTo(new Date()) <= 0) {
            commonService.throwException(INVALID_CREDENTIAL, Locale.forLanguageTag("us"));
        }

        User user = commonService.findUserByIdOrThrow(rt.getUserId(), Locale.forLanguageTag("us"));
        AccessTokenDto accessToken = jwtTokenProvider.generateToken(user.getEmail());
        Object[] response = {accessToken, rt, userMapper.entityToResponse(user)};
        return responseFactory.success(response);
    }

    @Override
    public ResponseEntity<BaseResponse<UserResponse>> editProfile(ProfileRequest request, Locale locale) {
        User user = commonService.findUserByIdOrThrow(commonService.getCurrentUserId(), locale);  //not happen

        user = userMapper.profileToEntity(request, user);
        user.setUpdatedAt(getNow());
        UserResponse response = userMapper.entityToResponse(userRepository.save(user));
        return responseFactory.success(response);
    }

    @Override
    public ResponseEntity<BaseResponse<String>> changePassword(PasswordChangeRequest request, Locale locale) {
        User user = commonService.findUserByIdOrThrow(commonService.getCurrentUserId(), locale);

        if (!validPassword(request.getOldPassword(), user.getPassword())) {
            commonService.throwException(PASSWORD_INCORRECT, locale);
        }

        if (!request.getNewPassword().equals(request.getRetypePassword())) {
            commonService.throwException(PASSWORD_NOT_MATCHED, locale);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return responseFactory.success(commonService.getMessageSrc("password_change.success", locale));
    }

    @Override
    public ResponseEntity<BaseResponse<UserResponse>> getAccount(Locale locale) {
        User user = commonService.findUserByIdOrThrow(commonService.getCurrentUserId(), locale);
        UserResponse response = userMapper.entityToResponse(user);

        return responseFactory.success(response);
    }

    @Override
    public ResponseEntity<BaseResponse<PageResponseUsers>> getAll(Integer pageNo, Integer pageSize, String sortBy, String sortDir, Locale locale) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<User> users = userRepository.findAll(pageable);
        PageResponseUsers pageResponse = paging(users);
        return responseFactory.success(pageResponse);
    }

    @Override
    public ResponseEntity<BaseResponse<UserResponse>> uploadAvatar(MultipartFile file, Locale locale) {
        User user = commonService.findUserByIdOrThrow(commonService.getCurrentUserId(), locale);
        return responseFactory.success(commonService.getMessageSrc("image_update.success", locale),
                userMapper.entityToResponse(userRepository.save(user)));
    }

    private PageResponseUsers paging(Page<User> users) {
        List<UserResponse> userList = users.getContent()
                .stream().map(entity -> {
                    UserResponse response = userMapper.entityToResponse(entity);
                    return response;
                })
                .toList();

        return (PageResponseUsers) PageResponseUsers.builder()
                .pageNo(users.getNumber())
                .pageSize(userList.size())
                .content(userList)
                .totalPages(users.getTotalPages())
                .totalItems((int) users.getTotalElements())
                .last(users.isLast())
                .build();
    }

    private boolean validPassword(String rawPassword, String archivePassword) {
        return passwordEncoder.matches(rawPassword, archivePassword);
    }
}
