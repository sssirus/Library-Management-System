package com.qa.demo.query;

import com.qa.demo.conf.Configuration;
import com.qa.demo.utils.kgprocess.KGModelClient;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *  Created time: 2017_11_12
 *  Author: Devin Hua
 *  Function description:
 *  To transform retrieval to SPARQL query.
 */

public class SparqlQuery {

    public static ArrayList<String> getObject(String subjectURI, String predicateURI)
    {
        Model dataModel = KGModelClient.getInstance().getDataModel();
        ArrayList<String> objects = new ArrayList<>();
        Resource s = dataModel.getResource(subjectURI);
        Property p = dataModel.getProperty(predicateURI);

        Iterator itr = dataModel.listObjectsOfProperty(s, p);

        while (itr.hasNext()) {
            RDFNode object = (RDFNode)itr.next();
            if (object instanceof Resource)
            {
                objects.add(((Resource)object).getLocalName());
            }
            else
                objects.add(object.toString());
        }

        return objects;
    }


}
