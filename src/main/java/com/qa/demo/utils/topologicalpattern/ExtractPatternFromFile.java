package com.qa.demo.utils.topologicalpattern;

import com.qa.demo.conf.FileConfig;
import com.qa.demo.dataStructure.TopologicalPattern;
import com.qa.demo.dataStructure.TopologicalStructure;
import com.qa.demo.utils.io.IOTool;
import org.nlpcn.commons.lang.util.logging.Log;
import org.nlpcn.commons.lang.util.logging.LogFactory;

import java.io.IOException;
import java.util.ArrayList;

public class ExtractPatternFromFile {

    public static final Log LOG = LogFactory.getLog(ExtractPatternFromFile.class);

    private static ArrayList<TopologicalStructure> topologicalStructureList
            = new ArrayList<>();
    private static ArrayList<TopologicalPattern> topologicalPatternList
            = new ArrayList<>();
    private static ArrayList<ArrayList<String>> predicateMentionList
            = new ArrayList<>();

    private static void readPatternsFromFile()
    {
        ArrayList<String> tp_lines = new ArrayList<>();
        try {
            tp_lines = IOTool.readLinesFromFile(FileConfig.TP_SUBTREE);
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("[error]无法打开记录拓扑模式的文件！");
        }

        ArrayList<String> predicateMention_lines = new ArrayList<>();
        try {
            predicateMention_lines = IOTool.readLinesFromFile(FileConfig.TP_PREDICATE_MENTION);
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("[error]无法打开记录拓扑模式对应谓词指称的文件！");
        }

        for(String tp_line : tp_lines)
        {
            if(!tp_line.contains("序号"))
            {
                String[] tp_line_array = tp_line.split(",");
                if(tp_line_array.length>=3)
                {
                    TopologicalPattern tp = new TopologicalPattern();
                    tp.setRoot_POS(tp_line_array[1].trim());
                    String[] tp_leaves = tp_line_array[2].split(" ");
                    ArrayList<String> leaves = new ArrayList<>();
                    for(String s : tp_leaves)
                        leaves.add(s.trim());
                    tp.setLeaves_POS(leaves);
                    topologicalPatternList.add(tp);
                }
            }
        }

        for(String predicateMention_line : predicateMention_lines)
        {
            if(!predicateMention_line.contains("序号"))
            {
                String[] predicateMention_line_array = predicateMention_line.split(",");
                if(predicateMention_line_array.length >= 2)
                {
                    ArrayList<String> predicateMention = new ArrayList<>();
                    String[] predicateMention_array = predicateMention_line_array[1].split(" ");
                    for(String s : predicateMention_array)
                        predicateMention.add(s.trim());
                    predicateMentionList.add(predicateMention);
                }
            }
        }
    }

    public static ArrayList<TopologicalStructure> getTopologicalStructure()
    {
        readPatternsFromFile();
        if (topologicalPatternList.size() == predicateMentionList.size())
        {
            for (int i = 0; i < topologicalPatternList.size(); i++)
            {
                TopologicalStructure topologicalStructure = new TopologicalStructure();
                topologicalStructure.setTopologicalPattern(topologicalPatternList.get(i));
                topologicalStructure.setPredicate_mention(predicateMentionList.get(i));
                topologicalStructureList.add(topologicalStructure);
            }
        }
        return topologicalStructureList;
    }
}
