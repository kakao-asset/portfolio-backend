package com.kakaoasset.portfolio.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberDto {

    private String userId;
    private String nickname;
    private String profile;
    private String Email;
}
