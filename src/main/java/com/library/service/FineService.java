package com.library.service;

import com.library.entity.Fine;
import com.library.entity.Transaction;
import com.library.repository.FineRepository;
import com.library.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class FineService {

    private static final BigDecimal FINE_PER_DAY = BigDecimal.valueOf(2);

    private final FineRepository fineRepository;
    private final TransactionRepository transactionRepository;

    public FineService(
            FineRepository fineRepository,
            TransactionRepository transactionRepository) {

        this.fineRepository = fineRepository;
        this.transactionRepository = transactionRepository;
    }

    public Fine calculateFine(Long transactionId) {

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException(
                        "Transaction not found"));

        LocalDate endDate;

        if (transaction.getReturnDate() != null) {
            endDate = transaction.getReturnDate();
        } else {
            endDate = LocalDate.now();
        }

        if (!endDate.isAfter(transaction.getDueDate())) {
            return createOrUpdateFine(
                    transaction,
                    BigDecimal.ZERO);
        }

        long overdueDays = ChronoUnit.DAYS.between(
                transaction.getDueDate(),
                endDate);

        BigDecimal fineAmount = FINE_PER_DAY.multiply(
                BigDecimal.valueOf(overdueDays));

        return createOrUpdateFine(
                transaction,
                fineAmount);
    }

    private Fine createOrUpdateFine(
            Transaction transaction,
            BigDecimal amount) {

        Fine fine = fineRepository
                .findByTransactionId(
                        transaction.getId())
                .orElseGet(Fine::new);

        fine.setTransaction(transaction);
        fine.setAmount(amount);

        return fineRepository.save(fine);
    }

    public Fine getFineByTransactionId(
            Long transactionId) {

        return fineRepository
                .findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException(
                        "Fine not found for this transaction"));
    }
    public List<Fine> getMemberFines(Long memberId) {

    return fineRepository.findByTransactionMemberId(memberId);
}

    public List<Fine> getAllFines() {
        return fineRepository.findAll();
    }

    public void markFineAsPaid(Long fineId) {

        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new RuntimeException(
                        "Fine not found"));

        fine.setPaid(true);

        fineRepository.save(fine);
    }
}