package com.lib.bibliosoft.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="borrowrecord")
public class BorrowRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String readerId;

    private Integer bookId;

    private Date borrowtime;

    private Date returntime;

    private  Integer lastday;

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
}
