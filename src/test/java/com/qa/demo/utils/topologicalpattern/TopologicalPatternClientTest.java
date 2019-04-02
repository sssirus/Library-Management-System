package com.qa.demo.utils.topologicalpattern;

import com.qa.demo.dataStructure.TopologicalStructure;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

    class TopologicalPatternClientTest {
        @Test
        void getInstance() {
        }

        @Test
        void getTopologicalStructureRepository() {

            ArrayList<TopologicalStructure> topologicalStructureRepository
                    = TopologicalPatternClient.getInstance().getTopologicalStructureRepository();
            for(TopologicalStructure tp : topologicalStructureRepository)
            {
                tp.printTopologicalStructure();
            }
        }
}