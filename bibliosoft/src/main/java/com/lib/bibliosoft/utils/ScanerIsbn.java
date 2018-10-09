package com.lib.bibliosoft.utils;
import com.alibaba.fastjson.JSONObject;
import com.lib.bibliosoft.entity.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * @Author: 毛文杰
 * @Description:
 * @Date: Created in 12:19 PM. 10/9/2018
 * @Modify By: maowenjie
 */
public class ScanerIsbn {

    private static Logger logger = LoggerFactory.getLogger(ScanerIsbn.class);

    /**
     *@Title: ScanerIsbn.java
     *@Params: isbn
     *@Return: Book
     *@Author: 毛文杰
     *@Description: get the detail info by isbn
     *@Date: 2:10 PM. 10/9/2018
     */
    public static Book getBookInfoByIsbn(String isbn) {
//        isbn = "9787806719633";
        String url = "https://api.douban.com/v2/book/isbn/:" + isbn;

        String json = loadJSON(url);
        JSONObject jsonObject = JSONObject.parseObject(json);
        String sauthor = jsonObject.getString("author");
        String publisher = jsonObject.getString("publisher");
        String image = jsonObject.getString("image");
        String sprice = jsonObject.getString("price").replace("元","");
        float price = Float.parseFloat(sprice);
        String id = jsonObject.getString("id");
        Integer bookid = Integer.parseInt(id);
        String title = jsonObject.getString("title");
        String desc = jsonObject.getString("summary");
        //String risbn = jsonObject.getString("isbn13");
        String []author = sauthor.split("\"");

        logger.info("bookId={},bookAuthor={},bookPublisher={},bookImage={},bookPrice={},bookTitle={}",id,author[1],publisher,image,price,title);

        //Create a new book, add some attributes first, then pass it to the controller.
        Book book = new Book();
        book.setBookAuthor(author[1]);
        book.setBookDesc(desc);
        book.setBookId(bookid);
        book.setBookIsbn(isbn);
        book.setBookName(title);
        book.setBookImg(image);
        book.setBookPrice(price);
        book.setBookPublisher(publisher);
        return book;
    }
    /**
     *@Title: ScanerIsbn.java
     *@Params: url
     *@Return: json String
     *@Author: 毛文杰
     *@Description: 通过传入调用豆瓣书籍数据库的url来获得书籍信息，转换成String返回
     *@Date: 2:00 PM. 10/9/2018
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
