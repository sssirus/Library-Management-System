package com.qa.demo.query;

import com.qa.demo.ontologyProcess.TDBCrudDriver;
import com.qa.demo.ontologyProcess.TDBCrudDriverImpl;
import org.apache.jena.rdf.model.*;

import java.util.ArrayList;
import java.util.List;

import static com.qa.demo.conf.Configuration.TDB_MODEL_NAME;

/**
 *  Created time: 2017_12_10
 *  Author: Devin Hua
 *  Function description:
 *  To employ TDB to retrieve knowledge base triplets.
 */

public class TDBQuery {

    public static ArrayList<String> getObject(String subjectURI, String predicateURI)
    {
        String modelName = TDB_MODEL_NAME;
        TDBCrudDriver tdbCrudDriver = new TDBCrudDriverImpl();

        ArrayList<String> objects = new ArrayList<>();
        List<Statement> list = tdbCrudDriver.getTriplet(modelName, subjectURI, predicateURI,null);

        if (list.size()>0) {
            for(Statement s : list)
            {
                objects.add(s.getObject().toString());
            }
        }
        return objects;
    }


}
