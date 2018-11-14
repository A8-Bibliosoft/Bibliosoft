package com.lib.bibliosoft.DAO.impl;
import com.lib.bibliosoft.DAO.IReaderDao;
import com.lib.bibliosoft.entity.Reader;
import com.lib.bibliosoft.repository.BorrowRecordRepository;
import com.lib.bibliosoft.repository.ReaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @Author: 毛文杰
 * @Description: DAO
 * @Date: Created in 3:22 PM. 9/29/2018
 * @Modify By:
 */
@Component
public class ReaderDAO implements IReaderDao {

    @Autowired
    private ReaderRepository readerRepository;

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Override
    public Reader findByReaderId(String id) {
        return readerRepository.findReaderByReaderId(id);
    }

    @Override
    public List<Reader> searchReaderByPhoneOrName(String string) {
        return readerRepository.searchReaderByPhoneOrName(string);
    }

    @Override
    public void updateReaderStatusById(Integer id, String status) {
        readerRepository.updateReaderStatusById(id, status);
    }

    /**
     *@Title: ReaderDAO.java
     *@Params: readerId
     *@Return: Integer
     *@Author: 毛文杰
     *@Description: conspicuous
     *@Date: 3:34 PM. 10/12/2018
     */
    @Override
    public Integer getBorrowCountByReaderId(String readerId) {
        return borrowRecordRepository.findAllByReaderId(readerId).size();
    }

    public List<Reader> findAll(){
        return readerRepository.findAll();
    }

    @Override
    public Reader findById(Integer id) {
        return readerRepository.findById(id).get();
    }

    /**
     * save or update reader
     * @param reader
     */
    @Override
    public void addReader(Reader reader) {
        readerRepository.saveAndFlush(reader);
    }

    /**
     * update reader's info
     */
    @Override
    public void updateReader(Integer id, String sex, String readerName, String phone, String readerId, String email, String status, String password, Date registTime) {
        readerRepository.updateReader(id, sex, readerName, phone, readerId, email, status, password, registTime);
    }

    @Override
    public void deleteReader(Reader reader) {
        readerRepository.delete(reader);
    }

    @Override
    public Reader findByUsername(String name) {
        return  readerRepository.findReaderByReaderName(name);
    }
}
