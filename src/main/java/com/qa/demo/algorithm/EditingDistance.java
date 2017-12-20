package com.qa.demo.algorithm;

/**
 * Description:
 * Created by T.Wu on 2017/10/15.
 */
public class EditingDistance {
    public static int editDis(String questionString, String target){
        int len1 = questionString.length();
        int len2 = target.length();

        String[] questionCharacters = toArray(questionString);
        String[] targetCharacters = toArray(target);

        //初始化矩阵
        int[][] matrix = new int[len1 + 1][len2 + 1];
        for(int i = 0; i <= len1; ++i){
            matrix[i][0] = i;
        }
        for(int j = 0; j <= len2; ++j){
            matrix[0][j] = j;
        }

        //DP
        for(int i = 1; i <= len1; ++i)
        {
            for(int j = 1; j <= len2; ++j)
            {
                int cost = 1;
//                System.out.println("question:"+i+"  "+questionCharacters[i-1]);
//                System.out.println("target:"+j+"  "+targetCharacters[j-1]);
                if(questionCharacters[i-1].equals(targetCharacters[j-1])){
                    cost = 0;
                }
                int deletion = matrix[i-1][j] + 1;
                int insertion = matrix[i][j-1] + 1;
                int substitution = matrix[i-1][j-1] + cost;
                matrix[i][j] = min(deletion, insertion, substitution);
            }
        }
        int ret = matrix[len1][len2];
        return ret;
    }

    private static String[] toArray(String s){
        String[] strs = new String[s.length()];
        for(int i = 1; i <= s.length(); i++){
            strs[i-1] = s.substring(i-1,i);
        }
        return strs;
    }

    private static int min(int a, int b, int c){
        int t = a<b?a:b;
        return t<c?t:c;
    }

    public static float getRepetitiveRate(String questionStr,String target){
        float editDistance = (float)editDis(questionStr, target);
        float questionLength = (float)questionStr.length();
        float targetLength = (float)target.length();
        return 1-(editDistance/(questionLength+targetLength));
    }
}
