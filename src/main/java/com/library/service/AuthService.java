package com.library.service;

import com.library.entity.Administrator;
import com.library.entity.Member;
import com.library.repository.AdministratorRepository;
import com.library.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AdministratorRepository administratorRepository;
    private final MemberRepository memberRepository;
    private final OtpService otpService;
    private final EmailService emailService;
    private final JwtService jwtService;

    public AuthService(
            AdministratorRepository administratorRepository,
            MemberRepository memberRepository,
            OtpService otpService,
            EmailService emailService,
            JwtService jwtService) {

        this.administratorRepository = administratorRepository;
        this.memberRepository = memberRepository;
        this.otpService = otpService;
        this.emailService = emailService;
        this.jwtService = jwtService;
    }

    public String sendOtp(String email) {

        // Check administrator
        Administrator administrator = administratorRepository.findByEmail(email).orElse(null);

        if (administrator != null && administrator.isActive()) {

            String otp = otpService.generateOtp(email);
            emailService.sendOtpEmail(email, otp);

            return "OTP sent successfully";
        }

        // Check member
        Member member = memberRepository.findByEmail(email).orElse(null);

        if (member != null && member.isActive()) {

            String otp = otpService.generateOtp(email);
            emailService.sendOtpEmail(email, otp);

            return "OTP sent successfully";
        }

        return "User not found or inactive";
    }

    public String verifyOtp(String email, String otp) {

        boolean valid = otpService.verifyOtp(email, otp);

        if (!valid) {
            return "Invalid or expired OTP";
        }

        Administrator administrator = administratorRepository.findByEmail(email).orElse(null);

        if (administrator != null && administrator.isActive()) {

            String token = jwtService.generateToken(email, "ADMIN");

            return "OTP verified. Role: ADMIN\nJWT: " + token;
        }

        Member member = memberRepository.findByEmail(email).orElse(null);

        if (member != null && member.isActive()) {

            String token = jwtService.generateToken(email, "MEMBER");

            return "OTP verified. Role: MEMBER\nJWT: " + token;
        }

        return "User not found or inactive";
    }
}