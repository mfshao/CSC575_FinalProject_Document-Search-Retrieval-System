/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import Object.DocumentSet;
import Object.ListViewChangeListener;
import Object.RetrievalResult;
import Utils.DocumentRetriever;
import Utils.GUIUtils;
import Utils.IndicesExporter;
import Utils.IndicesGenerator;
import Utils.IndicesImporter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jdom2.JDOMException;

/**
 *
 * @author Travis Shao
 */
public class MainGUIController implements Initializable {

    private final ToggleGroup indexingMethodGroup = new ToggleGroup();
    private final ToggleGroup retrieveMethodGroup = new ToggleGroup();
    private DocumentSet docSet;
    private List<RetrievalResult> resultList;
    private ListViewChangeListener lvcl;

    @FXML
    SplitPane splitPane1;

    @FXML
    SplitPane splitPane2;

    @FXML
    AnchorPane split1UpperAnchorPane;

    @FXML
    AnchorPane split1LowerAnchorPane;

    @FXML
    AnchorPane split2UpperAnchorPane;

    @FXML
    AnchorPane split2LowerAnchorPane;

    @FXML
    TextField filePathTextField;

    @FXML
    Button exportButton;

    @FXML
    RadioButton tfidfRadioButton;

    @FXML
    RadioButton snrRadioButton;

    @FXML
    Label loadedMsgLabel;

    @FXML
    Label noLoadedMsgLabel;

    @FXML
    TextField queryTextField;

    @FXML
    RadioButton cosineRadioButton;

    @FXML
    RadioButton simpleRadioButton;

    @FXML
    RadioButton diceRadioButton;

    @FXML
    RadioButton jaccardRadioButton;

    @FXML
    Button retrieveButton;

    @FXML
    ListView resultListView;

    @FXML
    TextArea resultTextArea;

    @FXML
    private void onBrowseButtonClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select XML File");
        fileChooser.setInitialDirectory(
                new File(".\\")
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML", "*.xml"),
                new FileChooser.ExtensionFilter("TXT", "*.txt")
        );
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            try {
                filePathTextField.setText(file.getCanonicalPath());
                noLoadedMsgLabel.setVisible(true);
                loadedMsgLabel.setVisible(false);
                exportButton.setDisable(true);
                retrieveButton.setDisable(true);
                this.docSet = null;
            } catch (IOException ex) {
                GUIUtils.displayExceptionDialog(ex);
                Logger.getLogger(MainGUIController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void onIndexButtonClicked() {
        if (indexingMethodGroup.getSelectedToggle() == null) {
            GUIUtils.displaySimpleDialog(AlertType.INFORMATION, "Please choose an indexing method!");
            return;
        }

        if (filePathTextField.getText().isEmpty()) {
            GUIUtils.displaySimpleDialog(AlertType.INFORMATION, "Please choose a XML data file!");
            return;
        }

        File inputFile = new File(filePathTextField.getText());
        IndicesGenerator ig = new IndicesGenerator(inputFile, (Integer) indexingMethodGroup.getSelectedToggle().getUserData());
        try {
            this.docSet = ig.process();
        } catch (JDOMException | IOException ex) {
            GUIUtils.displayExceptionDialog(ex);
            Logger.getLogger(MainGUIController.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (this.docSet != null && this.docSet.getIndicesMatrix() != null) {
            noLoadedMsgLabel.setVisible(false);
            loadedMsgLabel.setVisible(true);
            exportButton.setDisable(false);
            retrieveButton.setDisable(false);
            GUIUtils.displaySimpleDialog(AlertType.INFORMATION, "Documents indexing completed!");
        } else {
            noLoadedMsgLabel.setVisible(true);
            loadedMsgLabel.setVisible(false);
            exportButton.setDisable(true);
            retrieveButton.setDisable(true);
            GUIUtils.displaySimpleDialog(AlertType.ERROR, "Documents indices load error!");
        }
    }

    @FXML
    private void onExportButtonClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Indeices File");
        fileChooser.setInitialDirectory(
                new File(".\\")
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML", "*.xml"),
                new FileChooser.ExtensionFilter("TXT", "*.txt")
        );
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            IndicesExporter ie = new IndicesExporter(this.docSet, file);
            try {
                ie.export();
                GUIUtils.displaySimpleDialog(AlertType.INFORMATION, "Indices export completed!");
            } catch (IOException ex) {
                GUIUtils.displayExceptionDialog(ex);
                Logger.getLogger(MainGUIController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void onImportButtonClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Indeices File");
        fileChooser.setInitialDirectory(
                new File(".\\")
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML", "*.xml"),
                new FileChooser.ExtensionFilter("TXT", "*.txt")
        );
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            IndicesImporter ii = new IndicesImporter(file);
            try {
                this.docSet = ii.importFile();
                GUIUtils.displaySimpleDialog(AlertType.INFORMATION, "Indices import completed!");
                noLoadedMsgLabel.setVisible(false);
                loadedMsgLabel.setVisible(true);
                exportButton.setDisable(false);
                retrieveButton.setDisable(false);
            } catch (JDOMException | IOException ex) {
                GUIUtils.displayExceptionDialog(ex);
                noLoadedMsgLabel.setVisible(true);
                loadedMsgLabel.setVisible(false);
                exportButton.setDisable(true);
                retrieveButton.setDisable(true);
                Logger.getLogger(MainGUIController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void onRetrieveButtonClicked() {
        if (retrieveMethodGroup.getSelectedToggle() == null) {
            GUIUtils.displaySimpleDialog(AlertType.INFORMATION, "Please choose a retrieve method!");
            return;
        }

        if (queryTextField.getText().isEmpty()) {
            GUIUtils.displaySimpleDialog(AlertType.INFORMATION, "Please input a query!");
            return;
        }

        String rawQuery = queryTextField.getText();
        DocumentRetriever dr = new DocumentRetriever(this.docSet, rawQuery, (Integer) retrieveMethodGroup.getSelectedToggle().getUserData());
        this.resultList = dr.retrieve();
        if (this.resultList.isEmpty()) {
            GUIUtils.displaySimpleDialog(Alert.AlertType.ERROR, "Retrieval failed!");
            return;
        }

        this.populateListView(this.resultList);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        splitPane1.setDividerPositions(0.25);
        split1UpperAnchorPane.maxHeightProperty().bind(splitPane1.heightProperty().multiply(0.25));
        split1UpperAnchorPane.minHeightProperty().bind(splitPane1.heightProperty().multiply(0.25));

        splitPane2.setDividerPositions(0.25);
        split2UpperAnchorPane.maxHeightProperty().bind(splitPane2.heightProperty().multiply(0.25));
        split2UpperAnchorPane.minHeightProperty().bind(splitPane2.heightProperty().multiply(0.25));

        tfidfRadioButton.setToggleGroup(indexingMethodGroup);
        tfidfRadioButton.setUserData(0);
        tfidfRadioButton.setSelected(true);
        snrRadioButton.setToggleGroup(indexingMethodGroup);
        snrRadioButton.setUserData(1);

        cosineRadioButton.setToggleGroup(retrieveMethodGroup);
        cosineRadioButton.setUserData(0);
        cosineRadioButton.setSelected(true);
        simpleRadioButton.setToggleGroup(retrieveMethodGroup);
        simpleRadioButton.setUserData(1);
        diceRadioButton.setToggleGroup(retrieveMethodGroup);
        diceRadioButton.setUserData(2);
        jaccardRadioButton.setToggleGroup(retrieveMethodGroup);
        jaccardRadioButton.setUserData(3);

        noLoadedMsgLabel.setVisible(true);
        loadedMsgLabel.setVisible(false);
        exportButton.setDisable(true);
        retrieveButton.setDisable(true);
    }

    private void populateListView(List<RetrievalResult> resultList) {
        ObservableList<String> data = FXCollections.observableArrayList();

        int i = 1;
        DecimalFormat df = new DecimalFormat("#0.00");
        for (RetrievalResult result : resultList) {
            String label = Integer.toString(i) + ". " + result.getDocNo() + " (Score: " + df.format(result.getScore()) + ")";
            data.add(label);
            i++;
        }
        resultListView.setItems(data);
        if (this.lvcl != null) {
            resultListView.getSelectionModel().selectedItemProperty().removeListener(this.lvcl);
        }
        this.lvcl = new ListViewChangeListener(this.docSet, this.resultTextArea);
        resultListView.getSelectionModel().selectedItemProperty().addListener(this.lvcl);
    }
}
