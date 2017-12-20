package com.qa.demo.utils.trainingcorpus;
import com.qa.demo.conf.FileConfig;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hyh on 2017/8/20.
 * 从类别-属性文件中读取所有属性以及类别对应的属性;
 */
public class ExtractInfo {
    private Map<String,HashSet<String>> category;
    private String dir_name;
    private Vector<String> ver;
    public BufferedWriter out = null;

    public ExtractInfo(String dir_name) {
        category=new HashMap<String,HashSet<String>>();
        this.dir_name = dir_name;
        ver=new Vector<String>();    //用做堆栈
    }



    //读取文件
    public  void readInfo(String filePath)
    {

        try {

            String encoding="utf-8";
            File file=new File(filePath);
            if(file.isFile() && file.exists()){ //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file),encoding);//考虑到编码格式

                BufferedReader bufferedReader = new BufferedReader(read);
                String line=null;
                while(( line=bufferedReader.readLine()) != null) {
                    ArrayList<String> arrayList=regxChinese(line);
                    if(category.containsKey(arrayList.get(0)))
                    {
                        HashSet<String> hashSet=category.get(arrayList.get(0));
                        hashSet.add(arrayList.get(1));
                        category.put(arrayList.get(0),hashSet);
                    }
                    else
                    {
                        HashSet<String> hashSet=new HashSet<String>();
                        hashSet.add(arrayList.get(1));
                        category.put(arrayList.get(0),hashSet);
                    }

                }

                read.close();


            }else{
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
    }

    //读取某一个文件夹下的所有文件，并将其写入文件中,返回所有类的全部属性
    public HashSet<String> getList() throws IOException {


        ver.add(dir_name);
        while (ver.size() > 0) {
            File[] files = new File(ver.get(0).toString()).listFiles();    //获取该文件夹下所有的文件(夹)名
            ver.remove(0);

            int len = files.length;
            for (int i = 0; i < len; i++) {
                String tmp = files[i].getAbsolutePath();
                if (files[i].isDirectory())    //如果是目录，则加入队列。以便进行后续处理
                    ver.add(tmp);
                else {
                    System.out.println(tmp);
                    readInfo(tmp);

                }
            }
        }
        //写入txt的路径
        writeTxtFile(FileConfig.FILE_CATEGORY);
        HashSet<String> hashSet=writeTxtAttributeFile(FileConfig.FILE_ATTRIBUTE);
        return hashSet;
    }

    //识别中文字符
    public ArrayList<String> regxChinese(String source)
    {
        ArrayList<String> chineseList=new ArrayList<String>();
        String reg_charset = "([\u4E00-\u9FA5]*)";
        Pattern p = Pattern.compile(reg_charset);
        Matcher m = p.matcher(source);
        while (m.find())
        {
            String chinese=m.group(0).replaceAll(" ","");
            if(!chinese.equals(""))
            {
                chineseList.add(chinese);
            }

        }
        return chineseList;
    }

    //写入各个类及其属性文件
    public void writeTxtFile(String filePath) throws IOException
    {
        out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, true)));
        for(String catego:category.keySet())
        {

            String file="<"+catego+","+"<";
            HashSet<String> hashSet=category.get(catego);
            for(String value:hashSet)
            {
                file=file+value+",";
            }
            file=file+">>"+"\r\n";

            out.write(file);
        }
        out.close();
    }

    //统计全部属性
    public HashSet<String> writeTxtAttributeFile(String filePath) throws IOException {
        HashSet<String> attributeSet=new HashSet<String>();
        out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, true)));
        for(String catego:category.keySet())
        {

            HashSet<String> hashSet=category.get(catego);
            for(String value:hashSet)
            {
                attributeSet.add(value);
            }



        }

        for(String value:attributeSet)
        {
            value=value+"\r\n";
            out.write(value);
        }
        out.close();
        return attributeSet;
    }




}
