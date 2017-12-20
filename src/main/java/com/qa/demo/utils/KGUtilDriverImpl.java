package com.qa.demo.utils;

import com.qa.demo.conf.FileConfig;
import com.qa.demo.dataStructure.Triplet;
import com.qa.demo.utils.kgprocess.EntityLinking;
import com.qa.demo.utils.kgprocess.ExtractRdf;
import com.qa.demo.utils.kgprocess.FromFile;
import com.qa.demo.utils.kgprocess.PropertyLinking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *  Created time: 2017_08_30
 *  Author: Devin Hua
 *  Function description:
 *  The class to implement util driver interface.
 */

public class KGUtilDriverImpl implements KGUtilDriver {

    public List<String> getEntityListFromFile() {
        FromFile f = new FromFile();
        List<String> entity_list = new ArrayList<>();
        //需要将输入、输出文件地址固化到conf/FileConfig中
        f.AddFile(FileConfig.ENTITY_SOURCE_TOTAL, FileConfig.ENTITY_SOURCE_ONE, FileConfig.ENTITY_SOURCE_TWO, FileConfig.ENTITY_SOURCE_THERE, FileConfig.ENTITY_SOURCE_FOUR, FileConfig.ENTITY_SOURCE_FIVE, FileConfig.ENTITY_SOURCE_SIX);
        String Source = FileConfig.ENTITY_SOURCE_TOTAL;
        String To = FileConfig.ENTITY_RESULT;
        try{
            entity_list = f.EntityFromFile(Source,To);
        }catch (IOException e) {
            e.printStackTrace();
        }
        return entity_list;
    }

    public List<?> getPropertyListFromFile() {
        FromFile f = new FromFile();
        List<String> property_list = null;
        f.AddFile(FileConfig.PROPERTY_SOURCE_TOTAL, FileConfig.PROPERTY_SOURCE_ONE, FileConfig.PROPERTY_SOURCE_TWO, FileConfig.PROPERTY_SOURCE_THERE, FileConfig.PROPERTY_SOURCE_FOUR, FileConfig.PROPERTY_SOURCE_FIVE, FileConfig.PROPERTY_SOURCE_SIX);
        String Source = FileConfig.PROPERTY_SOURCE_TOTAL;
        String To = FileConfig.PROPERTY_RESULT;
        try{
            property_list = f.PropertyFromFile(Source,To);
        }catch (IOException e) {
            e.printStackTrace();
        }
        return property_list;
    }

    public List<Map<String, Object>> getEntityLinkingFromFile() {
        EntityLinking E = new EntityLinking();
        List<Map<String,Object>> list = new ArrayList<>();
        List<String> QuestionList = new ArrayList<>();
        QuestionList = E.getQuestion();
        String[] QuestionArray = QuestionList.toArray(new String[QuestionList.size()]);
        String EntitySource = FileConfig.ENTITY_RESULT;
        String To = FileConfig.ENTITYLINKING_RESULT;
        try {
            String[] Entity = E.doc2array(EntitySource);
            list = E.Linking(Entity,QuestionArray);
            E.ListMap2doc(list,To);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<?> getQuestionPropertyLinkingFromFile() {
        EntityLinking E = new EntityLinking();
        PropertyLinking P = new PropertyLinking();
        List<Map<String,Object>> ResultList = new ArrayList<>();
        try {
            String Source = FileConfig.PROPERTY_RESULT;
            String To = FileConfig.QUESTIONWITHPROPERTY_RESULT;
            String[] Property = E.doc2array(Source);
            List<String> QuestionList = new ArrayList<>();
            QuestionList = P.getQuestion();
            String[] QuestionArray = QuestionList.toArray(new String[QuestionList.size()]);
            ResultList = P.QuestionWithProperty(QuestionArray, Property);
            P.QuetionWithPropertyToFile(ResultList, To);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ResultList;
    }

    public List<?> getPropertyQuestionLinkingFromFile() {
        EntityLinking E = new EntityLinking();
        PropertyLinking P = new PropertyLinking();
        List<Map<String,Object>> ResultList = new ArrayList<>();
        try {
            String Source = FileConfig.PROPERTY_RESULT;
            String To = FileConfig.PROPERTYWITHQUESTION_RESULT;
            String[] Property = E.doc2array(Source);
            List<String> QuestionList = new ArrayList<>();
            QuestionList = P.getQuestion();
            String[] QuestionArray = QuestionList.toArray(new String[QuestionList.size()]);
            ResultList = P.PropertyWithQuestion(QuestionArray, Property);
            P.PropertyWithQuestionToFile(ResultList, To);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ResultList;
    }

    public List<?> getQuestionWithoutPropertyFromFile() {
        EntityLinking E = new EntityLinking();
        PropertyLinking P = new PropertyLinking();
        String Source = FileConfig.PROPERTY_RESULT;
        String To = FileConfig.QUESTIONWITHOUTPROPERTY_RESULT;
        List<String> ResultList = new ArrayList<>();
        try{
            String[] Property = E.doc2array(Source);
            List<String> QuestionList = new ArrayList<>();
            QuestionList = P.getQuestion();
            String[] QuestionArray = QuestionList.toArray(new String[QuestionList.size()]);
            ResultList = P.QuestionWithoutProperty(QuestionArray,Property);
            P.QuetionWithoutPropertyToFile(ResultList,To);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResultList;
    }

    public List<?> getPropertyWithoutQuestionFromFile() {
        EntityLinking E = new EntityLinking();
        PropertyLinking P = new PropertyLinking();
        String Source = FileConfig.PROPERTY_RESULT;
        String To = FileConfig.PROPERTYWITHOUTQUESTION_RESULT;
        List<String> ResultList = new ArrayList<>();
        try{
            String[] Property = E.doc2array(Source);
            List<String> QuestionList = new ArrayList<>();
            QuestionList = P.getQuestion();
            String[] QuestionArray = QuestionList.toArray(new String[QuestionList.size()]);
            ResultList = P.PropertyWithoutQuestion(QuestionArray,Property);
            P.PropertyWithoutQuestionToFile(ResultList,To);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResultList;
    }

    public List<Triplet> getObjectPropertyTripletsFromFile() {
        ExtractRdf E = new ExtractRdf();
        List<Triplet> ResultList = new ArrayList<>();
        String Source = FileConfig.PROPERTY_SOURCE_TOTAL;
        String To = FileConfig.RDFOBJ_RESULT;
        try {
            ResultList = E.ObjectRdf(Source);
            E.ObjTripletToFile(ResultList,To);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResultList;
    }

    public List<Triplet> getDatatypePropertyTripletsFromFile() {
        ExtractRdf E = new ExtractRdf();
        List<Triplet> ResultList = new ArrayList<>();
        String Source = FileConfig.PROPERTY_SOURCE_TOTAL;
        String To = FileConfig.RDFDATA_RESULT;
        try {
            ResultList = E.DataRdf(Source);
            E.DataTripletToFile(ResultList,To);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResultList;
    }

}
