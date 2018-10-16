package com.lib.bibliosoft.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="borrowrecord")
public class BorrowRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String readerId;

    @Column(name = "book_id",insertable = false,updatable = false)
    private Integer bookId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date borrowtime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date returntime;

    private  Integer lastday;

    private Integer debt;

    @JoinColumn(name = "book_id",referencedColumnName="book_id")
    @ManyToOne(cascade = CascadeType.ALL)
    private Book book;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReaderId() {
        return readerId;
    }

    public void setReaderId(String readerId) {
        this.readerId = readerId;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Date getBorrowtime() {
        return borrowtime;
    }

    public void setBorrowtime(Date borrowtime) {
        this.borrowtime = borrowtime;
    }

    public Date getReturntime() {
        return returntime;
    }

    public void setReturntime(Date returntime) {
        this.returntime = returntime;
    }

    public Integer getLastday() {
        return lastday;
    }

    public void setLastday(Integer lastday) {
        this.lastday = lastday;
    }

    public Integer getDebt() {
        return debt;
    }

    public void setDebt(Integer debt) {
        this.debt = debt;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
