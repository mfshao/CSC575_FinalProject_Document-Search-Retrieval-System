/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Object;

import Utils.GUIUtils;
import java.util.HashMap;
import java.util.LinkedList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

/**
 *
 * @author Travis Shao
 */
public class ListViewChangeListener implements ChangeListener<String> {

    private final DocumentSet docSet;
    private final TextArea resultTextArea;

    public ListViewChangeListener(DocumentSet docSet, TextArea resultTextArea) {
        this.docSet = docSet;
        this.resultTextArea = resultTextArea;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (newValue != null && !newValue.isEmpty()) {
            String[] labels = newValue.split("\\s");
            if (labels.length < 4) {
                GUIUtils.displaySimpleDialog(Alert.AlertType.ERROR, "Parse ListView item error!");
                return;
            }
            String docNo = labels[1];
            String score = labels[2];
            HashMap<String, LinkedList> documentMatrix = this.docSet.getDocumentMatrix();
            LinkedList<String> contentList = documentMatrix.get(docNo);

            StringBuilder sb = new StringBuilder();
            sb.append("DocNo: ").append(docNo).append("\n");
            sb.append(labels[2]).append(" ").append(labels[3]).append("\n\n");
            sb.append("Content:\n");

            if (contentList != null) {
                for (String content : contentList) {
                    sb.append(content);
                    sb.append("\n");
                }
            }

            this.resultTextArea.setText(sb.toString());
        }
    }
}
