package com.qa.demo.inputQuestion;

import com.qa.demo.dataStructure.Question;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * Description: 从命令行中输入问题
 * Created by TT. Wu on 2017/9/3.
 */
public class InputFromConsole implements InputDriver {
    private static Logger LOG = LogManager.getLogger(InputFromConsole.class.getName());

    /**
     * 从命令行中输入单个的问题，封装成Question对象返回。
     * @return Question
     */
    public Question getQuestion() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入问题:");
        String input = scanner.next();
        Question question = new Question(input);
        LOG.info("[info]问题已输入，等待系统处理");
        scanner.close();
        return question;
    }

    /**
     * 从命令行中输入多个问题，封装成Question对象，并返回列表
     * @return
     */
    public List<Question> getQuestions() {
        Scanner scanner = new Scanner(System.in);
        List<Question> questions = new ArrayList<Question>();
        System.out.println("请输入问题，换行表示输入下一题，‘#’结束");
        while(true){
            String input = scanner.next();
            if (input == "" || input == null || input.equals("#")){
                break;
            }else{
                Question question = new Question(input);
                questions.add(question);
            }
        }
        LOG.info("[log info]问题输入完成，等待系统处理");
        scanner.close();
        return questions;
    }
}
