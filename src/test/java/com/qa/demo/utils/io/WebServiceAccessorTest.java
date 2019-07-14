package com.qa.demo.utils.io;

import com.qa.demo.dataStructure.Triplet;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class WebServiceAccessorTest {
    @Test
    void query() {
        Triplet triplet = new Triplet();
        List<Triplet> tripletList = null;
        List<String> subjects = new ArrayList<>();
//         subjects.add("http://zhishi.me/hudongbaike/resource/苹果");
//         subjects.add("http://zhishi.me/hudongbaike/resource/乔治·奥韦尔");
//         subjects.add("苹果");
//        subjects.add("中国");
//         triplet.setSubjectURI("中国");
        triplet.setSubjectURI("http://zhishi.me/hudongbaike/resource/文化学园大学");

//        triplet.setObjectURI("中国");
        // subjects.add("http://zhishi.me/hudongbaike/resource/CW400");

//         tripletList = WebServiceAccessor.queryByMultiSubjects(subjects);
//         tripletList = WebServiceAccessor.queryByMultiObjects(subjects);
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