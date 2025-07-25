package com.pumping.domain.inbody.controller;

import com.pumping.domain.inbody.dto.InBodyRequest;
import com.pumping.domain.inbody.dto.InBodyResponse;
import com.pumping.domain.inbody.service.InBodyService;
import com.pumping.domain.member.model.Member;
import com.pumping.global.common.annotation.AuthMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class InBodyController {

    private final InBodyService inBodyService;

    @ResponseBody
    @PostMapping(value = "/inbody")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@AuthMember Member member, @Valid @RequestBody InBodyRequest inBodyRequest) {
        inBodyService.save(member, inBodyRequest.getWeight(), inBodyRequest.getSmm(), inBodyRequest.getBfm(), inBodyRequest.getDate());
    }

    @GetMapping("/inbody/recent")
    public ResponseEntity<InBodyResponse> findRecentInBody(@AuthMember Member member) {
        InBodyResponse recentInBody = inBodyService.findRecentInBody(member);
        if (recentInBody == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(recentInBody);
    }

    @GetMapping("/inbody")
    public ResponseEntity<List<InBodyResponse>> findByDate(
            @AuthMember Member member,
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<InBodyResponse> inBodyResponses = inBodyService.findByDate(member, from, to);

        return ResponseEntity.ok(inBodyResponses);
    }


}
