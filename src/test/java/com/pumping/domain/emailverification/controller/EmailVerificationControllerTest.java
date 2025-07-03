package com.pumping.domain.emailverification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pumping.AbstractControllerTest;
import com.pumping.domain.member.dto.EmailCodeCheckRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EmailVerificationControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class 이메일_전송_API {

        @Test
        void 이메일_전송_요청을_보내면_201을_반환한다() throws Exception {

            String email = "test@example.com";

            mockMvc.perform(get("/emails")
                            .param("email", email))
                    .andExpect(status().isCreated());

            verify(emailVerificationService).sendCode(email);
        }
    }

    @Nested
    class 인증코드_검증_API {

        @Test
        void 유효한_인증코드_요청은_204를_반환한다() throws Exception {
            EmailCodeCheckRequest request = new EmailCodeCheckRequest("test@example.com", "12345");

            mockMvc.perform(post("/emails")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent());

            verify(emailVerificationService).checkCode(any(EmailCodeCheckRequest.class));
        }

        @ParameterizedTest
        @CsvSource({
                "'', 12345, 이메일은 필수입니다.",
                "invalidEmail, 12345, 올바른 이메일 형식이어야 합니다.",
                "test@example.com, 1234, 인증 코드는 5자리여야 합니다.",
                "test@example.com, 123456, 인증 코드는 5자리여야 합니다."
        })
        void 잘못된_입력값이면_400과_에러메시지를_반환한다(String email, String code, String expectedMessage) throws Exception {

            EmailCodeCheckRequest request = new EmailCodeCheckRequest(email, code);

            mockMvc.perform(post("/emails")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(expectedMessage));
        }
    }
}