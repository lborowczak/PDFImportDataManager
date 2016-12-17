package PDFImportDataManager.Controllers;

import PDFImportDataManager.DataManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

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
            case 'i':
                helper.notifyUser("The data could not be imported to the database.");
                return;
            case 'g':
                helper.notifyUser("PDF imported correctly.");
        }
        resetView();

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
        if (!mainManager.generateReport(PDFFile, currItem.getIDString())){
            helper.notifyUser("Error occurred when generating report.");
            return;
        }
        helper.notifyUser("Report generated successfully.");
    }


    public void onEditEntryPressed(ActionEvent actionEvent) {
        Dialog<List<Map<String, Integer>>> editDialog = createEntryEditorDialog(mainManager.getEntryData(currItem.getIDString()));
        Optional<List<Map<String, Integer>>> editedData = editDialog.showAndWait();
        if (editedData.isPresent()){
            if (editedData.get() != null){
                mainManager.updateEntry(currItem.getIDString(), editedData.get());
                resetView();
            }
        }
    }

    public void onDeleteEntryPressed(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Confirm entry deletion");
        alert.setContentText("Are you sure you want to delete this entry?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == ButtonType.OK) {
                mainManager.deleteEntry(currItem.getIDString());
                resetView();
            }
        }
    }

    private void resetView(){
        mainAccordion.getPanes().removeAll(mainAccordion.getPanes());
        loadData();
    }

    private void loadData(){
        //Data structure:
        //Map<Year, Map<MonthName, Map<WeekString, entryID>
        generateReportButton.setDisable(true);
        editEntryButton.setDisable(true);
        deleteEntryButton.setDisable(true);
        entryInfoText.setText("No entry selected.");

        Map<Integer, Map<String, Map<String, String>>> entryList = mainManager.getOverview();
        entryList.forEach( (k,v) -> addYearAccordion(k, v, mainAccordion));
    }

    private void addYearAccordion(Integer year, Map<String, Map<String, String>> months, Accordion accordionToPopulate) {
        //System.out.println("Entries:" + months);
        Accordion yearAccordion = new Accordion();
        TitledPane yearPane = new TitledPane(year.toString(), yearAccordion);
        months.forEach( (k, v) -> addMonthTitledPane(k, v, yearAccordion));
        accordionToPopulate.getPanes().add(yearPane);

    }

    private void addMonthTitledPane(String month, Map<String, String> monthEntries, Accordion accordionToPopulate) {
        //System.out.println("Entries:" + monthEntries);
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


    private Dialog<List<Map<String, Integer>>> createEntryEditorDialog(List<Map<String, Integer>> currDataList){
        Map<String, Integer> currData = currDataList.get(0);
        Map<String, Integer> extraData = currDataList.get(1);

        //Create DecimalFormatter to round numbers to 2 digits
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.HALF_UP);

        Dialog<List<Map<String, Integer>>> editEntryDialog = new Dialog<>();
        editEntryDialog.setHeaderText("Edit entry");
        editEntryDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        GridPane dialogGridPane = new GridPane();
        dialogGridPane.setHgap(10);
        dialogGridPane.setVgap(10);
        dialogGridPane.setAlignment(Pos.CENTER);
        dialogGridPane.add(new Label("Start date day: "), 0, 0);
        dialogGridPane.add(new Label("Start date month: "), 0, 1);
        dialogGridPane.add(new Label("Start date year: "), 0, 2);
        dialogGridPane.add(new Label("End date day: "), 0, 3);
        dialogGridPane.add(new Label("End date month: "), 0, 4);
        dialogGridPane.add(new Label("End date year: "), 0, 5);
        dialogGridPane.add(new Label("Pay date day: "), 0, 6);
        dialogGridPane.add(new Label("Pay date month: "), 0, 7);
        dialogGridPane.add(new Label("Pay date year: "), 0, 8);
        dialogGridPane.add(new Label("Gross Pay: "), 0, 9);
        dialogGridPane.add(new Label("Federal Withholding"), 0, 10);
        dialogGridPane.add(new Label("State Withholding"), 0, 11);
        dialogGridPane.add(new Label("Medicare Withholding"), 0, 12);
        dialogGridPane.add(new Label("Social Security Withholding"), 0, 13);

        /*
        int tmpInt = 14;
        for(Map.Entry<String, Integer> entry : extraData.entrySet()){
            dialogGridPane.add(new Label(entry.getKey()), 0, tmpInt);
            TextField tmpField = new TextField(df.format(entry.getValue() / 100.0 ));
            GridPane.setConstraints(tmpField, 1, tmpInt);
            dialogGridPane.getChildren().add(tmpField);
            tmpInt++;
        };*/

        TextField startDayField = new TextField(currData.get("Start_Day").toString());
        TextField startMonthField = new TextField(currData.get("Start_Month").toString());
        TextField startYearField = new TextField(currData.get("Start_Year").toString());
        TextField endDayField = new TextField(currData.get("End_Day").toString());
        TextField endMonthField = new TextField(currData.get("End_Month").toString());
        TextField endYearField = new TextField(currData.get("End_Year").toString());
        TextField payDayField = new TextField(currData.get("Pay_Day").toString());
        TextField payMonthField = new TextField(currData.get("Pay_Month").toString());
        TextField payYearField = new TextField(currData.get("Pay_Year").toString());
        TextField grossPayField = new TextField(df.format(currData.get("Gross_Pay") / 100.0));
        TextField federalWithholdingField = new TextField(df.format(currData.get("Federal_Withholding") / 100.0));
        TextField stateWithholdingField = new TextField(df.format(currData.get("State_Withholding") / 100.0 ));
        TextField medicareEmployeeWithholdingField = new TextField(df.format(currData.get("Medicare_Employee_Withholding") / 100.0));
        TextField socialSecurityWithholdingField = new TextField(df.format(currData.get("Social_Security_Employee_Withholding") / 100.0));


        GridPane.setConstraints(startDayField, 1, 0);
        GridPane.setConstraints(startMonthField, 1, 1);
        GridPane.setConstraints(startYearField, 1, 2);
        GridPane.setConstraints(endDayField, 1, 3);
        GridPane.setConstraints(endMonthField, 1, 4);
        GridPane.setConstraints(endYearField, 1, 5);
        GridPane.setConstraints(payDayField, 1, 6);
        GridPane.setConstraints(payMonthField, 1, 7);
        GridPane.setConstraints(payYearField, 1, 8);
        GridPane.setConstraints(grossPayField, 1, 9);
        GridPane.setConstraints(federalWithholdingField, 1, 10);
        GridPane.setConstraints(stateWithholdingField, 1, 11);
        GridPane.setConstraints(medicareEmployeeWithholdingField, 1, 12);
        GridPane.setConstraints(socialSecurityWithholdingField, 1, 13);
        dialogGridPane.getChildren().addAll(startDayField, startMonthField, startYearField,
                endDayField, endMonthField, endYearField,
                payDayField, payMonthField, payYearField,
                grossPayField, federalWithholdingField, stateWithholdingField,
                medicareEmployeeWithholdingField, socialSecurityWithholdingField);
        editEntryDialog.getDialogPane().setContent(dialogGridPane);

        editEntryDialog.setResultConverter(clickedButton -> {
            if (clickedButton == ButtonType.OK) {
                List<Map<String, Integer>> tmpReturnMap = new ArrayList<Map<String, Integer>>();
                Map<String, Integer> tmpDataMap = new HashMap<String, Integer>();
                tmpDataMap.put("Start_Day", Integer.parseInt(startDayField.getText()));
                tmpDataMap.put("Start_Month", Integer.parseInt(startMonthField.getText()));
                tmpDataMap.put("Start_Year", Integer.parseInt(startYearField.getText()));
                tmpDataMap.put("End_Day", Integer.parseInt(endDayField.getText()));
                tmpDataMap.put("End_Month", Integer.parseInt(endMonthField.getText()));
                tmpDataMap.put("End_Year", Integer.parseInt(endYearField.getText()));
                tmpDataMap.put("Pay_Day", Integer.parseInt(payDayField.getText()));
                tmpDataMap.put("Pay_Month", Integer.parseInt(payMonthField.getText()));
                tmpDataMap.put("Pay_Year", Integer.parseInt(payYearField.getText())) ;
                tmpDataMap.put("Gross_Pay", (int)Math.ceil(Double.parseDouble(grossPayField.getText()) * 100.0));
                tmpDataMap.put("Federal_Withholding", (int)Math.ceil(Double.parseDouble(federalWithholdingField.getText()) * 100.0));
                tmpDataMap.put("State_Withholding", (int)Math.ceil(Double.parseDouble(stateWithholdingField.getText()) * 100.0));
                tmpDataMap.put("Medicare_Employee_Withholding", (int)Math.ceil(Double.parseDouble(medicareEmployeeWithholdingField.getText()) * 100.0));
                tmpDataMap.put("Social_Security_Employee_Withholding", (int)Math.ceil(Double.parseDouble(socialSecurityWithholdingField.getText()) * 100.0));
                tmpReturnMap.add(tmpDataMap);
                tmpReturnMap.add(extraData);

                return tmpReturnMap;
            }
            return null;
        });

        return editEntryDialog;
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

