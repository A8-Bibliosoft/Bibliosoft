package com.lib.bibliosoft.utils;

import java.util.Random;

/**
 * @author 毛文杰
 * @project bibliosoft
 * @description
 * @date Created in 9:16 PM. 11/8/2018
 * @modify By 毛文杰
 */
public class RandomId {

    //生成随机数
    public static String getRandomNum(Integer len){
        Random rand=new Random();//生成随机数
        String rnum="";
        for(int a=0;a<len;a++){
            rnum += rand.nextInt(10);//生成6位数字
        }
        return rnum;
    }
}
