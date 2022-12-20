package com.kakaoasset.portfolio.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberDto {

    private String memberId;
    private String nickname;
    private String profile;
    private String email;
}
