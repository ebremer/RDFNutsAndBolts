package com.ebremer.rdfnutsandbolts;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;
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
        Dataset dataset = TDBFactory.createDataset("\\tdb");
        dataset.begin(ReadWrite.WRITE);
        Model tdb = dataset.getDefaultModel();
        HttpClient httpClient = new HttpClient();
        httpClient.setFollowRedirects(true);
        httpClient.start();
        ContentResponse response = httpClient.newRequest("http://dx.doi.org/10.1007/springerreference_38182")
        .method(HttpMethod.GET)
        .agent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:17.0) Gecko/20100101 Firefox/17.0")
        .accept("text/turtle")
        .send();
        System.out.println(response.getContentAsString());
        httpClient.stop();
    }
    
}
