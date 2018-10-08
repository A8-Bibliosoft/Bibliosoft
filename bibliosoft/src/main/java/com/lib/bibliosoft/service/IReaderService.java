package com.lib.bibliosoft.service;

import com.lib.bibliosoft.entity.Reader;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @Author: 毛文杰
 * @Description: interface
 * @Date: Created in 3:51 PM. 10/2/2018
 * @Modify By:
 */
public interface IReaderService {
    // 普通分页
    Page<Reader> getPage(Integer pageNum, Integer pageLimit);

    // 排序分页
    Page<Reader> getPageSort(Integer pageNum, Integer pageLimit);

    List<Reader> searchReaderByPhoneOrName(String string);
}
