package PDFImportDataManager;


import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class PDFReportGenerator implements ReportGenerator {

    private String companyName, companyEIN, companyPIN = "";
    private Map<String, Integer> entries = null;
    private double totalGross, federalWithholding, stateWithholding, taxPercent, deposit = 0.0;
    private TripleDate dates = null;



    @Override
    public void setData (String companyName, String companyEIN, String companyPIN, Map<String, Integer> entries,
                         double totalGross, double federalWithholding, double stateWithholding, double deposit, double taxPercent,
                         TripleDate dates) {
        this.companyName = companyName;
        this.companyEIN = companyEIN;
        this.companyPIN = companyPIN;
        this.entries = entries;
        this.totalGross = totalGross;
        this.federalWithholding = federalWithholding;
        this.stateWithholding = stateWithholding;
        this.taxPercent = taxPercent;
        this.deposit = deposit;
        this.dates = dates;
    }

    @Override
    public boolean outputReport(File reportFile){


        PDDocument generatedPDF = new PDDocument();
        String monthString = dates.getPayDate().minusDays(7).getMonth().toString();
        String datesString = dates.getBeginDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + " - " +
                dates.getEndDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        int quarterString = (int)dates.getPayDate().minusDays(7).getMonthValue() / 4;

        //Points per inch variable
        float PPI = 72;

        //Set the initial fonts
        PDFont font = PDType1Font.HELVETICA;
        PDFont boldFont = PDType1Font.HELVETICA_BOLD;

        //Create a new page in landscape format and put it in the document
        PDPage firstPage = new PDPage(new PDRectangle(11f * PPI, 8.5f * PPI));
        generatedPDF.addPage(firstPage);

        //Create DecimalFormatter to round numbers to 2 digits
        DecimalFormat df = new DecimalFormat("#.00");
        df.setRoundingMode(RoundingMode.HALF_UP);

        try {
            PDPageContentStream contentStream = new PDPageContentStream(generatedPDF, firstPage);
            //Write company name and dates
            writeText(contentStream, boldFont, 10, companyName + " - Weekly payroll", 3.5f * PPI, 7.0f * PPI);
            writeText(contentStream, boldFont, 10, monthString, 7.0f * PPI, 7.0f * PPI);
            writeText(contentStream, font, 10, datesString, 3.5f * PPI, 6.5f * PPI);
            writeText(contentStream, boldFont, 14, "Quarter " + quarterString,  7.0f * PPI, 6.5f * PPI);

            //Write state and federal withholding information
            writeText(contentStream, boldFont, 12, "S/W:", 1.5f * PPI, 4.0f * PPI);
            writeText(contentStream, font, 10, "$" + df.format(stateWithholding), 1.5f * PPI, 3.8f * PPI);
            contentStream.drawLine(1.5f * PPI, 3.95f * PPI, 2.25f * PPI, 3.95f * PPI);

            writeText(contentStream, boldFont, 10, "F/W:", 6.5f * PPI, 6.0f * PPI);
            writeText(contentStream, font, 10, "$" + df.format(federalWithholding), 6.5f * PPI, 5.8f * PPI);
            contentStream.drawLine(6.5f * PPI, 5.95f * PPI, 7.25f * PPI, 5.95f * PPI);

            //write headings for custom entries
            writeText(contentStream, boldFont, 10, "Salary", 2.5f * PPI, 6.0f * PPI);
            writeText(contentStream, boldFont, 10, "Amount", 3.5f * PPI, 6.0f * PPI);
            contentStream.drawLine(2.4f * PPI, 5.95f * PPI ,4.2f * PPI, 5.95f * PPI);

            //Write custom entries
            float distanceToMove = 5.8f * PPI;
            for (Map.Entry<String, Integer> entry : entries.entrySet()){
                writeText(contentStream, font, 10, entry.getKey(), 2.5f * PPI, distanceToMove);
                writeText(contentStream, font, 10, "$" + (entry.getValue() / 100.0), 3.5f * PPI, distanceToMove);
                distanceToMove -= 0.2*PPI;
            }
            //Write total amount and draw lines as seen in example report
            writeText(contentStream, boldFont, 10, "TOTAL:", 2.5f * PPI, distanceToMove);
            writeText(contentStream, boldFont, 10, "$" + df.format(totalGross), 3.5f * PPI, distanceToMove);
            contentStream.drawLine(3.45f * PPI, 5.95f * PPI, 3.45f * PPI, distanceToMove - 2.0f);
            contentStream.drawLine(2.4f * PPI, distanceToMove + (0.15f * PPI), 4.2f * PPI, distanceToMove + (0.15f * PPI));


            //Write summary of deposit info
            writeText(contentStream, font, 10, "DEPOSIT:", 3.5f * PPI, 3.0f * PPI);
            writeText(contentStream, font, 10, "$" + df.format(totalGross) + " x " + df.format(taxPercent * 100) + "%", 5.0f * PPI, 3.0f * PPI);
            writeText(contentStream, font, 10, "$" + df.format(totalGross * taxPercent), 6.5f * PPI, 3.0f * PPI);
            writeText(contentStream, font, 10, "F/W:", 6.0f * PPI, 2.8f * PPI);
            writeText(contentStream, font, 10, "$" + df.format(federalWithholding), 6.5f * PPI, 2.8f * PPI);
            writeText(contentStream, boldFont, 14, "$" + df.format(deposit), 6.4f * PPI, 2.5f * PPI);
            contentStream.drawLine(5.0f * PPI, 2.7f * PPI, 7.25f * PPI, 2.7f * PPI);


            //Write other information
            writeText(contentStream, font, 10, "EIN: " + companyEIN, 1.0f * PPI, 2.0f * PPI);
            writeText(contentStream, font, 10, "PIN: " + companyPIN, 1.0f * PPI, 1.8f * PPI);
            writeText(contentStream, boldFont, 10, "Deposit: $" + df.format(deposit), 1.0f * PPI, 1.6f * PPI);
            writeText(contentStream, font, 10, "Date: " + dates.getPayDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")), 1.0f * PPI, 1.4f * PPI);
            writeText(contentStream, font, 10, "Confirmation #: _________________________", 4f * PPI, 1.4f * PPI);


            contentStream.close();

            generatedPDF.save(reportFile);
            generatedPDF.close();
        } catch (IOException | COSVisitorException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void writeText(PDPageContentStream contentStream, PDFont font, float fontSize, String content, float pos1, float pos2){
        try {
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.moveTextPositionByAmount(pos1, pos2);
            contentStream.drawString(content);
            contentStream.endText();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
