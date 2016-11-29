package PDFImportDataManager;


import java.sql.Connection;
import java.util.List;
import java.util.Map;


public interface DatabaseManager {

    Connection DBConnection = null;

    public boolean createDatabase(String DBFile);
    public boolean openDatabase(String DBFile);
    public List<String> getEntryList();

    //
    public Map getEntryInfo(String entryID);
    public Map getCompanyInfo();
    public List<Map> getEntry(String entryID);
    public boolean removeEntry(String entryID);

    public boolean addEntry(List<Map> data);
    //Format:


}
