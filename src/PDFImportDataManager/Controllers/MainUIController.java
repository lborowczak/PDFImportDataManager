package PDFImportDataManager.Controllers;

import PDFImportDataManager.DataManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Map;

public class MainUIController {


    private DataManager mainManager;
    private Stage currStage;
    private ControllerHelper helper = new ControllerHelper();
    private listItem currItem = null;

    @FXML private Accordion mainAccordion;
    @FXML private Text entryInfoText;
    @FXML private Button generateReportButton;
    @FXML private Button editEntryButton;
    @FXML private Button deleteEntryButton;


    public void setData(DataManager manager, Stage stage) {
        mainManager = manager;
        currStage = stage;
    }

    public void loadDB(){
        loadData();
    }


    public void onImportPDFMenuItemPressed(ActionEvent actionEvent) {
        File PDFFile = helper.chooseFile("Select PDF File...", false, currStage);

        //e = error importing PDF file, c = data checking error
        switch (mainManager.importPDF(PDFFile)) {
            case 'e':
                helper.notifyUser("An error occurred when importing the PDF file.");
                return;
            case 'c':
                helper.notifyUser("Warning: The extracted data values did not match.");
                break;
            case 'g':
                helper.notifyUser("PDF imported correctly.");
        }

    }

    public void onCloseMenuItemPressed(ActionEvent actionEvent) {
        currStage.close();
    }


    public void onAboutMenuItemPressed(ActionEvent actionEvent) {
        helper.notifyUser("PDF Import Data Manager Version 1.0");
    }



    public void onGenerateReportPressed(ActionEvent actionEvent) {
        File PDFFile = helper.chooseFile("Save PDF file as...", true, currStage);
        if (PDFFile == null){
            return;
        }
        if (!helper.fileHasProperties(PDFFile, "c")){
            helper.notifyUser("Cannot write to this file. It may be on a write-protected device.");
            return;
        }
        mainManager.generateReport(PDFFile, currItem.getIDString());
    }


    public void onEditEntryPressed(ActionEvent actionEvent) {
    }

    public void onDeleteEntryPressed(ActionEvent actionEvent) {
    }


    private void loadData(){
        //Data structure:
        //Map<Year, Map<MonthName, Map<WeekString, entryID>
        Map<Integer, Map<String, Map<String, String>>> entryList = mainManager.getOverview();

        entryList.forEach( (k,v) -> addYearAccordion(k, v, mainAccordion));

    }

    private void addYearAccordion(Integer year, Map<String, Map<String, String>> months, Accordion accordionToPopulate) {
        Accordion yearAccordion = new Accordion();
        TitledPane yearPane = new TitledPane(year.toString(), yearAccordion);
        months.forEach( (k, v) -> addMonthTitledPane(k, v, yearAccordion));
        accordionToPopulate.getPanes().add(yearPane);

    }

    private void addMonthTitledPane(String month, Map<String, String> monthEntries, Accordion accordionToPopulate) {

        ObservableList<listItem> monthEntriesList = FXCollections.observableArrayList();
        final ListView<listItem> monthEntriesListView = new ListView<listItem>(monthEntriesList);
        monthEntriesListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                listItem clickedItem = monthEntriesListView.getSelectionModel().getSelectedItem();
                if (clickedItem == null){
                    return;
                }
                currItem = clickedItem;
                entryInfoText.setText(mainManager.getFormattedEntryData(clickedItem.getIDString()));
                generateReportButton.setDisable(false);
                editEntryButton.setDisable(false);
                deleteEntryButton.setDisable(false);
            }
        });


        TitledPane monthPane = new TitledPane(month, monthEntriesListView);
        monthEntries.forEach( (k, v) -> monthEntriesListView.getItems().add(new listItem(k, v)));


        accordionToPopulate.getPanes().add(monthPane);
    }

    private class listItem{
        private String displayString;
        private String IDString;

        listItem(String displayString, String IDString){
            this.displayString = displayString;
            this.IDString = IDString;
        }

        public String getIDString(){
            return IDString;
        }

        @Override
        public String toString() {
            return displayString;
        }

    }

}

