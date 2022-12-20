package com.kakaoasset.portfolio.controller;

import com.kakaoasset.portfolio.dto.*;
import com.kakaoasset.portfolio.entity.auth.OauthToken;
import com.kakaoasset.portfolio.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/oauth/kakao")
    public ResponseEntity<BasicResponse> login(@RequestParam("code") String code) {
        // 프론트 넘어온 인가 코드를 통해 kakao access token 발급
        OauthToken oauthToken = authService.getKakaoAccessToken(code);

        BasicResponse response = BasicResponse.builder()
                .code(HttpStatus.OK.value())
                .message("로그인에 성공했습니다.")
                .data(authService.saveMemberAndGetJwtToken(oauthToken.getAccess_token()))
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/refresh")
    public ResponseEntity<BasicResponse> refreshToken(@RequestBody RefreshDto refreshDto){
        TokenDto tokenResponseDto = authService.reissue(refreshDto);
        BasicResponse response = BasicResponse.builder()
                .code(HttpStatus.OK.value())
                .message("토큰 재발급에 성공했습니다.")
                .data(tokenResponseDto)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/logout/{userId}")
    public ResponseEntity<BasicResponse> logout(@PathVariable("userId") Long userId){
        // 카카오에 연결 끊기 요청 및 레디스에서 리프레시 토큰 삭제
        authService.logout(userId);

        BasicResponse response = BasicResponse.builder()
                .code(HttpStatus.OK.value())
                .message("로그아웃에 성공했습니다.")
                .data(Collections.emptyList())
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/unlink/{userId}")
    public ResponseEntity<BasicResponse> unlink(@PathVariable("userId") Long userId){
        // 카카오에 연결 끊기 요청 및 레디스에서 리프레시 토큰 삭제
        authService.unlink(userId);

        BasicResponse response = BasicResponse.builder()
                .code(HttpStatus.OK.value())
                .message("회원탈퇴에 성공했습니다.")
                .data(Collections.emptyList())
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

