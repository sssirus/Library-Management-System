package com.qa.demo.utils.kgprocess;

import com.qa.demo.dataStructure.Entity;
import com.qa.demo.dataStructure.Triplet;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EntityRetrievalTest {
    @Test
    void retrieveTriplets() {

        Entity entity = new Entity();
        entity.setKgEntityName("蝴蝶犬");
        entity.setEntityURI("http://zhishi.me/hudongbaike/resource/蝴蝶犬");

        ArrayList<Triplet> triplets = EntityRetrieval.retrieveTripletsName(entity.getKgEntityName());
        System.out.println("--------------------------Retrieval by entity name--------------------------");
        for(Triplet t : triplets)
        {
            t.printTriplet();
            System.out.print("\r\n");
        }

        System.out.println("\r\n--------------------------Retrieval by entity URI--------------------------");
        triplets = EntityRetrieval.retrieveTripletsURI(entity.getEntityURI());
        for(Triplet t : triplets)
        {
            t.printTriplet();
            System.out.print("\r\n");
        }

        System.out.println("\r\n--------------------------Retrieval by entity--------------------------");
        triplets = EntityRetrieval.retrieveTriplets(entity);
        for(Triplet t : triplets)
        {
            t.printTriplet();
            System.out.print("\r\n");
        }

    }

}