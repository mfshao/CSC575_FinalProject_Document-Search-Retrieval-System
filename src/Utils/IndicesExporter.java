/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import Object.DocumentSet;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 *
 * @author Travis Shao
 */
public class IndicesExporter {

    private final DocumentSet docSet;
    private final File outputFile;

    public IndicesExporter(DocumentSet docSet, File outputFile) {
        this.docSet = docSet;
        this.outputFile = outputFile;
    }

    public void export() throws IOException {
        Element rootElement = new Element("root");
        Document doc = new Document(rootElement);
        HashMap<String, HashMap> indicesMatrix = this.docSet.getIndicesMatrix();
        HashMap<String, LinkedList> documentMatrix = this.docSet.getDocumentMatrix();

        for (String docNo : documentMatrix.keySet()) {
            Element documentElement = new Element("document");

            Element docNoElement = new Element("docno");
            docNoElement.setText(docNo);
            documentElement.addContent(docNoElement);

            Element indexElement = new Element("index");
            HashMap<String, Double> termMatrix = indicesMatrix.get(docNo);
            for (String term : termMatrix.keySet()) {
                Element termElement = new Element("term");
                termElement.setText(term);
                Element valueElement = new Element("value");
                valueElement.setText(termMatrix.get(term).toString());

                indexElement.addContent(termElement);
                indexElement.addContent(valueElement);
            }
            documentElement.addContent(indexElement);

            LinkedList<String> contentList = documentMatrix.get(docNo);
            for (String content : contentList) {
                Element contentElement = new Element("content");
                contentElement.setText(content);
                documentElement.addContent(contentElement);
            }
            doc.getRootElement().addContent(documentElement);
        }

        Element weightElement = new Element("weight");
        HashMap<String, Double> weightMatrix = this.docSet.getWeightMatrix();
        for (String term : weightMatrix.keySet()) {
            Element termElement = new Element("term");
            termElement.setText(term);
            Element valueElement = new Element("value");
            valueElement.setText(weightMatrix.get(term).toString());
            weightElement.addContent(termElement);
            weightElement.addContent(valueElement);
        }
        doc.getRootElement().addContent(weightElement);

        XMLOutputter xmlOutput = new XMLOutputter();
        xmlOutput.setFormat(Format.getPrettyFormat());
        xmlOutput.output(doc, new FileWriter(this.outputFile));
    }
}
