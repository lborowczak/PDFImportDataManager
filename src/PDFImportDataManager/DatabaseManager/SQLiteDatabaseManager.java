package PDFImportDataManager.DatabaseManager;

import PDFImportDataManager.EntryData;
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
    public boolean createDatabase(File DBFile, Map<String, String> companyInfo) {
        boolean returnVal = true;
        Statement createDatabaseStatement = null;
        PreparedStatement setCompanyInfoStatement = null;
        try {
            Class.forName("org.sqlite.JDBC");
            DBConnection = DriverManager.getConnection("jdbc:sqlite:" + DBFile, enableForeignKeysConfig.toProperties());
            createDatabaseStatement = DBConnection.createStatement();
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
            setCompanyInfoStatement = DBConnection.prepareStatement(setCompanyInfoString);
            setCompanyInfoStatement.setString(1, companyInfo.get("Company_Name"));
            setCompanyInfoStatement.setString(2, companyInfo.get("Company_EIN"));
            setCompanyInfoStatement.setString(3, companyInfo.get("Company_PIN"));
            setCompanyInfoStatement.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            returnVal = false;
        } finally {
            if (createDatabaseStatement != null) {
                try {
                    createDatabaseStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (setCompanyInfoStatement != null) {
                try {
                    setCompanyInfoStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                DBConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return returnVal;
    }

    @Override
    public boolean openDatabase(File DBFile) {
        boolean returnVal = true;
        Statement testStatement = null;
        try {
            Class.forName("org.sqlite.JDBC");
            DBConnection = DriverManager.getConnection("jdbc:sqlite:" + DBFile, enableForeignKeysConfig.toProperties());
            //Test to make sure the file opened is a database file
            testStatement = DBConnection.createStatement();
            testStatement.execute("SELECT * FROM CompanyInfo");

        } catch (Exception e) {
            e.printStackTrace();
            returnVal = false;
        }
        finally {
            if (testStatement != null){
                try {
                    testStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return returnVal;
    }

    @Override
    public List<String> getEntryList() {
        List<String> result = new ArrayList<>();
        Statement getEntryListStatement = null;
        ResultSet entriesSet = null;
        try {
            getEntryListStatement = DBConnection.createStatement();
            String statementString = "SELECT ENTRY_ID FROM Entries;";
            entriesSet = getEntryListStatement.executeQuery(statementString);
            while (entriesSet.next()) {
                result.add("" + entriesSet.getInt("ENTRY_ID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (getEntryListStatement != null) {
                try {
                    getEntryListStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (entriesSet != null) {
                try {
                    entriesSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    @Override
    public Map<String, Integer> getEntryInfo(String entryID) {
        //Returns a mapping of Month, Year, Start_Day, and End_Day to integers
        Map<String, Integer> result = new HashMap<String, Integer>();
        PreparedStatement getEntryInfoStatement = null;
        ResultSet entriesSet = null;
        int entryIDInt = Integer.parseInt(entryID);
        try {
            String statementString = "SELECT ENTRY_MONTH, ENTRY_YEAR, START_DAY FROM Entries where ENTRY_ID = ?;";
            getEntryInfoStatement = DBConnection.prepareStatement(statementString);
            getEntryInfoStatement.setInt(1, entryIDInt);
            getEntryInfoStatement.execute();
            entriesSet = getEntryInfoStatement.getResultSet();
            result.put("Month", entriesSet.getInt("ENTRY_MONTH"));
            result.put("Year", entriesSet.getInt("ENTRY_YEAR"));
            result.put("Start_Day", entriesSet.getInt("START_DAY"));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (getEntryInfoStatement != null) {
                try {
                    getEntryInfoStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (entriesSet != null) {
                try {
                    entriesSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    @Override
    public List<Map<String, Integer>> getEntry(String entryID) {
        Map<String, Integer> basicEntries = new HashMap<String, Integer>();
        List<Map<String, Integer>> result = new ArrayList<>();
        EntryData resultsData = new EntryData();
        int entryIDInt = Integer.parseInt(entryID);
        PreparedStatement getEntryStatement = null;
        ResultSet entriesSet = null;
        try {
            String statementString = "SELECT * FROM EntryData where CURR_ENTRY_ID = ?;";
            getEntryStatement = DBConnection.prepareStatement(statementString);
            getEntryStatement.setInt(1, entryIDInt);
            getEntryStatement.execute();
            entriesSet = getEntryStatement.getResultSet();
            basicEntries.put("Start_Day", entriesSet.getInt("Start_Day"));
            basicEntries.put("Start_Month", entriesSet.getInt("Start_Month"));
            basicEntries.put("Start_Year", entriesSet.getInt("Start_Year"));
            basicEntries.put("End_Day", entriesSet.getInt("End_Day"));
            basicEntries.put("End_Month", entriesSet.getInt("End_Month"));
            basicEntries.put("End_Year", entriesSet.getInt("End_Year"));
            basicEntries.put("Pay_Day", entriesSet.getInt("Pay_Day"));
            basicEntries.put("Pay_Month", entriesSet.getInt("Pay_Month"));
            basicEntries.put("Pay_Year", entriesSet.getInt("Pay_Year"));
            basicEntries.put("Gross_Pay", entriesSet.getInt("Gross_Pay_In_Cents"));
            basicEntries.put("Federal_Withholding", entriesSet.getInt("Federal_Withholding_In_Cents"));
            basicEntries.put("State_Withholding", entriesSet.getInt("State_Withholding_In_Cents"));
            basicEntries.put("Medicare_Employee_Withholding", entriesSet.getInt("Medicare_Employee_Withholding_In_Cents"));
            basicEntries.put("Social_Security_Employee_Withholding",
                    entriesSet.getInt("Social_Security_Employee_Withholding_In_Cents"));

            result.add(basicEntries);


            //entriesSet = getEntryStatement.getResultSet();
            resultsData.setStartDay(entriesSet.getInt("Start_Day"));
            resultsData.setStartMonth(entriesSet.getInt("Start_Month"));
            resultsData.setStartYear(entriesSet.getInt("Start_Year"));
            resultsData.setEndDay(entriesSet.getInt("End_Day"));
            resultsData.setEndMonth(entriesSet.getInt("End_Month"));
            resultsData.setEndYear(entriesSet.getInt("End_Year"));
            resultsData.setPayDay(entriesSet.getInt("Pay_Day"));
            resultsData.setpPayMonth(entriesSet.getInt("Pay_Month"));
            resultsData.setPayYear(entriesSet.getInt("Pay_Year"));
            resultsData.setGrossPay(entriesSet.getInt("Gross_Pay_In_Cents"));
            resultsData.setFederalWithholding(entriesSet.getInt("Federal_Withholding_In_Cents"));
            resultsData.setStateWithholding(entriesSet.getInt("State_Withholding_In_Cents"));
            resultsData.setMedicareEmployeeWithholding(entriesSet.getInt("Medicare_Employee_Withholding_In_Cents"));
            resultsData.setSocialSecurityEmployeeWithholding(entriesSet.getInt("Social_Security_Employee_Withholding_In_Cents"));


            //Gross_Breakdown_Info is a string that shows the breakdown of the entries that get added up into the gross total.
            //It is as follows: Entry1,Entry1Amount;Entry2,Entry2Amount; ...
            Map<String, Integer> breakdownMap = new HashMap<String, Integer>();
            String breakdownInfo = entriesSet.getString("Gross_Breakdown_Info");
            for (String breakdownLine : breakdownInfo.split(";")) {
                String[] lineEntries = breakdownLine.split(",");
                breakdownMap.put(lineEntries[0], Integer.parseInt(lineEntries[1]));
                resultsData.setExtraData(lineEntries[0], Integer.parseInt(lineEntries[1]));
            }
            result.add(breakdownMap);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (getEntryStatement != null) {
                try {
                    getEntryStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (entriesSet != null) {
                /*try {
                    entriesSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }*/
            }
        }
        return result;
    }

    @Override
    public Map<String, String> getCompanyInfo() {
        Map<String, String> result = new HashMap<String, String>();
        Statement getEntryListStatement = null;
        ResultSet entriesSet = null;
        try {
            getEntryListStatement = DBConnection.createStatement();
            String statementString = "SELECT * FROM CompanyInfo;";
            entriesSet = getEntryListStatement.executeQuery(statementString);
            result.put("Company_Name", entriesSet.getString("COMPANY_NAME"));
            result.put("Company_EIN", entriesSet.getString("COMPANY_EIN"));
            result.put("Company_PIN", entriesSet.getString("COMPANY_PIN"));

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (getEntryListStatement != null) {
                try {
                    getEntryListStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (entriesSet != null) {
                try {
                    entriesSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    @Override
    public boolean removeEntry(String entryID) {
        int entryIDInt = Integer.parseInt(entryID);
        PreparedStatement deleteEntryDataStatement = null, deleteEntryStatement = null;
        try {
            String statementString1 = "DELETE FROM EntryData WHERE CURR_ENTRY_ID = ?;";
            String statementString2 = "DELETE FROM Entries WHERE ENTRY_ID = ?;";
            deleteEntryDataStatement = DBConnection.prepareStatement(statementString1);
            deleteEntryStatement = DBConnection.prepareStatement(statementString2);
            deleteEntryDataStatement.setInt(1, entryIDInt);
            deleteEntryDataStatement.executeUpdate();
            deleteEntryStatement.setInt(1, entryIDInt);
            deleteEntryStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (deleteEntryDataStatement != null) {
                try {
                    deleteEntryDataStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (deleteEntryStatement != null) {
                try {
                    deleteEntryStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;

    }

    @Override
    public boolean addEntry(List<Map<String, Integer>> data) {
        boolean returnVal = true;
        Map<String, Integer> normalData = data.get(0);
        Map<String, Integer> extraData = data.get(1);
        PreparedStatement addNewEntryStatement = null, addEntryInfoStatement = null;
        ResultSet rowID = null;
        try {
            //Add entry
            String addNewEntryStatementString = "INSERT INTO Entries VALUES(NULL, ?, ?, ?)";
            addNewEntryStatement = DBConnection.prepareStatement(addNewEntryStatementString);
            addNewEntryStatement.setInt(1, normalData.get("Start_Month"));
            addNewEntryStatement.setInt(2, normalData.get("Start_Year"));
            addNewEntryStatement.setInt(3, normalData.get("Start_Day"));
            addNewEntryStatement.executeUpdate();

            String addEntryInfoStatementString = "INSERT INTO EntryData VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            addEntryInfoStatement = DBConnection.prepareStatement(addEntryInfoStatementString);
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
            extraData.forEach((k, v) -> grossBreakdownInfoString.append(k + "," + v + ";"));
            addEntryInfoStatement.setString(11, grossBreakdownInfoString.toString());


            addEntryInfoStatement.setInt(12, normalData.get("Federal_Withholding"));
            addEntryInfoStatement.setInt(13, normalData.get("Medicare_Employee_Withholding"));
            addEntryInfoStatement.setInt(14, normalData.get("Social_Security_Employee_Withholding"));
            addEntryInfoStatement.setInt(15, normalData.get("State_Withholding"));

            //Get and set ID of the entry info
            rowID = DBConnection.createStatement().executeQuery("SELECT last_insert_rowid();");
            addEntryInfoStatement.setInt(16, rowID.getInt(1));

            addEntryInfoStatement.executeUpdate();
            returnVal = true;


            //New object usage
            /*
            EntryData data = new EntryData();
            
            //Add entry
            String addNewEntryStatementString = "INSERT INTO Entries VALUES(NULL, ?, ?, ?)";
            addNewEntryStatement = DBConnection.prepareStatement(addNewEntryStatementString);
            addNewEntryStatement.setInt(1, data.getStartMonth());
            addNewEntryStatement.setInt(2, data.getStartYear());
            addNewEntryStatement.setInt(3, data.getStartDay());
            addNewEntryStatement.executeUpdate();

            String addEntryInfoStatementString = "INSERT INTO EntryData VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            addEntryInfoStatement = DBConnection.prepareStatement(addEntryInfoStatementString);
            addEntryInfoStatement.setInt(1, data.getStartDay());
            addEntryInfoStatement.setInt(2, data.getStartMonth());
            addEntryInfoStatement.setInt(3, data.getStartYear());
            addEntryInfoStatement.setInt(4, data.getEndDay());
            addEntryInfoStatement.setInt(5, data.getEndMonth());
            addEntryInfoStatement.setInt(6, data.getEndYear());
            addEntryInfoStatement.setInt(7, data.getPayDay());
            addEntryInfoStatement.setInt(8, data.getPayMonth());
            addEntryInfoStatement.setInt(9, data.getPayYear());
            addEntryInfoStatement.setInt(10, data.getGrossPay());

            //Build Gross_Breakdown_Info string for insertion into database
            final StringBuilder grossBreakdownInfoString = new StringBuilder();
            extraData.forEach((k, v) -> grossBreakdownInfoString.append(k + "," + v + ";"));
            addEntryInfoStatement.setString(11, grossBreakdownInfoString.toString());


            addEntryInfoStatement.setInt(12, data.getFederalWithholding());
            addEntryInfoStatement.setInt(13, data.getMedicareEmployeeWithholding());
            addEntryInfoStatement.setInt(14, data.getSocialSecurityEmployeeWithholding());
            addEntryInfoStatement.setInt(15, data.getStateWithholding());

            //Get and set ID of the entry info
            rowID = DBConnection.createStatement().executeQuery("SELECT last_insert_rowid();");
            addEntryInfoStatement.setInt(16, rowID.getInt(1));

            addEntryInfoStatement.executeUpdate();
            returnVal = true;
            */



        } catch (SQLException e) {
            e.printStackTrace();
            returnVal = false;
        } finally {
            if (addNewEntryStatement != null) {
                try {
                    addEntryInfoStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (addEntryInfoStatement != null) {
                try {
                    addEntryInfoStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (rowID != null) {
                try {
                    rowID.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return returnVal;
    }

    @Override
    public boolean closeDatabase(){
        try {
            DBConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}