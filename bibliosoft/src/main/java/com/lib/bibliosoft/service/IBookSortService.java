package com.lib.bibliosoft.service;

import com.lib.bibliosoft.entity.BookSort;
import org.springframework.data.domain.Page;

public interface IBookSortService {
    Page<BookSort> PageBook(Integer page, Integer size, Integer typeId);
}
