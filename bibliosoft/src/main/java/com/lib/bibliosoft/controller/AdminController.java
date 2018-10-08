package com.lib.bibliosoft.controller;

import com.lib.bibliosoft.entity.Admin;
import com.lib.bibliosoft.entity.Librarian;
import com.lib.bibliosoft.repository.AdminRepository;
import com.lib.bibliosoft.repository.LibrarianRepository;
import com.lib.bibliosoft.service.impl.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @author 董杭
 * @date 2018.10.6
 */
@Controller
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private LibrarianRepository librarianRepository;
    @Autowired
    private AdminService adminService;

    private Integer totalcount;

    private Integer pagesize = 6;

    private final static Logger logger = LoggerFactory.getLogger(AdminController.class);

    /*
     * @author 董杭
     * @date 2018.10.6
     * @description 登录主页地址
     */

    @RequestMapping("/goAdminLogin")
    public String adminLogin(Model model) throws  Exception{
        return "admin_login";
    }

    /*
     * @Author 董杭
     * @Date  2018.10.6
     * @Description 登录验证
     */

    @PostMapping("/admin_login")
    @ResponseBody
    public String loginAdmin(String name, String password,
                              HttpServletRequest request, HttpServletResponse response) throws IOException {
        Admin admin =null;
        if((admin= adminRepository.findByAdminName(name))!=null){
            HttpSession session = request.getSession();
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
    @ResponseBody
    @GetMapping("/find_all_librarian")
    public List<Librarian> findAll(Model model){
        List<Librarian> list=adminService.findAll();
        model.addAttribute("librarians",list);
        return list;
    }

    @PostMapping("/create_librarian")
    public String create(Librarian librarian){
        librarianRepository.save(librarian);
        return "redirect:/lib_list";
    }


    @PostMapping("/update_librarian")
    public String update(Librarian librarian){
        String id=librarianRepository.findByLibId(librarian.getLibId()).getLibId();
        librarianRepository.updateLibrarian(librarian.getLibId(),librarian.getPassword(),librarian.getLibName(),librarian.getEmail(),librarian.getPhone());
        return "redirect:/lib_list";
    }

    @ResponseBody
    @DeleteMapping("/delete_librarian")
    public void  deleteById(String id){
        adminService.deleteByLibId(id);
    }


    /**
     *@Title: AdminController.java
     *@Params: libname
     *@Return: lib_list
     *@Author: 毛文杰
     *@Description: 现在是只能查询出来一个用户或者0个用户，因为不是模糊查询，所以对于某个要搜索的图书馆员的名字只能有零个或者一个
     *@Date: 10:34 PM. 10/7/2018
     */
    @PostMapping("/lib_search")
    public String search_book(Model model, @RequestParam("libname") String libname){
        logger.info("lib name==={}",libname);
        Librarian searchLibrarian = null;
        try {
            searchLibrarian = librarianRepository.findByLibName(libname);
        } catch (Exception e) {
            e.printStackTrace();
        }
        model.addAttribute("libs",searchLibrarian);
        if(searchLibrarian == null){
            model.addAttribute("currpage",0);
            model.addAttribute("totalcount", 0);
            model.addAttribute("totalpages", 0);
        }else{
            model.addAttribute("currpage",1);
            model.addAttribute("totalcount", 1);
            Integer totalPages = (1 + pagesize - 1)/pagesize;
            model.addAttribute("totalpages", totalPages);
        }
        return "lib_list";
    }



    @GetMapping("/lib_list")
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
        logger.info("list.size = {}",list.size());
        logger.info("list[0]={}", list.get(0));
        //放在model
        model.addAttribute("libs", list);
        model.addAttribute("currpage",currpage);
        return "lib_list";
    }

    @GetMapping("/lib_page")
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
}
