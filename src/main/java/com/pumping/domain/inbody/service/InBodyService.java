package com.pumping.domain.inbody.service;

import com.pumping.domain.inbody.dto.InBodyResponse;
import com.pumping.domain.inbody.model.InBody;
import com.pumping.domain.inbody.repository.InBodyRepository;
import com.pumping.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InBodyService {

    private final InBodyRepository inBodyRepository;

    @Transactional
    public void save(Member member, Float weight, Float smm, Float bfm, LocalDate date) {

        Optional<InBody> optionalInBody = inBodyRepository.findByMemberAndDate(member, date);

        if (optionalInBody.isEmpty()) {
            InBody inBody = new InBody(member, weight, smm, bfm, date);
            inBodyRepository.save(inBody);
        } else {
            InBody inBody = optionalInBody.get();
            inBody.updateWeight(weight);
            inBody.updateSmm(smm);
            inBody.updateBfm(bfm);
        }

    }

    @Transactional(readOnly = true)
    public InBodyResponse findRecentInBody(Member member) {
        return inBodyRepository.findTopByMemberOrderByDateDesc(member)
                .map(inBody -> new InBodyResponse(
                        inBody.getWeight(),
                        inBody.getSmm(),
                        inBody.getBfm(),
                        inBody.getDate()
                ))
                .orElse(null);
    }

    @Transactional
    public List<InBodyResponse> findByDate(Member member, LocalDate from, LocalDate to) {
        return inBodyRepository.findByMemberAndDateBetween(member, from, to).stream()
                .map(inBody -> new InBodyResponse(
                        inBody.getWeight(),
                        inBody.getSmm(),
                        inBody.getBfm(),
                        inBody.getDate()
                ))
                .toList();
    }

}
