/*
PropertyLinking
Last updated by QM Ren on 2017/9/7
 */
package com.qa.demo.utils.kgprocess;

import com.qa.demo.conf.FileConfig;
import com.qa.demo.dataStructure.Question;
import com.qa.demo.questionAnalysis.QuestionAnalysisDriver;
import com.qa.demo.questionAnalysis.QuestionAnalysisDriverImpl;
import com.qa.demo.utils.TrainingCorpusUtilDriverImpl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;

public class PropertyLinking {

    public List<Map<String,Object>> QuestionWithProperty(String[] QuestionArray,String[] Property){
        Question question = new Question();
        QuestionAnalysisDriver q = new QuestionAnalysisDriverImpl();
        List<String>  SegmentationList = new ArrayList<>();
        List<String> PropertyList = new ArrayList<>();
        List<Map<String,Object>> ResultList = new ArrayList<>();
        Map<String,Object> ResultMap = new HashMap<String, Object>();
        int ID = 0;
        for(String element1:QuestionArray){
            int flag = 0;
            SegmentationList.clear();
            PropertyList = new ArrayList<>();
            question.setQuestionString(element1);
            q.segmentationQuestion(question);
            //todo 只有一个分词结果;
            Iterator it = question.getQuestionToken().entrySet().iterator();
            Map.Entry entry = (Map.Entry)it.next();
            SegmentationList = (ArrayList<String>)entry.getValue();
            for(String element2:SegmentationList){
                for(String element3:Property){
                    boolean retval = element2.trim().equals(element3.trim());
                    if(retval){
                        flag = 1;
                        PropertyList.add(element3);
                    }
                }
            }
            if(flag == 1){
                ID = ID + 1;
                ResultMap = new HashMap<String, Object>();
                ResultMap.put("Question"+ID,element1);
                ResultMap.put("Property"+ID,PropertyList);
                ResultList.add(ResultMap);
            }
        }
        return ResultList;
    }

    public void QuetionWithPropertyToFile(List<Map<String,Object>> List,String To)throws IOException{
        OutputStreamWriter bw = new OutputStreamWriter(new FileOutputStream(To, false), "UTF-8");
        int ID = 0;
        for(Map<String,Object> element1:List){
            ID = ID + 1;
            String string = "Question"+ID;
            String QuestionString = element1.get(string).toString();
            List<String> PropertyList = new ArrayList<>();
            string = "Property"+ID;
            PropertyList = (ArrayList<String>) element1.get(string);
            bw.write("<"+QuestionString);
            for(String element2:PropertyList){
                bw.write(","+element2);
            }
            bw.write(">\r\n");
        }
        bw.close();
    }

    public List<Map<String,Object>> PropertyWithQuestion(String[] QuestionArray,String[] Property){
        Question question = new Question();
        QuestionAnalysisDriver q = new QuestionAnalysisDriverImpl();
        List<String> QuestionList = new ArrayList<>();
        List<String> SegmentationList = new ArrayList<>();
        List<Map<String,Object>> ResultList = new ArrayList<>();
        Map<String,Object> ResultMap = new HashMap<String, Object>();
        int ID = 0;
        for(String element1:Property){
            QuestionList = new ArrayList<>();
            int flag = 0;
            for(String element2:QuestionArray){
                SegmentationList.clear();
                question.setQuestionString(element2);
                q.segmentationQuestion(question);
                //todo 只有一个分词结果;
                Iterator it = question.getQuestionToken().entrySet().iterator();
                Map.Entry entry = (Map.Entry)it.next();
                SegmentationList = (ArrayList<String>)entry.getValue();
                for(String element3:SegmentationList){
                    boolean retval = element1.trim().equals(element3.trim());
                    if(retval){
                        QuestionList.add(element2);
                        flag = 1;
                    }
                }
            }
            if(flag == 1){
                ID = ID + 1;
                ResultMap = new HashMap<String, Object>();
                ResultMap.put("Property"+ID,element1);
                ResultMap.put("Question"+ID,QuestionList);
                ResultList.add(ResultMap);
            }
        }
        return ResultList;
    }

    public void PropertyWithQuestionToFile(List<Map<String,Object>> List,String To)throws IOException{
        OutputStreamWriter bw = new OutputStreamWriter(new FileOutputStream(To, false), "UTF-8");
        int ID = 0;
        for(Map<String,Object> element1:List){
            ID = ID + 1;
            String PropertyString = element1.get("Property"+ID).toString();
            List<String> QuestionList = new ArrayList<>();
            QuestionList = (ArrayList<String>) element1.get("Question"+ID);
            bw.write("<"+PropertyString);
            for(String element2:QuestionList){
                bw.write(","+element2);
            }
            bw.write(">\r\n");
        }
        bw.close();
    }


    public List<String> QuestionWithoutProperty(String[] QuestionArray,String[] Property){
        Question question = new Question();
        QuestionAnalysisDriver q = new QuestionAnalysisDriverImpl();
        List<String>  SegmentationList = new ArrayList<>();
        List<String> PropertyList = new ArrayList<>();
        List<String> ResultList = new ArrayList<>();
        int ID = 0;
        for(String element1:QuestionArray){
            int flag = 0;
            SegmentationList.clear();
            PropertyList = new ArrayList<>();
            question.setQuestionString(element1);
            q.segmentationQuestion(question);
            //todo 只有一个分词结果;
            Iterator it = question.getQuestionToken().entrySet().iterator();
            Map.Entry entry = (Map.Entry)it.next();
            SegmentationList = (ArrayList<String>)entry.getValue();
            for(String element2:SegmentationList){
                for(String element3:Property){
                    boolean retval = element2.trim().equals(element3.trim());
                    if(retval){
                        flag = 1;
                    }
                }
            }
            if(flag == 0){
                ResultList.add(element1);
            }
        }
        return ResultList;
    }

    public void QuetionWithoutPropertyToFile(List<String> List,String To)throws IOException{
        OutputStreamWriter bw = new OutputStreamWriter(new FileOutputStream(To, false), "UTF-8");
        for(String element:List){
            bw.write("<"+element+">\r\n");
        }
        bw.close();
    }


    public List<String> PropertyWithoutQuestion(String[] QuestionArray,String[] Property){
        Question question = new Question();
        QuestionAnalysisDriver q = new QuestionAnalysisDriverImpl();
        List<String> SegmentationList = new ArrayList<>();
        List<String> ResultList = new ArrayList<>();
        for(String element1:Property){
            int flag = 0;
            for(String element2:QuestionArray){
                SegmentationList.clear();
                question.setQuestionString(element2);
                q.segmentationQuestion(question);
                //todo 只有一个分词结果;
                Iterator it = question.getQuestionToken().entrySet().iterator();
                Map.Entry entry = (Map.Entry)it.next();
                SegmentationList = (ArrayList<String>)entry.getValue();
                for(String element3:SegmentationList){
                    boolean retval = element1.trim().equals(element3.trim());
                    if(retval){
                        flag = 1;
                    }
                }
            }
            if(flag == 0){
                ResultList.add(element1);
            }
        }
        return ResultList;
    }

    public void PropertyWithoutQuestionToFile(List<String> List,String To)throws IOException {
        OutputStreamWriter bw = new OutputStreamWriter(new FileOutputStream(To, false), "UTF-8");
        for (String element : List) {
            bw.write("<" + element + ">\r\n");
        }
        bw.close();
    }

    public List<String> getQuestion(){
        HashMap<Integer, HashMap<String, String>> QuestionAnswer = new HashMap<Integer, HashMap<String, String>>();
        List<Map<String, String>> List = new ArrayList<Map<String, String>>();
        List<String> QuestionList = new ArrayList<>();
        TrainingCorpusUtilDriverImpl T = new TrainingCorpusUtilDriverImpl();
        QuestionAnswer = T.getQuestionAnswerPairFromFile(FileConfig.FILE_FAQ);
        for (HashMap.Entry<Integer, HashMap<String, String>> entry : QuestionAnswer.entrySet()) {
            List.add(entry.getValue());
        }
        for (Map<String, String> element : List) {
            QuestionList.add(element.get("QUESTION").toString());
        }
        return QuestionList;
    }

}
