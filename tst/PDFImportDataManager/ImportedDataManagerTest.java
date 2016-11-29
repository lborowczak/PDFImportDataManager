package PDFImportDataManager;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.*;


public class ImportedDataManagerTest {

    private ImportedDataManager testPortraitImportedDataManager = new ImportedDataManager();
    private ImportedDataManager testLandscapeImportedDataManager = new ImportedDataManager();
    private LocalDate date1 = LocalDate.parse("2016-10-02");
    private LocalDate date2 = LocalDate.parse("2016-10-08");
    private LocalDate paydate = LocalDate.parse("2016-10-12");

    private TripleDate correctDate = new TripleDate(date1, date2, paydate);

    @Before
    public void setUp() throws Exception {
        testPortraitImportedDataManager.importPDF("tst/Example_report_sent_in_portrait.pdf");
        testLandscapeImportedDataManager.importPDF("tst/Example_report_sent_in_landscape.pdf");
    }

    @Test
    public void testPortraitGetDates() throws Exception {
        assertEquals(correctDate,  testPortraitImportedDataManager.getDates());
    }

    @Test
    public void testLandscapeGetDates() throws Exception {
        assertEquals(correctDate, testLandscapeImportedDataManager.getDates());
    }


}
