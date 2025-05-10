package com.pumping.domain.member.repository;

import com.pumping.domain.member.fixture.MemberFixture;
import com.pumping.domain.member.model.Member;
import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.sql.DataSource;
import java.util.Locale;
import java.util.Optional;


@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryTest {

    Faker faker = new Faker(new Locale("ko", "KR"));

    @Autowired
    DataSource dataSource;

    @Autowired
    MemberRepository memberRepository;

//    @Test
//    @Rollback(value = false)
//    void insertDummy() throws SQLException {
//        Connection connection = dataSource.getConnection();
//        connection.setAutoCommit(false);
//
//        PreparedStatement ps = connection.prepareStatement("INSERT INTO member (nickname, email, password, profile_image, deleted) VALUES (?, ?, ?, ?, ?)");
//
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start("memberInsert");
//
//        for (int i = 0; i < 1000; i++) {
//            String nickname = faker.name().fullName();
//            String email = faker.internet().emailAddress();
//            String password = faker.internet().password();
//
//            int size = 100 * 1024 + faker.number().numberBetween(0, 200 * 1024);
//            byte[] profileImage = new byte[size];
//            new Random().nextBytes(profileImage);
//
//            ps.setString(1, nickname);
//            ps.setString(2, email);
//            ps.setString(3, password);
//            ps.setBytes(4, profileImage);
//            ps.setBoolean(5, false);
//
//            ps.addBatch();
//
//            if (i % 100 == 0) {
//                ps.executeBatch();
//                ps.clearBatch();
//            }
//        }
//
//        ps.executeBatch();
//        connection.commit();
//
//        ps.close();
//        connection.close();
//
//        stopWatch.stop();
//        System.out.println(stopWatch.prettyPrint());
//
//    }
//
//    @Test
//    void selectTest() {
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start("memberSelect");
//        List<Member> all = memberRepository.findAll();
//

    /// /        List<MemberRepository.MemberProjection> summaryById = memberRepository.findAllSummary();
//        stopWatch.stop();
//        System.out.println(stopWatch.prettyPrint());
//    }
    @Test
    void 삭제되지_않은_사용자_이메일로_확인() {
        Member member = MemberFixture.createMember();
        memberRepository.save(member);
        boolean b = memberRepository.existsByEmailAndDeletedFalse(member.getEmail());

        Assertions.assertThat(b).isTrue();
    }

    @Test
    void 삭제되지_않은_사용자_이메일로_조회() {

        Member member = MemberFixture.createMember();
        memberRepository.save(member);

        Optional<Member> optionalMember = memberRepository.findByEmailAndDeletedFalse(member.getEmail());

        Assertions.assertThat(optionalMember).isPresent();
        Assertions.assertThat(optionalMember.get()).isEqualTo(member);
    }


}
