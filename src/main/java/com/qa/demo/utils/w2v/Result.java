package com.qa.demo.utils.w2v;

import java.util.ArrayList;
import java.util.List;

/**
 * @author J.Y.Zhang
 * @create 2018-03-30
 * Function description:
 * 子字向量对未登录词做处理
 **/
public class Result {
    public String word = "";
    public List<String> subwords_for_check = new ArrayList<String>();
    public int label = 0;
    public double[] vec = null;
}
