package com.qa.demo.utils.test;

import com.qa.demo.conf.FileConfig;
import com.qa.demo.systemController.FaqDemo;
import com.sun.xml.internal.fastinfoset.util.CharArray;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.qa.demo.conf.FileConfig.*;

public class Test {

    private static Logger LOG = LogManager.getLogger(Test.class.getName());

    public static void main(String[] args) throws IOException {

//        Model dataModel = ModelFactory.createDefaultModel();
//        String inputFileName = NT_TRIPLETS;
//        InputStream in = FileManager.get().open(inputFileName);
//        if (in == null)
//        {
//            throw new IllegalArgumentException("File: " + inputFileName + " not found");
//        }
//        dataModel.read(in, "","N3");
//        Resource s = dataModel.getResource("http://zhishi.me/baidubaike/resource/可蒙犬");
//        Property p = dataModel.getProperty("http://zhishi.me/baidubaike/property/体型分类");
//
//        Iterator itr = dataModel.listObjectsOfProperty(s, p);
//        while (itr.hasNext()) {
//            RDFNode object = (RDFNode)itr.next();
//            if (object instanceof Resource)
//            {
//                System.out.println(" 宾语 " + ((Resource) object).getLocalName());
//            }
//
//            else
//                System.out.println(object.toString());
//        }
//        PropertyConfigurator.configure(LOG_PROPERTY);
//        LOG.info("this is info message\r\n");
//        LOG.debug("this is debug message\r\n");
//        LOG.warn("this is warn message\r\n");
//        LOG.error("this is error message\r\n");

        String[] userid = {"aa","bb","cc"};
        List<String> userList = new ArrayList<String>();
        Collections.addAll(userList, userid);
        System.out.println(userList);

        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m;

        String temp = "投资方的";
//        System.out.println(temp.matches("[\\u4E00-\\u9FA5]+"));
        m = p.matcher(temp);
        System.out.println(temp + ": " + m.find());

        temp = "NN";
//        System.out.println(temp.matches("[\\u4E00-\\u9FA5]+"));
        m = p.matcher(temp);
        System.out.println(temp + ": " + m.find());

        temp = "的]]";
//        System.out.println(temp.matches("[\\u4E00-\\u9FA5]+"));
        m = p.matcher(temp);
        System.out.println(temp + ": " + m.find());

        temp = "]]";
        m = p.matcher(temp);
        System.out.println(temp + ": " + m.find());

        temp = "[[是";
        m = p.matcher(temp);
        System.out.println(temp + ": " + m.find());


        String s = "[DEG 的]] [NP [NN 规范] [NN 汉字] [NN 编号]]]]";
        char[] c = s.toCharArray();
        System.out.println(c[5]+": "+ isChinese(c[5]) +c[6] +": "+ isChinese(c[6]));

    }

    public static boolean isChinese(char c) {
        return c >= 0x4E00 &&  c <= 0x9FA5;// 根据字节码判断
    }

}
