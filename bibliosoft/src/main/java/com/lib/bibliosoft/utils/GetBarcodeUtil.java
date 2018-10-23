package com.lib.bibliosoft.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @author 毛文杰
 * @project bibliosoft
 * @description 文件下载相关代码
 * @date Created in 2:58 PM. 10/23/2018
 * @modify By 毛文杰
 */
@Controller
public class GetBarcodeUtil {

    Logger logger = LoggerFactory.getLogger(GetBarcodeUtil.class);

    /**
     * @title GetBarcodeUtil.java
     * @param imageName 要下载的条形码的名字, request, response
     * @return java.lang.String
     * @author 毛文杰
     * @method name downloadImage
     * @date 4:30 PM. 10/23/2018
     */
    @GetMapping(value = "/downloadImage")
    public String downloadImage(String imageName, HttpServletRequest request, HttpServletResponse response) {
//        String imageName = "25862579";
        //所有图片都是以.png为后缀名
        imageName+=".png";
        logger.debug("the imageName is : " + imageName);
        File p = null;
        try {
            p = new File(ResourceUtils.getURL("classpath:").getPath());
            //logger.info(p.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        File file = new File(p.getAbsolutePath(), "static/barcodeimages/" + imageName);
        //logger.info(upload.getAbsolutePath());

        if (file.exists()) {
            response.setContentType("application/force-download");// 设置强制下载不打开
            response.addHeader("Content-Disposition",
                    "attachment;fileName=" + imageName);// 设置文件名
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
                System.out.println("success");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }
}

