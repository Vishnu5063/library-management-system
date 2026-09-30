package com.library.controller;

import com.library.entity.Member;
import com.library.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<Member> addMember(
            @Valid @RequestBody Member member) {

        Member savedMember = memberService.addMember(member);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedMember);
    }

    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers() {

        return ResponseEntity.ok(
                memberService.getAllMembers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                memberService.getMemberById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(
            @PathVariable Long id,
            @Valid @RequestBody Member member) {

        return ResponseEntity.ok(
                memberService.updateMember(id, member));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deactivateMember(
            @PathVariable Long id) {

        memberService.deactivateMember(id);

        return ResponseEntity.ok(
                "Member deactivated successfully");
    }
}