package com.qa.demo.utils.io;

import com.qa.demo.dataStructure.Triplet;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

class WebServiceAccessorTest {
    @Test
    void query() {
        Triplet triplet = new Triplet();
        List<Triplet> tripletList = null;
        List<String> subjects = new ArrayList<>();
        // subjects.add("http://zhishi.me/hudongbaike/resource/苹果");
        // subjects.add("http://zhishi.me/hudongbaike/resource/乔治·奥韦尔");
        // subjects.add("苹果");
        subjects.add("中国");
        // triplet.setSubjectURI("中国");
        triplet.setObjectURI("中国");
        // subjects.add("http://zhishi.me/hudongbaike/resource/CW400");

        // tripletList = WebServiceAccessor.queryByMultiSubjects(subjects);
        // tripletList = WebServiceAccessor.queryByMultiObjects(subjects);
        tripletList = WebServiceAccessor.query(triplet);
        System.out.println();
        System.out.println(tripletList.size());
        for(Triplet t : tripletList){
            System.out.println();
            System.out.println(t.getSubjectURI());
            System.out.println(t.getPredicateURI());
            System.out.println(t.getObjectURI());
        }
    }

}