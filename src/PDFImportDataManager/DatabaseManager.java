package PDFImportDataManager;


import java.sql.Connection;
import java.util.List;


public interface DatabaseManager {

    Connection DBConnection = null;

    public boolean createDatabase(String DBFile);
    public boolean openDatabase(String DBFile);
    public List<String> getEntryList();
    public List<String> getEntryInfo(String entryID);
    public List<String> getEntry(String entryID);
    public void removeEntry(String entryID);
    public boolean addEntry(List<String> data);


}
