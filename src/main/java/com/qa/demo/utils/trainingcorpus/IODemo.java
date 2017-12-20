/*
 IO读取
 功能：将包含实体的nt文件读取到一个统一的txt文件中
 说明：读取输出的文件中含有中文，所以将文件的编码格式设置为UTF-8无BOM
 时间：2017/8/20/12:57
 作者：任乔牧
 */
package com.qa.demo.utils.trainingcorpus;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IODemo {
    public static void main(String[] args) {
        IODemo IO = new IODemo();
        try {
            String source = "E:\\Task\\Question Answering\\material\\agriculture_kg_data\\entity\\";
            source = source + "kbfile\\zhwiki_entitiy_properties.nt";
            //baidubaike_entitiy_properties
            //baidubaike_extra_entitiy_properties
            //hudong_extra_entitiy_properties
            //hudongbaike_entitiy_properties
            //zhwiki_entitiy_properties
            //String source = "E:\\Task\\Question Answering\\data\\entity_hudong.txt";
            String to = "E:\\Task\\Question Answering\\data\\agriculturebaike\\attribute_filter\\property.txt";
            IO.demo(source , to );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void demo(String source,String to)throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(source),"UTF-8"));
        //使用InputStreamReader代替FileReader，解决UTF-8的编码问题
        OutputStreamWriter bw = new OutputStreamWriter(new FileOutputStream(to,true),"UTF-8");
        //true追加写文件，false覆盖重写文件
        Pattern pattern = Pattern.compile("<http://zhishi.me/zhwiki/property/(.*?)>");
        String s;
        long start = System.currentTimeMillis();
        while((s = br.readLine())!=null){
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()){
                System.out.println(matcher.group(0));
                bw.write(matcher.group(0));
                bw.write("\r\n");
            }
			/*
			System.out.println(s);
			bw.write(s);
			bw.write("\r\n");
			*/
        }
        long end = System.currentTimeMillis();
        System.out.println("总共用时"+(end-start)+"毫秒");
        br.close();
        bw.close();
    }
}
