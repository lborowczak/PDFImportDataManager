package PDFImportDataManager;


import java.time.LocalDate;
import java.util.List;

public class DataManager {

    private DatabaseManager globalDBManager = new SQLiteDatabaseManager();

    public TripleDate<LocalDate> getDates(){
        return null;
    }

    public void openDatabase(String DBFile){
        //TODO add checks for file path existence, or do it in controller?
        globalDBManager.openDatabase(DBFile);
    }

}
