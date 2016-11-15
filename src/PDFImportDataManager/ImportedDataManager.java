package PDFImportDataManager;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ImportedDataManager {

    private PDFImporter managedPDFImporter = new TabulaPDFImporter();

    public boolean importPDF(String PDFFile){
        return (managedPDFImporter.importPDF(PDFFile));
    }
    //TODO think about below methods and where they should be
    public List<String> getUsers(){
        return null;
    }

    public TripleDate getDates() {


        String yearString, firstMonthString, secondMonthString, firstDayString, secondDayString = "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd");

        //String format: "Month day1 - day2, year" or "Month day1 through Month day2, year"
        String tmpDateString = managedPDFImporter.getDatesString();
        String[] dateStringArray = tmpDateString.split(" ");

        //Get the year, month, and days for the payment week
        yearString = dateStringArray[dateStringArray.length];
        firstMonthString = dateStringArray[0].substring(0,2);
        if (dateStringArray[2].contains("through")){
            secondMonthString = dateStringArray[4];
        }
        else {
            secondMonthString = firstMonthString;
        }

        return null;
    }

}
