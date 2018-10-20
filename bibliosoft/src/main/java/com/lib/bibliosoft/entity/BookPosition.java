package com.lib.bibliosoft.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 毛文杰
 * @project bibliosoft
 * @description
 * @date Created in 5:51 PM. 10/20/2018
 * @modify By 毛文杰
 */
@Entity
@Table(name = "bookposition")
public class BookPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String place;

    @OneToMany(mappedBy = "bookPosition",cascade=CascadeType.ALL,fetch=FetchType.LAZY)
    private List<Book> books = new ArrayList<>();

    public BookPosition() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
