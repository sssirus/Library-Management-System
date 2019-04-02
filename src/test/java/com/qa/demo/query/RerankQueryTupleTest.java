package com.qa.demo.query;

import com.qa.demo.conf.Configuration;
import com.qa.demo.dataStructure.Entity;
import com.qa.demo.dataStructure.QueryTuple;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RerankQueryTupleTest {
    @Test
    void rankTuples() {

        ArrayList<QueryTuple> tuples = new ArrayList<>();
        Entity a = new Entity();
        a.setKgEntityName("a");
        QueryTuple t_a = new QueryTuple();
        t_a.setSubjectEntity(a);
        t_a.setTupleScore(0.5);
        tuples.add(t_a);

        Entity b = new Entity();
        b.setKgEntityName("b");
        QueryTuple t_b = new QueryTuple();
        t_b.setSubjectEntity(b);
        t_b.setTupleScore(0.35);
        tuples.add(t_b);

        Entity c = new Entity();
        c.setKgEntityName("c");
        QueryTuple t_c = new QueryTuple();
        t_c.setSubjectEntity(c);
        t_c.setTupleScore(10.35);
        tuples.add(t_c);

        Entity d = new Entity();
        d.setKgEntityName("d");
        QueryTuple t_d = new QueryTuple();
        t_d.setSubjectEntity(d);
        t_d.setTupleScore(6.35);
        tuples.add(t_d);

        Entity e = new Entity();
        e.setKgEntityName("e");
        QueryTuple t_e = new QueryTuple();
        t_e.setSubjectEntity(e);
        t_e.setTupleScore(3.0);
        tuples.add(t_e);

        tuples = RerankQueryTuple.rankTuples(tuples);
        ArrayList<QueryTuple> returned_tuples = new ArrayList<>();
        int size = 4;
        if(tuples.size() <= size)
            returned_tuples = tuples;
        else
        {
            returned_tuples = new ArrayList<>();
            for(int i=0;i<size;i++)
            {
                returned_tuples.add(tuples.get(i));
            }
        }

        for(QueryTuple t : returned_tuples)
        {
            System.out.println(t.getSubjectEntity().getKgEntityName() + " " + t.getTupleScore());
        }

    }

}