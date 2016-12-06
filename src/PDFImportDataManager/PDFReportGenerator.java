package PDFImportDataManager;


import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        this.dates = dates;
    }

    @Override
    public boolean outputReport(String reportFilename){


        PDDocument generatedPDF = new PDDocument();
        String monthString = dates.getPayDate().minusDays(7).getMonth().toString();
        String datesString = dates.getBeginDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + " - " +
                dates.getEndDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        int quarterString = (int)dates.getPayDate().minusDays(7).getMonthValue() / 4;

        //Set the initial fonts
        PDFont font = PDType1Font.HELVETICA;
        PDFont boldFont = PDType1Font.HELVETICA_BOLD;

        //Create a new page in Landscape, at 72 Points Per Inch and add it to the document
        PDPage firstPage = new PDPage(new PDRectangle(11f * 72, 8.5f * 72));
        generatedPDF.addPage(firstPage);


        try {
            PDPageContentStream contentStream = new PDPageContentStream(generatedPDF, firstPage);
            writeText(contentStream, boldFont, 10, companyName + " - Weekly payroll", 3.5f * 72, 7.0f * 72);
            writeText(contentStream, boldFont, 10, monthString, 7.0f * 72, 7.0f * 72);
            writeText(contentStream, font, 10, datesString, 3.5f * 72, 6.5f * 72);
            writeText(contentStream, boldFont, 14, "Quarter " + quarterString,  7.0f * 72, 6.5f * 72);

            writeText(contentStream, boldFont, 10, "Salary", 2.5f * 72, 6.0f * 72);
            writeText(contentStream, boldFont, 10, "Amount", 3.5f * 72, 6.0f * 72);
            contentStream.drawLine(2.4f * 72, 5.95f * 72 ,4.2f * 72, 5.95f * 72);

            writeText(contentStream, boldFont, 10, "F/W", 6.5f * 72, 6.0f * 72);
            writeText(contentStream, font, 10, "$" + federalWithholding, 6.5f * 72, 5.8f * 72);




            //Write entries
            int tmpcounter=0;
            float distanceToMove = 5.8f * 72;
            for (String amount: entries){
                writeText(contentStream, font, 10, amount, 2.5f * 72, distanceToMove);
                writeText(contentStream, font, 10, "$" + entryValues.get(tmpcounter).toString(), 3.5f * 72, distanceToMove);
                distanceToMove -= 0.2*72;
                tmpcounter++;
            }
            contentStream.drawLine(3.45f * 72, 5.95f * 72, 3.45f * 72, distanceToMove - 2.0f);
            contentStream.drawLine(2.4f * 72, distanceToMove + (0.15f * 72), 4.2f * 72, distanceToMove + (0.15f * 72));

            writeText(contentStream, boldFont, 10, "TOTAL:", 2.5f * 72, distanceToMove);
            writeText(contentStream, boldFont, 10, "$" + totalGross, 3.5f * 72, distanceToMove);


            contentStream.close();

            generatedPDF.save(reportFilename);
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
