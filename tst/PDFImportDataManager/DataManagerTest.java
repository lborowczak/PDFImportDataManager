package PDFImportDataManager;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.*;


public class DataManagerTest {

    private DataManager testDataManager = new DataManager();

    @org.junit.Before
    public void setUp() throws Exception {
        testDataManager.openDatabase("tst/testDatabase.db");
    }

    @Test
    public void testGetDates(){
        TripleDate<LocalDate> correctDates;
        //correctDates.add()
        //assertEquals(testDataManager.getDates(), correctDates);

    }

}