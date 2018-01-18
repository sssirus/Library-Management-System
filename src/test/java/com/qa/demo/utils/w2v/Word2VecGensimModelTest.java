package com.qa.demo.utils.w2v;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.junit.jupiter.api.Test;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Scanner;


class Word2VecGensimModelTest {
    @Test
    void getVecLength() {

        Word2VecGensimModel w2vModel = null;
        try {
            w2vModel = Word2VecGensimModel.getInstance();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int vec_length;
        Word2Vec vec;
        vec = w2vModel.getVec();

        vec_length = w2vModel.getVecLength();
        System.out.println("The length of word vector is: " + vec_length);

        Collection<String> lst = vec.wordsNearest("计算机", 10);
        System.out.println(lst);
//        WeightLookupTable weightLookupTable = vec.lookupTable();
//        Iterator<INDArray> vectors = weightLookupTable.vectors();
//        INDArray wordVectorMatrix = vec.getWordVectorMatrix("农业");

        System.out.println("农业 " + "计算机： ");
        double cosSim = vec.similarity("农业", "计算机");
        System.out.println("model自带相似度计算器： " + cosSim);
        System.out.println("重新编写计算器： " + w2vModel.calcWordSimilarity("农业","计算机"));

        System.out.println("农业 " + "hua： ");
        System.out.println("model自带相似度计算器： " + vec.similarity("农业", "hua"));
        System.out.println("重新编写计算器： " + w2vModel.calcWordSimilarity("农业","hua"));

        System.out.println("人工智能 " + "计算机： ");
        System.out.println("model自带相似度计算器： " + vec.similarity("人工智能", "计算机"));
        System.out.println("重新编写计算器： " + w2vModel.calcWordSimilarity("人工智能","计算机"));

        System.out.println("出生 " + "生于： ");
        System.out.println("model自带相似度计算器： " + vec.similarity("出生", "生于"));
        System.out.println("重新编写计算器： " + w2vModel.calcWordSimilarity("出生","生于"));

        System.out.println("栽培 " + "种植： ");
        System.out.println("model自带相似度计算器： " + vec.similarity("栽培", "种植"));
        System.out.println("重新编写计算器： " + w2vModel.calcWordSimilarity("栽培","种植"));

        System.out.println("花色 " + "颜色： ");
        System.out.println("model自带相似度计算器： " + vec.similarity("花色", "颜色"));
        System.out.println("重新编写计算器： " + w2vModel.calcWordSimilarity("花色","颜色"));
    }

    @Test
    void getVec() {
    }

    @Test
    void setVec() {
    }

    @Test
    void setVecLength() {
    }

    @Test
    void getInstance() {
    }

}