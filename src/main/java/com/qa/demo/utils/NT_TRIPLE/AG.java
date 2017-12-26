package com.qa.demo.utils.NT_TRIPLE;

import com.franz.agraph.repository.*;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.qa.demo.conf.FileConfig.NT_TRIPLETS;

/**
 * @Description: AllegroGgraph 接口
 * @Function：建立连接；添加三元组；直接读取文件添加；查询所有；输入主语宾语查询三元组；输入主语谓语返回宾语；
 * @Author: J.Y.Zhang
 * @Date: 2017/12/26
 */
public class AG {

    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(AG.class);

    private static final String SERVER_URL = "http://172.16.120.28:10035";
    private static final String CATALOG_ID = "java-catalog";
    private static final String REPOSITORY_ID = "test";
    private static final String USERNAME = "test";
    private static final String PASSWORD = "xyzzy";
    private static final File DATA_DIR = new File(".");
    private static final String FOAF_NS = "http://xmlns.com/foaf/0.1/";


    /**
     * Creating a Repository test
     */
    public static AGRepositoryConnection createConnection(boolean close) throws Exception{
        // Tests getting the repository up.
        AGServer server = new AGServer(SERVER_URL, USERNAME, PASSWORD);
        try {
            LOG.info("Server version: " + server.getVersion());
            LOG.info("Server build date: " + server.getBuildDate());
            LOG.info("Server revision: " + server.getRevision());
            LOG.info("Available catalogs: " + server.listCatalogs());
        } catch (Exception e) {
            throw new Exception("Got error when attempting to connect to server at "
                    + SERVER_URL + ": " + e);
        }

        AGCatalog catalog = server.getCatalog(CATALOG_ID); // open catalog

        if (catalog == null) {
            throw new Exception("Catalog " + CATALOG_ID + " does not exist. Either "
                    + "define this catalog in your agraph.cfg or modify the CATALOG_ID "
                    + "in this tutorial to name an existing catalog.");
        }

        AGRepository myRepository = catalog.createRepository(REPOSITORY_ID);
        myRepository.initialize();
        AGRepositoryConnection conn = myRepository.getConnection();
        closeBeforeExit(conn);

        List<String> indices = conn.listValidIndices();

        conn.dropIndex("gospi");
        conn.dropIndex("gposi");
        conn.dropIndex("gspoi");
        indices = conn.listIndices();
        LOG.info("Current triple indices: " + indices);

        if (close) {
            // tidy up
            conn.close();
            myRepository.shutDown();
            server.close();
            return null;
        }
        return conn;
    }

    /**
     * Asserting and Retracting Triples
     */
    public static AGRepositoryConnection assertTriples(boolean close) throws Exception {
        // Asserts some statements and counts them.
        AGRepositoryConnection conn = createConnection(false);
        AGValueFactory vf = conn.getRepository().getValueFactory();
        LOG.info("\nStarting assertTriples().");

        // Create some resources and literals to make statements from.
        IRI alice = vf.createIRI("http://example.org/people/alice");
        IRI bob = vf.createIRI("http://example.org/people/bob");
        IRI name = vf.createIRI("http://example.org/ontology/name");
        IRI person = vf.createIRI("http://example.org/ontology/Person");
        Literal bobsName = vf.createLiteral("Bob");
        Literal alicesName = vf.createLiteral("Alice");

        // Alice's name is "Alice"
        conn.add(alice, name, alicesName);
        // Alice is a person
        conn.add(alice, RDF.TYPE, person);
        //Bob's name is "Bob"
        conn.add(bob, name, bobsName);
        //Bob is a person, too.
        conn.add(bob, RDF.TYPE, person);

        conn.remove(bob, name, bobsName);
        // put it back so we can continue with other examples
        conn.add(bob, name, bobsName);

        LOG.info("Triple count after inserts: " +
                (conn.size()));
        /*
        RepositoryResult<Statement> result = conn.getStatements(null, null, null, false);
        while (result.hasNext()) {
            Statement st = result.next();
            LOG.info(st);
        }
        */

        if (close) {
            conn.close();
            conn.getRepository().shutDown();
            return null;
        }
        return conn;

    }

    /**
     * SPARQL Query all
     */
    public  static void queryAll() throws Exception{
        AGRepositoryConnection conn = createConnection(false);
        LOG.info("\nStarting query().");
        try {
            String queryString = "SELECT ?s ?p ?o  WHERE {?s ?p ?o .}";//查询所有三元组
            AGTupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
            TupleQueryResult result = tupleQuery.evaluate();
            try {
                while (result.hasNext()) {//从bandingset中提取值的方法
                    BindingSet bindingSet = result.next();
                    Value s = bindingSet.getValue("s");
                    Value p = bindingSet.getValue("p");
                    Value o = bindingSet.getValue("o");
                    System.out.format("%s %s %s\n", s, p, o);
                }
            } finally {
                result.close();
            }
            // Just the count now.  The count is done server-side,
            // and only the count is returned.
            long count = tupleQuery.count();//数量
            LOG.info("count: " + count);
        } finally {
            conn.close();
        }
    }

    /**
     * SPARQL Query
     * 输入主语 宾语 返回所有相关三元组 可以有一个是null
     * @throws Exception
     */
    public static TupleQueryResult queryTriples(String subject, String object) throws Exception{
        // 两种方法 SPARQL直接匹配 和 SPARQL过滤器匹配
        // SELECT ?s ?p WHERE {?s ?p "Red"}
        // SELECT ?s ?p ?o WHERE {?s ?p ?o . filter (?o = "Red")}

        RepositoryConnection conn = createConnection(false);
        Repository myRepository = conn.getRepository();
        ValueFactory f = myRepository.getValueFactory();
        String exns = "http://zhishi.me/baidubaike/resource/";

        conn.setNamespace("resource", exns);

        //String queryString = "SELECT ?s ?p ?o WHERE { ?s ?p ?o . FILTER ((?s = resource:百里香) && (?p = property:中文学名))  }";

        String queryFillter = "";
        if (object != null && subject == null){
            queryFillter = "(?o = '"+object+"')";
        }
        if (object == null && subject != null){
            queryFillter = "(?s = resource:"+ subject +")";
        }
        if (object != null && subject != null){
            queryFillter = "((?s = resource:"+ subject +") && (?o = '"+object+"'))";
        }

        String queryString = "SELECT ?s ?p ?o WHERE {?s ?p ?o . filter "+queryFillter+"}";
        //String queryString = "SELECT ?s ?p ?o WHERE {?s ?p ?o . filter (?s = '<http://zhishi.me/baidubaike/resource/五指毛桃根>')}";
        System.out.println(queryString);
        TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
        TupleQueryResult result = tupleQuery.evaluate();
        ArrayList<String> triples = new ArrayList<>();

        try {
            while (result.hasNext()) {
                BindingSet bindingSet = result.next();
                Value s = bindingSet.getValue("s");
                Value p = bindingSet.getValue("p");
                Value o = bindingSet.getValue("o");
                System.out.println("  " + s + " " + p + " " + o);
                triples.add("  " + s + " " + p + " " + o);
            }
        } finally {
            result.close();
        }
        return result;

    }


    /**
     * 输入主语，谓语返回宾语
     * 参数：主语，谓语
     * return ArrayList<Value> 宾语列表
     */
    public  static ArrayList<Value> queryObject(String subject, String predicate) throws Exception{
        AGRepositoryConnection conn = createConnection(false);
        ValueFactory f = conn.getValueFactory();

        String exns = "http://zhishi.me/baidubaike/resource/";
        String exns2 = "http://zhishi.me/baidubaike/property/";

        conn.setNamespace("resource", exns);
        conn.setNamespace("property",exns2);
        // Check triples
        //println("\nListing all triples.");
        //RepositoryResult<Statement> statements = conn.getStatements(null, null, null, false);
        //printRows(statements);

        String queryFillter = "((?s = resource:"+subject+") && (?p = property:"+predicate+"))";
        String queryString = "SELECT ?s ?p ?o WHERE { ?s ?p ?o . FILTER "+queryFillter+"  }";
        //String queryString = "SELECT ?s ?p ?o WHERE { ?s ?p ?o . FILTER ((?s = resource:百里香) && (?p = property:中文学名))  }";

        TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
        TupleQueryResult result = tupleQuery.evaluate();
        // 提取结果的value值
        ArrayList<Value> objects = new ArrayList<>();
        try {
            while (result.hasNext()) {
                BindingSet bindingSet = result.next();
                Value o = bindingSet.getValue("o");
                objects.add(o);
            }
        } finally {
            result.close();
        }
        println(objects);
        return objects;
    }
    /**
     * 导入三元组文件
     * @return
     * @throws Exception
     */
    public static AGRepositoryConnection storeTriples() throws Exception{

        AGRepositoryConnection conn = createConnection(false);
        closeBeforeExit(conn);
        conn.clear();

        conn.begin();  // start a transaction
        ValueFactory f = conn.getValueFactory();

        final File path2 = new File(NT_TRIPLETS);
        String baseURI = "http://example.org/example/local";

        // rdf文件和xml文件 还可以用addFile方法
        //final File path1 = new File(DATA_DIRECTORY, "java-vcards.rdf");
        //IRI context = f.createIRI("http://example.org#vcards");
        // read vcards triples into the context 'context':
        //conn.add(path1, baseURI, RDFFormat.RDFXML, context);

        // read Kennedy triples into the null context:
        conn.add(path2, baseURI, RDFFormat.NTRIPLES);
        LOG.info("After loading, repository contains "  +
                conn.size((Resource)null) + " kennedy triples in context 'null'.");
        conn.commit();
        return conn;
    }


    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        long now = System.currentTimeMillis();
        List<Integer> choices = new ArrayList<Integer>();

        for (int i = 1; i <= 4; i++) {
            choices.add(i);
        }

        try {
            for (Integer choice : choices) {
                LOG.info("\n** Running example " + choice);
                switch(choice) {
                    case 1: //createConnection(true); break;
                    case 2: //assertTriples(true); break;
                    case 3:
                        //queryAll();
                        queryTriples("卷毛猫","卷毛猫");
                        //queryObject("百里香","分布区域");
                    case 4: storeTriples();
                        break;
                    default: LOG.info("Example" + choice + "() is not available in this release.");
                }
            }
        } finally {
            closeAll();
            LOG.info("Elapsed time: " + (System.currentTimeMillis() - now)/1000.00 + " seconds.");
        }
    }



























    public static void println(Object x) {
        System.out.println(x);
    }
    static void printRows(RepositoryResult<Statement> rows) throws Exception {
        while (rows.hasNext()) {
            println(rows.next());
        }
        rows.close();
    }
    static void printRows(String headerMsg, int limit, RepositoryResult<Statement> rows) throws Exception {
        println(headerMsg);
        int count = 0;
        while (count < limit && rows.hasNext()) {
            println(rows.next());
            count++;
        }
        println("Number of results: " + count);
        rows.close();
    }
    static void printRows(String headerMsg, TupleQueryResult rows) throws Exception {
        println(headerMsg);
        try {
            while (rows.hasNext()) {
                println(rows.next());
            }
        } finally {
            rows.close();
        }
    }
    private static List<RepositoryConnection> toClose = new ArrayList<RepositoryConnection>();
    private static void closeBeforeExit(RepositoryConnection conn) {
        toClose.add(conn);
    }
    static void close(RepositoryConnection conn) {
        try {
            conn.close();
        } catch (Exception e) {
            System.err.println("Error closing repository connection: " + e);
            e.printStackTrace();
        }
    }
    private static void closeAll() {
        while (!toClose.isEmpty()) {
            RepositoryConnection conn = toClose.get(0);
            close(conn);
            while (toClose.remove(conn)) {
                // ...
            }
        }
    }
}
