package com.qa.demo.utils.nt_triple;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.qa.demo.utils.io.IOTool;
import org.eclipse.rdf4j.model.*;

import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.query.*;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;

import com.franz.agraph.repository.AGCatalog;
import com.franz.agraph.repository.AGRepository;
import com.franz.agraph.repository.AGRepositoryConnection;
import com.franz.agraph.repository.AGServer;
import com.franz.agraph.repository.AGValueFactory;




/**
 * @Description:
 * @Author: J.Y.Zhang
 * @Date: 2017/12/25
 */
public class AGTest {
    private static final String SERVER_URL = "http://10.61.67.218:10035";
    private static final String CATALOG_ID = "java-catalog";
    private static final String REPOSITORY_ID = "yaotest";
    private static final String USERNAME = "test";
    private static final String PASSWORD = "xyzzy";
    private static final File DATA_DIR = new File(".");

    private static final String FOAF_NS = "http://xmlns.com/foaf/0.1/";

    /**
     * Creating a Repository test
     */
    public static AGRepositoryConnection example1(boolean close)
            throws Exception {
        // Tests getting the repository up.
        println("\nStarting example1().");
        AGServer server = new AGServer(SERVER_URL, USERNAME, PASSWORD);
        try {
            println("Server version: " + server.getVersion());
            println("Server build date: " + server.getBuildDate());
            println("Server revision: " + server.getRevision());
            println("Available catalogs: " + server.listCatalogs());
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

        println("Available repositories in catalog " +
                (catalog.getCatalogName()) + ": " +
                catalog.listRepositories());
        closeAll();
        catalog.deleteRepository(REPOSITORY_ID);
        AGRepository myRepository = catalog.createRepository(REPOSITORY_ID);
        println("Got a repository.");
        myRepository.initialize();
        println("Initialized repository.");
        println("Repository is writable? " + myRepository.isWritable());
        AGRepositoryConnection conn = myRepository.getConnection();
        closeBeforeExit(conn);
        println("Got a connection.");
        println("Repository " + (myRepository.getRepositoryID()) +
                " is up! It contains " + (conn.size()) +
                " statements."
        );
        List<String> indices = conn.listValidIndices();
        println("All valid triple indices: " + indices);
        indices = conn.listIndices();
        println("Current triple indices: " + indices);
        println("Removing graph indices...");
        conn.dropIndex("gospi");
        conn.dropIndex("gposi");
        conn.dropIndex("gspoi");
        indices = conn.listIndices();
        println("Current triple indices: " + indices);
        println("Adding one graph index back in...");
        conn.addIndex("gspoi");
        indices = conn.listIndices();
        println("Current triple indices: " + indices);
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
    public static AGRepositoryConnection example2(boolean close) throws Exception {
        // Asserts some statements and counts them.
        AGRepositoryConnection conn = example1(false);
        AGValueFactory vf = conn.getRepository().getValueFactory();
        println("\nStarting example2().");
        // Create some resources and literals to make statements from.
        IRI alice = vf.createIRI("http://example.org/people/alice");
        IRI bob = vf.createIRI("http://example.org/people/bob");
        IRI name = vf.createIRI("http://example.org/ontology/name");
        IRI person = vf.createIRI("http://example.org/ontology/Person");
        Literal bobsName = vf.createLiteral("Bob");
        Literal alicesName = vf.createLiteral("Alice");
        println("Triple count before inserts: " +
                (conn.size()));
        // Alice's name is "Alice"
        conn.add(alice, name, alicesName);
        // Alice is a person
        conn.add(alice, RDF.TYPE, person);
        //Bob's name is "Bob"
        conn.add(bob, name, bobsName);
        //Bob is a person, too.
        conn.add(bob, RDF.TYPE, person);
        println("Added four triples.");
        println("Triple count after inserts: " +
                (conn.size()));
        RepositoryResult<Statement> result = conn.getStatements(null, null, null, false);
        while (result.hasNext()) {
            Statement st = result.next();
            println(st);
        }
        conn.remove(bob, name, bobsName);
        println("Removed one triple.");
        println("Triple count after deletion: " +
                (conn.size()));
        // put it back so we can continue with other examples
        conn.add(bob, name, bobsName);
        if (close) {
            conn.close();
            conn.getRepository().shutDown();
            return null;
        }
        return conn;
    }
    /**
     * Usage: all
     * Usage: [1-24]+
     */
    public static void main(String[] args) throws Exception {
        ArrayList<String> linesFromFile = new  ArrayList<>();
        linesFromFile = IOTool.readLinesFromFile("C:\\Users\\yaoleo\\IdeaProjects\\DaSanCYuYanZuoYe-TuShuGuanLiXiTong\\src\\main\\resources\\data\\KG\\input\\Entity\\baidu_extra_entities.nt");
        for(String temp:linesFromFile)
        {
            println(temp);
        }
        long now = System.currentTimeMillis();
        List<Integer> choices = new ArrayList<Integer>();
        if (args.length == 0 || args[0].equals("all")) {
            for (int i = 1; i <= 2; i++) {
                choices.add(i);
            }
        } else {
            for (int i = 0; i < args.length; i++) {
                choices.add(Integer.parseInt(args[i]));
            }
        }
        try {
            for (Integer choice : choices) {
                println("\n** Running example " + choice);
                switch(choice) {
                    case 1: example1(true); break;
                    case 2: example2(true); break;
                    default: println("Example" + choice + "() is not available in this release.");
                }
            }
        } finally {
            closeAll();
            println("Elapsed time: " + (System.currentTimeMillis() - now)/1000.00 + " seconds.");
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

    static void close(RepositoryConnection conn) {
        try {
            conn.close();
        } catch (Exception e) {
            System.err.println("Error closing repository connection: " + e);
            e.printStackTrace();
        }
    }

    private static List<RepositoryConnection> toClose = new ArrayList<RepositoryConnection>();

    /**
     * This is just a quick mechanism to make sure all connections get closed.
     */
    private static void closeBeforeExit(RepositoryConnection conn) {
        toClose.add(conn);
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
