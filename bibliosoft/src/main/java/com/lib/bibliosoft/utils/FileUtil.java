package com.lib.bibliosoft.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author 毛文杰
 * @description File upload kit
 * @date Created in 12:07 PM. 10/7/2018
 * @modify By: maowenjie
 */
public class FileUtil {

    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);
    /**
     * @title FileUtil.java
     * @param file 文件
     * @param path 文件存放路径
     * @param fileName 源文件名
     * @return boolean
     * @author 毛文杰
     * @description upload a file
     * @date 12:08 PM. 10/7/2018
     */
    public static boolean upload(MultipartFile file, String path, String fileName){

        // 拼接上生成的新的文件名
        String realPath = path + "/" + FileNameUtil.getFileName(fileName);

        //使用原文件名
        //String realPath = path + "/" + fileName;

        File dest = new File(realPath);

        logger.info("文件保存路径==={}, 文件名字==={}", realPath, dest.getName());

        //判断文件父目录是否存在
        if(!dest.getParentFile().exists()){
            if (dest.getParentFile().mkdir()){
                logger.info("生成新的文件父目录={}",dest.getParentFile().getName());
            }else{
                logger.info("生成新的文件父失败！请检查代码！");
            }
        }

        try {
            //保存文件
            file.transferTo(dest);
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

}
