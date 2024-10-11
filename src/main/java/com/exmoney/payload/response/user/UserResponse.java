package com.exmoney.payload.response.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private String id;
    private String name;
    private String email;;
    private String avatarUrl;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String status;
    private String deviceToken;
}
