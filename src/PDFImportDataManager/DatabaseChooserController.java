package PDFImportDataManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.sqlite.core.DB;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class DatabaseChooserController {

    private DataManager mainManager = new DataManager();

    private Stage currStage;

    public void onLoadDatabasePressed(ActionEvent actionEvent) {
        File DBFile = chooseFile("Open database...", false);

        if (DBFile == null){
            return;
        }
        if (!fileHasProperties(DBFile, "erw")){
            notifyUser("This file does not exist or cannot be used.");
            return;
        }
        onDatabaseChosen(DBFile);
    }


    public void onCreateDatabasePressed(ActionEvent actionEvent) {

        File DBFile = chooseFile("Create new database...", true);

        //JavaFX UI already asks the user if they want to replace the file, so we don't have to.

        if (DBFile == null){
            return;
        }
        if (!fileHasProperties(DBFile, "c")){
            notifyUser("Cannot write to this file. It may be on a write-protected device.");
            return;
        }

        //Show window for user to enter company info
        Dialog<Map<String, String>> enterCompanyInfoDialog = createCompanyInfoDialog();
        Optional<Map<String, String>> companyInfo = enterCompanyInfoDialog.showAndWait();

        if (!companyInfo.isPresent()){
            return;
        }

        if (!mainManager.createDatabase(DBFile, companyInfo.get())){
            notifyUser("There was a problem creating the database.");
            return;
        }



        notifyUser("The new database has been created successfully.");
    }

    public void onQuitButtonPressed(ActionEvent actionEvent) {
        currStage.close();
    }



/**********************************************************************************************************************/
//Helper methods

    void setStage(Stage stage) {
    currStage = stage;
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

    //Check file properties:
    // e=existence, c=createable, r=readable, w=writeable, x=executable,
    private boolean fileHasProperties(File fileToCheck, String properties) {
        boolean hasProperties = true;
        for (int i = 0; i < properties.length(); i++) {
            switch (properties.charAt(i)) {
                case 'c':
                    //See if a temporary file can be created at the path
                    if (Files.exists(fileToCheck.toPath())){
                        hasProperties = hasProperties && fileHasProperties(fileToCheck, "rw");
                    }
                    else try {
                        Files.createFile(fileToCheck.toPath());
                        Files.delete(fileToCheck.toPath());
                    } catch (IOException e){
                        hasProperties = false;
                    }
                    break;
                case 'e':
                    hasProperties = hasProperties && Files.exists(fileToCheck.toPath());
                    break;
                case 'r':
                    hasProperties = hasProperties && Files.isReadable(fileToCheck.toPath());
                    break;
                case 'w':
                    hasProperties = hasProperties && Files.isWritable(fileToCheck.toPath());
                    break;
                case 'x':
                    hasProperties = hasProperties && Files.isExecutable(fileToCheck.toPath());
                    break;
            }
        }
        return hasProperties;
    }

    private void notifyUser(String dialogText){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        //alert.setTitle("");
        alert.setHeaderText(null);
        alert.setContentText(dialogText);
        alert.showAndWait();
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
            notifyUser("Database could not be opened.");
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
