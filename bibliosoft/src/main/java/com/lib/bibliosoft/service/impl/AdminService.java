package com.lib.bibliosoft.service.impl;

import com.lib.bibliosoft.entity.DefSetting;
import com.lib.bibliosoft.entity.Librarian;
import com.lib.bibliosoft.repository.DefSettingRepository;
import com.lib.bibliosoft.repository.LibrarianRepository;
import com.lib.bibliosoft.service.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ Author 董杭
 * @ Date 2018.10.6
 */
@Service
public class AdminService implements IAdminService {

    @Autowired
    private LibrarianRepository librarianRepository;
    @Autowired
    private DefSettingRepository defSettingRepository;
    @Override
    public  List<Librarian> findAll() {
        return  librarianRepository.findAll();
    }

//    @Override
//    @Transactional
//    @Query(value = "UPDATE" LibrarianEntity lib SET lib.)
//    public Librarian updateById(int id, Librarian librarian) {
//        librarianRepository.u
//    }

    public Librarian update(Librarian librarian){
        return  librarianRepository.save(librarian);
    }


    public void deleteByLibId(String id){
        librarianRepository.deleteByLibId(id);

    }

    @Override
    public Page<Librarian> getPage(Integer currpage, Integer pagesize) {
        Pageable pageable = PageRequest.of(currpage - 1, pagesize);
        return librarianRepository.findAll(pageable);

    }

    @Override
    @Transactional
    public void saveSetting(DefSetting defSetting1, DefSetting defSetting2, DefSetting defSetting3) {

        defSettingRepository.save(defSetting1);
        defSettingRepository.save(defSetting2);
        defSettingRepository.save(defSetting3);
    }


}
