package com.lib.bibliosoft.entity;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 *@title: Book.java
 *@author: 毛文杰
 *@description:
 *@date: 3:58 PM. 9/26/2018
 */
@Entity
@Table(name="book")
public class Book  implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "book_id")
    private Integer bookId;

    private String bookName;

    @Column(name = "book_isbn",insertable = false,updatable = false)
    private String bookIsbn;

    private float bookPrice;

    private String bookDesc;

    private String bookImg;

    private String bookAuthor;

    @Max(value=4, message = "书籍状态：0-5")
    @Min(value=0, message = "书籍状态：0-5")
    private Integer bookStatus;

    @ManyToOne
    @JoinColumn(name = "bookposition_id")
    private BookPosition bookPosition;
//    private String bookPosition;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date registerTime;

    private String bookPublisher;
    //借书
    @OneToMany(mappedBy = "book",fetch = FetchType.EAGER)
    private Set<BorrowRecord> borrowRecordSet;
    //查找
    @ManyToOne()
    @JoinColumn(name = "book_isbn",referencedColumnName = "book_isbn")
    private  BookSort bookSort;

    public Book() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public float getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(float bookPrice) {
        this.bookPrice = bookPrice;
    }

    public int getBookStatus() {
        return bookStatus;
    }

    public void setBookStatus(Integer bookStatus) {
        this.bookStatus = bookStatus;
    }

    public BookPosition getBookPosition() {
        return bookPosition;
    }

    public void setBookPosition(BookPosition bookPosition) {
        this.bookPosition = bookPosition;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public String getBookDesc() {
        return bookDesc;
    }

    public void setBookDesc(String bookDesc) {
        this.bookDesc = bookDesc;
    }

    public String getBookImg() {
        return bookImg;
    }

    public void setBookImg(String bookImg) {
        this.bookImg = bookImg;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookPublisher() {
        return bookPublisher;
    }

    public void setBookPublisher(String bookPublisher) {
        this.bookPublisher = bookPublisher;
    }

    public Set<BorrowRecord> getBorrowRecordSet() {
        return borrowRecordSet;
    }

    public void setBorrowRecordSet(Set<BorrowRecord> borrowRecordSet) {
        this.borrowRecordSet = borrowRecordSet;
    }

    public BookSort getBookSort() {
        return bookSort;
    }

    public void setBookSort(BookSort bookSort) {
        this.bookSort = bookSort;
    }

}
