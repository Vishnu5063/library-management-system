package com.library.repository;

import com.library.entity.Transaction;
import com.library.entity.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    List<Transaction> findByMemberId(Long memberId);

    List<Transaction> findByBookId(Long bookId);

    List<Transaction> findByStatus(TransactionStatus status);

    List<Transaction> findByMemberIdAndStatus(
            Long memberId,
            TransactionStatus status);

    List<Transaction> findByIssueDateBetween(
            LocalDate startDate,
            LocalDate endDate);
}