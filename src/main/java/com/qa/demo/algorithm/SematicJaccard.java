package com.qa.demo.algorithm;

import com.qa.demo.utils.w2v.Word2VecGensimModel;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author J.Y.Zhang
 * @create 2018-06-10
 * Function description:
 **/
public class SematicJaccard {
    public static double semJaccard(String questionString, String candidatePredict){
        Word2VecGensimModel w2vModel = null;
        try {
            w2vModel = Word2VecGensimModel.getInstance();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 需要把语句拆成3-gram 这里先暂时这样
        String[] questionStringArr = questionString.split(" ");
        String[] candidatePredictArr = candidatePredict.split(" ");

        int len1 = questionStringArr.length;
        int len2 = candidatePredictArr.length;

        // 构造相似性矩阵：以句子长度比较短的句子集合元素作为矩阵纵坐标，较长的句子集合元素作为横坐标 暂时不考虑长短
        double[][] matrix = new double[len1][len2];
        for(int i = 0; i < len1; ++i){
            for(int j = 0; j < len2; ++j){
                double temp_score = w2vModel.calcWordSimilarity(questionStringArr[i], candidatePredictArr[j]);
                matrix[i][j] = temp_score;
                System.out.println(i+" "+j+" :"+temp_score);
            }
        }

        // 阈值a=0.6 暂时
        double a = 0.6;
        double max = a;

        double total = 0; // 分子
        // 计算分子
        while(max>=a){
            // step1 找到相似性矩阵S中的当前所有元素中的最高值
            max = 0;
            int[] position = {0,0};
            for(int i = 0; i < len1; ++i){
                for(int j = 0; j < len2; ++j){
                    if(matrix[i][j] > max){
                        max = matrix[i][j];
                        position[0] = i;
                        position[1] = j;
                    }
                }
            }
            // Step 2.如果这个最高值高于阈值a，则累加到Total中，转步骤3
            if(max >= a) total += max;
            // Step 3.把当前矩阵中的最高值对应矩阵横坐标和纵坐标的行及列中所有元素值置为0
            for(int i = 0; i < len1; ++i){
                matrix[i][position[1]] = 0;
            }
            for(int j = 0; j < len2; ++j){
                matrix[position[0]][j] = 0;
            }
        }
        //计算分母
        int m = 0;
        double xx = 0;
        for(int i = 0; i < len1; ++i){
            for(int j = 0; j < len2; ++j){
                if(matrix[i][j]!=0) m +=1;
                xx += max = matrix[i][j];
            }
        }
        double fenmu_total = total + m*(1-xx);



        return 0.0;
    }

    @Test
    public void test() {
        System.out.println("ss");
        semJaccard("电脑 多少 钱","计算机 的 价格");
    }
}



