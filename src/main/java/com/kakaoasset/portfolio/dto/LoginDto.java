package com.kakaoasset.portfolio.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginDto {
    private String userId;
    private String nickname;
    private String profile;
    private String accessToken;
    private String refreshToken;
}
