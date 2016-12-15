package PDFImportDataManager;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface ReportGenerator {

    public void setData(String companyName, String companyEIN, String companyPIN, Map<String, Integer> entries,
                        double totalGross, double federalWithholding, double stateWithholding, double deposit, double taxPercent,
                        TripleDate dates);

    public boolean outputReport(File outputFile);

}
