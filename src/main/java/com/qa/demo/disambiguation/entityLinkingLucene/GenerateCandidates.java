package com.qa.demo.disambiguation.entityLinkingLucene;

import com.qa.demo.utils.lucene.AgricultureAnalyzer;
import info.debatty.java.stringsimilarity.JaroWinkler;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.qa.demo.conf.FileConfig.ENTITY_LINKING_LUCENE_INDEX;


public class GenerateCandidates {

    public ScoreDoc[] doSearch(Query query) throws IOException {

//        File indexFile = new File(ENTITY_LINKING_LUCENE_INDEX);
        Path indexFile = Paths.get(ENTITY_LINKING_LUCENE_INDEX);
        Directory directory = FSDirectory.open(indexFile);
        if(directory.listAll().length==0)
            return null;

        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        TopDocs topDocs = searcher.search(query, 8);

        int count = topDocs.totalHits;
        //System.out.println("匹配出的记录总数:" + count);
        if (count == 0) {
            return null;
        }

        ScoreDoc[] scoreDocs = topDocs.scoreDocs;

        reader.close();

        return scoreDocs;
    }

    public List<Candidate> SelectCandidate(List<Candidate> candidates) {
        if (candidates == null) {
            return null;
        }

        List<Candidate> res = new ArrayList<Candidate>();

        double maxScore = candidates.get(0).getScore();
        res.add(candidates.get(0));

        for (int i = 1; i < candidates.size(); i++) {
            if (candidates.get(i).getScore() == maxScore) {
                res.add(candidates.get(i));
            }
            else if(candidates.get(i).getScore() > maxScore)
            {
                res.clear();
                res.add(candidates.get(i));
            }
        }

        return res;
    }

    public String generateResult(List<Candidate> candidates) throws IOException {
        if (candidates == null) {
            //System.out.println("\tNIL");
            return "NIL";
        }
//        else if (scoreDocs.size() > 1) {
//            System.out.println("\tNIL");
//        }
        else {
//            File indexFile = new File(FileConfig.ENTITY_LINKING_LUCENE_INDEX);
//            Directory directory = FSDirectory.open(indexFile);
//            IndexReader reader = DirectoryReader.open(directory);
//            IndexSearcher searcher = new IndexSearcher(reader);
//
//            int docId = scoreDocs.get(0).doc;
//            org.apache.lucene.document.Document doc = searcher.doc(docId);
//            System.out.println("\t" + doc.get("name"));

            //System.out.println(candidates.get(0).getName());
            double maxScore = candidates.get(0).getSimilarityScore();
            String ans = candidates.get(0).getName();

            for(int i=1;i<candidates.size();i++)
            {
                if(candidates.get(i).getSimilarityScore()>maxScore)
                {
                    maxScore = candidates.get(i).getSimilarityScore();
                    ans = candidates.get(i).getName();
                }
            }
            //System.out.println(ans);
            return ans;
        }
    }

    public double computerSimilarity(String name1,String name2)
    {
        JaroWinkler jw = new JaroWinkler();
        return jw.similarity(name1, name2);
    }

    List<Mention> getMentions()
    {
        try
        {
            List<Mention> allMentions = new ArrayList<Mention>();

            String encoding = "UTF-8";
            File file = new File("mentionList");

            if (file.isFile() && file.exists())
            {
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;

                while ((lineTxt = bufferedReader.readLine()) != null) {

                    Mention current = new Mention();
                    current.setName(lineTxt);
                    allMentions.add(current);

                }
                read.close();
                return allMentions;
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

    public String entityDisambiguation(List<Candidate> OriginCandidateSet) throws Exception {


        List<Candidate> FinnalCandidateSet = SelectCandidate(OriginCandidateSet);

        // ShowScoreDoc(FinnalCandidateSet);

        return generateResult(FinnalCandidateSet);

    }

    public  List<Candidate> CandidateGen(String curName)throws Exception {

        List<Candidate> allCandidates = new ArrayList<Candidate>();

        // String[] fields = { "name", "fact" };

        String[] fields = { "name" };
        Analyzer analyzer = new AgricultureAnalyzer(AgricultureAnalyzer.TYPE.INDEX);
        MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, analyzer);
        Query query = parser.parse(curName);

        ScoreDoc[] OriginCandidateSet = doSearch(query);
        if(OriginCandidateSet.length==0||OriginCandidateSet==null)
            return allCandidates;


//        File indexFile = new File(ENTITY_LINKING_LUCENE_INDEX);
        Path indexFile = Paths.get(ENTITY_LINKING_LUCENE_INDEX);
        Directory directory = FSDirectory.open(indexFile);
        if(directory.listAll().length==0)
            return allCandidates;
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        for (int i = 0; i < OriginCandidateSet.length; i++) {
            int docId = OriginCandidateSet[i].doc;
            org.apache.lucene.document.Document doc = searcher.doc(docId);
            System.out.println(curName+"\t" + doc.get("name"));

            Candidate curCandidate = new Candidate();
            curCandidate.setName(doc.get("name"));
            curCandidate.setScore(OriginCandidateSet[i].score);
            curCandidate.setMentionname(curName);
            curCandidate.setSimilarityScore(computerSimilarity(doc.get("name"),curName));

            allCandidates.add(curCandidate);
        }

        //return  OriginCandidateSet;
        return allCandidates;
    }


    public List<String> EntityLinkingQA(List<String> AllMention) throws Exception {

        List<String> ans = new ArrayList<String>();

//        PrintStream console = System.out;
//        PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream("Answer.txt")));
//
//        System.setOut(out);

        for (int i = 0; i < AllMention.size(); i++) {

            String curName = AllMention.get(i);

            List<Candidate> CandidateSet = CandidateGen(curName);
            if(CandidateSet.isEmpty()||CandidateSet.size()==0)
                continue;

            //System.out.println(curName);

            // System.out.println(curName);
            // 配套 ShowScoreDoc(FinnalCandidateSet); 函数使用

            String entityname = entityDisambiguation(CandidateSet);
            ans.add(entityname);
        }

        return ans;

//        out.close();
//        System.setOut(console);

    }




    @Test
    public void EntityLinking() throws Exception {

        List<Mention> AllMention = getMentions();

//        PrintStream console = System.out;
//        PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream("Answer.txt")));
//
//        System.setOut(out);

        for (int i = 0; i < AllMention.size(); i++) {

            String curName = AllMention.get(i).getName();

            List<Candidate> CandidateSet = CandidateGen(curName);

            //System.out.println(curName);

            // System.out.println(curName);
            // 配套 ShowScoreDoc(FinnalCandidateSet); 函数使用

            entityDisambiguation(CandidateSet);

        }

//        out.close();
//        System.setOut(console);

    }
}
