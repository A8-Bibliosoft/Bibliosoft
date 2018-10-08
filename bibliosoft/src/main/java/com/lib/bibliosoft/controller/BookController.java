package com.lib.bibliosoft.controller;

import com.lib.bibliosoft.entity.Book;
import com.lib.bibliosoft.entity.Result;
import com.lib.bibliosoft.repository.BookRepository;
import com.lib.bibliosoft.service.impl.BookService;
import com.lib.bibliosoft.utils.FileUtil;
import com.lib.bibliosoft.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Date;
import java.text.ParseException;
import java.util.*;

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

    private final static Logger logger = LoggerFactory.getLogger(BookController.class);

    private Integer totalcount;

    private Integer pagesize = 6;

    private String path;


    /**
     * add a book
     * @return
     */
    @PostMapping("/books")
    @ResponseBody
    public Result<Book> bookAdd(@Valid Book book, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return ResultUtil.error(1, bindingResult.getFieldError().getDefaultMessage());
        }
        return ResultUtil.success(bookRepository.save(book));
    }

    /**
     * find a book by id
     * @param id
     * @return
     */
    @GetMapping("/books/{id}")
    @ResponseBody
    public Book bookFindOne(@PathVariable("id") Integer id){
        return bookRepository.findById(id).orElse(null);
        //the return statement also can write like this:
        //return bookRepository.findById(id).get();
        //When the method does not receive a parameter or the parameter is illegal, an error will be reported.
    }

    /**
     * update a book by id
     * @param id
     * @param position
     * @return
     */
    @PutMapping("/books/{id}")
    @ResponseBody
    public Book updateBook(@PathVariable("id") Integer id,
                           @RequestParam("bookPosition") String position){
        Book book = bookRepository.findById(id).get();
        book.setBookPosition(position);
        return bookRepository.save(book);
    }

    /**
     *@Title: BookController.java
     *@Params: id
     *@Return: ResponseEntity
     *@Author: 毛文杰
     *@Description: delete a book by it's id
     *@Date: 6:10 PM. 10/7/2018
     */
    @PostMapping("/book/{id}")
    public ResponseEntity<Map<String,Object>> bookDelete(@PathVariable("id") Integer id){
        bookRepository.deleteById(id);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("msg", "success");
        return new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);
    }

    /**
     *@Title: BookController.java
     *@Params: isbn
     *@Return: List<Book>
     *@Author: 毛文杰
     *@Description:
     *@Date: 11:14 PM. 9/27/2018
     */
    @GetMapping("/books/isbn/{isbn}")
    @ResponseBody
    public List<Book> findBooksByIsbn(@PathVariable("isbn") String isbn){
        return bookRepository.findBookByBookIsbn(isbn);
    }

//    /**
//     * insert two items to database
//     */
//    @PostMapping("/books/two")
//    public void booksTwo(){
//        bookService.insertTwo();
//    }

    /**
     * get the price of a book，Throw the exception to the ExceptionHandler for processing
     * @param id
     */
    @GetMapping("/books/getPrice/{id}")
    @ResponseBody
    public void getPrice(@PathVariable("id") Integer id) throws Exception {
        bookService.getPrice(id);
    }

    /**
     * list all the books
     * @param model
     * @return
     */
    @GetMapping("/book_list")
    public String list_book(Model model){
        Integer currpage = 1;
        totalcount = bookService.findAll().size();
        model.addAttribute("totalcount", totalcount);
        Integer totalPages = (totalcount + pagesize - 1)/pagesize;
        model.addAttribute("totalpages", totalPages);

        //获得每页的数据
        Iterator<Book> bookIterator = bookService.getPage(currpage, pagesize).iterator();

        logger.info("currpage={}",currpage);
        List<Book> list = new ArrayList<>();
        while(bookIterator.hasNext()) {
            list.add(bookIterator.next());
        }
//        logger.info("list.size = {}",list.size());
//        logger.info("list[0]={}", list.get(0));
        //放在model
        model.addAttribute("books", list);
        model.addAttribute("currpage",currpage);
        return "book_list";
    }

    /**
     * paging search
     * @param currpage
     * @param model
     * @return
     */
    @GetMapping("/book_page")
    public String page_book(@RequestParam(value = "currpage") Integer currpage, Model model){
        totalcount = bookService.findAll().size();
        model.addAttribute("totalcount", totalcount);
        Integer totalPages = (totalcount + pagesize - 1)/pagesize;
        model.addAttribute("totalpages", totalPages);

        if(currpage == 0)
            currpage = 1;
        if(currpage == totalPages+1)
            currpage = totalPages;
        //获得每页的数据
        Iterator<Book> bookIterator = bookService.getPage(currpage, pagesize).iterator();

        logger.info("currpage={}",currpage);
        List<Book> list = new ArrayList<>();
        while(bookIterator.hasNext()) {
            list.add(bookIterator.next());
        }
        logger.info("list.size = {}",list.size());
        //放在model
        model.addAttribute("books", list);
        model.addAttribute("currpage",currpage);
        return "book_list";
    }

    /**
     * search by bookname or ISBN or add time
     * @param model
     * @param bookname
     * @param bookaddtime
     * @return
     */
    @RequestMapping("/book_search")
    public String search_book(Model model, @RequestParam("bookname") String bookname, @RequestParam("bookaddtime") String bookaddtime){
        logger.info("book name==={}, book add time==={}",bookname, bookaddtime);
        List<Book> searchBook = null;
        try {
            searchBook = bookService.searchBookByNameOrAddTime(bookname, bookaddtime);
        } catch (ParseException e) {
            logger.error("Parse Date Error={}", e);
            e.printStackTrace();
        }

        logger.info("查询结果===大小size={}",searchBook.size());
        model.addAttribute("books",searchBook);
        //写死了
        model.addAttribute("currpage",1);
        model.addAttribute("totalcount", searchBook.size());
        Integer totalPages = (searchBook.size() + pagesize - 1)/pagesize;
        model.addAttribute("totalpages", totalPages);
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
     * @param flag
     * @return
     */
    @PostMapping("/edit_book")
    public String book_edit(Integer bookId,String bookName,String bookPosition,
                             String isbn, String price, String author,
                             @RequestParam("form-field-radio1") String status, String flag){
        float fprice = Float.parseFloat(price);
        Integer istatus = Integer.parseInt(status);
        if (flag.equals("edit")){
            Integer id = bookRepository.findBookByBookId(bookId).getId();
            bookService.updateBook(id, bookName, bookPosition, isbn, bookId, fprice, author, istatus);
        }else if(flag.equals("add")){
            Book book = new Book();
            book.setBookId(bookId);
            book.setBookIsbn(isbn);
            book.setBookPrice(fprice);
            book.setBookName(bookName);
            book.setBookPosition(bookPosition);
            book.setBookAuthor(author);
            book.setBookStatus(istatus);
            bookService.addBook(book);
            logger.info("Add book={}",book.toString());
        }
        return "redirect:/book_list";
    }

    /**
     * @Title: BookController.java
     * @param booktitle
     * @param bookcover
     * @param bookisbn
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
    @RequestMapping("/book_addnewbook")
    public ResponseEntity<Map<String,Object>> add_newbook(String booktitle, MultipartFile bookcover, String bookisbn, String bookauthor,
                                                          String bookposition, String bookprice, String bookid, String bookstatus, String bookaddtime,
                                                          String booksummary){
        Book book = new Book();
        Integer Ibookstatus = Integer.parseInt(bookstatus);
        book.setBookStatus(Ibookstatus);
        book.setBookAuthor(bookauthor);
        book.setBookPosition(bookposition);
        book.setBookName(booktitle);
        float Fbookprice = Float.parseFloat(bookprice);
        book.setBookPrice(Fbookprice);
        book.setBookIsbn(bookisbn);
        Integer Ibookid = Integer.parseInt(bookid);
        book.setBookId(Ibookid);
        //java.sql.Date
        Date date = Date.valueOf(bookaddtime);
        book.setRegisterTime(date);
        book.setBookDesc(booksummary);
        bookService.addBook(book);


        // 要上传的目标文件存放路径
        //String localPath = "E:/Develop/Files/Photos";
        File p = null;
        try {
            p = new File(ResourceUtils.getURL("classpath:").getPath());
            logger.info(p.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        File upload = new File(p.getAbsolutePath(),"static/bookimages/");
        logger.info(upload.getAbsolutePath());
        if(!upload.exists())
            upload.mkdirs();
        // 上传成功或者失败的提示
        String msg;
        String ph = ClassUtils.getDefaultClassLoader().getResource("").getPath();
        logger.info("path juedui={}",ph);
        if (FileUtil.upload(bookcover, upload.getAbsolutePath(), bookcover.getOriginalFilename())){
            // 上传成功，给出页面提示
            msg = "Upload Cover success!";
        }else {
            msg = "Upload Cover failed!";
        }

        Map<String,Object> map = new HashMap<String,Object>();
        // 显示图片
        map.put("msg", msg);
        //map.put("fileName", file.getOriginalFilename());
        return new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);
    }

    /**
     * @Title: BookController.java
     * @param bookId
     * @param model
     * @return String -> view page
     * @Author: 毛文杰
     * @Description: show detail information of a book
     * @Date: 6:24 PM. 10/7/2018
     */
    @GetMapping("/book_show/{id}")
    public String show_book(@PathVariable("id") String bookId, Model model){
        Integer bookid = Integer.parseInt(bookId);
        Book book = bookService.getBookById(bookid);
        String sstatus = bookService.findsstatusByid(bookid);
        logger.info("书籍状态 === {}", sstatus);
        model.addAttribute("book", book);
        model.addAttribute("sstatus", sstatus);
        return "book_show";
    }

}

