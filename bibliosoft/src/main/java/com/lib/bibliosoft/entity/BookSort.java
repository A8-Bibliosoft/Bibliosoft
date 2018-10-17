package com.lib.bibliosoft.entity;


import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 *@Title: BookSort.java
 *@Author: huhao
 */
@Entity
@Table(name="booksort")
public class BookSort {
    @Id
    @Column(name = "book_isbn")
    private String bookIsbn;

    private String bookName;

    private String bookAuthor;

    private Integer typeId;


    @OneToMany(mappedBy = "bookSort",fetch = FetchType.EAGER)
    private List<Book> bookList;

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public List<Book> getBookList() {
        return bookList;
    }

    public void setBookList(List<Book> bookList) {
        this.bookList = bookList;
    }

    public Book getOneBook() {
        if(bookList.size()>0){
            return bookList.get(0);
        }else{
            return null;
        }
    }
}
