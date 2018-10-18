package com.lib.bibliosoft.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="booktype")
public class BookType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private Integer typeId;

    private String typeName;

    @OneToMany(mappedBy = "bookType",fetch = FetchType.EAGER)
    private List<BookSort> bookSortList;

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<BookSort> getBookSortList() {
        return bookSortList;
    }

    public void setBookSortList(List<BookSort> bookSortList) {
        this.bookSortList = bookSortList;
    }
}
