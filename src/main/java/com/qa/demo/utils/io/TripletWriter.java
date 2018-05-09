package com.qa.demo.utils.io;

import com.qa.demo.dataStructure.Triplet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class TripletWriter {
    /**
     * 将提取出来的 tripletList 写入到文件里面
     * @param tripletList 提取出来的 tripletList
     * @param is_append 是否为 append 模式
     * @param filepath 文件路径
     * @throws IOException
     */
    public static void write_triplets_to_file(List<Triplet> tripletList, boolean is_append, String filepath) throws IOException {
        File file = new File(filepath);
        if(!is_append){
            if(file.exists() && file.isFile()){
                file.delete();
            }
        }
        if(!file.exists()){
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        String str;
        for(Triplet triplet : tripletList){
            str = "";
            str += triplet.getSubjectURI() + " ";
            str += triplet.getPredicateURI() + " ";
            str += triplet.getObjectURI() + " ";
            str += ".\t\n";
            bufferedWriter.write(str);
        }
        bufferedWriter.close();
    }
}
