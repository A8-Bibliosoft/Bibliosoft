package com.lib.bibliosoft.entity;

import javax.persistence.*;

@Entity
@Table(name="appointmentrecord")
public class AppointmentRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String readerId;

    @Column(name = "book_id",insertable = false,updatable = false)
    private Integer bookId;

    private  Integer lasttime;

    @OneToOne
    @JoinColumn(name = "book_id",referencedColumnName = "book_id")
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

    public Integer getLasttime() {
        return lasttime;
    }

    public void setLasttime(Integer lasttime) {
        this.lasttime = lasttime;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
