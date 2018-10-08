package com.lib.bibliosoft.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * @Author: 毛文杰
 * @Description:
 * @Date: Created in 11:13 PM. 9/30/2018
 * @Modify By:
 */
public class VerifyCode {

    private static Logger logger = LoggerFactory.getLogger(VerifyCode.class);
    public static final String RANDOMCODEKEY= "RANDOMVALIDATECODEKEY";//放到session中的key

    public static void ran(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        //设置图片宽度和高度
        int width = 150;
        int height = 60;
        //干扰线条数
        int lines = 10;
        //验证码数组
        int[] random = new int[4];
        Random r = new Random();
        BufferedImage b = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = b.getGraphics();
        g.setFont(new Font("宋体", Font.BOLD, 30));
        for (int i = 0; i < 4; i++) {
            int number = r.nextInt(10);
            random[i] = number;
            int y = 10 + r.nextInt(40);// 10~40范围内的一个整数，作为y坐标
            //随机颜色，RGB模式
            Color c = new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255));
            g.setColor(c);
//            g.drawString("" + a, 5 + i * width / 4, y);
            //写验证码
            g.drawString(Integer.toString(number), 5 + i * width / 4, y);
        }
        for (int i = 0; i < lines; i++) {
            //设置干扰线颜色
            Color c = new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255));
            g.setColor(c);
            g.drawLine(r.nextInt(width), r.nextInt(height), r.nextInt(width), r.nextInt(height));
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 4; i++)
            stringBuilder.append(random[i]);
        String s = stringBuilder.toString();
        logger.info("验证码为={}",s);
        //将生成的随机字符串保存到session中
        session.removeAttribute(RANDOMCODEKEY);
        session.setAttribute(RANDOMCODEKEY, s);

        try {
            //清空缓冲
            g.dispose();
            //向文件中写入

//            String imgsrc = "G:\\IntelliJ IDEA\\bibliosoft\\src\\main\\resources\\static\\images\\" + stringBuilder + ".jpg";
            ImageIO.write(b, "jpg", response.getOutputStream());
        } catch (IOException e) {
            logger.error("将内存中的图片通过流动形式输出到客户端失败>>>>", e);
            e.printStackTrace();
        }
    }
}
