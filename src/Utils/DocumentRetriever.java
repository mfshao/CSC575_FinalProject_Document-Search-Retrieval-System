/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import Object.DocumentSet;
import Object.RetrievalResult;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javafx.scene.control.Alert;
import opennlp.tools.stemmer.PorterStemmer;

/**
 *
 * @author Travis Shao
 */
public class DocumentRetriever {

    private final DocumentSet docSet;
    private final String rawQuery;
    private String[] queryWords;
    private HashMap<String, Double> queryMatrix;
    private final int mode;
    private final List<RetrievalResult> resultList;

    public DocumentRetriever(DocumentSet docSet, String rawQuery, int mode) {
        this.docSet = docSet;
        this.rawQuery = rawQuery;
        this.mode = mode;
        this.resultList = new LinkedList();
    }

    public List<RetrievalResult> retrieve() {
        initializeQuery();
        switch (this.mode) {
            case 0:
                doCosineRetrieve();
                break;
            case 1:
                doSimpleRetrieve();
                break;
            case 2:
                doDiceRetrieve();
                break;
            case 3:
                doJaccardRetrieve();
                break;
            default:
                GUIUtils.displaySimpleDialog(Alert.AlertType.ERROR, "Invalid retrieval option!");
                break;
        }
        if (!this.resultList.isEmpty()) {
            Collections.sort(resultList, new Comparator<RetrievalResult>() {
                @Override
                public int compare(RetrievalResult o1, RetrievalResult o2) {
                    return o2.getScore().compareTo(o1.getScore());
                }
            });
        }
        return this.resultList;
    }

    private void doCosineRetrieve() {
        HashMap<String, HashMap> indicesMatrix = this.docSet.getIndicesMatrix();
        for (String docNo : indicesMatrix.keySet()) {
            double score;
            HashMap<String, Double> termMatrix = indicesMatrix.get(docNo);
            if (this.sumSq(termMatrix) == 0.0 || this.sumSq(this.queryMatrix) == 0.0) {
                score = 0.0;
            } else {
                score = this.sumProduct(termMatrix, this.queryMatrix) / (Math.sqrt(this.sumSq(termMatrix) * this.sumSq(this.queryMatrix)));
            }
            RetrievalResult result = new RetrievalResult(docNo, score, this.docSet.getDocumentMatrix().get(docNo));
            this.resultList.add(result);
        }
    }

    private void doSimpleRetrieve() {
        HashMap<String, HashMap> indicesMatrix = this.docSet.getIndicesMatrix();
        for (String docNo : indicesMatrix.keySet()) {
            double score;
            HashMap<String, Double> termMatrix = indicesMatrix.get(docNo);
            score = this.sumProduct(termMatrix, this.queryMatrix);
            RetrievalResult result = new RetrievalResult(docNo, score, this.docSet.getDocumentMatrix().get(docNo));
            this.resultList.add(result);
        }
    }

    private void doDiceRetrieve() {
        HashMap<String, HashMap> indicesMatrix = this.docSet.getIndicesMatrix();
        for (String docNo : indicesMatrix.keySet()) {
            double score;
            HashMap<String, Double> termMatrix = indicesMatrix.get(docNo);
            if (this.sumSq(termMatrix) == 0.0 || this.sumSq(this.queryMatrix) == 0.0) {
                score = 0.0;
            } else {
                score = (2 * this.sumProduct(termMatrix, this.queryMatrix)) / (this.sumSq(termMatrix) + this.sumSq(this.queryMatrix));
            }
            RetrievalResult result = new RetrievalResult(docNo, score, this.docSet.getDocumentMatrix().get(docNo));
            this.resultList.add(result);
        }
    }

    private void doJaccardRetrieve() {
        HashMap<String, HashMap> indicesMatrix = this.docSet.getIndicesMatrix();
        for (String docNo : indicesMatrix.keySet()) {
            double score;
            HashMap<String, Double> termMatrix = indicesMatrix.get(docNo);
            if (this.sumSq(termMatrix) == 0.0 || this.sumSq(this.queryMatrix) == 0.0) {
                score = 0.0;
            } else {
                score = this.sumProduct(termMatrix, this.queryMatrix) / (this.sumSq(termMatrix) + this.sumSq(this.queryMatrix) - this.sumProduct(termMatrix, this.queryMatrix));
            }
            RetrievalResult result = new RetrievalResult(docNo, score, this.docSet.getDocumentMatrix().get(docNo));
            this.resultList.add(result);
        }
    }

    private void initializeQuery() {
        PorterStemmer stemmer = new PorterStemmer();
        String query = this.rawQuery.trim().replaceAll("[^A-Za-z0-9 ]", "").replaceAll(" +", " ").toLowerCase();
        this.queryWords = query.split("\\s");

        for (int i = 0; i < this.queryWords.length; i++) {
            this.queryWords[i] = stemmer.stem(this.queryWords[i]);
        }

        this.queryMatrix = new HashMap();
        for (String term : this.queryWords) {
            if (this.queryMatrix.containsKey(term)) {
                this.queryMatrix.put(term, this.queryMatrix.get(term) + 1);
            } else {
                this.queryMatrix.put(term, 1.0);
            }
        }

        HashMap<String, Double> weightMatrix = this.docSet.getWeightMatrix();
        for (String term : this.queryMatrix.keySet()) {
            if (weightMatrix.containsKey(term)) {
                this.queryMatrix.put(term, this.queryMatrix.get(term) * weightMatrix.get(term));
            } else {
                this.queryMatrix.put(term, 0.0);
            }
        }
    }

    private double sumProduct(HashMap<String, Double> docVector, HashMap<String, Double> queryVector) {
        double sum = 0.0;
        for (String term : queryVector.keySet()) {
            if (docVector.containsKey(term)) {
                sum += docVector.get(term) * queryVector.get(term);
            }
        }
        return sum;
    }

    private double sumSq(HashMap<String, Double> vector) {
        double sum = 0.0;
        for (String term : vector.keySet()) {
            sum += Math.pow(vector.get(term), 2.0);
        }
        return sum;
    }
}
