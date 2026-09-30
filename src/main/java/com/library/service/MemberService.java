package com.library.service;

import com.library.entity.Member;
import com.library.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member addMember(Member member) {

        if (memberRepository.findByEmail(member.getEmail()).isPresent()) {
            throw new RuntimeException(
                    "A member with this email already exists");
        }

        return memberRepository.save(member);
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));
    }

    public Member updateMember(Long id, Member updatedMember) {

        Member existingMember = getMemberById(id);

        if (!existingMember.getEmail().equals(updatedMember.getEmail())
                && memberRepository.findByEmail(updatedMember.getEmail()).isPresent()) {

            throw new RuntimeException(
                    "A member with this email already exists");
        }

        existingMember.setName(updatedMember.getName());
        existingMember.setEmail(updatedMember.getEmail());
        existingMember.setMobile(updatedMember.getMobile());

        return memberRepository.save(existingMember);
    }

    public void deactivateMember(Long id) {

        Member member = getMemberById(id);

        member.setActive(false);

        memberRepository.save(member);
    }
}