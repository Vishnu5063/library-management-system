package com.library.controller;

import com.library.entity.Transaction;
import com.library.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

        private final TransactionService transactionService;

        public TransactionController(TransactionService transactionService) {
                this.transactionService = transactionService;
        }

        // ADMIN: Issue a book
        @PostMapping("/issue")
        public ResponseEntity<Transaction> issueBook(
                        @RequestParam Long memberId,
                        @RequestParam Long bookId) {

                Transaction transaction = transactionService.issueBook(memberId, bookId);

                return ResponseEntity.ok(transaction);
        }

        // ADMIN: Return a book
        @PutMapping("/return/{id}")
        public ResponseEntity<Transaction> returnBook(
                        @PathVariable Long id) {

                Transaction transaction = transactionService.returnBook(id);

                return ResponseEntity.ok(transaction);
        }

        // ADMIN: View all transactions
        @GetMapping
        public ResponseEntity<List<Transaction>> getAllTransactions() {

                return ResponseEntity.ok(
                                transactionService.getAllTransactions());
        }

        // MEMBER + ADMIN: View their own transaction history
        @GetMapping("/my")
        public ResponseEntity<List<Transaction>> getMyTransactions(
                        Authentication authentication) {

                String email = authentication.getName();

                return ResponseEntity.ok(
                                transactionService
                                                .getMemberTransactionsByEmail(email));
        }

        // MEMBER + ADMIN: View currently issued books
        @GetMapping("/my/current")
        public ResponseEntity<List<Transaction>> getMyCurrentLoans(
                        Authentication authentication) {

                String email = authentication.getName();

                return ResponseEntity.ok(
                                transactionService
                                                .getCurrentLoansByEmail(email));
        }

        // ADMIN: View transaction by ID
        @GetMapping("/{id}")
        public ResponseEntity<Transaction> getTransactionById(
                        @PathVariable Long id) {

                return ResponseEntity.ok(
                                transactionService.getTransactionById(id));
        }

        // ADMIN: View transactions of a particular member
        @GetMapping("/member/{memberId}")
        public ResponseEntity<List<Transaction>> getMemberTransactions(
                        @PathVariable Long memberId) {

                return ResponseEntity.ok(
                                transactionService
                                                .getMemberTransactions(memberId));
        }

        // ADMIN: View transactions of a particular book
        @GetMapping("/book/{bookId}")
        public ResponseEntity<List<Transaction>> getBookTransactions(
                        @PathVariable Long bookId) {

                return ResponseEntity.ok(
                                transactionService
                                                .getBookTransactions(bookId));
        }
}