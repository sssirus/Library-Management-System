package com.qa.demo.inputQuestion;

import com.qa.demo.dataStructure.Question;

import java.util.List;

/**
 * Description: 提供各种问题输入的接口，如从命令行中获取问题，从文件中读取问题等。
 * Created by TT. Wu on 2017/9/3.
 */
public interface InputDriver {
    /**
     * 获取单个问题
     * @return
     */
    public static Question getQuestion(){
        return null;
    }

    /**
     * 批量输入，获取多个问题
     * @return
     */
    public static List<Question> getQuestions(){
        return null;
    }

}
