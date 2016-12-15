package PDFImportDataManager.Controllers;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

class ControllerHelper {
    File chooseFile(String windowTitle, boolean isSaveDialog, Stage currStage) {
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
    boolean fileHasProperties(File fileToCheck, String properties) {
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

    void notifyUser(String dialogText){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        //alert.setTitle("");
        alert.setHeaderText(null);
        alert.setContentText(dialogText);
        alert.showAndWait();
    }

}
