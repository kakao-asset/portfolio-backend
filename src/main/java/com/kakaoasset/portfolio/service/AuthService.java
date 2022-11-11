package com.kakaoasset.portfolio.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaoasset.portfolio.dto.*;
import com.kakaoasset.portfolio.entity.Member;
import com.kakaoasset.portfolio.entity.auth.KakaoProfile;
import com.kakaoasset.portfolio.entity.auth.OauthToken;
import com.kakaoasset.portfolio.exception.UnauthorizedException;
import com.kakaoasset.portfolio.jwt.JwtProvider;
import com.kakaoasset.portfolio.repostiory.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${kakao-oauth.client-id}")
    private String clientId;

    @Value("${kakao-oauth.redirect-uri}")
    private String redirectUri;

    public OauthToken getKakaoAccessToken(String code) {

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers);

        ResponseEntity<String> accessTokenResponse = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        OauthToken oauthToken = null;
        try {
            oauthToken = objectMapper.readValue(accessTokenResponse.getBody(), OauthToken.class);
        } catch (JsonProcessingException e) {
            log.error("Json 처리 에러");
            e.printStackTrace();
        }

        return oauthToken;
    }

    public LoginDto saveMemberAndGetJwtToken(String token) {

        KakaoProfile profile = findProfile(token);

        Member member = memberRepository.findByEmail(profile.getKakao_account().getEmail());

        if(member == null) {
            member = Member.builder()
                    .kakaoId(String.valueOf(profile.getId()))
                    .profile(profile.getKakao_account().getProfile().getProfile_image_url())
                    .nickname(profile.getKakao_account().getProfile().getNickname())
                    .email(profile.getKakao_account().getEmail())
                    .build();

            memberRepository.save(member);
        }

        MemberDto memberDto = MemberDto.builder()
                .userId(member.getKakaoId())
                .Email(member.getEmail())
                .nickname(member.getNickname())
                .profile(member.getProfile())
                .build();

        TokenDto tokenDto = TokenDto.builder()
                        .accessToken(jwtProvider.createAccessToken(memberDto.getUserId()))
                        .refreshToken(jwtProvider.createRefreshToken())
                        .build();

        LoginDto loginDto = LoginDto.builder()
                .userId(memberDto.getUserId())
                .nickname(memberDto.getNickname())
                .profile(memberDto.getProfile())
                .accessToken(tokenDto.getAccessToken())
                .refreshToken(tokenDto.getRefreshToken())
                .build();

        // redis에 refreshToken 저장
        redisTemplate.opsForValue().set(memberDto.getUserId(), tokenDto.getRefreshToken(), jwtProvider.getExpirationTime(tokenDto.getRefreshToken()), TimeUnit.MILLISECONDS);

        return loginDto;
    }

    public KakaoProfile findProfile(String token) {

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest =
                new HttpEntity<>(headers);

        // Http 요청 (POST 방식) 후, response 변수에 응답을 받음
        ResponseEntity<String> kakaoProfileResponse = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        KakaoProfile kakaoProfile = null;

        try {
            kakaoProfile = objectMapper.readValue(kakaoProfileResponse.getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return kakaoProfile;
    }

    public TokenDto reissue(RefreshDto refreshDto){

        if(!jwtProvider.isTokenValid(refreshDto.getRefreshToken())){
            throw new UnauthorizedException();
        }

        return TokenDto.builder().accessToken(jwtProvider.createAccessToken(refreshDto.getUserId())).build();

    }

    public void logout(String id){

        // 1. 카카오에 unlink 요청하기
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        headers.add("Authorization", "KakaoAk " + "81fa08c2d9abab66a331d2f5295a825a");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("target_id_type", "user_id");
        params.add("target_id", id);
        ResponseEntity<String> unlinkResponse = rt.exchange(
                "https://kapi.kakao.com/v1/user/unlink",
                HttpMethod.POST,
                new HttpEntity<>(headers, params),
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            System.out.println(objectMapper.readValue(unlinkResponse.getBody(), OauthToken.class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // 2. redis에서 리프레쉬 토큰 삭제
        redisTemplate.delete(id);
    }

    public boolean isValidToken(String token){
        return jwtProvider.isTokenValid(token);
    }
}
