package com.qa.demo.ontologyProcess;

import com.qa.demo.conf.Configuration;
import com.qa.demo.conf.FileConfig;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.qa.demo.conf.Configuration.TDB_MODEL_NAME;
import static com.qa.demo.conf.FileConfig.TDB;

class TDBPersistenceTest {

    @Test
    void getModel() {

        String TDBPath = TDB;
        String modelName = "TDB_agriculture";
        TDBPersistence tdbPersistence = new TDBPersistence(FileConfig.TDB);
        Model model = tdbPersistence.getModel(modelName);
        tdbPersistence.closeTDB();
    }

    @Test
    void findModel() {

        String TDBPath = TDB;
        String modelName = "TDB_agriculture";
        TDBPersistence tdbPersistence = new TDBPersistence(FileConfig.TDB);
        System.out.println(modelName + "是否存在： " + tdbPersistence.findModel(modelName));
        tdbPersistence.closeTDB();
    }

    @Test
    void listModels() {

        String TDBPath = TDB;
        TDBPersistence tdbPersistence = new TDBPersistence(FileConfig.TDB);
        List<String> models = tdbPersistence.listModels();
        if (models == null || models.isEmpty() || models.size() == 0)
            System.out.println("Dataset中不存在非默认model！");
        else {
            for (String model : models) {
                System.out.println("model: " + model);
            }
        }
        tdbPersistence.closeTDB();
    }

    @Test
    void removeModel() {

        String TDBPath = TDB;
        String modelName = "TDB_agriculture";
        TDBPersistence tdbPersistence = new TDBPersistence(FileConfig.TDB);
        tdbPersistence.removeModel(modelName);
        tdbPersistence.closeTDB();
    }

    @Test
    void loadModel() {

        //TDB数据文件夹地址；
        String TDBPath = TDB;
        //在Dataset中存放model的名字；
        String modelName = "TDB_agriculture";
        //表示若有同名model，是否需要覆盖；
        Boolean flag = true;
        //rdf三元组文件的绝对路径（注意：写相对路径会报错）；
        String rdfPathName = "E:\\demo\\src\\main\\resources\\data\\kbfile\\NT_triplets.nt";
        TDBPersistence tdbPersistence = new TDBPersistence(FileConfig.TDB);
        //建立对象；
        tdbPersistence.loadModel(modelName, rdfPathName, flag);
        //事务完成后必须关闭Dataset；
        tdbPersistence.closeTDB();
    }

}