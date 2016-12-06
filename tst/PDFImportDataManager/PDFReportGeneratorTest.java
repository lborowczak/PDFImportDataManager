package PDFImportDataManager;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;


public class PDFReportGeneratorTest {

    private ReportGenerator testReportGenerator = new PDFReportGenerator();
    private LocalDate date1 = LocalDate.parse("2016-10-02");
    private LocalDate date2 = LocalDate.parse("2016-10-08");
    private LocalDate paydate = LocalDate.parse("2016-10-12");

    private TripleDate correctDate = new TripleDate(date1, date2, paydate);

    @Before
    public void setUp() throws Exception {
        testReportGenerator.setData("Example Company, Inc.", "12-345678", "0123", asList("testOne", "testTwo"), asList(150.0, 240.0), 1000.0, 1024.0, 1012.0, 1016.0, correctDate);
    }

    @Test
    public void testPDFGen() throws Exception {
        testReportGenerator.outputReport("tst/example.pdf");
    }


}
