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
import com.pumping.global.config.FirebaseConfig;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CommentControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MemberRepository memberRepository;

    @MockitoBean
    JavaMailSender javaMailSender;

    @MockitoBean
    FirebaseConfig firebaseConfig;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    CommentRepository commentRepository;

    Member member;

    @BeforeEach
    void setUp() {
        member = MemberFixture.createMember();
        memberRepository.save(member);
    }

    @AfterEach
    void tearDown() {
        commentRepository.deleteAll();
        boardRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @Transactional
    void 댓글_저장_요청이_정상적인_경우_201_응답을_반환한다() throws Exception {

        Board board = BoardFixture.createBoard(member);
        boardRepository.save(board);

        CommentRequest commentRequest = CommentFixture.createCommentRequest();

        String json = objectMapper.writeValueAsString(commentRequest);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        MvcResult result = mockMvc.perform(post("/boards/{boardId}/comments", board.getId())
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", startsWith("/boards/"+board.getId()+"/comments/")))
                .andDo(print())
                .andReturn();

        String location = result.getResponse().getHeader("Location");
        assertThat(location).isNotNull();
        Long commentId = Long.valueOf(location.substring(location.lastIndexOf("/") + 1));

        assertThat(commentRepository.findById(commentId)).isPresent();

    }

    @Test
    void 존재하지_않는_게시판에_댓글을_저장하면_404_에러를_반환한다() throws Exception {
        Long invalidBoardId = 9999L;

        CommentRequest commentRequest = CommentFixture.createCommentRequest();
        String json = objectMapper.writeValueAsString(commentRequest);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(post("/boards/{boardId}/comments", invalidBoardId)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void 댓글_내용이_비어있으면_400_에러와_에러_메시지를_반환한다() throws Exception {
        Board board = BoardFixture.createBoard(member);
        boardRepository.save(board);

        CommentRequest commentRequest = new CommentRequest("");
        String json = objectMapper.writeValueAsString(commentRequest);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(post("/boards/{boardId}/comments", board.getId())
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("댓글 내용을 입력해주세요."))
                .andDo(print());
    }

    @Test
    void 댓글_내용이_50자를_초과하면_400_에러와_에러_메시지를_반환한다() throws Exception {
        Board board = BoardFixture.createBoard(member);
        boardRepository.save(board);

        String overLimitContent = "가".repeat(51);
        CommentRequest commentRequest = new CommentRequest(overLimitContent);
        String json = objectMapper.writeValueAsString(commentRequest);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(post("/boards/{boardId}/comments", board.getId())
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("댓글은 50자 이내여야 합니다."))
                .andDo(print());
    }

    @Test
    @Transactional
    void 댓글_목록_요청이_정상적인_경우_200_응답과_댓글_리스트를_반환한다() throws Exception {

        Board board = BoardFixture.createBoard(member);
        boardRepository.save(board);

        int size = 5;

        List<Comment> comments = CommentFixture.createComments(member, board, size);
        commentRepository.saveAll(comments);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(get("/boards/{boardId}/comments", board.getId())
                        .session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(size)))
                .andExpect(jsonPath("$.content[0].commentId").isNumber())
                .andExpect(jsonPath("$.content[0].memberId").isNumber())
                .andExpect(jsonPath("$.content[0].memberNickname").isString())
                .andExpect(jsonPath("$.content[0].content").isString())
                .andDo(print());
    }

    @Test
    @Transactional
    void 댓글_수정_요청이_정상적인_경우_204_응답을_반환한다() throws Exception {

        Board board = BoardFixture.createBoard(member);
        boardRepository.save(board);

        Comment comment = CommentFixture.createComment(member, board);
        commentRepository.save(comment);

        CommentRequest commentRequest = CommentFixture.createCommentRequest();
        String json = objectMapper.writeValueAsString(commentRequest);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(patch("/boards/{boardId}/comments/{commentId}",board.getId(), comment.getId())
                        .session(session)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent())
                .andDo(print());

        Comment updated = commentRepository.findById(comment.getId()).orElseThrow();
        assertThat(updated.getContent()).isEqualTo(commentRequest.getComment());

    }

    @Test
    @Transactional
    void 댓글_삭제_요청이_정상적인_경우_204_응답을_반환한다() throws Exception {

        Board board = BoardFixture.createBoard(member);
        boardRepository.save(board);

        Comment comment = CommentFixture.createComment(member, board);
        commentRepository.save(comment);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        mockMvc.perform(delete("/boards/{boardId}/comments/{commentId}", board.getId(), comment.getId())
                        .session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());

        Optional<Comment> deleted = commentRepository.findById(comment.getId());
        assertThat(deleted).isEmpty();
    }

    @Test
    void 세션없이_댓글을_저장하려고_하면_에러를_반환한다() throws Exception {
        Board board = BoardFixture.createBoard(member);
        boardRepository.save(board);

        CommentRequest commentRequest = CommentFixture.createCommentRequest();
        String json = objectMapper.writeValueAsString(commentRequest);

        mockMvc.perform(post("/boards/{boardId}/comments", board.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }


}