package com.pwj.utils;

import android.support.annotation.NonNull;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.Annotation;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.PngImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PdfItextUtil {

    private Document document;

    // savePath:保存pdf的路径
    public PdfItextUtil(String savePath) throws Exception {
        //创建新的PDF文档：A4大小，左右上下边框均为0
//        File file = new File(IpConfig.PATH_DATA  + IpConfig.path_pdf);
//        if (!file.exists()){
//            file.mkdirs();
//        }else {
//            file.delete();
//        }
        document = new Document(PageSize.ROYAL_QUARTO, 50, 50, 0, 0);
        //获取PDF书写器
        PdfWriter.getInstance(document, new FileOutputStream(savePath));
        //打开文档
        document.open();
    }
    public PdfItextUtil(String savePath,String path) throws Exception {
        //创建新的PDF文档：A4大小，左右上下边框均为0
        File file = new File(path);
        if (!file.exists()){
            file.mkdirs();
        }else {
            file.delete();
        }
        document = new Document(PageSize.ROYAL_QUARTO, 50, 50, 0, 0);
        //获取PDF书写器
        PdfWriter.getInstance(document, new FileOutputStream(savePath));
        //打开文档
        document.open();
    }
    public void close() {
        if (document.isOpen()) {
            document.close();
        }
    }

    // 添加图片到pdf中，这张图片在pdf中居中显示
    // imgPath:图片的路径，我使用的是sdcard中图片
    // imgWidth：图片在pdf中所占的宽
    // imgHeight：图片在pdf中所占的高
    public PdfItextUtil addImageToPdfCenterH(@NonNull String imgPath, float imgWidth, float imgHeight) throws IOException, DocumentException {
        //获取图片
        Image img = Image.getInstance(imgPath);
        img.setAlignment(Element.ALIGN_BASELINE);
        img.scaleToFit(imgWidth, imgHeight);
        //添加到PDF文档
        document.add(img);

        return this;
    }

    public PdfItextUtil addPngToPdf(InputStream inputStream) throws DocumentException, IOException {
        Image img = PngImage.getImage(inputStream);
        img.setAlignment(Element.ALIGN_CENTER);
        //添加到PDF文档
        document.add(img);
        return this;
    }

    // 添加文本到pdf中
    public PdfItextUtil addTextToPdf(String content) throws DocumentException {
        Paragraph elements = new Paragraph(content, setChineseFont());
        elements.setAlignment(Element.ALIGN_BASELINE);
//        elements.setIndentationLeft(55);  //设置距离左边的距离
        document.add(elements); // result为保存的字符串
        return this;
    }

    // 添加文本到pdf中
    public PdfItextUtil addTextLinkToPdf() throws DocumentException {
        Paragraph elements = new Paragraph();
        elements.setAlignment(Element.ALIGN_BASELINE);
        Chunk chunk = new Chunk(IpConfig.DOWNLOAD_EXPLAIN, setBlueFont(23));
        chunk.setAnchor("https://a.app.qq.com/o/simple.jsp?pkgname=com.pwj.helloya");
        elements.add(chunk);
        elements.setMultipliedLeading(3);  //设置距离左边的距离
        document.add(elements); // result为保存的字符串
        return this;
    }
    // 添加文本到pdf中
    public PdfItextUtil addTextLinkTitleToPdf(String url) throws DocumentException {
        Paragraph elements = new Paragraph();
        elements.setAlignment(Element.ALIGN_CENTER);
        Chunk chunk = new Chunk(IpConfig.OPEN_BIDDING, setBlueFont(23));
        chunk.setAnchor(url);
        elements.add(chunk);
        elements.setMultipliedLeading(3);  //设置距离左边的距离
        document.add(elements); // result为保存的字符串
        return this;
    }
    // 给pdf添加个标题，居中黑体
    public PdfItextUtil addTitleToPdf(String title) {
        try {
            Paragraph elements = new Paragraph(title, setChineseTiltleFont(25));
            elements.setAlignment(Element.ALIGN_CENTER);
            document.add(elements); // result为保存的字符串
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return this;
    }
    // 给pdf添加个标题，靠左黑体
    public PdfItextUtil addTitleLeftToPdf(String title) {
        try {
            Paragraph elements = new Paragraph(title, setChineseTiltleFont(25));
            elements.setAlignment(Element.ALIGN_LEFT);
            document.add(elements); // result为保存的字符串
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return this;
    }
    private Font setBlueFont(int size) {
        BaseFont bf;
        Font fontChinese = null;
        try {
            // STSong-Light : Adobe的字体
            // UniGB-UCS2-H : pdf 字体
            bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            fontChinese = new Font(bf, size, Font.BOLD, BaseColor.BLUE);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fontChinese;

    }
    private Font setChineseFont() {
        return setChineseFont(23);
    }

    private Font setChineseFont(int size) {
        BaseFont bf;
        Font fontChinese = null;
        try {
            // STSong-Light : Adobe的字体
            // UniGB-UCS2-H : pdf 字体
            bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            fontChinese = new Font(bf, size, Font.NORMAL);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fontChinese;
    }

    private Font setChineseTiltleFont(int size) {
        BaseFont bf;
        Font fontChinese = null;
        try {
            // STSong-Light : Adobe的字体
            // UniGB-UCS2-H : pdf 字体
            bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            fontChinese = new Font(bf, size, Font.BOLD);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fontChinese;
    }
}
