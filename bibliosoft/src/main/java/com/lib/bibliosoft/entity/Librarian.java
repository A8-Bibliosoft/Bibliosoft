package com.lib.bibliosoft.entity;

import javax.persistence.*;

/**
 * @Author: 毛文杰
 * @Description: the entity of Librarians.
 * @Date: Created in 10:01 AM. 10/4/2018
 * @Modify By: maowenjie
 */
@Entity
@Table(name = "librarian")
public class Librarian {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String libName;

    private String libId;

    private String password;

    private String phone;

    private String email;

    public Librarian() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLibName() {
        return libName;
    }

    public void setLibName(String libName) {
        this.libName = libName;
    }

    public String getLibId() {
        return libId;
    }

    public void setLibId(String libId) {
        this.libId = libId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Librarian{" +
                "id=" + id +
                ", libName='" + libName + '\'' +
                ", libId='" + libId + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
