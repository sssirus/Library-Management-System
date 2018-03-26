package com.qa.demo.utils.io;

import com.qa.demo.conf.Configuration;
import com.qa.demo.conf.FileConfig;
import com.qa.demo.dataStructure.Question;
import com.qa.demo.utils.trainingcorpus.ExtractQuestionsFromText;

import java.io.*;
import java.util.*;

/**
 *  Created time: 2017_09_08
 *  Author: Devin Hua
 *  Function description:
 *  The I/O tool.
 */
public class IOTool {

    private static HashMap<String, String> filename_filepath_mapping = new HashMap<>();

    public static void getFile(String path) {
        // get file list where the path has
        File file = new File(path);
        // get the folder list
        File[] array = file.listFiles();

        for (int i = 0; i < array.length; i++) {
            if (array[i].isFile()) {
                // only take file name
                String filename = array[i].getName().trim().split("\\.")[0].trim();
                // only take file path
                String filepath = array[i].getPath();
                filename_filepath_mapping.put(filename, filepath);
            } else if (array[i].isDirectory()) {
                getFile(array[i].getPath());
            }
        }
    }

    public static HashMap<String, String> getFilename_filepath_mapping() {
        return filename_filepath_mapping;
    }

    public static void  printFileNamePathMap(){

        Iterator iterator = filename_filepath_mapping.entrySet().iterator();
        while(iterator.hasNext())
        {
            Map.Entry<String, String> entry =(Map.Entry) iterator.next();
            System.out.println("Filename: "+entry.getKey()+" Filepath: "+entry.getValue());
        }
    }

    public static ArrayList<String> readLinesFromFile(String filepath) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath),"utf-8"));
        String line = "";
        ArrayList<String> list = new ArrayList<>();
        while ((line=br.readLine())!=null){
            line = line.trim();
            if(line!=null && line!="")
                list.add(line);
        }
        br.close();
        return list;
    }

    //从由三元组形成的问题文件中抽取问题做测试；
    public static ArrayList<Question> getQuestionsFromTripletGeneratedQuestionFile()
    {
        ArrayList<String> stringList = new  ArrayList<>();
        ArrayList<Question> questionList = new ArrayList<>();
        try {
            stringList = ExtractQuestionsFromText.readLinesFromFile(FileConfig.QUESTION_FOR_TEST);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(String temp:stringList)
        {
            String[] temps = temp.split(Configuration.SPLITSTRING);
            if(temps.length<3)
                continue;
            Question q = new Question();
            q.setQuestionString(temps[1]);
            questionList.add(q);
            String acturalAnswer = temps[2];
            q.setActuralAnswer(acturalAnswer);
        }
        return questionList;
    }

    //To write assembled question-answer pair into file.
    public static void writeToFile(ArrayList<String> list, String resultFilePath) throws IOException
    {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultFilePath,false), "UTF-8"));
        for(String value:list)
        {
            out.write(value);
        }
        out.close();
    }

    //To concatenate assembled question-answer pair into file.
    public static void concatenateToFile(ArrayList<String> list, String resultFilePath) throws IOException
    {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultFilePath,true), "UTF-8"));
        out.write("\r\t");
        for(String value:list)
        {
            out.write(value);
        }
        out.close();
    }

    /**
     *
     * @param filepath
     * @return
     * @throws IOException
     */
    public static Set<List<String>> parseEncyclopedia(String filepath) throws IOException {
        String str = null;
        FileReader fileReader = new FileReader(filepath);
        BufferedReader br = new BufferedReader(fileReader);

        Set content = new HashSet<List<String>>();

        while((str = br.readLine()) != null){
            List<String> document = new ArrayList<>();
            if(str.startsWith("INSERT")){
                String s = str.split("\\(")[1].split("\\)")[0];
                String[] strs = s.split(",");
                for(int j = 0; j < strs.length; j++){
                    String ss = strs[j];
                    if(j>7 && j<10)
                        document.add(ss);
                }
            }
            content.add(document);
        }
        return content;
    }

}
