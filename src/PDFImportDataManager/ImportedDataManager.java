package PDFImportDataManager;

import PDFImportDataManager.PDFImporter.TabulaPDFImporter;
import PDFImportDataManager.interfaces.PDFImporter;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;

public class ImportedDataManager {

    private PDFImporter managedPDFImporter = new TabulaPDFImporter();
    //private List<List<List<String>>> importedData;
    private Map<String, Map<String, Integer>> parsedData;
    private List<String> users;
    private List<Map> userData;


    public boolean importPDF(File PDFFile) {

        if (!managedPDFImporter.importPDF(PDFFile)) {

            //Import failed
            return false;
        }
        List<List<List<String>>> importedData = managedPDFImporter.getData();
        parseData(importedData);





        return true;
    }

    //TODO think about below methods and where they should be
    public List<String> getUsers() {
        //List<List<List<String>>>
        return users;
    }

    public TripleDate getDates() {

        String yearString, firstMonthString, secondMonthString, firstDayString, secondDayString = "";
        LocalDate firstDate, secondDate, payDate;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd");
        formatter.withLocale(Locale.US);

        //String format: "Month day1 - day2, year" or "Month day1 through Month day2, year"
        String tmpDateString = managedPDFImporter.getDatesString();
        String[] dateStringArray = tmpDateString.split(" ");

        //Get the year, month, and days for the payment week
        yearString = dateStringArray[dateStringArray.length - 1];

        firstMonthString = dateStringArray[0].substring(0, 3);
        if (dateStringArray[2].contains("through")) {
            secondMonthString = dateStringArray[4];
        } else {
            secondMonthString = firstMonthString;
        }

        firstDayString = String.format("%02d", Integer.parseInt(dateStringArray[1]));
        secondDayString = dateStringArray[dateStringArray.length - 2].replaceAll(",", "");
        secondDayString = String.format("%02d", Integer.parseInt(secondDayString));

        firstDate = LocalDate.parse(yearString + "-" + firstMonthString + "-" + firstDayString, formatter);

        secondDate = LocalDate.parse(yearString + "-" + secondMonthString + "-" + secondDayString, formatter);

        //Get Wednesday of the next week
        TemporalField tmpWeekDescription = WeekFields.of(Locale.US).dayOfWeek();

        //Week in US starts on Sunday, so get the 4th day of the week (Wednesday)
        payDate = firstDate.plusDays(10).with(tmpWeekDescription, 4);

        return new TripleDate(firstDate, secondDate, payDate);
    }

    public Map<String, String> getUserData(String user) {
        return null;
    }


    private void parseData(List<List<List<String>>> dataToParse){
        List<List<String>> combinedPageData = new ArrayList<>();
        for (List<List<String>> currPage : dataToParse) {

        }
    }



}