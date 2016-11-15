package PDFImportDataManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class PDFImporterTest {

    PDFImporter testPortraitImporter = new TabulaPDFImporter();
    PDFImporter testLandscapeImporter = new TabulaPDFImporter();

    @Before
    public void setUp() throws Exception {
        assertEquals(true, testPortraitImporter.importPDF("tst/Example_report_sent_in_portrait.pdf"));
        assertEquals(true, testLandscapeImporter.importPDF("tst/Example_report_sent_in_landscape.pdf"));
    }

    @After
    public void tearDown() throws Exception {


    }

    @Test
    public void checkPortraitDate() throws Exception{
        assertEquals("October 2 - 8, 2016", testPortraitImporter.getDatesString());
    }

    @Test
    public void checkLandscapeDate() throws Exception{
        assertEquals("October 2 - 8, 2016", testLandscapeImporter.getDatesString());
    }

    @Test
    public void checkPortraitData() throws Exception{
        assertEquals("", testPortraitImporter.getData());
    }

    @Test
    public void checkLandscapeData() throws Exception{
        assertEquals("", testLandscapeImporter.getData());
    }


}