package com.qa.demo.utils.io;

import com.qa.demo.dataStructure.Triplet;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

class WebServiceAccessorTest {
    @Test
    void query() {

        // 作为载体的三元组
        Triplet triplet = new Triplet();
        // triplet.setSubjectURI("http://zhishi.me/hudongbaike/resource/新疆小麦");
        triplet.setPredicateURI("http://zhishi.me/baidubaike/property/分布区域");
        // triplet.setObjectURI("彼得巴甫洛夫小麦");
        // 存放结果
        List<Triplet> tripletList = null;

        // 进行请求
        /*tripletList = WebServiceAccessor.query(triplet);
        List<String> list = new ArrayList<>();
        list.add("彼得巴甫洛夫小麦");
        list.add("北半球的高寒地带");
        // tripletList = WebServiceAccessor.queryByMultiObjects(list);
        System.out.println(tripletList.size());
        if (tripletList.size() == 0)
            return;
        System.out.println(tripletList.get(tripletList.size() - 1).getSubjectURI());
        System.out.println(tripletList.get(tripletList.size() - 1).getPredicateURI());
        System.out.println(tripletList.get(tripletList.size() - 1).getObjectURI());*/

        List<String> subjects = new ArrayList<>();
        subjects.add("http://zhishi.me/hudongbaike/resource/新疆小麦");
        subjects.add("http://zhishi.me/hudongbaike/resource/CW400");

        tripletList = WebServiceAccessor.queryByMultiSubjects(subjects);
        System.out.println();
        System.out.println(tripletList.size());
        if (tripletList.size() == 0)
            return;
        System.out.println(tripletList.get(tripletList.size() - 1).getSubjectURI());
        System.out.println(tripletList.get(tripletList.size() - 1).getPredicateURI());
        System.out.println(tripletList.get(tripletList.size() - 1).getObjectURI());

    }

}