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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import javafx.scene.control.Alert;
import opennlp.tools.stemmer.PorterStemmer;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 *
 * @author Travis Shao
 */
public class IndicesGenerator {

    File inputFile;
    HashMap<String, HashMap> indicesMatrix = new HashMap();
    int mode;
    DocumentSet docSet = new DocumentSet();

    public IndicesGenerator(File inputFile, int mode) {
        this.inputFile = inputFile;
        this.mode = mode;
    }

    public DocumentSet process() throws JDOMException, IOException {
        initializeDocTermTable();
        switch (this.mode) {
            case 0:
                createTFIDFIndices();
                break;
            case 1:
                createSNRIndices();
                break;
            default:
                GUIUtils.displaySimpleDialog(Alert.AlertType.ERROR, "Invalid indexing option!");
                break;
        }
        docSet.setIndicesMatrix(this.indicesMatrix);
        return docSet;
    }

    private void initializeDocTermTable() throws JDOMException, IOException {
        PorterStemmer stemmer = new PorterStemmer();
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(inputFile);
        Element rootElement = document.getRootElement();
        List<Element> docList = rootElement.getChildren();
        HashMap<String, LinkedList> documentMatrix = new HashMap();

        for (int temp = 0; temp < docList.size(); temp++) {
            Element doc = docList.get(temp);
            String docNo = doc.getChild("title").getChild("docno").getText();
            HashMap<String, Double> termMatrix = new HashMap();

            LinkedList<String> docContent = new LinkedList();
            docContent.add(doc.getChild("title").getChild("tag").getText());
            docContent.add(doc.getChild("desc").getChild("tag").getText());
            documentMatrix.put(docNo, docContent);

            String rawWords = doc.getChild("title").getChild("tag").getText() + " " + doc.getChild("desc").getChild("tag").getText();
            rawWords = rawWords.trim().replaceAll("[^A-Za-z0-9 ]", "").replaceAll(" +", " ").toLowerCase();
            String[] docWords = rawWords.split("\\s");

            for (String docWord : docWords) {
                String stemmedWord = stemmer.stem(docWord);
                if (termMatrix.containsKey(stemmedWord)) {
                    termMatrix.put(stemmedWord, termMatrix.get(stemmedWord) + 1.0);
                } else {
                    termMatrix.put(stemmedWord, 1.0);
                }
            }
            indicesMatrix.put(docNo, termMatrix);
            documentMatrix.put(docNo, docContent);
            docSet.setDocumentMatrix(documentMatrix);
        }
    }

    private void createTFIDFIndices() {
        HashMap<String, HashSet> tempDFMatrix = new HashMap();
        HashMap<String, Double> idfMatrix = new HashMap();

        for (String docNo : indicesMatrix.keySet()) {
            HashMap<String, Double> termMatrix = indicesMatrix.get(docNo);

            for (String term : termMatrix.keySet()) {
                HashSet<String> hSet;
                if (tempDFMatrix.containsKey(term)) {
                    hSet = tempDFMatrix.get(term);
                } else {
                    hSet = new HashSet();
                }
                hSet.add(docNo);
                tempDFMatrix.put(term, hSet);
            }
        }

        double n = tempDFMatrix.size();
        for (String term : tempDFMatrix.keySet()) {
            double df = tempDFMatrix.get(term).size();
            double idf = Math.log(n / df) / Math.log(2);
            idfMatrix.put(term, idf);
        }
        docSet.setWeightMatrix(idfMatrix);
        
        for (String docNo : indicesMatrix.keySet()) {
            HashMap<String, Double> termMatrix = indicesMatrix.get(docNo);

            for (String term : termMatrix.keySet()) {
                termMatrix.put(term, termMatrix.get(term) * idfMatrix.get(term));
            }
            indicesMatrix.put(docNo, termMatrix);
        }
    }

    private void createSNRIndices() {
        HashMap<String, Double> totalTFMatrix = new HashMap();
        for (String docNo : indicesMatrix.keySet()) {
            HashMap<String, Double> termMatrix = indicesMatrix.get(docNo);

            for (String term : termMatrix.keySet()) {
                if (totalTFMatrix.containsKey(term)) {
                    totalTFMatrix.put(term, totalTFMatrix.get(term) + termMatrix.get(term));
                } else {
                    totalTFMatrix.put(term, termMatrix.get(term));
                }
            }
        }

        HashMap<String, Double> signalMatrix = new HashMap();
        for (String docNo : indicesMatrix.keySet()) {
            HashMap<String, Double> termMatrix = indicesMatrix.get(docNo);

            for (String term : termMatrix.keySet()) {
                double info = (termMatrix.get(term) / totalTFMatrix.get(term)) * (Math.log(termMatrix.get(term) / totalTFMatrix.get(term)) / Math.log(2));
                if (signalMatrix.containsKey(term)) {
                    signalMatrix.put(term, signalMatrix.get(term) + info);
                } else {
                    signalMatrix.put(term, info);
                }
            }
        }

        for (String term : signalMatrix.keySet()) {
            signalMatrix.put(term, ((Math.log(totalTFMatrix.get(term)) / Math.log(2)) + signalMatrix.get(term)));
        }
        docSet.setWeightMatrix(signalMatrix);

        for (String docNo : indicesMatrix.keySet()) {
            HashMap<String, Double> termMatrix = indicesMatrix.get(docNo);

            for (String term : termMatrix.keySet()) {
                termMatrix.put(term, termMatrix.get(term) * signalMatrix.get(term));
            }
            indicesMatrix.put(docNo, termMatrix);
        }
    }
}
