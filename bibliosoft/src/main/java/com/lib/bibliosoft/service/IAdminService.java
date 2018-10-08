package com.lib.bibliosoft.service;

import com.lib.bibliosoft.entity.Librarian;
import com.lib.bibliosoft.entity.Result;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author 董杭
 * @date ${date} ${time}
 */
public interface IAdminService {
    List<Librarian> findAll();

    Librarian update(Librarian librarian);

    void deleteByLibId(String id);

    Page<Librarian> getPage(Integer currpage, Integer pagesize);
}
