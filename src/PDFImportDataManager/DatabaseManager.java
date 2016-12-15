package PDFImportDataManager;


import java.io.File;
import java.sql.Connection;
import java.util.List;
import java.util.Map;


public interface DatabaseManager {

    Connection DBConnection = null;

    public boolean createDatabase(File DBFile,  Map<String, String> companyInfo);
    public boolean openDatabase(File DBFile);
    public List<String> getEntryList();

    //
    public Map getEntryInfo(String entryID);
    public Map getCompanyInfo();
    public List<Map<String, Integer>> getEntry(String entryID);
    public boolean removeEntry(String entryID);

    public boolean addEntry(List<Map> data);
    //Format:


}
