package com.library.controller;

import com.library.dto.MostBorrowedBookResponse;
import com.library.entity.Transaction;
import com.library.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // ADMIN: Dashboard summary
    @GetMapping("/dashboard")
    public ResponseEntity<String> getDashboard() {

        long totalBooks = reportService.getTotalBooks();
        long totalMembers = reportService.getTotalMembers();
        long currentlyIssued = reportService.getCurrentlyIssuedBooks();

        String response = "Total Books: " + totalBooks
                + "\nTotal Members: " + totalMembers
                + "\nCurrently Issued Books: "
                + currentlyIssued;

        return ResponseEntity.ok(response);
    }

    // ADMIN: View overdue books
    @GetMapping("/overdue")
    public ResponseEntity<List<Transaction>> getOverdueBooks() {

        return ResponseEntity.ok(
                reportService.getOverdueBooks());
    }

    // ADMIN: View all transactions
    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getAllTransactions() {

        return ResponseEntity.ok(
                reportService.getAllTransactions());
    }

    // ADMIN: Most borrowed books within a date range
    @GetMapping("/most-borrowed")
    public ResponseEntity<List<MostBorrowedBookResponse>> getMostBorrowedBooks(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        return ResponseEntity.ok(
                reportService.getMostBorrowedBooks(
                        startDate,
                        endDate));
    }
}