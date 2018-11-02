package com.lib.bibliosoft.entity;


import javax.persistence.*;
import java.util.List;

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
    @Column(name = "type_id",insertable = false,updatable = false)
    private Integer typeId;

    @OneToMany(mappedBy = "bookSort",fetch = FetchType.EAGER)
    private List<Book> bookList;

    @ManyToOne()
    @JoinColumn(name = "type_id")
    private BookType bookType;

    //相同isbn书籍的数目
    @Column(columnDefinition = "int default 1")
    private Integer num;

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

    public BookType getBookType() {
        return bookType;
    }

    public void setBookType(BookType bookType) {
        this.bookType = bookType;
    }

    public BookSort(String bookIsbn, String bookName, String bookAuthor, Integer typeId, List<Book> bookList, BookType bookType, Integer num) {
        this.bookIsbn = bookIsbn;
        this.bookName = bookName;
        this.bookAuthor = bookAuthor;
        this.typeId = typeId;
        this.bookList = bookList;
        this.bookType = bookType;
        this.num = num;
    }

    public BookSort() {
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
