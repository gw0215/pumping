package com.pumping.domain.inbody.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pumping.AbstractControllerTest;
import com.pumping.domain.board.controller.BoardController;
import com.pumping.domain.inbody.dto.InBodyRequest;
import com.pumping.domain.inbody.dto.InBodyResponse;
import com.pumping.domain.inbody.service.InBodyService;
import com.pumping.domain.member.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InBodyController.class)
class InBodyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InBodyService inBodyService;

    private MockHttpSession session;

    @BeforeEach
    void setup() {
        Member member = new Member(); // 필요한 필드 채워도 됨
        session = new MockHttpSession();
        session.setAttribute("member", member);
    }

    @Nested
    class InBody_저장_API {

        @Test
        void 올바른_요청은_201을_반환한다() throws Exception {
            InBodyRequest request = new InBodyRequest(70.0f, 30.0f, 15.0f, LocalDate.of(2024, 6, 28));

            mockMvc.perform(post("/inbody")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .session(session))
                    .andExpect(status().isCreated());

            verify(inBodyService).save(any(), eq(70.0f), eq(30.0f), eq(15.0f), eq(LocalDate.of(2024, 6, 28)));
        }

        @ParameterizedTest
        @CsvSource({
                ",30.0,15.0,2024-06-28,체중은 필수입니다.",
                "0.0,30.0,15.0,2024-06-28,체중은 0보다 커야 합니다.",
                "70.0,,15.0,2024-06-28,골격근량은 필수입니다.",
                "70.0,0.0,15.0,2024-06-28,골격근량은 0보다 커야 합니다.",
                "70.0,30.0,,2024-06-28,체지방량은 필수입니다.",
                "70.0,30.0,0.0,2024-06-28,체지방량은 0보다 커야 합니다.",
                "70.0,30.0,15.0,,날짜는 필수입니다."
        })
        void 잘못된_요청은_400과_에러메시지를_반환한다(
                String weightStr, String smmStr, String bfmStr, String dateStr, String expectedMessage) throws Exception {

            Float weight = parseFloat(weightStr);
            Float smm = parseFloat(smmStr);
            Float bfm = parseFloat(bfmStr);
            LocalDate date = parseDate(dateStr);

            InBodyRequest request = new InBodyRequest(weight, smm, bfm, date);

            mockMvc.perform(post("/inbody")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .session(session))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(expectedMessage));
        }

        private Float parseFloat(String value) {
            return (value == null || value.isBlank()) ? null : Float.parseFloat(value);
        }

        private LocalDate parseDate(String value) {
            return (value == null || value.isBlank()) ? null : LocalDate.parse(value);
        }
    }

    @Nested
    class 최근_InBody_조회_API {

        @Test
        void 데이터가_있으면_200과_응답을_반환한다() throws Exception {
            InBodyResponse response = new InBodyResponse(70.0f, 30.0f, 15.0f, LocalDate.of(2024, 6, 28));
            given(inBodyService.findRecentInBody(any())).willReturn(response);

            mockMvc.perform(get("/inbody/recent")
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.weight").value(70.0))
                    .andExpect(jsonPath("$.smm").value(30.0))
                    .andExpect(jsonPath("$.bfm").value(15.0))
                    .andExpect(jsonPath("$.date").value("2024-06-28"));
        }

        @Test
        void 데이터가_없으면_204를_반환한다() throws Exception {
            given(inBodyService.findRecentInBody(any())).willReturn(null);

            mockMvc.perform(get("/inbody/recent")
                            .session(session))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    class InBody_범위_조회_API {

        @Test
        void 조회_기간에_맞는_데이터가_있으면_200과_리스트를_반환한다() throws Exception {
            List<InBodyResponse> responses = List.of(
                    new InBodyResponse(70.0f, 30.0f, 15.0f, LocalDate.of(2024, 6, 28)),
                    new InBodyResponse(71.0f, 31.0f, 14.5f, LocalDate.of(2024, 6, 29))
            );

            given(inBodyService.findByDate(any(), eq(LocalDate.of(2024, 6, 28)), eq(LocalDate.of(2024, 6, 29))))
                    .willReturn(responses);

            mockMvc.perform(get("/inbody")
                            .param("from", "2024-06-28")
                            .param("to", "2024-06-29")
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].date").value("2024-06-28"))
                    .andExpect(jsonPath("$[1].date").value("2024-06-29"));
        }
    }
}
