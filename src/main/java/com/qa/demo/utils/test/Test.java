package com.qa.demo.utils.test;

import com.qa.demo.conf.FileConfig;
import com.qa.demo.systemController.FaqDemo;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import static com.qa.demo.conf.FileConfig.*;

public class Test {

    private static Logger LOG = LogManager.getLogger(Test.class.getName());

    public static void main(String[] args) throws IOException {

//        Model dataModel = ModelFactory.createDefaultModel();
//        String inputFileName = NT_TRIPLETS;
//        InputStream in = FileManager.get().open(inputFileName);
//        if (in == null)
//        {
//            throw new IllegalArgumentException("File: " + inputFileName + " not found");
//        }
//        dataModel.read(in, "","N3");
//        Resource s = dataModel.getResource("http://zhishi.me/baidubaike/resource/可蒙犬");
//        Property p = dataModel.getProperty("http://zhishi.me/baidubaike/property/体型分类");
//
//        Iterator itr = dataModel.listObjectsOfProperty(s, p);
//        while (itr.hasNext()) {
//            RDFNode object = (RDFNode)itr.next();
//            if (object instanceof Resource)
//            {
//                System.out.println(" 宾语 " + ((Resource) object).getLocalName());
//            }
//
//            else
//                System.out.println(object.toString());
//        }
        PropertyConfigurator.configure(LOG_PROPERTY);
        LOG.info("this is info message\r\n");
        LOG.debug("this is debug message\r\n");
        LOG.warn("this is warn message\r\n");
        LOG.error("this is error message\r\n");
    }

}
