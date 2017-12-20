/*
EntityLinking
Last updated by QM Ren on 2017/9/7
 */
package com.qa.demo.utils.kgprocess;

import com.qa.demo.conf.FileConfig;
import com.qa.demo.utils.TrainingCorpusUtilDriverImpl;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityLinking {

    public String[] doc2array(String Source)throws IOException {
        List<String> listOfString = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(Source),"UTF-8"));
        String s;
        while((s = br.readLine())!=null){
            listOfString.add(s);
        }
        br.close();
        String[] string = listOfString.toArray(new String[listOfString.size()]);
        return string;
    }

    public  List<Map<String, Object>> Linking(String[] Entity, String[] QuestionArray){
        List<String> HouxuanEntity = new ArrayList<String>();
        List<String> FinalEntity  = new ArrayList<String>();
        List<Map<String,Object>> ResultList = new ArrayList<>();
        Map<String,Object> LinkingMap = new HashMap<>();
        int ID = 0;
        for(String element1:QuestionArray){
            int flag = 0;
            ID = ID + 1;
            for(String element2:Entity){
                boolean retval = element1.contains(element2);
                if(retval == true){
                    flag = 1;
                }
            }
            if(flag == 1){
                HouxuanEntity = new ArrayList<String>();
                for(String element3:Entity){
                    boolean retval = element1.contains(element3);
                    if(retval){
                        HouxuanEntity.add(element3);
                    }
                }
                //从候选实体中挑选出最终的实体
                FinalEntity  = new ArrayList<String>();
                for(String element4:HouxuanEntity){
                    int contain_flag = 0;
                    for(String element5: HouxuanEntity){
                        boolean retval = element5.contains(element4);
                        if(retval){
                            contain_flag = contain_flag + 1;
                        }
                    }
                    if(contain_flag == 1){
                        FinalEntity.add(element4);
                    }
                }
            }
            LinkingMap = new HashMap<>();
            String string = "Question"+ID;
            LinkingMap.put(string,element1);
            string = "HouxuanEntity"+ID;
            LinkingMap.put(string,HouxuanEntity);
            string = "FinalEntity"+ID;
            LinkingMap.put(string,FinalEntity);
            ResultList.add(LinkingMap);
        }
        return ResultList;
    }

    public void ListMap2doc(List<Map<String, Object>> LinkingList,String To)throws  IOException{
        OutputStreamWriter bw = new OutputStreamWriter(new FileOutputStream(To, false), "UTF-8");
        int ID = 0;
        for(Map<String,Object> element:LinkingList){
            ID = ID + 1;
            String QuestionString = " ";
            QuestionString = element.get("Question"+ID).toString();
            bw.write("<"+QuestionString+",");
            List<String> HouxuanEntity = new ArrayList<String>();
            HouxuanEntity = (ArrayList<String>) element.get("HouxuanEntity"+ID);
            bw.write("<");
            int flag1 = 0;
            for(String houxaun:HouxuanEntity){
                if(flag1 >= 1){
                    bw.write(",");
                }
                bw.write(houxaun);
                flag1 = flag1 + 1;
            }
            bw.write(">,<");
            List<String> FinalEntity = new ArrayList<String>();
            FinalEntity = (ArrayList<String>) element.get("FinalEntity"+ID);
            int flag2 = 0;
            for(String zuizhong:FinalEntity){
                if(flag2 >= 1){
                    bw.write(",");
                }
                bw.write(zuizhong);
                flag2 = flag2 + 1;
            }
            bw.write(">>\r\n");
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
