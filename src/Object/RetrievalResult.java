/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Object;

import java.util.List;

/**
 *
 * @author Travis Shao
 */
public class RetrievalResult {
    private String docNo;
    private Double score;
    private List<String> contentList;

    public RetrievalResult() {
    }

    public RetrievalResult(String docNo, Double score, List<String> contentList) {
        this.docNo = docNo;
        this.score = score;
        this.contentList = contentList;
    }

    public String getDocNo() {
        return docNo;
    }

    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public List<String> getContentList() {
        return contentList;
    }

    public void setContentList(List<String> contentList) {
        this.contentList = contentList;
    }
}
