package com.lib.bibliosoft.DAO.impl;
import com.lib.bibliosoft.DAO.IReaderDao;
import com.lib.bibliosoft.entity.Reader;
import com.lib.bibliosoft.repository.ReaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    @Override
    public Reader findByReaderId(Integer id) {
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
    public void updateReader(Integer id, String sex, String readerName, String phone, Integer readerId, String email, String status) {
        readerRepository.updateReader(id, sex, readerName, phone, readerId, email, status);
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
