package PDFImportDataManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SQLiteDatabaseManager implements DatabaseManager {

    Connection DBConnection = null;

    @Override
    public boolean createDatabase(String DBFile){
        try {
            Class.forName("org.sqlite.JDBC");
            DBConnection = DriverManager.getConnection("jdbc:sqlite:" + DBFile);
            Statement getEntryListStatement = DBConnection.createStatement();
            String statementString = "";

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean openDatabase(String DBFile){
        try {
            Class.forName("org.sqlite.JDBC");
            DBConnection = DriverManager.getConnection("jdbc:sqlite:" + DBFile);
        } catch ( Exception e ) {
            e.printStackTrace();
            return false;
        }


        return true;
    }

    @Override
    public List<String> getEntryList(){
        try {
            Statement getEntryListStatement = DBConnection.createStatement();
            String statementString = "";

        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<String> result = new ArrayList<>();
        result.add("test");
        return result;
    }

    @Override
    public List<String> getEntryInfo(String entryID){
        return null;
    }

    @Override
    public List<String> getEntry(String entryID) {
        return null;
    }

    @Override
    public void removeEntry(String entryID) {
    }

    @Override
    public boolean addEntry(List<String> data) {
        return false;
    }
}