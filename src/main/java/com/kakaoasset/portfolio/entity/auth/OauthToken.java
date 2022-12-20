package com.kakaoasset.portfolio.entity.auth;

import lombok.Data;

@Data
public class OauthToken {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private Long expires_in;
    private String scope;
    private Long refresh_token_expires_in;
}
