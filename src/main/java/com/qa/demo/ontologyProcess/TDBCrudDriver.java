package com.qa.demo.ontologyProcess;

import org.apache.jena.rdf.model.Statement;
import java.util.List;
/**
 * Created time: 2017_12_09
 * Author: Devin Hua
 * Function description:
 * The interface to implement CRUD of TDB.
 */

public interface TDBCrudDriver {

    /**
     * 查询Model中三元组；
     */
    List<Statement> getTriplet
    (String modelName, String subject, String predicate, String object);

    /**
     * 增加Model中三元组；
     */
    void addTriplet
    (String modelName, String subject, String predicate, String object);

    /**
     * 删除Model中的三元组；
     */
    void removeTriplet
    (String modelName, String subject, String predicate, String object);

    /**
     * 新建TDB的model；
     */
    void loadTDBModel();

}
