package com.qa.demo.disambiguation.entityLinkingLucene;

import com.qa.demo.conf.FileConfig;
import org.ansj.lucene6.AnsjAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class GenerateIndexForQA {

    List<LinkEntity> getAllEntities()
    {
        try
        {
            List<LinkEntity> allEntities= new ArrayList<LinkEntity>();

            String encoding = "UTF-8";
            File file = new File(FileConfig.ENTITY_HASH);

            if (file.isFile() && file.exists()) { // 判断文件是否存在

                InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;

                while ((lineTxt = bufferedReader.readLine()) != null) {

                    LinkEntity current = new LinkEntity();
                    current.setName(lineTxt);
                    allEntities.add(current);

                }
                read.close();
                return allEntities;
            }
            else
            {
                System.out.println("找不到指定的文件");
                return null;
            }
        }
        catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
            return null;
        }
    }

    @Test
    public void createIndex() throws Exception {

        List<LinkEntity> allEntities = getAllEntities();

        // 将采集到的数据封装到Document对象中

        List<Document> docList = new ArrayList<Document>();
        Document document;
        for (LinkEntity entity : allEntities) {
            document = new Document();
            // store:如果是yes，则说明存储到文档域中

            // 实体名称
            // 分词、索引、存储 TextField
            Field name = new TextField("name", entity.getName(), Store.YES);

            // 实体ID
            // 不分词、索引、存储 StringField
            // Field kbid = new StringField("kbid", entity.getKbid(), Store.YES);

            // 实体类别
            // 分词、索引、存储 TextField
            // Field category = new TextField("category", entity.getCategory(), Store.YES);

            // 实体属性
            // 分词、索引、存储 TextField
            // Field property = new TextField("property", entity.getFact().toString(), Store.YES);

            // 实体摘要
            // 分词、索引、存储 TextField
            // Field abstruct = new TextField("abstruct", entity.getName(), Store.YES);


            // 将field域设置到Document对象中

            document.add(name);

//            document.add(kbid);
//            document.add(category);
//            document.add(property);
//            document.add(abstruct);
            docList.add(document);
        }

        // 创建分词器，标准分词器
        // Analyzer analyzer = new StandardAnalyzer();
        //Analyzer analyzer = new AgricultureAnalyzer(AgricultureAnalyzer.TYPE.INDEX);

        Analyzer analyzer = new AnsjAnalyzer(AnsjAnalyzer.TYPE.base_ansj);

        // 创建IndexWriter
        IndexWriterConfig cfg = new IndexWriterConfig(analyzer);
        // 指定索引库的地址
//        File indexFile = new File(FileConfig.ENTITY_LINKING_LUCENE_INDEX);
        Path indexFile = Paths.get(FileConfig.ENTITY_LINKING_LUCENE_INDEX);
        Directory directory = FSDirectory.open(indexFile);
        IndexWriter writer = new IndexWriter(directory, cfg);

        // 通过IndexWriter对象将Document写入到索引库中
        for (Document doc : docList) {
            writer.addDocument(doc);
        }

        // 关闭writer
        writer.close();
    }

    @Test
    public void deleteIndex() throws Exception {
        // 创建分词器，标准分词器
        Analyzer analyzer = new StandardAnalyzer();

        // 创建IndexWriter
        IndexWriterConfig cfg = new
                IndexWriterConfig(analyzer);
        Directory directory = FSDirectory
                .open(Paths.get(FileConfig.ENTITY_LINKING_LUCENE_INDEX));
        // 创建IndexWriter
        IndexWriter writer = new IndexWriter(directory, cfg);

        // Terms
        // writer.deleteDocuments(new Term("id", "1"));

        // 删除全部（慎用）
        writer.deleteAll();

        writer.close();
    }

    @Test
    public void updateIndex() throws Exception {
        // 创建分词器，标准分词器
        Analyzer analyzer = new StandardAnalyzer();

        // 创建IndexWriter
        IndexWriterConfig cfg = new IndexWriterConfig(analyzer);

        Directory directory = FSDirectory
                .open(Paths.get(FileConfig.ENTITY_LINKING_LUCENE_INDEX));
        // 创建IndexWriter
        IndexWriter writer = new IndexWriter(directory, cfg);

        // 第一个参数：指定查询条件
        // 第二个参数：修改之后的对象
        // 修改时如果根据查询条件，可以查询出结果，则将以前的删掉，然后覆盖新的Document对象，如果没有查询出结果，则新增一个Document
        // 修改流程即：先查询，再删除，在添加
        Document doc = new Document();
        doc.add(new TextField("name", "lisi", Store.YES));
        writer.updateDocument(new Term("name", "zhangsan"), doc);

        writer.close();
    }
}
