package com.shop.service;

import com.shop.entity.Member;
import com.shop.jwt.TokenProvider;
import com.shop.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final MemberService memberService;

    public String createNewAccessToken(String refreshToken) {
        if(!tokenProvider.validToken(refreshToken))
            throw new IllegalArgumentException("Unexpected token");

        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        Member member = memberService.findById(userId);
        return tokenProvider.generateToken(member, Duration.ofHours(2));
    }
}
