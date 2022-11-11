package com.kakaoasset.portfolio.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //(2)
    @Column(name = "user_id") //(3)
    private Long id;

    @Column(name = "kakao_id")
    private String kakaoId;

    @Column(name = "profile")
    private String profile;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public Member(String kakaoId, String profile, String nickname,
                  String email) {

        this.kakaoId = kakaoId;
        this.profile = profile;
        this.nickname = nickname;
        this.email = email;
    }
}

