package com.lib.bibliosoft.controller;

import com.lib.bibliosoft.entity.Admin;
import com.lib.bibliosoft.entity.DefSetting;
import com.lib.bibliosoft.entity.Librarian;
import com.lib.bibliosoft.repository.AdminRepository;
import com.lib.bibliosoft.repository.DefSettingRepository;
import com.lib.bibliosoft.repository.LibrarianRepository;
import com.lib.bibliosoft.service.impl.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @ Author: 董杭
 * @ Date :2018.10.6
 */
@Controller
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private DefSettingRepository defSettingRepository;
    @Autowired
    private LibrarianRepository librarianRepository;
    @Autowired
    private AdminService adminService;

    private Integer totalcount;

    private Integer pagesize = 6;

    private final static Logger logger = LoggerFactory.getLogger(AdminController.class);

    /**
     * @ Author :董杭
     * @ Date :2018.10.6
     * @ Description :登录主页地址
     */

    @RequestMapping("goAdminLogin")
    public String adminLogin(Model model) throws  Exception{
        return "admin_login";
    }

    /**
     * @ Author :董杭
     * @ Date  :2018.10.6
     * @ Description :登录验证
     */

    @PostMapping("admin_login")
    @ResponseBody
    public String loginAdmin(String name, String password,
                              HttpServletRequest request, HttpServletResponse response) throws IOException {
        Admin admin =null;
        HttpSession session = request.getSession();
        if((admin= adminRepository.findByAdminName(name))!=null){
            response.setContentType("text/plain;charset=UTF-8");
            if(password.equals(admin.getPassword())){
                logger.info("login success");
                session.setAttribute("admin",admin);
                session.setAttribute("islogin", true);
                session.setAttribute("logintype","admin");
                return "success";
            }
            else{
                logger.info("usererror");
                return "usererror";
            }
        }else{
            return "usererror";
        }
    }

    @GetMapping("adminLogout")
    public String logout(HttpServletRequest request, HttpServletResponse response){
        HttpSession session=request.getSession();
        session.setAttribute("islogin",false);
        return "redirect:goAdminLogin";
    }

    @ResponseBody
    @GetMapping("find_all_librarian")
    public List<Librarian> findAll(Model model){
        List<Librarian> list=adminService.findAll();
        model.addAttribute("librarians",list);
        return list;
    }

    @PostMapping("create_librarian")
    public String create(Librarian librarian){
        librarianRepository.save(librarian);
        return "redirect:lib_list";
    }


    @PostMapping("update_librarian")
    public String update(Librarian librarian){
        String id=librarianRepository.findByLibId(librarian.getLibId()).getLibId();
        if(librarian.getPassword()!="") {
            librarianRepository.updateLibrarianWithPass(librarian.getLibId(), librarian.getPassword(), librarian.getLibName(), librarian.getEmail(), librarian.getPhone());
        }else {
            librarianRepository.updateLibrarianWithoutPass(librarian.getLibId(), librarian.getLibName(), librarian.getEmail(), librarian.getPhone());
        }
        return "redirect:lib_list";
    }

    @ResponseBody
    @DeleteMapping("delete_librarian")
    public void  deleteById(String id){
        adminService.deleteByLibId(id);
    }


    /**
     *@ Title: AdminController.java
     *@ Params: libname
     *@ Return: lib_list
     *@ Author: 董杭
     *@ Description: 查询图书馆员
     *@ Date: 2018.10.22
     */
    @PostMapping("lib_search")
    public String search_book(Model model, @RequestParam("libname") String libname){
        logger.info("lib name==={}",libname);
        List<Librarian> searchLibrarian = null;
        try {
            searchLibrarian = librarianRepository.findByLibNameLike("%"+libname+"%");
        } catch (Exception e) {
            e.printStackTrace();
        }
        model.addAttribute("libs",searchLibrarian);
        if(searchLibrarian == null){
            model.addAttribute("currpage",0);
            model.addAttribute("totalcount", 0);
            model.addAttribute("totalpages", 0);
        }else{
            Integer totalPages = (totalcount + pagesize - 1)/pagesize;
            model.addAttribute("currpage",1);
            model.addAttribute("totalcount", searchLibrarian.size());
            model.addAttribute("totalpages", totalPages);
        }
        return "lib_list";
    }



    @GetMapping("lib_list")
    public String list_librarian(Model model){
        Integer currpage = 1;
        totalcount = adminService.findAll().size();
        model.addAttribute("totalcount", totalcount);
        Integer totalPages = (totalcount + pagesize - 1)/pagesize;
        model.addAttribute("totalpages", totalPages);

        //获得每页的数据
        Iterator<Librarian> librarianIterator = adminService.getPage(currpage, pagesize).iterator();

        logger.info("currpage={}",currpage);
        List<Librarian> list = new ArrayList<>();
        while(librarianIterator.hasNext()) {
            list.add(librarianIterator.next());
        }
//        logger.info("list.size = {}",list.size());
//        logger.info("list[0]={}", list.get(0));
        //放在model
        model.addAttribute("libs", list);
        model.addAttribute("currpage",currpage);
        return "lib_list";
    }

    @GetMapping("lib_page")
    public String page_lib(@RequestParam(value = "currpage") Integer currpage, Model model){
        totalcount = librarianRepository.findAll().size();
        model.addAttribute("totalcount", totalcount);
        Integer totalPages = (totalcount + pagesize - 1)/pagesize;
        model.addAttribute("totalpages", totalPages);

        if(currpage == 0)
            currpage = 1;
        if(currpage == totalPages+1)
            currpage = totalPages;
        //获得每页的数据
        Iterator<Librarian> librarianIterator = adminService.getPage(currpage, pagesize).iterator();

        logger.info("currpage={}",currpage);
        List<Librarian> list = new ArrayList<>();
        while(librarianIterator.hasNext()) {
            list.add(librarianIterator.next());
        }
        logger.info("list.size = {}",list.size());
        //logger.info("list[0]={}", list.get(0));
        //放在model
        model.addAttribute("libs", list);
        model.addAttribute("currpage",currpage);
        return "lib_list";
    }

    @Component
    class Def{
        int fine;
        int deposit;
        int lastday;

        public Def(){}
        public int getFine() {
            return fine;
        }

        public void setFine(int fine) {
            this.fine = fine;
        }

        public int getDeposit() {
            return deposit;
        }

        public void setDeposit(int deposit) {
            this.deposit = deposit;
        }

        public int getLastday() {
            return lastday;
        }

        public void setLastday(int lastday) {
            this.lastday = lastday;
        }

        @Override
        public String toString() {
            return "Def{" +
                    "fine=" + fine +
                    ", deposit=" + deposit +
                    ", lastday=" + lastday +
                    '}';
        }
    }

    @ResponseBody
    @GetMapping("get_threshold")
    public  Def getThreahole(){
       Def def=new Def();
       def.fine= defSettingRepository.findDefSettingById(1).getDefnumber();
       def.deposit=defSettingRepository.findDefSettingById(2).getDefnumber();
       def.lastday=defSettingRepository.findDefSettingById(3).getDefnumber();
       return def;
    }


    @PostMapping("update_threshold")
    public String  changeThreshold(Def def){
        DefSetting setting1=new DefSetting();
        DefSetting setting2=new DefSetting();
        DefSetting setting3=new DefSetting();
        setting1.setId(1);
        setting1.setDeftype("fine");
        setting2.setId(2);
        setting2.setDeftype("deposit");
        setting3.setId(3);
        setting3.setDeftype("lastday");

        setting1.setDefnumber(def.fine);
        setting2.setDefnumber(def.deposit);
        setting3.setDefnumber(def.lastday);

        adminService.saveSetting(setting1,setting2,setting3);
        logger.info("阈值修改成功");

        return "redirect:lib_list";
    }

    @Autowired
    private JavaMailSender sender;
    @Value("${spring.mail.username}")
    String from;

    @ResponseBody
    @PostMapping("find_pass")
    public String findPass(String id){
        //logger.info(librarianRepository.findByLibId(id).getPassword());
        //判空！！！
        if(librarianRepository.findByLibId(id) == null){
            return "failed";
        }
        String password=librarianRepository.findByLibId(id).getPassword();
        String mail=librarianRepository.findByLibId(id).getEmail();
        SimpleMailMessage mailMessage=new SimpleMailMessage();

        mailMessage.setFrom(from);
        mailMessage.setTo(mail);
        mailMessage.setSubject("Subject：find your password");
        mailMessage.setText("your password is : "+password);

        try{sender.send(mailMessage);}
        catch (Exception ex){
            logger.info(ex.getMessage());
            return ex.getMessage();
        }
        return "success";
    }

    @ResponseBody
    @PostMapping("change_pass")
    public String change_pass(String old_pass, String new_pass, String new_pass_check){
        if(!new_pass.equals(new_pass_check)){
            return "input inconsistency";
        }else if(!adminRepository.findByAdminName("admin").getPassword().equals(old_pass)){
            return "wrong password";
        }
        else{
            Admin admin= adminRepository.findByAdminName("admin");
            admin.setPassword(new_pass);
            adminRepository.save(admin);
            return "success";
        }

    }
}
