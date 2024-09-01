package com.exmoney.service.impl;

import com.exmoney.entity.RefreshToken;
import com.exmoney.entity.User;
import com.exmoney.payload.OAuthUserInfo;
import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.common.ResponseFactory;
import com.exmoney.payload.dto.AccessTokenDto;
import com.exmoney.payload.enumerate.Role;
import com.exmoney.payload.enumerate.Status;
import com.exmoney.payload.enumerate.UserProvider;
import com.exmoney.payload.mapper.OAuthUserMapper;
import com.exmoney.payload.mapper.TokenMapper;
import com.exmoney.payload.mapper.UserMapper;
import com.exmoney.payload.response.auth.GithubResponseToken;
import com.exmoney.payload.response.auth.GithubResponseUser;
import com.exmoney.payload.response.auth.GoogleResponseUser;
import com.exmoney.repository.RefreshTokenRepository;
import com.exmoney.repository.UserRepository;
import com.exmoney.security.jwt.JwtTokenProvider;
import com.exmoney.service.OAuthService;
import com.exmoney.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static com.exmoney.util.Utils.getNow;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthServiceImpl implements OAuthService {

    private final UserMapper userMapper;
    @Value("${exmoney.application.oauth.google.api-get-info}")
    private String googleApiGetInfo;

    @Value("${exmoney.application.oauth.github.api-get-info}")
    private String githubApiGetInfo;

    @Value("${exmoney.application.oauth.github.api-valid-token}")
    private String githubApiValidToken;

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String githubClientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String githubClientSecret;

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final RefreshTokenRepository tokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenMapper tokenMapper;
    private final ResponseFactory responseFactory;
    private final OAuthUserMapper oAuthUserMapper;

    @Override
    public ResponseEntity<BaseResponse<Object>> validateGoogleToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<GoogleResponseUser> response = restTemplate.exchange(googleApiGetInfo, HttpMethod.GET, entity, GoogleResponseUser.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                GoogleResponseUser data = response.getBody();
                log.info("response_user_info - {}", data);
                return saveUser(oAuthUserMapper.toUserInfo(data), UserProvider.GOOGLE);

            } else {
                log.info("Failure...");
                return responseFactory.fail(HttpStatus.BAD_REQUEST, "Đăng nhập thất bại...", null);
            }
        } catch (HttpClientErrorException e) {
            log.error("Failed request in try-catch - {}", e.getMessage());
            return responseFactory.fail(HttpStatus.BAD_REQUEST, "Đăng nhập thất bại...", null);
        }
    }

    @Override
    public ResponseEntity<BaseResponse<Object>> validateGithubCode(String code) {
        log.info("Logging github - {}", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(githubApiValidToken)
                .queryParam("client_id", githubClientId)
                .queryParam("client_secret", githubClientSecret)
                .queryParam("code", code);

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<GithubResponseToken> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                request,
                GithubResponseToken.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Token - {}", response.getBody().getAccess_token());
            GithubResponseUser userInfo = getGithubUserInfo(response.getBody());
            return saveUser(oAuthUserMapper.toUserInfo(userInfo), UserProvider.GITHUB);

        } else {
            log.info("Failure...");
            return responseFactory.fail(HttpStatus.BAD_REQUEST, "Đăng nhập thất bại...", null);
        }
    }

    private GithubResponseUser getGithubUserInfo(GithubResponseToken responseToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, responseToken.getToken_type() + " " + responseToken.getAccess_token());
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<GithubResponseUser> response = restTemplate.exchange(githubApiGetInfo, HttpMethod.GET, httpEntity, GithubResponseUser.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                GithubResponseUser data = response.getBody();
                log.info("response_user_info - {}", data);
                return data;
            } else {
                log.info("Failure...");
            }
        } catch (HttpClientErrorException e) {
            log.error("Failed request in try-catch - {}", e.getMessage());
        }

        return null;
    }

    private ResponseEntity<BaseResponse<Object>> saveUser(OAuthUserInfo userInfo, UserProvider provider) {
        User user = userRepository.findByEmail(userInfo.getEmail())
                .orElse(User.builder()
                        .name(userInfo.getName())
                        .email(userInfo.getEmail())
                        .avatarUrl(userInfo.getAvatarUrl())
                        .createdAt(getNow())
                        .role(Role.USER.name())
                        .userProvider(provider.name())
                        .status(Status.ACTIVE.name())
                        .build());
        user = userRepository.save(user);

        RefreshToken refreshToken = tokenRepository.findByUserId(user.getId())
                .orElse(RefreshToken.builder()
                        .userId(user.getId())
                        .build());
        tokenRepository.save(refreshToken);
        refreshToken = tokenService.generateTokenObject(user);
        tokenRepository.save(refreshToken);
        AccessTokenDto accessToken = jwtTokenProvider.generateToken(user.getEmail());

        Object[] response = {accessToken, refreshToken, userMapper.entityToResponse(user)};
        return responseFactory.success("Success", response);
    }
}
