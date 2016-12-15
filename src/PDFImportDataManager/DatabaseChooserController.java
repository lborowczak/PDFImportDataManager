package PDFImportDataManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.sqlite.core.DB;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

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
        if (!mainManager.createDatabase(DBFile)){
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
                    try {
                        Files.deleteIfExists(fileToCheck.toPath());
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
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
