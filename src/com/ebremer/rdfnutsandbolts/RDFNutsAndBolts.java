package com.ebremer.rdfnutsandbolts;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpMethod;

/**
 *
 * @author erich
 */
public class RDFNutsAndBolts {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        String url = "http://dx.doi.org/10.1093/bioinformatics/btk009";
        System.out.println("\n\nGrabbing "+url+" as RDF Turtle (TTL).....");
        HttpClient httpClient = new HttpClient();
        httpClient.setFollowRedirects(true);
        httpClient.start();
        ContentResponse response = httpClient.newRequest(url)
        .method(HttpMethod.GET)
        .agent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:17.0) Gecko/20100101 Firefox/17.0")
        .accept("text/turtle")
        .send();
        System.out.println(response.getContentAsString());
        
        System.out.println("\n\nread downloaded TTL into Jena Model...");
        String ttl = response.getContentAsString();
        httpClient.stop();
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(ttl.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            System.out.println(ex.toString());
        }
        Model m = ModelFactory.createDefaultModel();
        m.read(is,null,"ttl");
        System.out.println("# of triples downloaded : "+m.size());      
        
        System.out.println("\n\nList all downloaded triples...........");
        String qs = "select ?s ?p ?o where {?s ?p ?o}";
        Query query = QueryFactory.create(qs);
        QueryExecution qe = QueryExecutionFactory.create(query,m);
        ResultSet results = qe.execSelect();
        while (results.hasNext()) {
            QuerySolution soln = results.nextSolution();
            System.out.println(soln);
        }
        
        System.out.println("\n\nList of titles and their dates...........");
        query = QueryFactory.create("select ?s ?title ?date where {?s <http://purl.org/dc/terms/title> ?title; <http://purl.org/dc/terms/date> ?date}");
        qe = QueryExecutionFactory.create(query,m);
        results = qe.execSelect();
        while (results.hasNext()) {
            QuerySolution soln = results.nextSolution();
            System.out.println(soln.get("s").toString()+" ==> "+soln.get("title").asLiteral().toString()+"  "+soln.get("date").asLiteral().getValue());
        }
        
        System.out.println("\n\nLump all objects under one predicate...........");
        String updatestring = "insert {?s <http://www.ebremer.com/lump> ?o} where {?s ?p ?o}"; 
        UpdateRequest request = UpdateFactory.create();
        request.add(updatestring);
        UpdateAction.execute(request,m);
        m.write(System.out, "TTL");
        
        System.out.println("\n\nLoad triples into a Jena TDB Quad store as a particular named graph...");
        Dataset dataset = TDBFactory.createDataset("\\tdb");
        dataset.addNamedModel("<http://www.ebremer.com/original>", m);
        Model org = dataset.getNamedModel("<http://www.ebremer.com/original>");
        System.out.println(org.size());
        
        query = QueryFactory.create("select distinct ?g where {graph ?g {?s ?p ?o}}");
        qe = QueryExecutionFactory.create(query,dataset);
        results = qe.execSelect();
        while (results.hasNext()) {
            QuerySolution soln = results.nextSolution();
            System.out.println("named graph : "+soln.get("g").toString());
        }
    }
    
}
