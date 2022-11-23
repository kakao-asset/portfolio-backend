package com.kakaoasset.portfolio.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

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

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Stock> stockList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<StockHistory> stockHistoryList;

    @Builder
    public Member(String kakaoId, String profile, String nickname,
                  String email, String role) {

        this.kakaoId = kakaoId;
        this.profile = profile;
        this.nickname = nickname;
        this.email = email;
        this.role = Role.valueOf(role);
    }
}

