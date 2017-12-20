package com.qa.demo.utils.kgprocess;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

import java.io.InputStream;

import static com.qa.demo.conf.FileConfig.NT_TRIPLETS;

/**
 * Created time: 2017_11_12
 * Author: Devin Hua
 * Function description:
 * To generate KB model for system access.
 */

public class KGModelClient {

    private static KGModelClient kgModelClient;
    private Model dataModel;

    //单例模式;
    private KGModelClient() {
        this.dataModel = ModelFactory.createDefaultModel();
        String inputFileName = NT_TRIPLETS;
        InputStream in = FileManager.get().open(inputFileName);
        if (in == null) {
            throw new IllegalArgumentException("File: " + inputFileName + " not found");
        }
        this.dataModel = dataModel.read(in, "", "N3");
    }

    public static synchronized KGModelClient getInstance() {
        if (kgModelClient == null) {
            kgModelClient = new KGModelClient();
        }
        return kgModelClient;
    }

    public Model getDataModel() {
        return dataModel;
    }
}
