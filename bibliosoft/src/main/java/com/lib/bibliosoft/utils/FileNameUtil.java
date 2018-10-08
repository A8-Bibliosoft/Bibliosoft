package com.lib.bibliosoft.utils;

import java.util.UUID;

/**
 * @Author: 毛文杰
 * @Description: Set the name of uploaded file
 * @Date: Created in 12:02 PM. 10/7/2018
 * @Modify By:maowenjie
 */
public class FileNameUtil {
    /**
     *@Title: FileNameUtil.java
     *@Params: fileName
     *@Return: String
     *@Author: 毛文杰
     *@Description: Get file suffix
     *@Date: 12:04 PM. 10/7/2018
     */
    public static String getSuffix(String fileName){
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     *@Title: FileNameUtil.java
     *@Params: fileOriginName
     *@Return: String
     *@Author: 毛文杰
     *@Description: Generate a new file name
     *@Date: 12:05 PM. 10/7/2018
     */
    public static String getFileName(String fileOriginName){
        return UUID.randomUUID().toString().replace("-", "") + FileNameUtil.getSuffix(fileOriginName);
    }

}
