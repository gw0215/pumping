package com.pumping.domain.board.repository;

import com.pumping.domain.board.fixture.BoardFixture;
import com.pumping.domain.board.model.Board;
import com.pumping.domain.favorite.fixture.FavoriteFixture;
import com.pumping.domain.favorite.model.Favorite;
import com.pumping.domain.member.fixture.MemberFixture;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.member.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BoardRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BoardRepository boardRepository;

    @Test
    void 게시글_즐겨찾기_포함_조회_테스트() {
        Member member = memberRepository.save(MemberFixture.createMember());

        List<Board> boards = BoardFixture.createBoards(member, 3);
        Favorite favorite = FavoriteFixture.createFavorite(member, boards.get(0));
        boards.get(0).addFavorite(favorite);
        boardRepository.saveAll(boards);

        Pageable pageable = PageRequest.of(0, 20);

        Page<Board> result = boardRepository.findBoards(pageable);

        Assertions.assertThat(result.getContent()).hasSize(3);
        Assertions.assertThat(result.getContent().get(0).getFavoriteList()).isNotEmpty();

        Favorite fav = result.getContent().get(0).getFavoriteList().get(0);
        Assertions.assertThat(fav).isNotNull();
        Assertions.assertThat(fav.getMember().getId()).isEqualTo(member.getId());
    }

    @Test
    void 삭제된_멤버가_포함된_게시글은_조회되지_않는다() {
        Member deletedMember = MemberFixture.createMember();
        deletedMember.deleteMember();
        memberRepository.save(deletedMember);

        Board board = BoardFixture.createBoard(deletedMember, null);
        boardRepository.save(board);

        Pageable pageable = PageRequest.of(0, 20);

        Page<Board> result = boardRepository.findBoards(pageable);

        Assertions.assertThat(result.getContent()).doesNotContain(board);
    }

    @Test
    void 삭제된_게시글은_조회되지_않는다() {
        Member member = memberRepository.save(MemberFixture.createMember());

        Board board = BoardFixture.createBoard(member, null);
        board.deleteBoard();
        boardRepository.save(board);

        Pageable pageable = PageRequest.of(0, 20);

        Page<Board> result = boardRepository.findBoards(pageable);

        Assertions.assertThat(result.getContent()).doesNotContain(board);
    }
}