package com.qa.demo.utils.w2v;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nlpcn.commons.lang.util.logging.Log;
import org.nlpcn.commons.lang.util.logging.LogFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static com.qa.demo.conf.FileConfig.W2V_vector;

/**
 *  Created time: 2018_01_05
 *  Author: Devin Hua
 *  Function description:
 *  To manipulate word vector to do NLP tasks.
 */

public class Word2VecGensimModel {

    //单例模式，全局访问从三元组生成问题中分割出来的模板库；
    private static Word2VecGensimModel uniqueInstance;
    public static final Log LOG = LogFactory.getLog(Word2VecGensimModel.class);
    private Word2Vec vec;
    private int vecLength = 0;

    private Word2VecGensimModel() throws FileNotFoundException, UnsupportedEncodingException {

        File gModel = new File(W2V_vector);
        this.LOG.info("Loading gensim model...");
        setVec(WordVectorSerializer.readWord2VecModel(gModel));
        setVecLength(vec.getWordVector("农业").length);
        this.LOG.info("The number of the trained words is: " + this.vec.vocab().numWords());
        this.LOG.info("Reading word vector is done.\n");
    }

    public int getVecLength() {
        return vecLength;
    }

    public Word2Vec getVec() {
        return vec;
    }

    public void setVec(Word2Vec vec) {
        this.vec = vec;
    }

    public void setVecLength(int vecLength) {
        this.vecLength = vecLength;
    }

    public static synchronized Word2VecGensimModel getInstance() throws FileNotFoundException, UnsupportedEncodingException {
        if(uniqueInstance==null)
        {
            uniqueInstance = new Word2VecGensimModel();
        }
        return uniqueInstance;
    }

    public double calcWordSimilarity(String word1, String word2)
    {
        return calcVecSimilarity(getWordVector(word1), getWordVector(word2));
    }

    public double[] getWordVector(String word)
    {
        double[] wordVector = vec.getWordVector(word);
        if(wordVector==null)
            wordVector = new double[this.getVecLength()];
        return wordVector;
    }

    public double calcVecSimilarity(double[] vec1, double[] vec2)
    {
        if(vec1==null||vec1.length==0)
            return 0.0;
        if(vec2==null||vec2.length==0)
            return 0.0;

        int length1 = vec1.length;
        int length2 = vec2.length;
        if(length1!=length2)
            return 0;

        double similarity = 0;
        double normOfVec1 = 0;
        double normOfVec2 = 0;

        for(int i=0;i<length1;i++)
        {
            similarity+=vec1[i]*vec2[i];
            normOfVec1+=Math.pow(vec1[i], 2);
            normOfVec2+=Math.pow(vec2[i], 2);
        }
        if(normOfVec1==0||normOfVec2==0)
            similarity = 0.0;
        else
            similarity = similarity/(Math.sqrt(normOfVec1)*Math.sqrt(normOfVec2));
        return similarity;
    }

}
