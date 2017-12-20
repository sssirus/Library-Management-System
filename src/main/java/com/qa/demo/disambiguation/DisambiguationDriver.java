package com.qa.demo.disambiguation;
/**
 *  Created time: 2017_09_01
 *  Author: Devin Hua
 *  Function description:
 *  The main driver interface for disambiguation including entity linking and predicate linking.
 */
import com.qa.demo.dataStructure.Question;

public interface DisambiguationDriver {

    //对于question中的实体entity列表，进行自然语句mention和KG中entity的链接；
    Question entityLinking(Question q);

    //对于question中的谓词predicate列表，进行自然语句mention和KG中predicate的链接；
    Question predicateLinking(Question q);


}
