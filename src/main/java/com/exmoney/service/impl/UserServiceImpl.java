package com.exmoney.service.impl;

import com.exmoney.entity.RefreshToken;
import com.exmoney.entity.User;
import com.exmoney.exception.APIException;
import com.exmoney.exception.ResourceNotFoundException;
import com.exmoney.exception.ServiceException;
import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.common.ResponseFactory;
import com.exmoney.payload.enumerate.ErrorCode;
import com.exmoney.payload.mapper.TokenMapper;
import com.exmoney.payload.mapper.UserMapper;
import com.exmoney.payload.request.auth.LoginRequest;
import com.exmoney.payload.request.auth.PasswordChangeRequest;
import com.exmoney.payload.request.user.ProfileRequest;
import com.exmoney.payload.request.auth.RegRequest;
import com.exmoney.payload.response.user.PageResponseUsers;
import com.exmoney.payload.response.auth.TokenObjectResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

import static com.exmoney.payload.enumerate.ErrorCode.*;
import static com.exmoney.util.Utils.getNow;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private static final String AVATAR = "avatarUrl";
    private static final String COVER = "";

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
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
    public ResponseEntity<BaseResponse<UserResponse>> register(RegRequest request) {

        request.setEmail(request.getEmail().trim().toLowerCase());
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ServiceException(EMAIL_EXISTED, request.getEmail());
        }

        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new ServiceException(PASSWORD_NOT_MATCHED, request.getPasswordConfirm());
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
    public ResponseEntity<BaseResponse<TokenObjectResponse>> signIn(LoginRequest request) {
        request.setEmail(request.getEmail().trim().toLowerCase());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ServiceException(USER_NOT_FOUND, request.getEmail()));

        if (!validPassword(request.getPassword(), user.getPassword())) {
            throw new ServiceException(PASSWORD_NOT_MATCHED);
        }

        String accessToken = jwtTokenProvider.generateToken(request.getEmail());
        RefreshToken refreshToken = tokenService.generateTokenObject(user);
        UserResponse userResponse = userMapper.entityToResponse(user);
        TokenObjectResponse response = TokenObjectResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .refreshToken(tokenMapper.mapToDto(refreshToken))
                .userResponse(userResponse)
                .build();

        return responseFactory.success("Đăng nhập thành công", response);
    }

    @Override
    public ResponseEntity<BaseResponse<String>> signOut() {
        tokenService.clearToken(commonService.getCurrentUserId());
        return responseFactory.success("Đã đăng xuất");
    }

    @Override
    public ResponseEntity<BaseResponse<TokenObjectResponse>> renewAccessToken(String refreshToken) {
        RefreshToken rt = tokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new ServiceException(INVALID_CREDENTIAL));

        User user = userRepository.findById(rt.getUserId())
                .orElseThrow(() -> new ServiceException(INTERNAL_SERVER_ERROR));

        if (rt.getExpireDate().compareTo(new Date()) <= 0) {
            throw new ServiceException(INVALID_CREDENTIAL);
        }

        return responseFactory.success(
                TokenObjectResponse.builder()
                        .accessToken(jwtTokenProvider.generateToken(user.getEmail()))
                        .refreshToken(tokenMapper.mapToDto(rt))
                        .build()
        );
    }

    @Override
    public ResponseEntity<BaseResponse<UserResponse>> editProfile(ProfileRequest request) {
        User user = userRepository.findById(commonService.getCurrentUserId())
                .orElseThrow(() -> new ServiceException(USER_NOT_FOUND));  //not happen

        user = userMapper.profileToEntity(request, user);
        user.setUpdatedAt(getNow());
        UserResponse response = userMapper.entityToResponse(userRepository.save(user));
        return responseFactory.success("Cập nhập trang cá nhân thành công!", response);
    }

    @Override
    public ResponseEntity<BaseResponse<String>> changePassword(PasswordChangeRequest request) {
        User user = userRepository.findById(commonService.getCurrentUserId())
                .orElseThrow(() -> new ServiceException(USER_NOT_FOUND));

        if (!request.getNewPassword().equals(request.getRetypePassword())) {
            return responseFactory.fail(HttpStatus.BAD_REQUEST, "Mật khâu không trùng khớp");
        }

        if (!validPassword(request.getOldPassword(), user.getPassword())) {
            return responseFactory.fail(HttpStatus.BAD_REQUEST, "Mật khẩu cũ không chính xác");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return responseFactory.success("Thiết lập mật khẩu thành công");
    }

    @Override
    public ResponseEntity<BaseResponse<UserResponse>> getAccount() {
        User user = userRepository.findById(commonService.getCurrentUserId())
                .orElse(new User());
        UserResponse response = userMapper.entityToResponse(user);

        return responseFactory.success(response);
    }

    @Override
    public ResponseEntity<BaseResponse<PageResponseUsers>> getAll(Integer pageNo, Integer pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<User> users = userRepository.findAll(pageable);
        PageResponseUsers pageResponse = paging(users);
        return responseFactory.success(pageResponse);
    }

    @Override
    public ResponseEntity<BaseResponse<UserResponse>> uploadAvatar(MultipartFile file) {
        User user = userRepository.findById(commonService.getCurrentUserId())
                .orElseThrow(() -> new ServiceException(INTERNAL_SERVER_ERROR));
        return responseFactory.success("Cập nhật ảnh đại diện thành công.",
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
