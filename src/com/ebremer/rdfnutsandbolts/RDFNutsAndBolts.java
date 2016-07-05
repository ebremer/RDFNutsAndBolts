package com.ebremer.rdfnutsandbolts;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;

/**
 *
 * @author erich
 */
public class RDFNutsAndBolts {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Dataset dataset = TDBFactory.createDataset("tdb");
        dataset.begin(ReadWrite.WRITE);
        Model tdb = dataset.getDefaultModel();
    }
    
}
