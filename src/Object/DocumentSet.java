/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Object;

import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author Travis Shao
 */
public class DocumentSet {
    private HashMap<String, HashMap> indicesMatrix;
    private HashMap<String, LinkedList> documentMatrix;
    private HashMap<String, Double> weightMatrix;

    public DocumentSet() {
    }
    
    public DocumentSet(HashMap<String, HashMap> indicesMatrix, HashMap<String, LinkedList> documentMatrix, HashMap<String, Double> weightMatrix) {
        this.indicesMatrix = indicesMatrix;
        this.documentMatrix = documentMatrix;
        this.weightMatrix = weightMatrix;
    }

    public HashMap<String, HashMap> getIndicesMatrix() {
        return indicesMatrix;
    }

    public void setIndicesMatrix(HashMap<String, HashMap> indicesMatrix) {
        this.indicesMatrix = indicesMatrix;
    }

    public HashMap<String, LinkedList> getDocumentMatrix() {
        return documentMatrix;
    }

    public void setDocumentMatrix(HashMap<String, LinkedList> documentMatrix) {
        this.documentMatrix = documentMatrix;
    }

    public HashMap<String, Double> getWeightMatrix() {
        return weightMatrix;
    }

    public void setWeightMatrix(HashMap<String, Double> weightMatrix) {
        this.weightMatrix = weightMatrix;
    }
}
