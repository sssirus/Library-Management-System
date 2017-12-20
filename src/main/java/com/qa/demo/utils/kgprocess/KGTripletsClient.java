package com.qa.demo.utils.kgprocess;

import com.qa.demo.conf.FileConfig;
import com.qa.demo.dataStructure.Triplet;
import com.qa.demo.utils.qgeneration.KBTripletBasedQuestionGeneration;

import java.util.ArrayList;

/**
 *  Created time: 2017_09_07
 *  Author: Devin Hua
 *  Function description:
 *  To generate KB triplets for system access.
 */

public class KGTripletsClient {

    private static KGTripletsClient kgTripletsClientInstance;
    private ArrayList<Triplet> kgTriplets;

    //单例模式;
    private KGTripletsClient()
    {
        this.kgTriplets = KBTripletBasedQuestionGeneration.generateTriplets(FileConfig.DATATYPE_PROPERTY_TRIPLETS_FILE);
        ArrayList<Triplet> triplets = KBTripletBasedQuestionGeneration.generateTriplets(FileConfig.OBJECT_PROPERTY_TRIPLETS_FILE);
        this.kgTriplets.addAll(triplets);
    }

    public static synchronized KGTripletsClient getInstance(){

        if(kgTripletsClientInstance==null)
        {
            kgTripletsClientInstance = new KGTripletsClient();
        }
        return kgTripletsClientInstance;

    }

    public ArrayList<Triplet> getKgTriplets() {
        return kgTriplets;
    }
}
