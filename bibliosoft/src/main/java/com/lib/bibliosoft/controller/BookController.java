package com.lib.bibliosoft.controller;

import com.alibaba.fastjson.JSONObject;
import com.lib.bibliosoft.entity.*;
import com.lib.bibliosoft.enums.ResultEnum;
import com.lib.bibliosoft.repository.*;
import com.lib.bibliosoft.service.impl.BookDelService;
import com.lib.bibliosoft.service.impl.BookService;
import com.lib.bibliosoft.utils.*;
import org.hibernate.annotations.Synchronize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lib.bibliosoft.utils.ScanerIsbn.loadJSON;

/**
 *@Title: BookController.java
 *@Author: 毛文杰
 *@Description: Book Controller
 *@Date: 10:07 PM. 10/6/2018
 */
@Controller
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private BookDelService bookDelService;

    @Autowired
    private BookDelRecordRepository bookDelRecordRepository;

    @Autowired
    private BookSortRepository bookSortRepository;

    @Autowired
    private BookStatusRepository bookStatusRepository;

    @Autowired
    private BookTypeRepository bookTypeRepository;

    @Autowired
    private BookPositionRepository bookPositionRepository;

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    private List<BookStatus> status;

    private final static Logger logger = LoggerFactory.getLogger(BookController.class);

    /*共查询出的数目*/
    private Integer totalcount;

    /*每页的大小*/
    private Integer pagesize = 6;

//    /**a'd'd
//     * Test
//     * add a book
//     * @return Result<Book>
//     */
//    @PostMapping("/books")
//    @ResponseBody
//    public Result<Book> bookAdd(@Valid Book book, BindingResult bindingResult){
//        if (bindingResult.hasErrors()){
//            return ResultUtil.error(1, bindingResult.getFieldError().getDefaultMessage());
//        }
//        return ResultUtil.success(bookRepository.save(book));
//    }

//    /**
//     * Test
//     * find a book by id
//     * @param id
//     * @return
//     */
//    @GetMapping("/books/{id}")
//    @ResponseBody
//    public Book bookFindOne(@PathVariable("id") Integer id){
//        return bookRepository.findById(id).orElse(null);
//        //the return statement also can write like this:
//        //return bookRepository.findById(id).get();
//        //When the method does not receive a parameter or the parameter is illegal, an error will be reported.
//    }
//
//    /**
//     * Test
//     * update a book's position by id
//     * @param id the book's id
//     * @param position book's position
//     * @return a Book
//     */
//    @PutMapping("/books/{id}")
//    @ResponseBody
//    public Book updateBook(@PathVariable("id") Integer id,
//                           @RequestParam("bookPosition") Integer position){
//        Book book = bookRepository.findById(id).get();
//        BookPosition bookPosition = bookPositionRepository.findById(position).orElse(null);
//        bookPosition.getBooks().add(book);
//        book.setBookPosition(bookPosition);
//        return bookRepository.save(book);
//    }

    /**
     * @title  BookController.java
     * @param id book's id not bookid
     * @return ResponseEntity type
     * @author  毛文杰
     * @description delete a book by it's id
     * @date  6:10 PM. 10/7/2018
     * @Modify time 5:05 PM 10/22/2018
     */
    @Transactional
    @PostMapping("book/{id}")
    public ResponseEntity<Map<String,Object>> bookDelete(@PathVariable("id") Integer id, HttpSession session){

        Map<String,Object> map = new HashMap<String,Object>();
        Book book = null;
        /*先判断是否已不在数据库*/
        if(bookRepository.findById(id).isPresent())
            book = bookRepository.findById(id).get();
        else {
            map.put("msg",ResultEnum.NOT_EXIST.getMsg());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
        Integer status = book.getBookStatus();
        //logger.info("status={}", status);
        if(status == 5){
            map.put("msg", ResultEnum.BOOK_ALREADY_DEL.getMsg());
        }else if(status == 0 || status == 2){//0：在架上 2：损坏、丢失
            /*先在删除记录表里记录*/
            BookDelRecord bookDelRecord = new BookDelRecord();
            Librarian librarian = (Librarian) session.getAttribute("librarian");
            //找到此时正在删除操作的图书馆员，记录到删除表中
            if(librarian == null){
                map.put("msg", "please login.");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
            Integer bookid = book.getBookId();
            //librarian与delrecord关联
            bookDelRecord.setLibId(librarian);
            bookDelRecord.setTime(new java.util.Date());
            bookDelRecord.setBookName(book.getBookName());
            librarian.getBookDelRecordList().add(bookDelRecord);
            bookDelRecordRepository.save(bookDelRecord);
            /*先将状态改为5，表示已删除*/
            bookRepository.updateBookStatus(5, bookid);
            /*然后删除book表中的对应的条目*/
            bookRepository.deleteById(id);
            //文件删除：要删除本地图书条形码
            String barcodename = String.valueOf(book.getBookId());

            File p = null;
            try {
                p = new File(ResourceUtils.getURL("classpath:").getPath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            File upload = new File(p.getAbsolutePath(),"static/barcodeimages/");
            if(!upload.exists())
                upload.mkdirs();

            //删除书籍barcode原图片
            String oldimgrc = barcodename+".png";
            logger.info(upload.getAbsolutePath()+"\\"+oldimgrc);
            File oldImg = new File(upload.getAbsolutePath()+"\\"+oldimgrc);
            if(oldImg.delete()){
                logger.info("删除原barcode图片成功");
            }else{
                logger.info("删除原barcode图片失败");
            }

            //bookSort表也要更新
            List<BookSort> bookSort = bookSortRepository.findByBookIsbn(book.getBookIsbn());
            //实际上最多只能有一条相同isbn的记录,这里为了判断是否存在,用size表示
            if(bookSort.size()>0){
                //同一类(ISBN)书籍的数目减一
                bookSortRepository.updateBookNumByisbn(-1,book.getBookIsbn());
//                List<BookSort> bookSort2 = bookSortRepository.findByBookIsbn(book.getBookIsbn());
//                //判断是否为0,是在删除此类(isbn)书籍
//                if(bookSort2.get(0).getNum() == 0){
//                    logger.info("进入删除");
//                    //无此类书籍,删除,否则在读者页面会出现空白格子
//                    bookSortRepository.deleteByIsbn(bookSort2.get(0).getBookIsbn());
//                }
                //以上写法执行顺序有问题，永远进入不了if判断，因为num即使数据库已经为0，bookSort2也为1，不知道为什么
                bookSortRepository.deleteNumEquals0();
            }
            map.put("msg", ResultEnum.BOOK_DEL_SUCCESS.getMsg());
        }else{
            map.put("msg", ResultEnum.BOOK_DEL_FAILED.getMsg());
        }
        return new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);
    }

    /**
     * @title: BookController.java
     * @param: isbn
     * @return: List<Book>
     * @author: 毛文杰
     * @date: 11:14 PM. 9/27/2018
     * @modifyTime 8:14 PM. 10/12/2018
     */
    @GetMapping("book_sBybisbn")
    public String findBooksByIsbn(@RequestParam("isbn") String isbn, Model model, HttpSession session){
        session.setAttribute("searchContent1",isbn);
        List<Book> books = bookRepository.findBookByBookIsbn(isbn);
//        logger.info("bookisbn={}", isbn);
        Integer totalcount =  books.size();
        model.addAttribute("totalcount", totalcount);
        Integer totalPages = (totalcount + pagesize - 1)/pagesize;
        model.addAttribute("totalpages", totalPages);
        List<Book> bookList = bookService.getPagebyIsbn(1,pagesize,isbn).getContent();
        model.addAttribute("books",bookList);
        if(totalcount == 0)
            model.addAttribute("currpage",0);
        else
            model.addAttribute("currpage",1);
        model.addAttribute("place", bookPositionRepository.findAll());
        model.addAttribute("status", status);
        model.addAttribute("sbtype", 3);
        return "book_list";
    }


    /**
     * list all the books
     * @param model
     * @return
     */
    @GetMapping("book_list")
    public String list_book(Model model){
        Integer currpage = 1;
        totalcount = bookService.findAll().size();
        if(totalcount == 0)
            currpage = 0;
        model.addAttribute("totalcount", totalcount);
        Integer totalPages = (totalcount + pagesize - 1)/pagesize;
        model.addAttribute("totalpages", totalPages);

        //获得每页的数据
        List<Book> bookList = bookService.getPage(currpage, pagesize).getContent();
        //放在model
        model.addAttribute("books", bookList);
        model.addAttribute("currpage",currpage);
        status = bookStatusRepository.findAll();
        model.addAttribute("status", status);
        model.addAttribute("place", bookPositionRepository.findAll());
        model.addAttribute("sbtype", 0);
        return "book_list";
    }

    /**
     * paging list book
     * @param currpage
     * @param model
     * @param sbtype 分页查询的时候在页面之间传递的参数,代表是按照什么搜索的
     *               0 :首次进入首页什么都不查
     *               1 :按照bookid搜索,分页
     *               2 :按照time来搜索
     *               3 :按照ISBN来搜索
     *               4 :按照bookname来搜索
     * @return
     */
    @GetMapping("book_page")
    public String page_book(@RequestParam(value = "currpage") Integer currpage, Integer sbtype, Model model, HttpSession session){
        List<Book> bookList = null;

        if(sbtype == 0){
            totalcount = bookService.findAll().size();
            model.addAttribute("totalcount", totalcount);
            Integer totalPages = (totalcount + pagesize - 1)/pagesize;
            model.addAttribute("totalpages", totalPages);
            if(currpage == 0)
                currpage = 1;
            if(currpage == totalPages+1)
                currpage = totalPages;
            //获得每页的数据
            bookList = bookService.getPage(currpage, pagesize).getContent();
            model.addAttribute("sbtype", 0);
        }else if(sbtype == 1 || sbtype == 2){
            try {
                totalcount = bookService.searchBookByNameOrAddTime((String)session.getAttribute("searchContent1"),
                        (String)session.getAttribute("searchContent2")).size();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            model.addAttribute("totalcount", totalcount);
            Integer totalPages = (totalcount + pagesize - 1)/pagesize;
            model.addAttribute("totalpages", totalPages);
            if(currpage == 0)
                currpage = 1;
            if(currpage == totalPages+1)
                currpage = totalPages;
            bookList = bookService.findbookbynameortime(currpage, pagesize, (String)session.getAttribute("searchContent1"),
                    (String)session.getAttribute("searchContent2")).getContent();
            model.addAttribute("sbtype", 2);
        }else if(sbtype == 3){
            totalcount = bookRepository.findBookByBookIsbn((String)session.getAttribute("searchContent1")).size();
            model.addAttribute("totalcount", totalcount);
            Integer totalPages = (totalcount + pagesize - 1)/pagesize;
            model.addAttribute("totalpages", totalPages);
            if(currpage == 0)
                currpage = 1;
            if(currpage == totalPages+1)
                currpage = totalPages;
            bookList = bookService.getPagebyIsbn(currpage,pagesize,(String)session.getAttribute("searchContent1")).getContent();
            model.addAttribute("sbtype", 3);
        }else if(sbtype == 4){
            totalcount = bookRepository.findBookByBookIsbn((String)session.getAttribute("searchContent1")).size();
            model.addAttribute("totalcount", totalcount);
            Integer totalPages = (totalcount + pagesize - 1)/pagesize;
            model.addAttribute("totalpages", totalPages);
            if(currpage == 0)
                currpage = 1;
            if(currpage == totalPages+1)
                currpage = totalPages;
            bookList = bookService.getPagebyIsbn(currpage, pagesize, (String)session.getAttribute("searchContent1")).getContent();
            model.addAttribute("sbtype", 3);
        }

        //放在model
        model.addAttribute("books", bookList);
        model.addAttribute("currpage",currpage);
        model.addAttribute("status", status);
        model.addAttribute("place", bookPositionRepository.findAll());
        return "book_list";
    }

    /**
     * search by bookname or add time
     * @param model
     * @param bookname
     * @param bookaddtime
     * @return
     */
    @RequestMapping("book_search")
    public String search_book(Model model, HttpSession session, @RequestParam("bookname") String bookname, @RequestParam("bookaddtime") String bookaddtime){
        session.setAttribute("searchContent1", bookname);
        session.setAttribute("searchContent2", bookaddtime);
//        logger.info("book name==={}, book add time==={}",bookname, bookaddtime);
        List<Book> searchBook = null;
//        try {
//            if (bookService.searchBookByNameOrAddTime(bookname, bookaddtime) != null){
//                searchBook = bookService.searchBookByNameOrAddTime(bookname, bookaddtime);
//                totalcount = searchBook.size();
//                model.addAttribute("totalcount", totalcount);
//                Integer totalPages = (totalcount + pagesize - 1)/pagesize;
//                model.addAttribute("totalpages", totalPages);
//                model.addAttribute("currpage",1);
//            }else {
//                model.addAttribute("totalcount", 0);
//                model.addAttribute("totalpages", 0);
//                model.addAttribute("currpage",0);
//            }
//
//        } catch (ParseException e) {
//            logger.error("Parse Date Error={}", e);
//            e.printStackTrace();
//        }

        try {
            if (bookService.searchBookByNameOrAddTime(bookname, bookaddtime) != null){
                searchBook = bookService.findbookbynameortime(1,pagesize,bookname, bookaddtime).getContent();
                totalcount = bookService.searchBookByNameOrAddTime(bookname, bookaddtime).size();
                model.addAttribute("totalcount", totalcount);
                Integer totalPages = (totalcount + pagesize - 1)/pagesize;
                model.addAttribute("totalpages", totalPages);
                model.addAttribute("currpage",1);
            }else {
                model.addAttribute("totalcount", 0);
                model.addAttribute("totalpages", 0);
                model.addAttribute("currpage",0);
            }

        } catch (Exception e) {
            logger.error("Error={}", e);
            e.printStackTrace();
        }

        //logger.info("查询结果===大小size={}",searchBook.size());
        model.addAttribute("books",searchBook);
        model.addAttribute("status", status);
        model.addAttribute("sbtype", 1);
        model.addAttribute("place", bookPositionRepository.findAll());
        return "book_list";
    }

    /**
     *@Title: BookController.java
     *@Params: bookid, model
     *@Return: String
     *@Author: 毛文杰
     *@Description: find a book by its bookid
     *@Date: 5:22 PM. 10/12/2018
     */
    @GetMapping("book_sBybid")
    public String searchBookByBookid(String bookid, Model model){
        Integer ibookid = Integer.parseInt(bookid);
//        List<Book> books = bookRepository.findBookByBookIsbn(sbookid);
        Book books = bookRepository.findBookByBookId(ibookid);
        if(books != null){
            model.addAttribute("totalcount", 1);
            model.addAttribute("totalpages", 1);
            model.addAttribute("currpage",1);
        }else{
            model.addAttribute("totalcount", 0);
            model.addAttribute("totalpages", 0);
            model.addAttribute("currpage",0);
        }
        model.addAttribute("books",books);
        model.addAttribute("status", status);
        model.addAttribute("sbtype", 4);
        model.addAttribute("place", bookPositionRepository.findAll());
        return "book_list";
    }



    /**
     * edit a book's info in the layer
     * @param bookId
     * @param bookName
     * @param bookPosition
     * @param isbn
     * @param price
     * @param author
     * @param status
     * @return
     */
    @PostMapping("edit_book")
    public String book_edit(String bookId, String bookName, Integer bookPosition,
                             String isbn, String price, String author,
                             @RequestParam("bookstatus") String status){
        float fprice = Float.parseFloat(price);
        Integer bid = Integer.parseInt(bookId);
        Integer istatus = Integer.parseInt(status);
//        if (flag.equals("edit")){
            Integer id = bookRepository.findBookByBookId(bid).getId();
            bookService.updateBook(id, bookName, bookPosition, isbn, bid, fprice, author, istatus);
//        }else if(flag.equals("add")){
//            Book book = new Book();
//            book.setBookId(bookId);
//            book.setBookIsbn(isbn);
//            book.setBookPrice(fprice);
//            book.setBookName(bookName);
//            book.setBookPosition(bookPosition);
//            book.setBookAuthor(author);
//            book.setBookStatus(istatus);
//            bookService.addBook(book);
//            logger.info("Add book={}",book.toString());
//        }
        return "redirect:book_list";
    }
    /**
     * @Title: BookController.java
     * @param booktitle
     * @param bookcover
     * @param bookauthor
     * @param bookposition
     * @param bookprice
     * @param bookid
     * @param bookstatus
     * @param bookaddtime
     * @param booksummary
     * @return "success" mean that the book is added to database successfully
     * @Author: 毛文杰
     * @Description: add a new book to the library
     * @Date: 10:07 PM. 10/6/2018
     */
    @RequestMapping("book_addnewbook")
    @ResponseBody
    public String add_newbook(String booktitle, MultipartFile bookcover, String bookpublisher, String bookauthor,
                                                          Integer bookposition, String bookprice, String booknum, Integer bookstatus,
                                                          String booksummary, String typeid){
        // 上传成功或者失败的提示
        String msg = "";
        String bookid = "";
        Integer Ibookid = 0;
        StringBuilder bookids = new StringBuilder();
        String s = "";
        //java.sql.Date 日期
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        sf.format(date);
        /*获得新的文件名*/
        String bookImg = FileNameUtil.getFileName(bookcover.getOriginalFilename());
        float Fbookprice = Float.parseFloat(bookprice);

        //通过用户选择的位置找到位置实体
        BookPosition bookPosition = bookPositionRepository.findById(bookposition).orElse(null);
        //生成多个book对象
        Integer num = Integer.parseInt(booknum);
        String bookisbn = "";
        //检测是否重复出现isbn,同一批的书的isbn是一样的
        bookisbn = RandomId.getRandomNum(13);
        //这里因为返回的是集合，所以不能使用是否等于null判断
        while(bookRepository.findBookByBookIsbn(bookisbn).size() > 0){
            bookisbn = RandomId.getRandomNum(13);
        }
        logger.info("isbn={}", bookisbn);
        for(int i=0;i<num;i++) {
            /*书籍*/
            Book book = new Book();
            book.setBookStatus(bookstatus);
            book.setBookAuthor(bookauthor);
            bookPosition.getBooks().add(book);
            book.setBookPosition(bookPosition);
            book.setBookName(booktitle);
            book.setBookPrice(Fbookprice);
            book.setBookIsbn(bookisbn);
            book.setBookPublisher(bookpublisher);

            //生成随机8位数，保证任意不同两本书的id不同
            bookid = RandomId.getRandomNum(8);
            Ibookid = Integer.parseInt(bookid);
            while (bookRepository.findByBookId(Ibookid) != null) {
                bookid = RandomId.getRandomNum(8);
                Ibookid = Integer.parseInt(bookid);
            }
            book.setBookId(Ibookid);
            book.setRegisterTime(date);
            book.setBookDesc(booksummary);
            book.setBookImg("/static/bookimages" + '/' + bookImg);
            //保存到book表
            bookService.addBook(book);
            //插入book表中的sort关联的isbn号
            bookRepository.insertBookIsbn(bookisbn, Ibookid);

            try {
                //生成条形码
                BarcodeUtil.generateFile(bookid);
                //提示语句
                String name = "static/barcodeimages/" + bookid + ".png";
                //把图片附加到末尾，直接显示图片
                s = "<img class='tempdel' src='" + name + "'>";
//                if(i == 0){
//                    s = "<br><!--startprint--><img class='tempdel' src='"+name+"'>";
//                }
//                if(num-1 == i){
//                    s = "<br><img class='tempdel' src='"+name+"'><!--endprint-->";
//                }
//                if(num == 1){
//                    s = "<br><!--startprint--><img class='tempdel' src='"+name+"'><!--endprint-->";
//                }
                bookids.append(s);
            } catch (Exception e) {
                logger.error("添加书籍出错！error = {}", e.getMessage());
                msg = ResultEnum.ADD_BOOK_FAILED.getMsg();
                e.printStackTrace();
                return msg;
            }
        }

        /*要上传的目标文件存放路径为 static/bookimages/ */
        File p = null;
        try {
            p = new File(ResourceUtils.getURL("classpath:").getPath());
            //logger.info(p.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        File upload = new File(p.getAbsolutePath(),"static/bookimages/");
        //logger.info(upload.getAbsolutePath());
        if(!upload.exists())
            upload.mkdirs();

        if (FileUtil.upload2(bookcover, upload.getAbsolutePath(), bookImg)){
            // 上传成功，给出页面提示
            msg = ResultEnum.ADD_BOOK_SUCCESS.getMsg();
        }else {
            msg = ResultEnum.ADD_BOOK_FAILED.getMsg();
        }

        //booksort表插入数据
        if(bookSortRepository.findByBookIsbn(bookisbn).size()>0){
            logger.info("已有ISBN编号为==={}的书籍，故不插入", bookisbn);
            bookSortRepository.updateBookNumByisbn(1,bookisbn);
        }else{
            //书籍分类表
            BookSort bookSort = new BookSort();
            bookSort.setBookAuthor(bookauthor);
            bookSort.setBookIsbn(bookisbn);
            bookSort.setBookName(booktitle);
            bookSort.setTypeId(Integer.parseInt(typeid));
            //第一本
            bookSort.setNum(num);
            //关联
//            bookSort.getBookList().add(book);
//            book.setBookSort(bookSort);
            bookSortRepository.save(bookSort);
            bookSortRepository.insertTypeId(Integer.parseInt(typeid),bookisbn);
        }

        /*这是获取项目根目录*/
//        String ph = ClassUtils.getDefaultClassLoader().getResource("").getPath();
//        logger.info("path的绝对路径 = {}", ph);

        return bookids+"";
    }

    /**
     * @title: BookController.java
     * @param bookId
     * @param model
     * @return String -> view page
     * @author 毛文杰
     * @description show detail information of a book
     * @date 6:24 PM. 10/7/2018
     */
    @GetMapping("book_show/{id}")
    public String show_book(@PathVariable("id") Integer bookId, Model model){
        //Integer bookid = Integer.parseInt(bookId);
        Book book = bookService.getBookById(bookId);
        String sstatus = bookService.findsstatusByid(bookId);
//        logger.info("书籍状态 === {}", sstatus);
        model.addAttribute("book", book);
        model.addAttribute("sstatus", sstatus);
        return "book_show";
    }

    /**
     * @param isbn
     * @param position
     * @param status
     * @return String
     * @title BookController.java
     * @author 毛文杰
     * @description add books by isbn
     * @date 2:19 PM. 10/9/2018
     */
    @PostMapping("book_isbn")
    public ResponseEntity<Map<String,Object>> getInfoByDouBan(String isbn, Integer position, String status, String num, String typeid, String bookname, String bookauthor, String bookpublisher){
        Map<String,Object> map = new HashMap<String,Object>();
        Integer number = Integer.parseInt(num);
        //当前时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String time = formatter.format(new java.util.Date());

        //get the books and improve their information
        List<Book> books = null;
        try {
            books = ScanerIsbn.getBookInfoByIsbn(isbn, number, time, position, status, typeid, bookname, bookauthor, bookpublisher);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("msg", e.getMessage());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }

        StringBuilder bookids = new StringBuilder();
        String s = "";
//        int i=0;
        try {
            for (Book b : books){
                bookService.addBook(b);
                //logger.info(isbn);
                //logger.info(""+b.getBookId());
                bookRepository.insertBookIsbn(isbn, b.getBookId());
                //生成条形码
                BarcodeUtil.generateFile(String.valueOf(b.getBookId()));
                //提示语句
                String name = "static/barcodeimages/"+String.valueOf(b.getBookId())+".png";
//                s = "<a target='view_window' href='http://localhost:8080/"+name+"'>"+b.getBookId()+"</a>;";
                //把图片附加到末尾，直接显示图片
                s = "<img class='tempdel' src='"+name+"'>";
                //注释掉的原因是，每次添加完书籍之后，无法删除每次携带到前端的<!--startprint-->和<!--endprint-->，导致前端即使remove掉上次append的东西，
                // 但注释还在，导致点击打印，出现的是空页面。改为把两个注释移到前端固定写死，问题解决
//                ++i;
//                if(i == 1){
//                    s = "<br><!--startprint--><img class='tempdel' src='"+name+"'>";
//                }
//                if(books.size() == i){
//                    s = "<br><img class='tempdel' src='"+name+"'><!--endprint-->";
//                }
//                if(books.size() == 1){
//                    s = "<br><!--startprint--><img class='tempdel' src='"+name+"'><!--endprint-->";
//                }
                bookids.append(s);
            }

        }catch(Exception e){
            logger.error("添加书籍出错！error = {}", e.getMessage());
            map.put("msg", ResultEnum.ADD_BOOK_FAILED.getMsg());
            e.printStackTrace();
            return new ResponseEntity<>(map, HttpStatus.valueOf(500));
        }
        //http://localhost:8080/downloadImage
//        map.put("msg", ResultEnum.ADD_BOOK_SUCCESS.getMsg() + " bookid: " + bookids);
        map.put("msg", bookids);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /**
     * @title BookController.java
     * @author 毛文杰
     * @date 1:59 PM. 11/8/2018
     */
    //补全添加isbn之后的表单信息
    @PostMapping("book_completeinfo")
    public ResponseEntity<Map<String,Object>> complete(String bookisbn){
        Map<String,Object> map = new HashMap<String,Object>();
        String url = "https://api.douban.com/v2/book/isbn/:" + bookisbn;
        String sauthor = "";
        String publisher = "";
        String author = "";
        String name = "";
        String imgsrc = "";
        try{
            String json = loadJSON(url);
            JSONObject jsonObject = JSONObject.parseObject(json);
            sauthor = jsonObject.getString("author");
            publisher = jsonObject.getString("publisher");
            author = sauthor.split("\"")[1];
            name = jsonObject.getString("title");
            imgsrc = jsonObject.getString("image");
        }
        catch(Exception e){
            map.put("msg",ResultEnum.DOUBAN_ERROR.getMsg());
            e.printStackTrace();
            return new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);
        }
        map.put("msg", ResultEnum.SUCCESS.getMsg());
        map.put("name", name);
        map.put("publisher", publisher);
        map.put("author", author);
        map.put("imgsrc", imgsrc);
        logger.info("name={},author={},publisher={},imgsrc={}",name,author,publisher,imgsrc);
        return new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);
    }



    /**
     * go to add book by isbn page
     * @title BookController.java
     * @param model
     * @return java.lang.String
     * @author 毛文杰
     * @method name goaddBookIsbnPage
     * @date 9:24 PM. 10/18/2018
     */
    @GetMapping("book_addByIsbn")
    public String goaddBookIsbnPage(Model model){
        model.addAttribute("types", bookTypeRepository.findAll());
        status = bookStatusRepository.findAll();
        model.addAttribute("status", status);
        model.addAttribute("place", bookPositionRepository.findAll());
        return "book_addByIsbn";
    }

    /**
     * go to add book details manually
     * @title BookController.java
     * @param model
     * @return java.lang.String
     * @author 毛文杰
     * @method name goAddBookpage
     * @date 9:24 PM. 10/19/2018
     */
    @GetMapping("bookadd_detail")
    public String goAddBookpage(Model model){
        model.addAttribute("types", bookTypeRepository.findAll());
        model.addAttribute("status", status);
        model.addAttribute("place", bookPositionRepository.findAll());
        return "bookadd_detail";
    }

    /*-------------------------------Category管理-------------------------------*/

    /**
     * go to book type management page
     * @title BookController.java
     * @param model
     * @return java.lang.String
     * @author 毛文杰
     * @method name gotoAddCategory
     * @date 10:12 PM. 10/19/2018
     */
    @GetMapping("book_category_add")
    public String gotoAddCategory(Model model){
        model.addAttribute("types", bookTypeRepository.findAll());
        return "book_category_add";
    }

    /**
     * delete a book type in database by id
     * @title BookController.java
     * @param id
     * @return org.springframework.http.ResponseEntity<java.util.Map<java.lang.String,java.lang.Object>>
     * @author 毛文杰
     * @method name deleteCategpory
     * @date 10:12 PM. 10/19/2018
     */
    @PostMapping("bookcategory/{id}")
    public ResponseEntity<Map<String,Object>> deleteCategpory(@PathVariable("id") Integer id) {
        Map<String,Object> map = new HashMap<String,Object>();
        if(bookRepository.findByBookStatus(5).size() > 0) {
            map.put("msg", ResultEnum.FAILED.getMsg());
        }else{
            bookTypeRepository.deleteById(id);
            map.put("msg", ResultEnum.SUCCESS.getMsg());
        }
        return new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);
    }

    /**
     * edit the category's name by id
     * @title BookController.java
     * @param typeid, typename
     * @return java.lang.String
     * @author 毛文杰
     * @method name editCategory
     * @date 10:21 PM. 10/19/2018
     */
    @PostMapping("edit_category")
    public String editCategory(String typeid, String typename){
        Integer Id = Integer.parseInt(typeid);
//        BookType bookType = bookTypeRepository.getOne(Id);
//        // !!!!!不能使用save方式修改——no session error
//        bookType.setTypeName(typename);
//        bookTypeRepository.save(bookType);
        bookTypeRepository.updateTypeById(typename, Id);
        return "redirect:book_category_add";
    }

    /**
     * add a new category
     * @title BookController.java
     * @param categoryname
     * @return java.lang.String
     * @author 毛文杰
     * @method name addCategory
     * @date 10:24 PM. 10/19/2018
     */
    @PostMapping("add_category")
    public ResponseEntity<Map<String,Object>> addCategory(String categoryname){
        Map<String,Object> map = new HashMap<String,Object>();
        if(bookTypeRepository.findByTypeName(categoryname).size()>0){
            map.put("msg", ResultEnum.ALSO_HAS_TYPE_ERROR.getMsg());
        }else {
            BookType bookType = new BookType();
            bookType.setTypeName(categoryname);
            bookTypeRepository.save(bookType);
            map.put("msg", ResultEnum.SUCCESS.getMsg());
        }
        return new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);
    }

    /*--------------------------------目录管理结束----------------------------------------*/


    /*--------------------------------图书位置管理----------------------------------------*/

    @GetMapping("book_position")
    public String gotoBookPositionManage(Model model){
        model.addAttribute("place", bookPositionRepository.findAll());
        return "book_position";
    }

    /**
     * add a book position to database
     * @title BookController.java
     * @param placename
     * @return org.springframework.http.ResponseEntity<java.util.Map<java.lang.String,java.lang.Object>>
     * @author 毛文杰
     * @method name addPosition
     * @date 6:12 PM. 10/20/2018
     */
    @PostMapping("add_position")
    public ResponseEntity<Map<String,Object>> addPosition(String placename){
        Map<String,Object> map = new HashMap<String,Object>();
        if(bookPositionRepository.findByPlace(placename).size()>0){
            map.put("msg", ResultEnum.ALSO_HAS_TYPE_ERROR.getMsg());
        }else{
            BookPosition bookPosition = new BookPosition();
            bookPosition.setPlace(placename);
            bookPositionRepository.save(bookPosition);
            map.put("msg", ResultEnum.SUCCESS.getMsg());
        }
        return new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);
    }

    /**
     * edit the position from layer
     * @title BookController.java
     * @param placeid, placename
     * @return java.lang.String
     * @author 毛文杰
     * @method name editPosition
     * @date 6:15 PM. 10/20/2018
     */
    @PostMapping("edit_position")
    public String editPosition(String placeid, String placename){
        Integer Id = Integer.parseInt(placeid);
        bookPositionRepository.updatePositionById(placename, Id);
        return "redirect:/book_position";
    }

    /**
     * delete a position
     * @title BookController.java
     * @return org.springframework.http.ResponseEntity<java.util.Map<java.lang.String,java.lang.Object>>
     * @author 毛文杰
     * @method name deletePosition
     * @date 7:36 PM. 10/20/2018
     */
    @PostMapping("bookposition/{id}")
    public ResponseEntity<Map<String,Object>> deletePosition(@PathVariable("id") Integer id) {
        Map<String,Object> map = new HashMap<String,Object>();
        //查看是否有其他书籍有此位置
        if(bookRepository.findByBookPosition(id).size()>0) {
            map.put("msg", ResultEnum.CAN_NOT_DEL_POS.getMsg());
        }else{
            bookPositionRepository.deleteById(id);
            map.put("msg", ResultEnum.DEL_POS_SUCCESS.getMsg());
        }
        return new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);
    }

    /*------------------------------图书位置管理结束--------------------------------------*/

    /*----------------------------------图书删除记录--------------------------------------*/

    /**
     * 首次进入页面显示第一页
     * @title BookController.java
     * @param model
     * @return java.lang.String
     * @author 毛文杰
     * @method name gotoBookDelRdc
     * @date 3:26 PM. 10/25/2018
     */
    @GetMapping("bookDelRecord")
    public String gotoBookDelRdc(Model model){
        int currpage = 1;
        int pages = 10;//自定义pagesize
        totalcount = bookDelRecordRepository.findAll().size();
        model.addAttribute("totalcount", totalcount);
        Integer totalPages = (totalcount + pagesize - 1)/pages;
        model.addAttribute("totalpages", totalPages);

        List<BookDelRecord> bookDelRecords = bookDelService.getPageSort(currpage, pages).getContent();
        model.addAttribute("deleteRecord", bookDelRecords);
        if(totalcount == 0)
            currpage = 0;
        model.addAttribute("currpage", currpage);
        return "book_deleteRecord";
    }

    /**
     * 分页显示删除记录
     * @title BookController.java
     * @param currpage, model
     * @return java.lang.String
     * @author 毛文杰
     * @method name delRecordPaging
     * @date 3:30 PM. 10/25/2018
     */
    @GetMapping("bookdelrecord_page")
    public String delRecordPaging(@RequestParam(value = "currpage") Integer currpage, Model model){
        int pages = 10;
        totalcount = bookDelRecordRepository.findAll().size();
        model.addAttribute("totalcount", totalcount);
        Integer totalPages = (totalcount + pagesize - 1)/pages;
        model.addAttribute("totalpages", totalPages);

        if(currpage == 0)
            currpage = 1;
        if(currpage == totalPages+1)
            currpage = totalPages;
        //获得每页的数据
        List<BookDelRecord> bookDelRecords = bookDelService.getPageSort(currpage, pages).getContent();
        //logger.info("currpage={}",currpage);

        //放在model
        model.addAttribute("deleteRecord", bookDelRecords);
        model.addAttribute("currpage",currpage);
        return "book_deleteRecord";
    }

    /*----------------------------------图书删除记录--------------------------------------*/


    /*---------------图书借阅详情------------------------------*/

    @GetMapping("/bookBorrowRecord")
    public String gotoDetail(Integer bookid, Model model){
        List<BorrowRecord> bookRecords = borrowRecordRepository.findAllByBookId(bookid);
        model.addAttribute("borrowRecords", bookRecords);
        return "borrowBookList";
    }

    /*-------------------------------------展示barcode-----------------------------------*/
    /**
     * 展示图书barcode的controller
     * @title ReaderController.java
     * @param
     * @return
     * @author 毛文杰
     * @method name
     * @date 12:16 PM. 11/6/2018
     */
    @PostMapping("view_barcode/{bookid}")
    @ResponseBody
    public String show_barcodeimg(@PathVariable("bookid") Integer bookid){
        String name = "static/barcodeimages/"+String.valueOf(bookid)+".png";
        //把图片附加到末尾，直接显示图片
        String img = "this book's barcode is: <input class='btn btn-default' type='button' value='print' onclick='doPrint()'><br><br><!--startprint--><img id='barcodeimg' alt='barcode' src='"+name+"'><!--endprint-->";
        return img;
    }
}

