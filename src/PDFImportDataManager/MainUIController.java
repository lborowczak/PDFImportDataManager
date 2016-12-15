package PDFImportDataManager;

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

    @FXML
    private Accordion mainAccordion;
    @FXML private Text entryInfoText;


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
        currStage.close();
    }


    public void onAboutMenuItemPressed(ActionEvent actionEvent) {
        notifyUser("PDF Import Data Manager Version 1.0");
    }



    public void onGenerateReportPressed(ActionEvent actionEvent) {
        //
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

        ObservableList<listItem> monthEntriesList = FXCollections.observableArrayList();
        final ListView<listItem> monthEntriesListView = new ListView<listItem>(monthEntriesList);
        monthEntriesListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                listItem clickedItem = monthEntriesListView.getSelectionModel().getSelectedItem();
                if (clickedItem == null){
                    return;
                }
                entryInfoText.setText(clickedItem.toString());
            }
        });


        TitledPane monthPane = new TitledPane(month, monthEntriesListView);
        monthEntries.forEach( (k, v) -> monthEntriesListView.getItems().add(new listItem(k, v)));


        /*
        monthEntriesListView.setCellFactory(new Callback<ListView<listItem>, ListCell<listItem>>(){
            public ListCell<listItem> call(ListView<listItem> param) {
                final Label leadLbl = new Label();
                final Tooltip tooltip = new Tooltip();
                final ListCell<listItem> cell = new ListCell<listItem>() {
                    @Override
                    public void updateItem(listItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            System.out.println("SELECTED " + item);
                            setText(item.toString());
                        }
                    }
                }; // ListCell
                return cell;
            }
        });*/


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

