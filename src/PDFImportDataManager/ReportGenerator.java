package PDFImportDataManager;

import java.util.List;

public interface ReportGenerator {

    public void setData(String companyName, String companyEIN, String companyPIN,
                        List<String> entries, List<Double> entryValues, double totalGross, double federalWithholding, double stateWithholding, double deposit,
                        TripleDate dates);

    public boolean outputReport(String outputFile);

}
