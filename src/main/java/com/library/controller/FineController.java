package com.library.controller;

import com.library.entity.Fine;
import com.library.entity.Member;
import com.library.repository.MemberRepository;
import com.library.service.FineService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fines")
public class FineController {

    private final FineService fineService;
    private final MemberRepository memberRepository;

    public FineController(
            FineService fineService,
            MemberRepository memberRepository) {

        this.fineService = fineService;
        this.memberRepository = memberRepository;
    }

    // ADMIN: Calculate fine
    @PostMapping("/calculate/{transactionId}")
    public ResponseEntity<Fine> calculateFine(
            @PathVariable Long transactionId) {

        return ResponseEntity.ok(
                fineService.calculateFine(transactionId));
    }

    // MEMBER: View their own fines
    @GetMapping("/my")
    public ResponseEntity<List<Fine>> getMyFines(
            Authentication authentication) {

        String email = authentication.getName();

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Member not found"));

        return ResponseEntity.ok(
                fineService.getMemberFines(member.getId()));
    }

    // ADMIN: View fine for a particular transaction
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<Fine> getFineByTransactionId(
            @PathVariable Long transactionId) {

        return ResponseEntity.ok(
                fineService.getFineByTransactionId(transactionId));
    }

    // ADMIN: View all fines
    @GetMapping
    public ResponseEntity<List<Fine>> getAllFines() {

        return ResponseEntity.ok(
                fineService.getAllFines());
    }

    // ADMIN: Mark fine as paid
    @PutMapping("/pay/{fineId}")
    public ResponseEntity<String> markFineAsPaid(
            @PathVariable Long fineId) {

        fineService.markFineAsPaid(fineId);

        return ResponseEntity.ok(
                "Fine marked as paid successfully");
    }
}