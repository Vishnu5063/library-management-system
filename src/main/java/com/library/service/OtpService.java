package com.library.service;

import com.library.entity.OtpVerification;
import com.library.repository.OtpVerificationRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class OtpService {

    private final OtpVerificationRepository otpVerificationRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom;

    public OtpService(OtpVerificationRepository otpVerificationRepository) {
        this.otpVerificationRepository = otpVerificationRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.secureRandom = new SecureRandom();
    }

    public String generateOtp(String email) {

        String otp = String.format("%06d", secureRandom.nextInt(1_000_000));

        OtpVerification verification = new OtpVerification();

        verification.setEmail(email);
        verification.setOtpHash(passwordEncoder.encode(otp));
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        verification.setAttempts(0);
        verification.setVerified(false);

        otpVerificationRepository.save(verification);

        return otp;
    }

    public boolean verifyOtp(String email, String otp) {

        OtpVerification verification = otpVerificationRepository
                .findTopByEmailOrderByCreatedAtDesc(email)
                .orElse(null);

        if (verification == null) {
            return false;
        }

        if (verification.isVerified()) {
            return false;
        }

        if (LocalDateTime.now().isAfter(verification.getExpiresAt())) {
            return false;
        }

        if (verification.getAttempts() >= 5) {
            return false;
        }

        verification.setAttempts(verification.getAttempts() + 1);

        boolean valid = passwordEncoder.matches(
                otp,
                verification.getOtpHash());

        if (valid) {
            verification.setVerified(true);
        }

        otpVerificationRepository.save(verification);

        return valid;
    }
}