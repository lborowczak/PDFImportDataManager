package PDFImportDataManager;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.junit.Assert.*;

public class DatabaseManagerTest {

    private  DatabaseManager testSQLiteDB = new SQLiteDatabaseManager();

    @Before
    public void setUp() throws Exception {
        //Files.copy("./tst/testDatabase.db", "./tst/tempDB.db", REPLACE_EXISTING);
        assertEquals(true, testSQLiteDB.openDatabase("./tst/tempDB.db"));
    }

    @Test
    public void CheckEntryListCount() throws Exception {
        assertEquals(5, testSQLiteDB.getEntryList().size());
    }

    @Test
    public void CheckEntryInfo() throws Exception {
        assertEquals("", testSQLiteDB.getEntry("1"));

    }

    @Test
    public void TestRemoveEntry() throws Exception {
        int prevSize = testSQLiteDB.getEntryList().size();

        //assertEquals(SQLiteDB.getEntryList().size(), 5);
        testSQLiteDB.removeEntry("1");
        assertEquals(prevSize - 1, testSQLiteDB.getEntryList().size());
        //TODO add code to re-add entry?
    }

    @Test
    public void TestAddEntry() throws Exception {
        int prevSize = testSQLiteDB.getEntryList().size();
        List entry = new ArrayList<String>();
        entry.add("test");
        testSQLiteDB.addEntry(entry);
    }

    @After
    public void tearDown() throws Exception {

    }
}