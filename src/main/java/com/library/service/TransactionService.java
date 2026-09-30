package com.library.service;

import com.library.entity.Book;
import com.library.entity.Member;
import com.library.entity.Transaction;
import com.library.entity.TransactionStatus;
import com.library.repository.BookRepository;
import com.library.repository.MemberRepository;
import com.library.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {

        private static final int MAX_BOOKS_PER_MEMBER = 3;
        private static final int BORROWING_PERIOD_DAYS = 14;

        private final TransactionRepository transactionRepository;
        private final BookRepository bookRepository;
        private final MemberRepository memberRepository;

        public TransactionService(
                        TransactionRepository transactionRepository,
                        BookRepository bookRepository,
                        MemberRepository memberRepository) {

                this.transactionRepository = transactionRepository;
                this.bookRepository = bookRepository;
                this.memberRepository = memberRepository;
        }

        @Transactional
        public Transaction issueBook(Long memberId, Long bookId) {

                Member member = memberRepository.findById(memberId)
                                .orElseThrow(() -> new RuntimeException("Member not found"));

                if (!member.isActive()) {
                        throw new RuntimeException(
                                        "Member is inactive");
                }

                Book book = bookRepository.findById(bookId)
                                .orElseThrow(() -> new RuntimeException("Book not found"));

                if (!book.isActive()) {
                        throw new RuntimeException(
                                        "Book is inactive");
                }

                if (book.getAvailableCopies() <= 0) {
                        throw new RuntimeException(
                                        "No copies of this book are available");
                }

                long activeLoans = transactionRepository
                                .findByMemberId(memberId)
                                .stream()
                                .filter(transaction -> transaction.getStatus() == TransactionStatus.ISSUED)
                                .count();

                if (activeLoans >= MAX_BOOKS_PER_MEMBER) {
                        throw new RuntimeException(
                                        "Member has reached the maximum limit of "
                                                        + MAX_BOOKS_PER_MEMBER
                                                        + " books");
                }

                Transaction transaction = new Transaction();

                LocalDate issueDate = LocalDate.now();

                transaction.setMember(member);
                transaction.setBook(book);
                transaction.setIssueDate(issueDate);
                transaction.setDueDate(
                                issueDate.plusDays(BORROWING_PERIOD_DAYS));
                transaction.setStatus(TransactionStatus.ISSUED);

                book.setAvailableCopies(
                                book.getAvailableCopies() - 1);

                bookRepository.save(book);

                return transactionRepository.save(transaction);
        }

        @Transactional
        public Transaction returnBook(Long transactionId) {

                Transaction transaction = transactionRepository.findById(transactionId)
                                .orElseThrow(() -> new RuntimeException(
                                                "Transaction not found"));

                if (transaction.getStatus() == TransactionStatus.RETURNED) {

                        throw new RuntimeException(
                                        "Book has already been returned");
                }

                Book book = transaction.getBook();

                book.setAvailableCopies(
                                book.getAvailableCopies() + 1);

                transaction.setReturnDate(LocalDate.now());
                transaction.setStatus(TransactionStatus.RETURNED);

                bookRepository.save(book);

                return transactionRepository.save(transaction);
        }

        public List<Transaction> getAllTransactions() {
                return transactionRepository.findAll();
        }

        public List<Transaction> getMemberTransactions(Long memberId) {
                return transactionRepository.findByMemberId(memberId);
        }

        public List<Transaction> getMemberTransactionsByEmail(String email) {

                Member member = memberRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("Member not found"));

                return transactionRepository.findByMemberId(member.getId());
        }

        public List<Transaction> getCurrentLoansByEmail(String email) {

                Member member = memberRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("Member not found"));

                return transactionRepository
                                .findByMemberIdAndStatus(
                                                member.getId(),
                                                TransactionStatus.ISSUED);
        }

        public List<Transaction> getBookTransactions(Long bookId) {
                return transactionRepository.findByBookId(bookId);
        }

        public Transaction getTransactionById(Long id) {
                return transactionRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException(
                                                "Transaction not found"));
        }
}