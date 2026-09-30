package com.library.service;

import com.library.entity.Book;
import com.library.dto.MostBorrowedBookResponse;
import java.util.Map;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import com.library.entity.Member;
import com.library.entity.Transaction;
import com.library.entity.TransactionStatus;
import com.library.repository.BookRepository;
import com.library.repository.MemberRepository;
import com.library.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final TransactionRepository transactionRepository;

    public ReportService(
            BookRepository bookRepository,
            MemberRepository memberRepository,
            TransactionRepository transactionRepository) {

        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.transactionRepository = transactionRepository;
    }

    public long getTotalBooks() {
        return bookRepository.count();
    }

    public long getTotalMembers() {
        return memberRepository.count();
    }

    public long getCurrentlyIssuedBooks() {
        return transactionRepository
                .findByStatus(TransactionStatus.ISSUED)
                .size();
    }

    public List<Transaction> getOverdueBooks() {

        LocalDate today = LocalDate.now();

        return transactionRepository
                .findByStatus(TransactionStatus.ISSUED)
                .stream()
                .filter(transaction -> transaction.getDueDate().isBefore(today))
                .toList();
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public List<MostBorrowedBookResponse> getMostBorrowedBooks(
            LocalDate startDate,
            LocalDate endDate) {

        if (startDate == null || endDate == null) {
            throw new RuntimeException(
                    "Start date and end date are required");
        }

        if (startDate.isAfter(endDate)) {
            throw new RuntimeException(
                    "Start date cannot be after end date");
        }

        List<Transaction> transactions = transactionRepository
                .findByIssueDateBetween(
                        startDate,
                        endDate);

        Map<Long, MostBorrowedBookResponse> result = new LinkedHashMap<>();

        for (Transaction transaction : transactions) {

            Long bookId = transaction.getBook().getId();

            String title = transaction.getBook().getTitle();

            if (result.containsKey(bookId)) {

                MostBorrowedBookResponse existing = result.get(bookId);

                result.put(
                        bookId,
                        new MostBorrowedBookResponse(
                                bookId,
                                title,
                                existing.getBorrowCount() + 1));

            } else {

                result.put(
                        bookId,
                        new MostBorrowedBookResponse(
                                bookId,
                                title,
                                1));
            }
        }

        List<MostBorrowedBookResponse> response = new ArrayList<>(result.values());

        response.sort(
                Comparator.comparing(
                        MostBorrowedBookResponse::getBorrowCount)
                        .reversed());

        return response;
    }
}