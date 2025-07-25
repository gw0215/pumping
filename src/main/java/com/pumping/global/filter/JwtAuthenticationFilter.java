package com.pumping.global.filter;

import com.pumping.domain.member.model.Member;
import com.pumping.domain.member.repository.MemberRepository;
import com.pumping.global.common.util.JwtUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements Filter {

    private final JwtUtil jwtUtil;

    private final MemberRepository memberRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpRes = (HttpServletResponse) response;
        String authHeader = httpReq.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Long memberId = jwtUtil.validateAndExtractMemberId(token);

            if (memberId != null) {
                Optional<Member> optionalMember = memberRepository.findById(memberId);
                if (optionalMember.isPresent()) {
                    Member member = optionalMember.get();
                    request.setAttribute("member", member);
                    log.debug("인증된 사용자: {} (ID: {})", member.getEmail(), member.getId());
                    chain.doFilter(request, response);
                    return;
                } else {
                    log.warn("존재하지 않는 회원 ID로 접근 시도: {}", memberId);
                    writeErrorResponse(httpRes, HttpServletResponse.SC_NOT_FOUND, "회원을 찾을 수 없습니다.");
                    return;
                }
            } else {
                log.warn("유효하지 않은 JWT 토큰. Authorization 헤더: {}", authHeader);
                writeErrorResponse(httpRes, HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다.");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private void writeErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        String body = String.format("{\"message\": \"%s\"}", message);
        response.getWriter().write(body);
    }
}