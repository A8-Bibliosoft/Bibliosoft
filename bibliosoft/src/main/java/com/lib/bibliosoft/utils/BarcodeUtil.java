package com.lib.bibliosoft.utils;

import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * @author 毛文杰
 * @project bibliosoft
 * @description 生成条形码的工具类
 * @date Created in 4:11 PM. 10/17/2018
 * @modify By 毛文杰
 */
public class BarcodeUtil {

    /**
     * 生成文件
     * @title BarcodeUtil.java
     * @param msg 信息
     * @return java.io.File
     * @author 毛文杰
     * @method name generateFile
     * @date 4:19 PM. 10/17/2018
     */
    public static File generateFile(String msg) {

        File p = null;
        try {
            p = new File(ResourceUtils.getURL("classpath:").getPath());
            //logger.info(p.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        File upload = new File(p.getAbsolutePath(),"static/barcodeimages/");
        //logger.info(upload.getAbsolutePath());
        if(!upload.exists())
            upload.mkdirs();

        //拼接完整路径
        String path = upload.getAbsolutePath()+"/"+msg+".png";
        String basepath = upload.getAbsolutePath()+"/";
        File file = new File(path);
        try {
            generate(msg, new FileOutputStream(file));
            mergeImage(basepath, file, msg+".png");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /***
     * @title BarcodeUtil.java
     * @param msg 信息
     * @param ous 生成的流
     * @return void
     * @author 毛文杰
     * @method name generate
     * @date 4:13 PM. 10/17/2018
     */
    public static void generate(String msg, OutputStream ous) {

        if (StringUtils.isEmpty(msg) || ous == null) {
            return;
        }

//        Code128Bean bean = new Code128Bean();
        Code39Bean bean = new Code39Bean();

        // 精细度
        final int dpi = 200;
        // module宽度
        final double moduleWidth = UnitConv.in2mm(1.5f / dpi);

        // 配置对象
        bean.setModuleWidth(moduleWidth);
//        bean.setWideFactor(2);
        bean.setBarHeight(6);
        bean.setFontName("微软雅黑");
        bean.setFontSize(2);
        bean.doQuietZone(true);
//        HumanReadablePlacement placement = HumanReadablePlacement.HRP_TOP;
//        bean.setMsgPosition(placement);

        String format = "image/png";
        try {

            // 输出到流
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(ous, format, dpi,
                    BufferedImage.TYPE_BYTE_BINARY, false, 0);
            // 生成条形码
            bean.generateBarcode(canvas,msg);
            //canvas.deviceText("Bibliosoft",10, 10, 2, "微软雅黑", 2, TextAlignment.TA_CENTER);
            // 结束绘制
            canvas.finish();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    public static void main(String args[]){
//        generateFile("123456");
//    }


    /**
     * 合并两张如图片
     * @title BarcodeUtil.java
     * @param file2
     * @return void
     * @author 毛文杰
     * @method name mergeImage
     * @date 8:37 PM. 10/31/2018
     */
    public static void mergeImage(String basepath, File file2, String msg) throws IOException {
        File file1 = new File(basepath, "conn.png");
        BufferedImage image1 = ImageIO.read(file1);
        BufferedImage image2 = ImageIO.read(file2);

        Integer width,height;
        width = image2.getWidth();
        height = image2.getHeight()+image1.getHeight();
        BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // paint both images, preserving the alpha channels
        Graphics g = combined.getGraphics();
        g.drawImage(image1, 0, 0, Color.white,null);
        g.drawImage(image2, 0, image1.getHeight(), Color.white,null);

        // Save as new image
        ImageIO.write(combined, "JPG", new File(basepath, msg));
    }
}
