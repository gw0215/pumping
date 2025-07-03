package com.pumping.domain.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pumping.AbstractControllerTest;
import com.pumping.domain.board.dto.BoardRequest;
import com.pumping.domain.board.dto.BoardResponse;
import com.pumping.domain.board.exception.NoPermissionException;
import com.pumping.domain.board.fixture.BoardFixture;
import com.pumping.domain.member.fixture.MemberFixture;
import com.pumping.domain.member.model.Member;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BoardControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void 게시글을_파일과_함께_저장하면_201을_반환한다() throws Exception {
        BoardRequest request = BoardFixture.createBoardRequest("제목", "내용");
        String json = objectMapper.writeValueAsString(request);

        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "test".getBytes());
        MockPart boardPart = new MockPart("board", json.getBytes(StandardCharsets.UTF_8));
        boardPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Member member = MemberFixture.createMember();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(multipart("/boards")
                        .file(file)
                        .part(boardPart)
                        .session(session)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());

        verify(boardService).save(
                any(Member.class),
                eq(request.getTitle()),
                eq(request.getContent()),
                any(MultipartFile.class)
        );
    }

    @Test
    void 게시글을_파일없이_저장하면_201을_반환한다() throws Exception {
        BoardRequest request = BoardFixture.createBoardRequest("제목", "내용");
        String json = objectMapper.writeValueAsString(request);

        MockPart boardPart = new MockPart("board", json.getBytes(StandardCharsets.UTF_8));
        boardPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Member member = MemberFixture.createMember();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(multipart("/boards")
                        .part(boardPart)
                        .session(session)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());

        verify(boardService).save(
                any(Member.class),
                eq(request.getTitle()),
                eq(request.getContent()),
                isNull()
        );
    }

    @Test
    void 게시글_저장_요청에서_title이_비어있으면_400을_반환한다() throws Exception {
        BoardRequest boardRequest = BoardFixture.createBoardRequest("", "내용");
        String json = objectMapper.writeValueAsString(boardRequest);

        MockMultipartFile file = new MockMultipartFile("file", "", "image/png", new byte[0]);
        MockPart boardPart = new MockPart("board", json.getBytes(StandardCharsets.UTF_8));
        boardPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Member member = MemberFixture.createMember();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(multipart("/boards")
                        .file(file)
                        .part(boardPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .session(session))
                .andExpect(status().isBadRequest());

        verify(boardService, never()).save(any(), any(), any(), any());
    }

    @Test
    void 게시글_저장_요청에서_content가_비어있으면_400을_반환한다() throws Exception {
        BoardRequest boardRequest = BoardFixture.createBoardRequest("제목", "");
        String json = objectMapper.writeValueAsString(boardRequest);

        MockMultipartFile file = new MockMultipartFile("file", "", "image/png", new byte[0]);
        MockPart boardPart = new MockPart("board", json.getBytes(StandardCharsets.UTF_8));
        boardPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Member member = MemberFixture.createMember();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(multipart("/boards")
                        .file(file)
                        .part(boardPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .session(session))
                .andExpect(status().isBadRequest());

        verify(boardService, never()).save(any(), any(), any(), any());
    }

    @Test
    void 게시글_목록을_조회하면_200과_게시글_목록을_반환한다() throws Exception {

        Member member = MemberFixture.createMember();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        List<BoardResponse> boardList = List.of(
                BoardFixture.createBoardResponse(1L),
                BoardFixture.createBoardResponse(2L)
        );
        Page<BoardResponse> boardPage = new PageImpl<>(boardList);

        Mockito.when(boardService.findAll(any(Member.class), any(Pageable.class)))
                .thenReturn(boardPage);

        mockMvc.perform(get("/boards")
                        .param("page", "0")
                        .param("size", "10")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(boardList.size()));


    }

    @Test
    void 게시글을_정상적으로_수정하면_204를_반환한다() throws Exception {
        Member member = MemberFixture.createMember();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        BoardRequest request = BoardFixture.createBoardRequest("수정된 제목", "수정된 내용");
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(patch("/boards/{boardId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .session(session))
                .andExpect(status().isNoContent());

        verify(boardService).update(
                eq(1L),
                eq(request.getTitle()),
                eq(request.getContent()),
                eq(member)
        );
    }

    @Test
    void 게시글_수정시_존재하지_않는_id면_404를_반환한다() throws Exception {
        Member member = MemberFixture.createMember();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        BoardRequest request = BoardFixture.createBoardRequest("수정된 제목", "수정된 내용");
        String json = objectMapper.writeValueAsString(request);

        doThrow(new EntityNotFoundException("게시글을 찾을 수 없습니다. 게시글 ID : " + 999L)).when(boardService)
                .update(eq(999L), any(), any(), eq(member));

        mockMvc.perform(patch("/boards/{boardId}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .session(session))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("게시글을 찾을 수 없습니다. 게시글 ID : 999"));
    }

    @Test
    void 게시글_수정_요청에서_title이_비어있으면_400을_반환한다() throws Exception {

        Member member = MemberFixture.createMember();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        BoardRequest invalidRequest = new BoardRequest("", "내용입니다");
        String json = objectMapper.writeValueAsString(invalidRequest);

        mockMvc.perform(patch("/boards/{boardId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .session(session))
                .andExpect(status().isBadRequest());

        verify(boardService, never()).update(any(), any(), any(), any());
    }

    @Test
    void 게시글_수정_요청에서_content가_비어있으면_400을_반환한다() throws Exception {
        Member member = MemberFixture.createMember();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        BoardRequest invalidRequest = new BoardRequest("제목입니다", "");
        String json = objectMapper.writeValueAsString(invalidRequest);

        mockMvc.perform(patch("/boards/{boardId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .session(session))
                .andExpect(status().isBadRequest());

        verify(boardService, never()).update(any(), any(), any(), any());
    }

    @Test
    void 게시글을_작성자가_삭제하면_204를_반환한다() throws Exception {
        Member member = MemberFixture.createMember();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(delete("/boards/{boardId}", 1L)
                        .session(session))
                .andExpect(status().isNoContent());

        verify(boardService).deleteById(1L, member);
    }

    @Test
    void 게시글_삭제시_작성자가_아니면_403을_반환한다() throws Exception {
        Member member = MemberFixture.createMember();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        doThrow(new NoPermissionException("해당 게시글을 삭제할 권한이 없습니다.")).when(boardService).deleteById(eq(1L), eq(member));

        mockMvc.perform(delete("/boards/{boardId}", 1L)
                        .session(session))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("해당 게시글을 삭제할 권한이 없습니다."));
    }

    @Test
    void 게시글_세션없으면_400을_반환한다() throws Exception {
        BoardRequest request = BoardFixture.createBoardRequest("제목", "내용");
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(patch("/boards/{boardId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}