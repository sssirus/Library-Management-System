package com.qa.demo.utils.kgprocess;
/**
 *  Created time: 2017_10_13
 *  Author: Devin Hua
 *  Function description:
 *  To get relevant triplet from KG with specified entity.
 */

import com.qa.demo.dataStructure.Entity;
import com.qa.demo.dataStructure.Triplet;
import java.util.ArrayList;

public class EntityRetrieval {

    public static ArrayList<Triplet> retrieveTriplets(Entity entity)
    {
        ArrayList<Triplet> returnedTriplets = new ArrayList<>();
        //得到知识图谱中所有三元组;
        ArrayList<Triplet> triplets = KGTripletsClient.getInstance().getKgTriplets();
        for(Triplet triplet : triplets) {
            if (triplet.getSubjectURI().equalsIgnoreCase(entity.getEntityURI())
                    &&triplet.getSubjectName().equalsIgnoreCase(entity.getKgEntityName())) {
                returnedTriplets.add(triplet);
            }
            else if (triplet.getObjectURI().equalsIgnoreCase(entity.getEntityURI())
                    &&triplet.getObjectName().equalsIgnoreCase(entity.getKgEntityName())) {
                returnedTriplets.add(triplet);
            }
        }
        return returnedTriplets;
    }


    public static ArrayList<Triplet> retrieveTripletsURI(String entityURI)
    {
        ArrayList<Triplet> returnedTriplets = new ArrayList<>();
        //得到知识图谱中所有三元组;
        ArrayList<Triplet> triplets = KGTripletsClient.getInstance().getKgTriplets();
        for(Triplet triplet : triplets) {
            if (triplet.getSubjectURI().equalsIgnoreCase(entityURI)) {
                returnedTriplets.add(triplet);
            } else if (triplet.getObjectURI().equalsIgnoreCase(entityURI)) {
                returnedTriplets.add(triplet);
            }
        }
        return returnedTriplets;
    }

    public static ArrayList<Triplet> retrieveTripletsName(String entityName)
    {
        ArrayList<Triplet> returnedTriplets = new ArrayList<>();
        //得到知识图谱中所有三元组;
        ArrayList<Triplet> triplets = KGTripletsClient.getInstance().getKgTriplets();
        for(Triplet triplet : triplets) {
            if (triplet.getSubjectName().equalsIgnoreCase(entityName)) {
                returnedTriplets.add(triplet);
            } else if (triplet.getObjectName().equalsIgnoreCase(entityName)) {
                returnedTriplets.add(triplet);
            }
        }
        return returnedTriplets;
    }

}
