package PDFImportDataManager.Controllers;

import PDFImportDataManager.DataManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DatabaseChooserController {

    private DataManager mainManager = new DataManager();
    private ControllerHelper helper = new ControllerHelper();

    private Stage currStage;

    public void onLoadDatabasePressed(ActionEvent actionEvent) {
        File DBFile = helper.chooseFile("Open database...", false, currStage);

        if (DBFile == null){
            return;
        }
        if (!helper.fileHasProperties(DBFile, "erw")){
            helper.notifyUser("This file does not exist or cannot be used.");
            return;
        }
        onDatabaseChosen(DBFile);
    }


    public void onCreateDatabasePressed(ActionEvent actionEvent) {

        File DBFile = helper.chooseFile("Create new database...", true, currStage);

        //JavaFX UI already asks the user if they want to replace the file, so we don't have to.

        if (DBFile == null){
            return;
        }
        if (!helper.fileHasProperties(DBFile, "c")){
            helper.notifyUser("Cannot write to this file. It may be on a write-protected device.");
            return;
        }

        //Show window for user to enter company info
        Dialog<Map<String, String>> enterCompanyInfoDialog = createCompanyInfoDialog();
        Optional<Map<String, String>> companyInfo = enterCompanyInfoDialog.showAndWait();

        if (!companyInfo.isPresent()){
            return;
        }

        try {
            Files.deleteIfExists(DBFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!mainManager.createDatabase(DBFile, companyInfo.get())){
            helper.notifyUser("There was a problem creating the database.");
            return;
        }

        helper.notifyUser("The new database has been created successfully.");

    }

    public void onQuitButtonPressed(ActionEvent actionEvent) {
        currStage.close();
    }



/**********************************************************************************************************************/
//Helper methods

    public void setStage(Stage stage) {
    currStage = stage;
}


    private Dialog<Map<String, String>> createCompanyInfoDialog(){
        Dialog<Map<String, String>> enterCompanyInfoDialog = new Dialog<>();
        enterCompanyInfoDialog.setHeaderText("Enter the company information");
        enterCompanyInfoDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        GridPane dialogGridPane = new GridPane();
        dialogGridPane.setHgap(10);
        dialogGridPane.setVgap(10);
        dialogGridPane.setAlignment(Pos.CENTER);
        dialogGridPane.add(new Label("Company Name: "), 0, 0);
        dialogGridPane.add(new Label("Company EIN: "), 0, 1);
        dialogGridPane.add(new Label("Company PIN: "), 0, 2);
        TextField companyNameField = new TextField();
        TextField companyEINField = new TextField();
        TextField companyPINField = new TextField();
        GridPane.setConstraints(companyNameField, 1, 0);
        GridPane.setConstraints(companyEINField, 1, 1);
        GridPane.setConstraints(companyPINField, 1, 2);
        dialogGridPane.getChildren().addAll(companyNameField, companyEINField, companyPINField);
        enterCompanyInfoDialog.getDialogPane().setContent(dialogGridPane);

        enterCompanyInfoDialog.setResultConverter(clickedButton -> {
            if (clickedButton == ButtonType.OK) {
                Map<String, String> tmpReturnMap = new HashMap<String, String>();
                tmpReturnMap.put("Company_Name", companyNameField.getText());
                tmpReturnMap.put("Company_EIN", companyEINField.getText());
                tmpReturnMap.put("Company_PIN", companyPINField.getText());
                return tmpReturnMap;
            }
            return null;
        });

        return enterCompanyInfoDialog;
    }



    private void onDatabaseChosen(File DBFile) {
        if (!mainManager.openDatabase(DBFile)) {
            helper.notifyUser("Database could not be opened.");
            return;
        }

        //Switch to new window
        try {
            FXMLLoader newLoader = new FXMLLoader(getClass().getResource("layouts/PDFImportDataManagerUI.fxml"));
            Stage newStage = new Stage(StageStyle.DECORATED);

            Parent newRoot = (Parent)newLoader.load();
            newStage.setScene(new Scene(newRoot));

            //Pass data to new controller
            MainUIController controller = newLoader.<MainUIController>getController();
            controller.setData(mainManager, newStage);
            controller.loadDB();

            currStage.hide();
            newStage.show();
            currStage.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
