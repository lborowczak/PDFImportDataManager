package PDFImportDataManager.DatabaseManager;

import PDFImportDataManager.interfaces.DatabaseManager;
import org.sqlite.SQLiteConfig;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLiteDatabaseManager implements DatabaseManager {

    private Connection DBConnection = null;
    private SQLiteConfig enableForeignKeysConfig = new SQLiteConfig();

    public SQLiteDatabaseManager() {
        enableForeignKeysConfig.enforceForeignKeys(true);
    }

    @Override
    public boolean createDatabase(File DBFile, Map<String, String> companyInfo){
        try {
            Class.forName("org.sqlite.JDBC");
            DBConnection = DriverManager.getConnection("jdbc:sqlite:" + DBFile, enableForeignKeysConfig.toProperties());
            Statement createDatabaseStatement = DBConnection.createStatement();
            String createTableOne =
                "CREATE TABLE CompanyInfo (" +
                    "COMPANY_NAME TEXT," +
                    "COMPANY_EIN TEXT," +
                    "COMPANY_PIN TEXT" +
                ");";
            String createTableTwo =
                "CREATE TABLE Entries (" +
                    "ENTRY_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "ENTRY_MONTH INTEGER," +
                    "ENTRY_YEAR INTEGER," +
                    "START_DAY INTEGER" +
                ");";
            String createTableThree =
                "CREATE TABLE EntryData (" +
                    "Start_Day INTEGER," +
                    "Start_Month INTEGER," +
                    "Start_Year INTEGER," +
                    "End_Day INTEGER," +
                    "End_Month INTEGER," +
                    "End_Year INTEGER," +
                    "Pay_Day INTEGER," +
                    "Pay_Month INTEGER," +
                    "Pay_Year INTEGER," +
                    "Gross_Pay_In_Cents INTEGER," +
                    "Gross_Breakdown_Info TEXT," +
                    "Federal_Withholding_In_Cents INTEGER," +
                    "Medicare_Employee_Withholding_In_Cents INTEGER," +
                    "Social_Security_Employee_Withholding_In_Cents INTEGER," +
                    "State_Withholding_In_Cents INTEGER," +
                    "CURR_ENTRY_ID INTEGER PRIMARY KEY," +
                    "FOREIGN KEY (CURR_ENTRY_ID) REFERENCES Entries(ENTRY_ID)" +
                ");";
            createDatabaseStatement.execute(createTableOne);
            createDatabaseStatement.execute(createTableTwo);
            createDatabaseStatement.execute(createTableThree);

            String setCompanyInfoString = "INSERT INTO CompanyInfo VALUES(?, ?, ?)";
            PreparedStatement setCompanyInfoStatement = DBConnection.prepareStatement(setCompanyInfoString);
            setCompanyInfoStatement.setString(1, companyInfo.get("Company_Name"));
            setCompanyInfoStatement.setString(2, companyInfo.get("Company_EIN"));
            setCompanyInfoStatement.setString(3, companyInfo.get("Company_PIN"));
            setCompanyInfoStatement.execute();


            DBConnection.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public boolean openDatabase(File DBFile){
        try {
            Class.forName("org.sqlite.JDBC");
            DBConnection = DriverManager.getConnection("jdbc:sqlite:" + DBFile, enableForeignKeysConfig.toProperties());
            //Test to make sure the file opened is a database file
            DBConnection.createStatement().execute("SELECT * FROM CompanyInfo");
        } catch ( Exception e ) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public List<String> getEntryList(){
        List<String> result = new ArrayList<>();
        try {
            Statement getEntryListStatement = DBConnection.createStatement();
            String statementString = "SELECT ENTRY_ID FROM Entries;";
            ResultSet entriesSet = getEntryListStatement.executeQuery(statementString);
            while (entriesSet.next()){
                result.add("" + entriesSet.getInt("ENTRY_ID"));
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Map<String, Integer> getEntryInfo(String entryID) {
        //Returns a mapping of Month, Year, Start_Day, and End_Day to integers
        Map<String, Integer> result = new HashMap<String, Integer>();
        int entryIDInt = Integer.parseInt(entryID);
        try {
            String statementString = "SELECT ENTRY_MONTH, ENTRY_YEAR, START_DAY FROM Entries where ENTRY_ID = ?;";
            PreparedStatement getEntryInfoStatement = DBConnection.prepareStatement(statementString);
            getEntryInfoStatement.setInt(1, entryIDInt);
            getEntryInfoStatement.execute();
            ResultSet entriesSet = getEntryInfoStatement.getResultSet();
            result.put("Month", entriesSet.getInt("ENTRY_MONTH"));
            result.put("Year", entriesSet.getInt("ENTRY_YEAR"));
            result.put("Start_Day", entriesSet.getInt("START_DAY"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<Map<String, Integer>> getEntry(String entryID) {
        Map<String, Integer> basicEntries = new HashMap<String, Integer>();
        List<Map<String, Integer>> result = new ArrayList<>();
        int entryIDInt = Integer.parseInt(entryID);
        try {
            String statementString = "SELECT * FROM EntryData where CURR_ENTRY_ID = ?;";
            PreparedStatement getEntryInfoStatement = DBConnection.prepareStatement(statementString);
            getEntryInfoStatement.setInt(1, entryIDInt);
            getEntryInfoStatement.execute();
            ResultSet entriesSet = getEntryInfoStatement.getResultSet();
            basicEntries.put("Start_Day", entriesSet.getInt("Start_Day"));
            basicEntries.put("Start_Month", entriesSet.getInt("Start_Month"));
            basicEntries.put("Start_Year", entriesSet.getInt("Start_Year"));
            basicEntries.put("End_Day", entriesSet.getInt("End_Day"));
            basicEntries.put("End_Month", entriesSet.getInt("End_Month"));
            basicEntries.put("End_Year", entriesSet.getInt("End_Year"));
            basicEntries.put("Pay_Day", entriesSet.getInt("Pay_Day"));
            basicEntries.put("Pay_Month",  entriesSet.getInt("Pay_Month"));
            basicEntries.put("Pay_Year", entriesSet.getInt("Pay_Year"));
            basicEntries.put("Gross_Pay", entriesSet.getInt("Gross_Pay_In_Cents"));
            basicEntries.put("Federal_Withholding", entriesSet.getInt("Federal_Withholding_In_Cents"));
            basicEntries.put("State_Withholding", entriesSet.getInt("State_Withholding_In_Cents"));
            basicEntries.put("Medicare_Employee_Withholding", entriesSet.getInt("Federal_Withholding_In_Cents"));
            basicEntries.put("Social_Security_Employee_Withholding",
                    entriesSet.getInt("Social_Security_Employee_Withholding_In_Cents"));

            result.add(basicEntries);

            //Gross_Breakdown_Info is a string that shows the breakdown of the entries that get added up into the gross total.
            //It is as follows: Entry1,Entry1Amount;Entry2,Entry2Amount; ...
            Map<String, Integer> breakdownMap = new HashMap<String, Integer>();
            String breakdownInfo = entriesSet.getString("Gross_Breakdown_Info");
            for (String breakdownLine: breakdownInfo.split(";")){
                String[] lineEntries = breakdownLine.split(",");
                breakdownMap.put(lineEntries[0], ((Double)(Double.parseDouble(lineEntries[1]) * 100)).intValue());
            }
            result.add(breakdownMap);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Map<String, String> getCompanyInfo(){
        Map<String, String> result = new HashMap<String, String>();
        try {
            Statement getEntryListStatement = DBConnection.createStatement();
            String statementString = "SELECT * FROM CompanyInfo;";
            ResultSet entriesSet = getEntryListStatement.executeQuery(statementString);
            result.put("Company_Name", entriesSet.getString("COMPANY_NAME"));
            result.put("Company_EIN", entriesSet.getString("COMPANY_EIN"));
            result.put("Company_PIN", entriesSet.getString("COMPANY_PIN"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean removeEntry(String entryID) {
        int entryIDInt = Integer.parseInt(entryID);
        try {
            String statementString1 = "DELETE FROM EntryData WHERE CURR_ENTRY_ID = ?;";
            String statementString2 = "DELETE FROM Entries WHERE ENTRY_ID = ?;";
            PreparedStatement deleteEntryDataStatement = DBConnection.prepareStatement(statementString1);
            PreparedStatement deleteEntryStatement = DBConnection.prepareStatement(statementString2);
            deleteEntryDataStatement.setInt(1, entryIDInt);
            deleteEntryStatement.setInt(1, entryIDInt);
            deleteEntryDataStatement.execute();
            deleteEntryStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }

    @Override
    public boolean addEntry(List<Map> data) {
        Map<String, Integer> normalData = data.get(1);
        Map<String, Integer> extraData = data.get(2);
        try{

            //Add entry
            String addNewEntryStatementString = "INSERT INTO Entries VALUES(NULL, ?, ?, ?)";
            PreparedStatement addNewEntryStatement = DBConnection.prepareStatement(addNewEntryStatementString);
            addNewEntryStatement.setInt(1, normalData.get("Start_Month"));
            addNewEntryStatement.setInt(2, normalData.get("Start_Year"));
            addNewEntryStatement.setInt(3, normalData.get("Start_Day"));
            addNewEntryStatement.execute();

            String addEntryInfoStatementString = "INSERT INTO Entries VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement addEntryInfoStatement = DBConnection.prepareStatement(addNewEntryStatementString);
            addEntryInfoStatement.setInt(1, normalData.get("Start_Day"));
            addEntryInfoStatement.setInt(2, normalData.get("Start_Month"));
            addEntryInfoStatement.setInt(3, normalData.get("Start_Year"));
            addEntryInfoStatement.setInt(4, normalData.get("End_Day"));
            addEntryInfoStatement.setInt(5, normalData.get("End_Month"));
            addEntryInfoStatement.setInt(6, normalData.get("End_Year"));
            addEntryInfoStatement.setInt(7, normalData.get("Pay_Day"));
            addEntryInfoStatement.setInt(8, normalData.get("Pay_Month"));
            addEntryInfoStatement.setInt(9, normalData.get("Pay_Year"));
            addEntryInfoStatement.setInt(10, normalData.get("Gross_Pay"));

            //Build Gross_Breakdown_Info string for insertion into database
            final StringBuilder grossBreakdownInfoString = new StringBuilder();
            extraData.forEach( (k,v) -> grossBreakdownInfoString.append(k + "," + v + ";") );
            addEntryInfoStatement.setString(11, grossBreakdownInfoString.toString());


            addEntryInfoStatement.setInt(12, normalData.get("Federal_Withholding"));
            addEntryInfoStatement.setInt(13, normalData.get("Medicare_Employee_Withholding"));
            addEntryInfoStatement.setInt(14, normalData.get("Social_Security_Employee_Withholding"));
            addEntryInfoStatement.setInt(15, normalData.get("State_Withholding"));

            //Get and set ID of the entry info
            ResultSet rowID =  DBConnection.createStatement().executeQuery("SELECT last_insert_rowid();");
            addEntryInfoStatement.setInt(16, rowID.getInt(1));

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

}