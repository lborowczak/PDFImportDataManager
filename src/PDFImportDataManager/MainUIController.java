package PDFImportDataManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class MainUIController {


    private DataManager mainManager;
    private Stage currStage;

    @FXML
    private Accordion mainAccordion;


    void setData(DataManager manager, Stage stage) {
        mainManager = manager;
        currStage = stage;
    }

    public void loadDB(){
        loadData();
    }


    public void onImportPDFMenuItemPressed(ActionEvent actionEvent) {

    }

    public void onCloseMenuItemPressed(ActionEvent actionEvent) {

    }


    public void onAboutMenuItemPressed(ActionEvent actionEvent) {
        notifyUser("PDF Import Data Manager Version 1.0");
    }


    private void notifyUser(String dialogText){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        //alert.setTitle("");
        alert.setHeaderText(null);
        alert.setContentText(dialogText);
        alert.showAndWait();
    }


    private File chooseFile(String windowTitle, boolean isSaveDialog) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(windowTitle);
        if (isSaveDialog) {
            return fileChooser.showSaveDialog(currStage);
        }
        else {
            return fileChooser.showOpenDialog(currStage);
        }
    }

    private void loadData(){
        //Data structure:
        //Map<Year, Map<MonthName, Map<Week, entryID>
        Map<Integer, Map<String, Map<String, String>>> entryList = mainManager.getOverview();

        entryList.forEach( (k,v) -> addYearAccordion(k, v, mainAccordion));
        //List<>
        //Howto: ID of entry == id of list item
        //mainManager.getOverview();

    }

    private void addYearAccordion(Integer year, Map<String, Map<String, String>> months, Accordion accordionToPopulate) {
        Accordion yearAccordion = new Accordion();
        TitledPane yearPane = new TitledPane(year.toString(), yearAccordion);
        months.forEach( (k, v) -> addMonthTitledPane(k, v, yearAccordion));
        accordionToPopulate.getPanes().add(yearPane);

    }

    private void addMonthTitledPane(String month, Map<String, String> monthEntries, Accordion accordionToPopulate) {
        ListView<listItem> monthEntriesList = new ListView<>();
        TitledPane monthPane = new TitledPane(month, monthEntriesList);
        monthEntries.forEach( (k, v) -> monthEntriesList.getItems().add(new listItem(k, v)));
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

