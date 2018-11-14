package com.lib.bibliosoft.controller;

import com.lib.bibliosoft.entity.Notices;
import com.lib.bibliosoft.enums.ResultEnum;
import com.lib.bibliosoft.repository.BulletinRepository;
import com.lib.bibliosoft.service.IBulletinService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 毛文杰
 * @project bibliosoft
 * @description 公告
 * @date Created in 4:20 PM. 10/18/2018
 * @modify By 毛文杰
 */
@Controller
public class BulletinController {


    private BulletinRepository bulletinRepository;

    private IBulletinService iBulletinService;

    private Integer totalcount;

    private Integer pagesize = 6;

    @Autowired
    public BulletinController(BulletinRepository bulletinRepository, IBulletinService iBulletinService){
        this.bulletinRepository = bulletinRepository;
        this.iBulletinService = iBulletinService;
    }

    private final static Logger logger = LoggerFactory.getLogger(BookController.class);

    /**
     * @title BulletinController.java
     * @description 分页展示 公告
     * @return java.lang.String
     * @author 毛文杰
     * @method name gotoBulletinManagePage
     * @date 4:53 PM. 10/18/2018
     */
    @GetMapping("bulletin_page")
    public String gotoBulletinManagePage(@RequestParam(value = "currpage") Integer currpage, Model model){
        totalcount = bulletinRepository.findAll().size();
        model.addAttribute("totalcount", totalcount);
        Integer totalPages = (totalcount + pagesize - 1)/pagesize;
        model.addAttribute("totalpages", totalPages);

        if(currpage == 0)
            currpage = 1;
        if(currpage == totalPages+1)
            currpage = totalPages;
        //获得每页的数据
        List<Notices> noticesList = iBulletinService.getPage(currpage, pagesize).getContent();
        //logger.info("currpage={}",currpage);

        //放在model
        model.addAttribute("notices", noticesList);
        model.addAttribute("currpage",currpage);
        return "book_bulletin";
    }


    /***
     * @title BulletinController.java
     * @param model 首次进入时展示
     * @return java.lang.String
     * @author 毛文杰
     * @method name getAllBulletins
     * @date 4:50 PM. 10/18/2018
     */
    @GetMapping("bulletin_list")
    public String getAllBulletins(Model model){
        int currpage = 1;
        totalcount = bulletinRepository.findAll().size();
        model.addAttribute("totalcount", totalcount);
        Integer totalPages = (totalcount + pagesize - 1)/pagesize;
        model.addAttribute("totalpages", totalPages);

        //获得每页的数据
        List<Notices> noticesList = iBulletinService.getPage(currpage, pagesize).getContent();
//        logger.info("currpage={}",currpage);

        //放在model
        model.addAttribute("notices", noticesList);
        if(totalcount == 0)
            currpage = 0;
        model.addAttribute("currpage", currpage);
        return "book_bulletin";
    }

    /**
     * @title BulletinController.java
     * @param id 公告的唯一标识， session
     * @return org.springframework.http.ResponseEntity<java.util.Map<java.lang.String,java.lang.Object>>
     * @author 毛文杰
     * @method name bulletinDelete
     * @date 5:47 PM. 10/18/2018
     * @description 通过id删除一条公告
     */
    @Transactional
    @PostMapping("bulletin/{id}")
    public ResponseEntity<Map<String,Object>> bulletinDelete(@PathVariable("id") Integer id){
        /*删除bulletin的notices表*/
        bulletinRepository.deleteById(id);

        Map<String,Object> map = new HashMap<>();
        map.put("msg", ResultEnum.SUCCESS.getMsg());
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /**
     * @title BulletinController.java
     * @param id bulletin's id in database, model
     * @return java.lang.String
     * @author 毛文杰
     * @method name show_bulletin
     * @date 7:42 PM. 10/18/2018
     */
    @GetMapping("/bulletin_show/{id}")
    public String show_bulletin(@PathVariable("id") String id, Model model){
        Integer Id = Integer.parseInt(id);
        Notices notices = bulletinRepository.findById(Id).orElse(null);
        model.addAttribute("bulletin", notices);
        return "bulletin_show";
    }

    /**
     * edit the bulletin
     * @title BulletinController.java
     * @param bulletinid, bulletintitle, bulletincontent, flag
     * @return java.lang.String
     * @author 毛文杰
     * @method name editBulletin
     * @date 8:06 PM. 10/18/2018
     */
    @PostMapping("editadd_bulletin")
    public String editBulletin( String bulletinid, String bulletintitle, String bulletincontent, String flag){
        if (flag.equals("edit")){
            Integer id = Integer.parseInt(bulletinid);
            //虽然这个不可能为空。。。
            Notices notices = bulletinRepository.findById(id).orElse(null);
            notices.setTitle(bulletintitle);
            notices.setContent(bulletincontent);
            notices.setTime(new Date());
            bulletinRepository.save(notices);
        }else if(flag.equals("add")){
            Notices notices = new Notices();
            notices.setContent(bulletincontent);
            notices.setTitle(bulletintitle);
            notices.setTime(new Date());
            bulletinRepository.save(notices);
            logger.info("Add notice={}", notices.toString());
        }
        return "redirect:bulletin_list";
    }
}
