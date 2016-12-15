package PDFImportDataManager;

import PDFImportDataManager.Controllers.DatabaseChooserController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Load UI
        FXMLLoader UILoader = new FXMLLoader(getClass().getResource("Controllers/layouts/DatabaseChooser.fxml"));
        Parent root = (Parent)UILoader.load();

        //Give controller access to the current stage
        DatabaseChooserController currController = (DatabaseChooserController) UILoader.getController();
        currController.setStage(primaryStage);

        
        primaryStage.setTitle("Open or create a database...");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
