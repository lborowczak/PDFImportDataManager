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
    private List<List<Map<String, Integer>>> parsedData;
    private List<String> users;
    private List<Map<String, Integer>> userData;


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

    public List<Map<String, Integer>> getUserData(int userIndex) {
        if (userIndex > parsedData.size() - 1) {
            return null;
        }
        else return parsedData.get(userIndex);
    }

    public int getUserCount(){
        return (parsedData.size() - 1);
    }


    public List<Map<String, Integer>> getTotalData() {
        return parsedData.get(parsedData.size() - 1);
    }


    private void parseData(List<List<List<String>>> dataToParse){

        //List of users, having a list of:
        //Map1: neededData, value
        //Map2: breakdownInfo, value
        parsedData = new ArrayList<>();
        int initialData = -1;
        int finalData = 1000;
        int grossStartIndex = -1;
        int grossEndIndex = 1000;
        int federalWithholdingIndex = -1;
        int medicareEmployeeWithholdingIndex = -1;
        int socialSecurityWithholdingIndex = -1;
        int stateWithholdingIndex = -1;
        List<String> grossPayEntries = new ArrayList<>();

                List<List<String>> firstItem = dataToParse.get(0);
        for (int i=1; i < firstItem.size(); i++){
            String tmpStringToCheck = firstItem.get(i).get(0).trim();
            switch (tmpStringToCheck){
                case "Gross Pay":
                    grossStartIndex = i + 1;
                    initialData = i + 1;
                    break;
                case "Total Gross Pay":
                    grossEndIndex = i;
                    break;
                case "Federal Withholding":
                    federalWithholdingIndex = i;
                    break;
                case "Medicare Employee":
                    medicareEmployeeWithholdingIndex = i;
                    break;
                case "Social Security Employee":
                    socialSecurityWithholdingIndex = i;
                    break;
                case "IL - Withholding":
                    stateWithholdingIndex = i;
                    finalData = i + 1;
                    break;
                default:
                    if (grossEndIndex != -1 && i >= grossStartIndex){
                        if (i < grossEndIndex){
                            grossPayEntries.add(tmpStringToCheck);
                        }
                    }
                    break;
            }
        }

        List<List<String>> fullRows = new ArrayList<>(finalData);
        //Initialize Arraylist
        for (int i=0; i <= finalData; i++){
            fullRows.add(new ArrayList<>());
        }
        int maxRowSize = 0;
        for (int i=0; i < dataToParse.size(); i++){
            List<List<String>> currPage = dataToParse.get(i);
            for (int j = initialData; j < finalData; j++){
                List<String> currRow = currPage.get(j);
                for (int k = 1; k < currRow.size() - 1; k++) {
                    maxRowSize = Math.max(currRow.size(), maxRowSize);
                    fullRows.get(j).add(currRow.get(k));

                    //Fix Tabula not getting empty column from total
                    if (i == dataToParse.size() - 2 && k == currRow.size() - 2){
                        fullRows.get(j).add("");
                    }

                }
            }
        }

        //System.out.println(fullRows);

        List<String> tmpRow = fullRows.get(initialData);
        for (int k = 0; (k < tmpRow.size() / 3); k++) {
            parsedData.add(k, Arrays.asList(new HashMap<String, Integer>(), new HashMap<String, Integer>()));
        }


        for (int j = initialData; j < finalData; j++){
            List<String> currRow = fullRows.get(j);
            int currUser = 0;
            int dataColumnCounter = 1;
            for (int k = 0; k < currRow.size(); k++){
                if (dataColumnCounter == 3){
                    dataColumnCounter = 1;
                    int currItemValue = 0;
                    if (!currRow.get(k).trim().equals("")) {
                        currItemValue = ((Double) Math.ceil(Math.abs(Double.parseDouble(currRow.get(k).replaceAll(",", "")) * 100.0))).intValue();
                    }
                    //If statements used because case requires constants
                    if (j >= grossStartIndex && j < grossEndIndex){
                        parsedData.get(currUser).get(1).put(grossPayEntries.get(j - grossStartIndex), currItemValue);
                    }
                    else if (j == grossEndIndex){
                        parsedData.get(currUser).get(0).put("Gross_Pay", currItemValue);
                        //System.out.println(" ... gross total is: " + currItemValue);
                    }
                    else if (j == federalWithholdingIndex){
                        parsedData.get(currUser).get(0).put("Federal_Withholding", currItemValue);
                        //System.out.println(" ... F/W is: " + currItemValue);
                    }
                    else if (j == medicareEmployeeWithholdingIndex){
                        parsedData.get(currUser).get(0).put("Medicare_Employee_Withholding", currItemValue);
                        //System.out.println(" ... Medicare is: " + currItemValue);
                    }
                    else if (j == socialSecurityWithholdingIndex){
                        parsedData.get(currUser).get(0).put("Social_Security_Employee_Withholding", currItemValue);
                        //System.out.println(" ... Social Security is: " + currItemValue);
                    }
                    else if (j == stateWithholdingIndex){
                        parsedData.get(currUser).get(0).put("State_Withholding", currItemValue);
                        //System.out.println(" ... S/W is: " + currItemValue);
                    }
                    currUser++;
                } else {
                    dataColumnCounter++;
                }
            }
        }

        TripleDate dates = getDates();
        for ( List<Map<String, Integer>> user : parsedData) {
            LocalDate startDate = dates.getBeginDate();
            LocalDate endDate = dates.getEndDate();
            LocalDate payDate = dates.getPayDate();
            user.get(0).put("Start_Day", startDate.getDayOfMonth());
            user.get(0).put("Start_Month", startDate.getMonthValue());
            user.get(0).put("Start_Year", startDate.getYear());
            user.get(0).put("End_Day", endDate.getDayOfMonth());
            user.get(0).put("End_Month", endDate.getMonthValue());
            user.get(0).put("End_Year", endDate.getYear());
            user.get(0).put("Pay_Day", payDate.getDayOfMonth());
            user.get(0).put("Pay_Month", payDate.getMonthValue());
            user.get(0).put("Pay_Year", payDate.getYear());
        }
    }


    public List<Map<String, Integer>> getParsedData() {
        return getTotalData();
    }
}