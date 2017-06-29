package PDFImportDataManager.interfaces;

import PDFImportDataManager.EntryData;
import PDFImportDataManager.TripleDate;

import java.io.File;
import java.util.Map;

public interface ReportGenerator {

    public void setData(String companyName, String companyEIN, String companyPIN, Map<String, Integer> entries,
                        double totalGross, double federalWithholding, double stateWithholding, double deposit, double taxPercent,
                        TripleDate dates);

    public void setEntryData(Map<String,String> companyInfo, EntryData reportEntryData, double deposit, double taxPercent, TripleDate dates);

    public boolean outputReport(File outputFile);

}
