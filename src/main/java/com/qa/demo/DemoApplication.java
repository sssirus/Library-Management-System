package com.qa.demo;

import com.qa.demo.dataStructure.DataSource;
import com.qa.demo.ontologyProcess.TDBCrudDriver;
import com.qa.demo.ontologyProcess.TDBCrudDriverImpl;
import com.qa.demo.systemController.FaqDemo;
import com.qa.demo.utils.es.IndexFile;
import com.qa.demo.utils.kgprocess.KGTripletsClient;
import com.qa.demo.utils.w2v.Word2VecGensimModel;
import org.bytedeco.javacpp.Loader;
import org.nd4j.nativeblas.Nd4jCpu;
import org.nlpcn.commons.lang.util.logging.Log;
import org.nlpcn.commons.lang.util.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static com.qa.demo.conf.FileConfig.W2V_file;

@SpringBootApplication
public class DemoApplication  /*extends SpringBootServletInitializer*/{

	public static final Log LOG = LogFactory.getLog(FaqDemo.class);


	public static void main(String[] args) throws IOException, InterruptedException {
        try {
            Loader.load(Nd4jCpu.class);
            KGTripletsClient.getInstance();//实体
        } catch (UnsatisfiedLinkError e) {
            String path = Loader.cacheResource(Nd4jCpu.class, "windows-x86_64/jniNd4jCpu.dll").getPath();
            new ProcessBuilder(W2V_file, path).start().waitFor();
        }
        Word2VecGensimModel w2vModel = null;
        try {
            w2vModel = Word2VecGensimModel.getInstance();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        SpringApplication.run(DemoApplication.class, args);
    }
}
//	public static void main(String[] args) throws IOException {

//		try {
//			//系统初始化操作：es建立索引
//			//SYNONYM为分词之后的模板；
//			IndexFile.indexFaqData(DataSource.SYNONYM);
//			//为19000条百科知识的索引；
//			IndexFile.indexEncyclopediaData(DataSource.ENCYCLOPEDIA);
//			//FAQ为常用问答对的索引；PATTERN为模板的索引;FAQ_T为生成的所有问题的模板；
////      IndexFile.indexFaqData(DataSource.FAQ, DataSource.PATTERN, DataSource.FAQ_T);
//			IndexFile.indexFaqData(DataSource.FAQ, DataSource.PATTERN);
////      IndexFile.indexFaqData(DataSource.FAQ);
//			TDBCrudDriver tdbCrudDriver = new TDBCrudDriverImpl();
//			tdbCrudDriver.loadTDBModel();
//		} catch (IOException e) {
//			e.printStackTrace();
//			LOG.error(" [error]发生异常！");
//		}
//
//		LOG.info(" [info]已建立faq索引！");
//		LOG.info(" [info]已建立TDB MODEL，系统初始化完成！");
//
//		SpringApplication.run(DemoApplication.class, args);
//
//	}

