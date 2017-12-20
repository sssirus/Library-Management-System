package com.qa.demo.ontologyProcess;

import com.qa.demo.conf.Configuration;
import com.qa.demo.conf.FileConfig;
import org.apache.jena.rdf.model.Statement;
import org.nlpcn.commons.lang.util.logging.Log;
import org.nlpcn.commons.lang.util.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created time: 2017_12_09
 * Author: Devin Hua
 * Function description:
 * The class to implement TDB-CRUD interface..
 */

public class TDBCrudDriverImpl implements TDBCrudDriver {

    public static final Log LOG = LogFactory.getLog(TDBCrudDriverImpl.class);

    @Override
    public List<Statement> getTriplet(String modelName, String subject, String predicate, String object) {

        List<Statement> list = new ArrayList<>();
        //如果不输入model name，则使用系统默认的TDB model：TDB_agriculture；
        if(modelName == null) {
            modelName = Configuration.TDB_MODEL_NAME;
            LOG.warn("MODEL NAME为空，使用默认model： " + Configuration.TDB_MODEL_NAME);
        }

        TDBPersistence tdbPersistence = new TDBPersistence(FileConfig.TDB);
        //如果TDB中有当前model；
        if (tdbPersistence.findModel(modelName)) {
            list = tdbPersistence.getTriplet(modelName, subject, predicate, object);
            tdbPersistence.closeTDB();
        }

        //如果TDB中没有当前model；
        else
            LOG.warn(modelName + " 不存在，无法查询！");
        return list;
    }

    @Override
    public void addTriplet(String modelName, String subject, String predicate, String object) {

        //如果不输入model name，则使用系统默认的TDB model：TDB_agriculture；
        if(modelName == null) {
            modelName = Configuration.TDB_MODEL_NAME;
            LOG.warn("MODEL NAME为空，使用默认model： " + Configuration.TDB_MODEL_NAME);
        }

        TDBPersistence tdbPersistence = new TDBPersistence(FileConfig.TDB);
        //如果TDB中有当前model；
        if (tdbPersistence.findModel(modelName)) {
            tdbPersistence.addTriplet(modelName, subject, predicate, object);
            tdbPersistence.closeTDB();
        }

        //如果TDB中没有当前model；
        else
            LOG.warn(modelName + " 不存在，不执行添加操作！");
    }

    @Override
    public void removeTriplet(String modelName, String subject, String predicate, String object) {

        //如果不输入model name，则使用系统默认的TDB model：TDB_agriculture；
        if(modelName == null) {
            modelName = Configuration.TDB_MODEL_NAME;
            LOG.warn("MODEL NAME为空，使用默认model： " + Configuration.TDB_MODEL_NAME);
        }

        TDBPersistence tdbPersistence = new TDBPersistence(FileConfig.TDB);
        //如果TDB中有当前model；
        if (tdbPersistence.findModel(modelName)) {
            tdbPersistence.removeTriplet(modelName, subject, predicate, object);
            tdbPersistence.closeTDB();
        }

        //如果TDB中没有当前model；
        else
            LOG.warn(modelName + " 不存在，不执行删除操作！");
    }
}
