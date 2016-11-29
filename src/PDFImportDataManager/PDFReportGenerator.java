package PDFImportDataManager;

import org.apache.pdfbox.*;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import java.io.IOException;
import java.util.List;

public class PDFReportGenerator implements ReportGenerator {

    String companyName, companyEIN, companyPIN = "";
    List<String> entries;
    List<Double> entryValues;
    double totalGross, federalWithholding, stateWithholding, deposit = 0.0;
    TripleDate dates;



    @Override
    public void setData (String companyName, String companyEIN, String companyPIN,
                         List<String> entries, List<Double> entryValues, double totalGross, double federalWithholding, double stateWithholding, double deposit,
                         TripleDate dates) {
        this.companyName = companyName;
        this.companyEIN = companyEIN;
        this.companyEIN = companyPIN;
        this.entries = entries;
        this.entryValues = entryValues;
        this.totalGross = totalGross;
        this.federalWithholding = federalWithholding;
        this.stateWithholding = stateWithholding;
        this.deposit = deposit;
    }

    @Override
    public boolean outputReport(String reportFilename){
        PDDocument generatedPDF = new PDDocument();
        PDPage blankPage = new PDPage();
        generatedPDF.addPage(blankPage);
        try {
            generatedPDF.save(reportFilename);
        } catch (IOException | COSVisitorException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
