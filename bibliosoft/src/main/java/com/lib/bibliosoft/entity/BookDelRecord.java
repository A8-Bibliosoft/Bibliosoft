package com.lib.bibliosoft.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="bookdelrecord")
public class BookDelRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

//    private Integer bookId;

    @ManyToOne()
    @JoinColumn(name = "lib_id")
    private Librarian libId;

    private String bookName;

    private Date time;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public Librarian getLibId() {
        return libId;
    }

    public void setLibId(Librarian libId) {
        this.libId = libId;
    }
}
