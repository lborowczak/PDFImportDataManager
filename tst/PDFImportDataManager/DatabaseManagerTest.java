package PDFImportDataManager;

import PDFImportDataManager.DatabaseManager.SQLiteDatabaseManager;
import PDFImportDataManager.interfaces.DatabaseManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DatabaseManagerTest {

    private DatabaseManager testSQLiteDB = new SQLiteDatabaseManager();
    //private List<Map<>>

    @Before
    public void setUp() throws Exception {
        Files.copy(new File("./tst/testDatabase.db").toPath(), new File("./tst/tempDB.db").toPath(), REPLACE_EXISTING);

        //testSQLiteDB.createDatabase("./tst/tempDB.db");
        assertEquals(true, testSQLiteDB.openDatabase(new File("./tst/tempDB.db")));

    }

    @Test
    public void RunTests() throws Exception{
        assertEquals(true, testSQLiteDB.openDatabase(new File("./tst/tempDB.db")));
        assertEquals(3, CheckEntryListCount());
        //assertEquals("", CheckEntryInfo());
        /*
                CheckEntryListCount();
        CheckEntryInfo();
        TestRemoveEntry();
        TestAddEntry();
         */
    }

    public int CheckEntryListCount() throws Exception {
        return testSQLiteDB.getEntryList().size();
    }

    @Test
    public void CheckEntryInfo() throws Exception {
        assertEquals("[{Start_Month=10, End_Year=2016, End_Day=8, Pay_Day=12, Pay_Year=2016, Social_Security_Employee_Withholding=43144, Medicare_Employee_Withholding=10114, Start_Day=2, End_Month=10, Start_Year=2016, State_Withholding=26109, Pay_Month=10, Federal_Withholding=108800, Gross_Pay=695700}, {Hourly Rate=375400, Overtime Pay=320300}]",testSQLiteDB.getEntry("8").toString());

    }

    public void TestRemoveEntry() throws Exception {
        int prevSize = testSQLiteDB.getEntryList().size();

        //assertEquals(SQLiteDB.getEntryList().size(), 5);
        testSQLiteDB.removeEntry("1");
        assertEquals(prevSize - 1, testSQLiteDB.getEntryList().size());
        //TODO add code to re-add entry?
    }


    public void TestAddEntry() throws Exception {
        /*
        int prevSize = testSQLiteDB.getEntryList().size();
        List<Map<String, Integer>> entryList = new ArrayList<>();
        Map<String, Integer> entry = new HashMap<String, Integer>();
        entry.put("",0);
        entryList.add(entry);
        testSQLiteDB.addEntry(entryList);
        */
    }

    @After
    public void tearDown() throws Exception {

        assertEquals(true, testSQLiteDB.closeDatabase());

    }
}