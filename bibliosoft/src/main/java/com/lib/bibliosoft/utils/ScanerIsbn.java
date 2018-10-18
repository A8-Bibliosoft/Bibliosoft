package com.lib.bibliosoft.utils;
import com.alibaba.fastjson.JSONObject;
import com.lib.bibliosoft.entity.Book;
import com.lib.bibliosoft.entity.BookSort;
import com.lib.bibliosoft.repository.BookRepository;
import com.lib.bibliosoft.repository.BookSortRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 毛文杰
 * @description
 * @date Created in 12:19 PM. 10/9/2018
 * @modify By maowenjie 2:05 PM. 10/14/2018
 */
@Component
public class ScanerIsbn {

    @Autowired
    private BookSortRepository bookSortRepository;
    @Autowired
    private BookRepository bookRepository;

    private static ScanerIsbn scanerIsbn;

    private static Logger logger = LoggerFactory.getLogger(ScanerIsbn.class);

    @PostConstruct
    public void init() {
        scanerIsbn = this;
        scanerIsbn.bookSortRepository = this.bookSortRepository;
        scanerIsbn.bookRepository = this.bookRepository;
        // 初使化时将已静态化的bookIdUtil实例化
    }

    /**
     * @title ScanerIsbn.java
     * @param isbn the book's identifier, but not unique
     * @param num you can add a batch of books one time
     * @return Book
     * @author 毛文杰
     * @description get the detail info by isbn
     * @date 2:10 PM. 10/9/2018
     * @modify 2:05 PM. 10/14/2018
     */
    public static List<Book> getBookInfoByIsbn(String isbn, Integer num, String time, String position, String status, String typeid) {

        /*解析豆瓣数据库返回的json数据*/
        String url = "https://api.douban.com/v2/book/isbn/:" + isbn;
        String json = loadJSON(url);
        JSONObject jsonObject = JSONObject.parseObject(json);
        String sauthor = jsonObject.getString("author");
        String publisher = jsonObject.getString("publisher");
        String image = jsonObject.getString("image");
        String sprice = jsonObject.getString("price").replace("元","");
        //从豆瓣获得的信息价格可能为空
        float price = 0;
        if (sprice == null || "".equals(sprice)){}
        else {
            price = Float.parseFloat(sprice);
        }
        String title = jsonObject.getString("title");
        String desc = jsonObject.getString("summary");
        /*再次从豆瓣获得isbn13位的编码*/
        //String risbn = jsonObject.getString("isbn13");
        /*从豆瓣获得的话，这个也有可能为空！。。。不能处理它这种乱七八糟令人捉摸不透的bug，不管了，以后只接受正版书籍的添加*/
        String author = sauthor.split("\"")[1];
        String id = jsonObject.getString("id");
        /*解析完成*/

        Date t = Date.valueOf(time);
        Integer s = Integer.parseInt(status);
        List<Book> books = new ArrayList<>();
        /*the max value of isbn now*/
        Integer maxbookid = BookIdUtil.getMaxBookId(isbn);
        /*what about bookId? 竟然同一个isbn号对应的bookid也相同...那就递增吧*/
        Integer bookid;
        /*如果之前不存在此isbn*/
        //修改
        if (maxbookid == -1)
            bookid = Integer.parseInt(id);
        else//存在就在原来基础上加一
            bookid = maxbookid+1;

        for (int i=0; i<num; i++){
            //Create a new book, add some attributes
            Book book = new Book();
            book.setRegisterTime(t);
            book.setBookPosition(position);
            book.setBookStatus(s);
            book.setBookAuthor(author);
            book.setBookDesc(desc);
            book.setBookIsbn(isbn);
            book.setBookName(title);
            book.setBookImg(image);
            book.setBookPrice(price);
            book.setBookPublisher(publisher);
            //每次都加一
            book.setBookId(bookid++);
            books.add(book);
        }

        List<BookSort> bs = scanerIsbn.bookSortRepository.findByBookIsbn(isbn);
        if(bs.size() != 0){
            logger.info("BookSort表已有ISBN编号为==={}的书籍，故不插入", isbn);
        }else{
            //书籍分类表
            BookSort bookSort = new BookSort();
            bookSort.setBookAuthor(author);
            bookSort.setBookIsbn(isbn);
            bookSort.setBookName(title);
            bookSort.setTypeId(Integer.parseInt(typeid));
            scanerIsbn.bookSortRepository.save(bookSort);
            scanerIsbn.bookSortRepository.insertTypeId(Integer.parseInt(typeid),isbn);
        }

        logger.info("bookId={},bookAuthor={},bookPublisher={},bookImage={},bookPrice={},bookTitle={}",id,author,publisher,image,price,title);
        return books;
    }
    /**
     *@title ScanerIsbn.java
     *@params url
     *@return json String
     *@author 毛文杰
     *@description 通过传入调用豆瓣书籍数据库的url来获得书籍信息，转换成String返回
     *@date 2:00 PM. 10/9/2018
     */
    public static String loadJSON(String url) {
        StringBuilder json = new StringBuilder();
        try {
            URL u = new URL(url);
            URLConnection yc = u.openConnection();
            //防止乱码
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    yc.getInputStream(), "utf-8"));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                json.append(inputLine);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

//            String result = HttpUtil.se("https://api.douban.com/v2/book/isbn/:" + isbn, "utf-8");
//            //将返回字符串转换为JSON对象
//            JSONObject json=JSONObject.fromObject(result);
//            //得到出版社
//            Publish=json.get("publisher").toString();
//            //得到书名
//            Name=json.get("title").toString();
//            //得到作者，因为得到的是数组，所以要转化
//            JSONArray arrAuthor=JSONArray.fromObject(json.get("author"));
//            Author=arrAuthor.getString(0).toString();
//            //得到价格
//            Price=json.get("price").toString();
//            //将得到的信息存储在集合中
}
