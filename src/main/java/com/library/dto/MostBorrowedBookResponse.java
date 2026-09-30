package com.library.dto;

public class MostBorrowedBookResponse {

    private Long bookId;
    private String title;
    private long borrowCount;

    public MostBorrowedBookResponse(
            Long bookId,
            String title,
            long borrowCount) {

        this.bookId = bookId;
        this.title = title;
        this.borrowCount = borrowCount;
    }

    public Long getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public long getBorrowCount() {
        return borrowCount;
    }
}