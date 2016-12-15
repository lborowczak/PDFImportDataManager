package PDFImportDataManager;

import PDFImportDataManager.ReportGenerator.PDFReportGenerator;
import PDFImportDataManager.interfaces.ReportGenerator;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


public class PDFReportGeneratorTest {

    private ReportGenerator testReportGenerator = new PDFReportGenerator();
    //private LocalDate date1 = LocalDate.parse("2016-10-02");
    //private LocalDate date2 = LocalDate.parse("2016-10-08");
    //private LocalDate paydate = LocalDate.parse("2016-10-12");
    private Map<String, Integer> tmpMap = new HashMap<>();

    private TripleDate correctDate = new TripleDate(LocalDate.parse("2016-10-02"), LocalDate.parse("2016-10-08"), LocalDate.parse("2016-10-12"));

    @Before
    public void setUp() throws Exception {
        tmpMap.put("TestOne", 102411);
        testReportGenerator.setData("Example Company, Inc.", "12-345678", "0123", tmpMap, 1000.0, 1024.0, 1012.0, 1016.0, 0.153, correctDate);
    }

    @Test
    public void testPDFGen() throws Exception {
        testReportGenerator.outputReport(new File("tst/example.pdf"));
    }


}
