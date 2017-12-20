package com.qa.demo;

import com.qa.demo.dataStructure.DataSource;
import com.qa.demo.ontologyProcess.TDBCrudDriver;
import com.qa.demo.ontologyProcess.TDBCrudDriverImpl;
import com.qa.demo.systemController.FaqDemo;
import com.qa.demo.utils.es.IndexFile;
import org.nlpcn.commons.lang.util.logging.Log;
import org.nlpcn.commons.lang.util.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;

import java.io.IOException;

@SpringBootApplication
public class DemoApplication  extends SpringBootServletInitializer{

	public static final Log LOG = LogFactory.getLog(FaqDemo.class);

	public static void main(String[] args) throws IOException {

		try {
			//系统初始化操作：es建立索引
			//SYNONYM为分词之后的模板；
			IndexFile.indexFaqData(DataSource.SYNONYM);
			//为19000条百科知识的索引；
			IndexFile.indexEncyclopediaData(DataSource.ENCYCLOPEDIA);
			//FAQ为常用问答对的索引；PATTERN为模板的索引;FAQ_T为生成的所有问题的模板；
//      IndexFile.indexFaqData(DataSource.FAQ, DataSource.PATTERN, DataSource.FAQ_T);
			IndexFile.indexFaqData(DataSource.FAQ, DataSource.PATTERN);
//      IndexFile.indexFaqData(DataSource.FAQ);
			TDBCrudDriver tdbCrudDriver = new TDBCrudDriverImpl();
			tdbCrudDriver.loadTDBModel();
		} catch (IOException e) {
			e.printStackTrace();
			LOG.error(" [error]发生异常！");
		}

		LOG.info(" [info]已建立faq索引！");
		LOG.info(" [info]已建立TDB MODEL，系统初始化完成！");

		SpringApplication.run(DemoApplication.class, args);

	}

}
