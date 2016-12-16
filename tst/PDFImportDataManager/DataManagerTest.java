package PDFImportDataManager;

import org.junit.Before;
import org.junit.Test;

import java.io.File;


public class DataManagerTest {

    private DataManager testDataManager = new DataManager();

    @Before
    public void setUp() throws Exception {
        testDataManager.openDatabase(new File("tst/testDatabase.db"));
    }

    @Test
    public void testGetDates(){
        TripleDate correctDates;
        //correctDates.add()
        //assertEquals(testDataManager.getDates(), correctDates);

    }

}