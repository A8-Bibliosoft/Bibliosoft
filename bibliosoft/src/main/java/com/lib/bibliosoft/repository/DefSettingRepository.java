package com.lib.bibliosoft.repository;

import com.lib.bibliosoft.entity.DefSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DefSettingRepository extends JpaRepository<DefSetting,Integer>{

    DefSetting findDefSettingById(Integer id);

}
