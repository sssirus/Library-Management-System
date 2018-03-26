package com.qa.demo.utils.io;

import com.qa.demo.dataStructure.Triplet;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

class WebServiceAccessorTest {
    @Test
    void query() throws UnsupportedEncodingException {

        // 作为载体的三元组
        Triplet triplet = new Triplet();
        triplet.setPredicateURI("http://zhishi.me/ontology/category");

        // 存放结果
        List<Triplet> tripletList = null;

        // 进行请求
        tripletList = WebServiceAccessor.query(triplet);

        // 输出结果
        System.out.println();
        System.out.println(tripletList.size());
        System.out.println();
        System.out.println(tripletList.get(0).getSubjectURI());
        System.out.println(tripletList.get(0).getPredicateURI());
        System.out.println(tripletList.get(0).getObjectURI());
    }

}