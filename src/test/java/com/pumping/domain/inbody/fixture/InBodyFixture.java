package com.pumping.domain.inbody.fixture;

import com.pumping.domain.inbody.dto.InBodyRequest;
import com.pumping.domain.inbody.model.InBody;
import com.pumping.domain.member.model.Member;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

public abstract class InBodyFixture {

    public static Float WEIGHT = 58.5f;

    public static Float SMM = 26.8f;

    public static Float BFM = 23.7f;

    public static LocalDate DATE = LocalDate.now();

    public static InBodyRequest createInBodyRequest() {
        return new InBodyRequest(WEIGHT, SMM, BFM, DATE);
    }

    public static InBody createInbody(Member member) {
        return new InBody(member, WEIGHT, SMM, BFM, DATE);
    }

    public static InBody createInbody(Member member,LocalDate date) {
        return new InBody(member, WEIGHT, SMM, BFM, date);
    }

    public static List<InBody> createInBodies(Member member, int count, LocalDate startDate) {
        return IntStream.range(0, count)
                .mapToObj(i -> createInbody(member, startDate.plusDays(i)))
                .toList();
    }

}
