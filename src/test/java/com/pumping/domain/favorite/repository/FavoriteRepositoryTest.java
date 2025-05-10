package com.pumping.domain.favorite.repository;

import com.pumping.domain.board.fixture.BoardFixture;
import com.pumping.domain.board.model.Board;
import com.pumping.domain.board.repository.BoardRepository;
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

import java.util.Optional;

@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FavoriteRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    FavoriteRepository favoriteRepository;

    @Test
    void 좋아요_삭제_테스트() {
        Member member = memberRepository.save(MemberFixture.createMember());
        Board board = boardRepository.save(BoardFixture.createBoard(member));

        Favorite favorite = favoriteRepository.save(FavoriteFixture.createFavorite(member, board));

        favoriteRepository.deleteByMemberAndBoard(member, board);

        Optional<Favorite> deletedFavorite = favoriteRepository.findById(favorite.getId());
        Assertions.assertThat(deletedFavorite).isNotPresent();
    }

}