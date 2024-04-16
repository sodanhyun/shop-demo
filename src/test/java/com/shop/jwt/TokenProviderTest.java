package com.shop.jwt;

import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TokenProviderTest {

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtProperties jwtProperties;

    @Test
    void generateToken() {
        Member saveMember = new Member();
        saveMember.setEmail("user@email.com");
        saveMember.setPassword("test");
        Member testMember = memberRepository.save(saveMember);

        String token = tokenProvider.generateToken(testMember, Duration.ofDays(14));

        Long userId = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);

        assertEquals(userId, testMember.getId());
    }

    @Test
    void validToken_invalidToken() {
        String token = JwtFactory.builder()
                .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
                .build()
                .createToken(jwtProperties);

        boolean result = tokenProvider.validToken(token);
        assertFalse(result);
    }

    @Test
    void validToken_validToken() {
        String token = JwtFactory.withDefaultValues()
                .createToken(jwtProperties);
        boolean result = tokenProvider.validToken(token);
        assertTrue(result);
    }

    @Test
    void getAuthentication() {
        String userEmail = "user@email.com";
        String token = JwtFactory.builder()
                .subject(userEmail)
                .build()
                .createToken(jwtProperties);

        Authentication authentication = tokenProvider.getAuthentication(token);
        assertEquals(((UserDetails)authentication.getPrincipal()).getUsername(), userEmail);
    }

    @Test
    void getUserId() {
        Long userId = 1L;
        String token = JwtFactory.builder()
                .claims(Map.of("id", userId))
                .build()
                .createToken(jwtProperties);

        Long userIdByToken = tokenProvider.getUserId(token);

        assertEquals(userIdByToken, userId);
    }

}