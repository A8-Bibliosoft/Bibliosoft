package com.lib.bibliosoft.entity;

import javax.persistence.*;

@Entity
@Table(name="appointmentrecord")
public class AppointmentRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer readerId;

    private Integer bookId;

    private  float lasttime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getReaderId() {
        return readerId;
    }

    public void setReaderId(Integer readerId) {
        this.readerId = readerId;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public float getLasttime() {
        return lasttime;
    }

    public void setLasttime(float lasttime) {
        this.lasttime = lasttime;
    }
}
