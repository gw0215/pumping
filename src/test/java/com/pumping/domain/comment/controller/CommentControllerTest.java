package com.pumping.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pumping.AbstractControllerTest;
import com.pumping.domain.board.exception.NoPermissionException;
import com.pumping.domain.comment.dto.CommentRequest;
import com.pumping.domain.comment.dto.CommentResponse;
import com.pumping.domain.comment.fixture.CommentFixture;
import com.pumping.domain.member.fixture.MemberFixture;
import com.pumping.domain.member.model.Member;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Member member;

    @BeforeEach
    void setUp() {
        member = MemberFixture.createMember();
    }

    @Test
    void 정상_댓글_등록시_201_응답을_반환한다() throws Exception {
        CommentRequest commentRequest = CommentFixture.createCommentRequest();
        String json = objectMapper.writeValueAsString(commentRequest);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(post("/boards/{boardId}/comments",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .session(session))
                .andExpect(status().isCreated());
    }

    @Test
    void 빈_내용으로_댓글_등록시_400_응답과_에러_메시지를_반환한다() throws Exception {
        CommentRequest request = new CommentRequest(" ");
        String json = objectMapper.writeValueAsString(request);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(post("/boards/{boardId}/comments", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .session(session))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("댓글 내용을 입력해주세요."));
    }

    @Test
    void 댓글_목록_조회시_200_응답과_리스트를_반환한다() throws Exception {
        int commentSize = 5;
        List<CommentResponse> content = CommentFixture.createCommentResponse(commentSize);
        Page<CommentResponse> commentPage = new PageImpl<>(content);

        when(commentService.findAll(eq(1L), any(Pageable.class))).thenReturn(commentPage);

        mockMvc.perform(get("/boards/{boardId}/comments", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(commentSize)))
                .andExpect(jsonPath("$.content[0].content").value(content.get(0).content()));
    }


    @Test
    void 정상_댓글_수정시_204_응답을_반환한다() throws Exception {
        CommentRequest request = new CommentRequest("수정된 댓글");
        String json = objectMapper.writeValueAsString(request);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(patch("/boards/{boardId}/comments/{commentId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .session(session))
                .andExpect(status().isNoContent());
    }

    @Test
    void 빈_내용으로_댓글_수정시_400_응답을_반환한다() throws Exception {
        CommentRequest invalidRequest = new CommentRequest("   ");
        String json = objectMapper.writeValueAsString(invalidRequest);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(patch("/boards/{boardId}/comments/{commentId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .session(session))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("댓글 내용을 입력해주세요."));
    }


    @Test
    void 존재하지_않는_댓글_수정시_404_응답을_반환한다() throws Exception {
        CommentRequest request = new CommentRequest("수정");
        String json = objectMapper.writeValueAsString(request);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        doThrow(new EntityNotFoundException("댓글을 찾을 수 없습니다."))
                .when(commentService).update(eq(99L), anyString(), any(Member.class));

        mockMvc.perform(patch("/boards/{boardId}/comments/{commentId}", 1L, 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .session(session))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("댓글을 찾을 수 없습니다."));
    }

    @Test
    void 권한_없는_사용자가_댓글_수정시_403_응답을_반환한다() throws Exception {
        CommentRequest request = new CommentRequest("수정");
        String json = objectMapper.writeValueAsString(request);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        doThrow(new NoPermissionException("해당 댓글을 수정할 권한이 없습니다."))
                .when(commentService).update(eq(1L), anyString(), any(Member.class));

        mockMvc.perform(patch("/boards/{boardId}/comments/{commentId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .session(session))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("해당 댓글을 수정할 권한이 없습니다."));
    }

    @Test
    void 정상_댓글_삭제시_204_응답을_반환한다() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(delete("/boards/{boardId}/comments/{commentId}", 1L, 1L)
                        .session(session))
                .andExpect(status().isNoContent());
    }

    @Test
    void 존재하지_않는_댓글_삭제시_404_응답을_반환한다() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        doThrow(new EntityNotFoundException("댓글을 찾을 수 없습니다. 댓글 ID : " + 99L))
                .when(commentService).delete(eq(1L), eq(99L), any(Member.class));

        mockMvc.perform(delete("/boards/{boardId}/comments/{commentId}", 1L, 99L)
                        .session(session))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("댓글을 찾을 수 없습니다. 댓글 ID : " + 99L));
    }

    @Test
    void 권한_없는_사용자가_댓글_삭제시_403_응답을_반환한다() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        doThrow(new NoPermissionException("해당 댓글을 수정할 권한이 없습니다."))
                .when(commentService).delete(eq(1L), eq(1L), any(Member.class));

        mockMvc.perform(delete("/boards/{boardId}/comments/{commentId}", 1L, 1L)
                        .session(session))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("해당 댓글을 수정할 권한이 없습니다."));
    }
}
