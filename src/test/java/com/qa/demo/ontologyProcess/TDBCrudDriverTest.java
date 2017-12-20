package com.qa.demo.ontologyProcess;

import com.qa.demo.conf.Configuration;
import org.apache.jena.rdf.model.Statement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.qa.demo.conf.Configuration.TDB_MODEL_NAME;
import static org.junit.jupiter.api.Assertions.*;

class TDBCrudDriverTest {
    @Test
    void loadTDBModel() {

        TDBCrudDriver tdbCrudDriver = new TDBCrudDriverImpl();
        tdbCrudDriver.loadTDBModel();
    }

    @Test
    void addTriplet() {

        String subject = Configuration.ENTITY_PREFIX_CAAS + "杨桃";
        String predicate = Configuration.PREDICATE_PREFIX_CAAS + "病虫害";
        String object = Configuration.ENTITY_PREFIX_CAAS + "红蜘蛛";

        TDBCrudDriver tdbCrudDriver = new TDBCrudDriverImpl();
        tdbCrudDriver.addTriplet(null, subject, predicate, object);
    }

    @Test
    void getTriplet() {

        //若主、谓、宾中有空缺，则表示该部分不参与匹配；
        //若主、谓、宾全部空缺，则输出所有三元组；
        String subject = Configuration.ENTITY_PREFIX_CAAS + "杨桃";
        String predicate = Configuration.PREDICATE_PREFIX_CAAS + "病虫害";
        String object = null;
        TDBCrudDriver tdbCrudDriver = new TDBCrudDriverImpl();
        List<Statement> list = tdbCrudDriver.getTriplet(null, subject, predicate, object);
        if(list.size()==0)
            System.out.println("没有相关三元组！");
        else {
            for (Statement s : list) {
                System.out.println(s);
            }
        }
    }

    @Test
    void removeTriplet() {

        String subject = Configuration.ENTITY_PREFIX_CAAS + "杨桃";
        String predicate = Configuration.PREDICATE_PREFIX_CAAS + "病虫害";
        String object = Configuration.ENTITY_PREFIX_CAAS + "红蜘蛛";

        TDBCrudDriver tdbCrudDriver = new TDBCrudDriverImpl();
        tdbCrudDriver.removeTriplet(null, subject, predicate, object);
    }

}