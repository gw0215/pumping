package com.pumping.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pumping.domain.board.fixture.BoardFixture;
import com.pumping.domain.board.model.Board;
import com.pumping.domain.board.repository.BoardRepository;
import com.pumping.domain.comment.dto.CommentRequest;
import com.pumping.domain.comment.fixture.CommentFixture;
import com.pumping.domain.comment.model.Comment;
import com.pumping.domain.comment.repository.CommentRepository;
import com.pumping.domain.member.fixture.MemberFixture;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.member.repository.MemberRepository;
import com.pumping.global.auth.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CommentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    MemberRepository memberRepository;

    @MockitoBean
    JavaMailSender javaMailSender;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    CommentRepository commentRepository;

    Member member;

    String token;

    @BeforeEach
    void setUp() {
        member = MemberFixture.createMember();
        memberRepository.save(member);

        token = jwtTokenProvider.createToken(member.getId());
    }

    @Test
    @Transactional
    void 댓글_저장_API_성공() throws Exception {

        Board board = BoardFixture.createBoard(member);
        boardRepository.save(board);

        CommentRequest commentRequest = CommentFixture.createCommentRequest();

        String json = objectMapper.writeValueAsString(commentRequest);

        mockMvc.perform(post("/boards/{boardId}/comments", board.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @Transactional
    void 댓글_전체조회_API_성공() throws Exception {

        Board board = BoardFixture.createBoard(member);
        boardRepository.save(board);

        List<Comment> comments = CommentFixture.createComments(member, board, 5);
        commentRepository.saveAll(comments);


        mockMvc.perform(get("/boards/{boardId}/comments", board.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @Transactional
    void 댓글_수정_API_성공() throws Exception {

        Board board = BoardFixture.createBoard(member);
        boardRepository.save(board);

        Comment comment = CommentFixture.createComment(member, board);
        commentRepository.save(comment);

        CommentRequest commentRequest = CommentFixture.createCommentRequest();
        String json = objectMapper.writeValueAsString(commentRequest);

        mockMvc.perform(patch("/boards/{boardId}/comments/{commentId}",board.getId(), comment.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @Transactional
    void 댓글_삭제_API_성공() throws Exception {

        Board board = BoardFixture.createBoard(member);
        boardRepository.save(board);

        Comment comment = CommentFixture.createComment(member, board);
        commentRepository.save(comment);

        mockMvc.perform(delete("/boards/{boardId}/comments/{commentId}", board.getId(), comment.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(MockMvcResultHandlers.print());

    }

}