package com.pumping.domain.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pumping.domain.board.dto.BoardRequest;
import com.pumping.domain.board.fixture.BoardFixture;
import com.pumping.domain.board.model.Board;
import com.pumping.domain.board.repository.BoardRepository;
import com.pumping.domain.media.fixture.MediaFixture;
import com.pumping.domain.media.model.Media;
import com.pumping.domain.member.fixture.MemberFixture;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.member.repository.MemberRepository;
import com.pumping.global.common.util.JwtUtil;
import com.pumping.global.config.FirebaseConfig;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BoardControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MemberRepository memberRepository;

    @MockitoBean
    JavaMailSender javaMailSender;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    JwtUtil jwtUtil;

    @MockitoBean
    FirebaseConfig firebaseConfig;

    Member member;

    String token;

    @BeforeEach
    void setUp() {
        member = MemberFixture.createMember();
        memberRepository.save(member);
        token = jwtUtil.generateToken(member);
    }

    @AfterEach
    void tearDown() {
        boardRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @Transactional
    void 게시글저장_유효한_요청과_세션이_있으면_201응답반환() throws Exception {

        BoardRequest boardRequest = BoardFixture.createBoardRequest();
        String json = objectMapper.writeValueAsString(boardRequest);

        MockMultipartFile boardPart = new MockMultipartFile(
                "board",
                "board.json",
                MediaType.APPLICATION_JSON_VALUE,
                json.getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile filePart = new MockMultipartFile(
                "file",
                "test-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "dummy-image-content".getBytes()
        );

        MvcResult result = mockMvc.perform(multipart("/boards")
                        .file(boardPart)
                        .file(filePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", startsWith("/boards/")))
                .andDo(print())
                .andReturn();

        String location = result.getResponse().getHeader("Location");
        assertThat(location).isNotNull();
        Long boardId = Long.valueOf(location.substring(location.lastIndexOf("/") + 1));

        assertThat(boardRepository.findById(boardId)).isPresent();
    }

    @Test
    @Transactional
    void 게시글전체조회_세션에_회원정보가_있으면_200응답반환() throws Exception {

        int size = 3;

        List<Board> boards = BoardFixture.createBoards(member, size);

        boards.forEach(board -> {
                    Media media = MediaFixture.createMedia(board);
                    board.addMedia(media);
                });

        boardRepository.saveAll(boards);

        mockMvc.perform(get("/boards")
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(size)))
                .andExpect(jsonPath("$.content[0].boardId").value(boards.get(size - 1).getId()))
                .andExpect(jsonPath("$.content[0].title").value(boards.get(size - 1).getTitle()))
                .andExpect(jsonPath("$.content[0].content").value(boards.get(size - 1).getContent()))
                .andExpect(jsonPath("$.content[0].mediaResponses").isArray())
                .andExpect(jsonPath("$.content[0].mediaResponses", hasSize(1)))
                .andExpect(jsonPath("$.content[0].liked").value(false))
                .andDo(print());
    }

    @Test
    @Transactional
    void 게시글수정_작성자세션과_유효한_내용이_있으면_204응답반환() throws Exception {

        Board board = BoardFixture.createBoard(member);
        boardRepository.save(board);

        BoardRequest boardRequest = BoardFixture.createBoardRequest();
        String json = objectMapper.writeValueAsString(boardRequest);

        mockMvc.perform(patch("/boards/{boardId}", board.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent())
                .andDo(print());

        Board updated = boardRepository.findById(board.getId()).orElseThrow();

        assertThat(updated.getTitle()).isEqualTo(boardRequest.getTitle());
        assertThat(updated.getContent()).isEqualTo(boardRequest.getContent());

    }

    @Test
    void 게시글수정_title이_비어있으면_400응답반환() throws Exception {
        Board board = boardRepository.save(BoardFixture.createBoard(member));

        BoardRequest invalid = new BoardRequest("", "내용");
        String json = objectMapper.writeValueAsString(invalid);

        mockMvc.perform(patch("/boards/{boardId}", board.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("제목은 비어 있을 수 없습니다."));
    }

    @Test
    @Transactional
    void 게시글수정_작성자가_아니면_403응답반환() throws Exception {
        Board board = boardRepository.save(BoardFixture.createBoard(member));

        Member other = MemberFixture.createMember("test", "test", "test");

        memberRepository.save(other);
        String otherToken = jwtUtil.generateToken(other);
        BoardRequest request = BoardFixture.createBoardRequest();
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(patch("/boards/{boardId}", board.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("해당 게시글을 수정할 권한이 없습니다."));
    }

    @Test
    void 게시글수정_존재하지않는_ID면_404응답반환() throws Exception {
        BoardRequest request = BoardFixture.createBoardRequest();
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(patch("/boards/{boardId}", 99999L)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("게시글을 찾을 수 없습니다. 게시글 ID : 99999"));
    }



    @Test
    @Transactional
    void 게시글삭제_작성자가_아니면_403응답반환() throws Exception {
        Board board = boardRepository.save(BoardFixture.createBoard(member));

        Member other = MemberFixture.createMember("test2", "test2", "test2");
        memberRepository.save(other);

        String otherToken = jwtUtil.generateToken(other);

        Long id = jwtUtil.validateAndExtractMemberId(otherToken);

        System.out.println("asdf" + otherToken);
        System.out.println("idididididi" + id);
        mockMvc.perform(delete("/boards/{boardId}", board.getId())
                        .header("Authorization", "Bearer " + otherToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("해당 게시글을 삭제할 권한이 없습니다."));


    }


}