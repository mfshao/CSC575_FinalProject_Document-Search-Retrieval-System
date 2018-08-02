/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import Object.DocumentSet;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 *
 * @author Travis Shao
 */
public class IndicesImporter {

    private final File inputFile;

    public IndicesImporter(File inputFile) {
        this.inputFile = inputFile;
    }

    public DocumentSet importFile() throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(inputFile);
        Element rootElement = document.getRootElement();
        List<Element> documentList = rootElement.getChildren("document");

        HashMap<String, HashMap> indicesMatrix = new HashMap();
        HashMap<String, LinkedList> documentMatrix = new HashMap();

        for (int temp = 0; temp < documentList.size(); temp++) {
            Element doc = documentList.get(temp);
            String docNo = doc.getChild("docno").getText();
            Element indices = doc.getChild("index");

            List<Element> termList = indices.getChildren("term");
            List<Element> valueList = indices.getChildren("value");
            HashMap<String, Double> termMatrix = new HashMap();

            for (int i = 0; i < termList.size(); i++) {
                termMatrix.put(termList.get(i).getText(), Double.parseDouble(valueList.get(i).getText()));
            }
            indicesMatrix.put(docNo, termMatrix);

            List<Element> contentList = doc.getChildren("content");
            LinkedList<String> contents = new LinkedList();
            for (int i = 0; i < contentList.size(); i++) {
                contents.add(contentList.get(i).getText());
            }
            documentMatrix.put(docNo, contents);
        }

        Element weightElement = rootElement.getChild("weight");
        List<Element> termList = weightElement.getChildren("term");
        List<Element> valueList = weightElement.getChildren("value");
        HashMap<String, Double> weightMatrix = new HashMap();

        for (int i = 0; i < termList.size(); i++) {
            weightMatrix.put(termList.get(i).getText(), Double.parseDouble(valueList.get(i).getText()));
        }

        return new DocumentSet(indicesMatrix, documentMatrix, weightMatrix);
    }
}
