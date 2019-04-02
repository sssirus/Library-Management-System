package com.qa.demo.utils.io;

import com.qa.demo.conf.FileConfig;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.qa.demo.conf.FileConfig.COMPLEMENTKB;
import static com.qa.demo.conf.FileConfig.ENCYCLOPEDIA_CONTENT;
import static org.junit.jupiter.api.Assertions.*;

class IOToolTest {
    @Test
    void getFile() {

        IOTool.getFile(COMPLEMENTKB);
        IOTool.printFileNamePathMap();
    }

    @Test
    void readLinesFromFile() {
    }

    @Test
    void getQuestionsFromTripletGeneratedQuestionFile() {
    }

    @Test
    void writeToFile() {
    }

    @Test
    void parseEncyclopedia() {
    }

    @Test
    void readFile() throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        HashSet<List<String>> content = (HashSet<List<String>>) IOTool.parseEncyclopedia(FileConfig.ENCYCLOPEDIA);
        for(List<String> list: content){
            String line = "";
            for(String s: list){
                System.out.print(s.replace("\'","").trim()+" ");
                line += s.replace("\'","").trim()+" ";
            }
            line = line.trim()+"\r\n";
            lines.add(line);
            System.out.println(" ");
        }
        IOTool.writeToFile(lines, ENCYCLOPEDIA_CONTENT);
    }
}