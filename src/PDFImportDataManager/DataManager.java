package PDFImportDataManager;

import PDFImportDataManager.Controllers.MainUIController;
import PDFImportDataManager.DatabaseManager.SQLiteDatabaseManager;
import PDFImportDataManager.ReportGenerator.PDFReportGenerator;
import PDFImportDataManager.interfaces.DatabaseManager;
import PDFImportDataManager.interfaces.ReportGenerator;

import java.io.File;
import java.math.RoundingMode;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager {

    private DatabaseManager globalDBManager = new SQLiteDatabaseManager();
    private ImportedDataManager globalImportedDataManager = new ImportedDataManager();
    private ReportGenerator globalReportGenerator = new PDFReportGenerator();
    private List<Map<String, Integer>> reportData = null;
    private double calculatedDeposit = 0;
    private static double taxPercent = 0.153;
    //private TripleDate dates = null;
    private static double differenceThreshold = 0.10;

    public boolean checkAmounts() {
        Map<String, Integer> data = reportData.get(0);
        calculateDeposit((double)data.get("Gross_Pay") / 100.0, (double)data.get("Federal_Withholding") / 100.0);
        double checkDepositValue = (((double)data.get("Social_Security_Employee_Withholding") / 100.0 * 2.0) +
                ((double)data.get("Medicare_Employee_Withholding") / 100.0 * 2) + (double)data.get("Federal_Withholding") / 100.0);
        return (Math.abs(checkDepositValue - calculatedDeposit) < differenceThreshold );
    }

    public TripleDate getDates(){
        return globalImportedDataManager.getDates();
        //return dates;
    }

    public boolean createDatabase(File DBFile,  Map<String, String> companyInfo){
        return globalDBManager.createDatabase(DBFile, companyInfo);
    }

    public boolean openDatabase(File DBFile){
        return globalDBManager.openDatabase(DBFile);
    }

    public char importPDF(File PDFFile){
        if (!globalImportedDataManager.importPDF(PDFFile)){
            return 'e';
        }
        //globalImportedDataManager

        return 'g';
        //);
    }

    public boolean generateReport(File PDFFile, String entryID){
        reportData = globalDBManager.getEntry(entryID);
        Map<String, Integer> basicEntries = reportData.get(0);
        calculateDeposit((double)basicEntries.get("Gross_Pay") / 100.0, (double)basicEntries.get("Federal_Withholding") / 100.0);
        String firstDate = basicEntries.get("Start_Year") + "-" + String.format("%02d", basicEntries.get("Start_Month")) +
                "-" + String.format("%02d", basicEntries.get("Start_Day"));
        String endDate = basicEntries.get("End_Year") + "-" + String.format("%02d", basicEntries.get("End_Month")) +
                "-" + String.format("%02d", basicEntries.get("End_Day"));
        String payDate = basicEntries.get("End_Year") + "-" + String.format("%02d", basicEntries.get("Pay_Month")) +
                "-" + String.format("%02d", basicEntries.get("Pay_Day"));
        TripleDate dates = new TripleDate(LocalDate.parse(firstDate), LocalDate.parse(endDate), LocalDate.parse(payDate));
        Map companyInfo = globalDBManager.getCompanyInfo();
        globalReportGenerator.setData(companyInfo.get("Company_Name").toString(), companyInfo.get("Company_EIN").toString(),
                companyInfo.get("Company_PIN").toString(), reportData.get(1), (double)basicEntries.get("Gross_Pay") / 100.0,
                (double)basicEntries.get("Federal_Withholding") / 100.0, (double)basicEntries.get("State_Withholding") / 100.0,
                calculatedDeposit, taxPercent, dates);
        return globalReportGenerator.outputReport(PDFFile);
    }

    private void calculateDeposit(double grossPay, double federalWithholding){
        calculatedDeposit = grossPay * taxPercent + federalWithholding;
    }

    public Map<Integer, Map<String, Map<String, String>>> getOverview() {

        //Map<Year, Map<MonthName, Map<Week, entryID>>>>
        Map <Integer, Map<String, Map<String, String>>> returnMap = new HashMap<>();
        for (String ID: globalDBManager.getEntryList()) {
            System.out.println("ID: " + ID);
            Map<String, Integer> tmpEntryInfo = globalDBManager.getEntryInfo(ID);
            int tmpMonth = tmpEntryInfo.get("Month");
            String tmpMonthName = new DateFormatSymbols().getMonths()[tmpMonth-1];
            int tmpYear = tmpEntryInfo.get("Year");
            int tmpStartDay = tmpEntryInfo.get("Start_Day");

            Map<String, Map <String, String>> tmpMonthNamesMap = returnMap.getOrDefault(tmpYear, new HashMap<>());
            Map<String, String> tmpDatesToIDMap = tmpMonthNamesMap.getOrDefault(tmpMonthName, new HashMap<>());
            System.out.println("MonthsMap: " + tmpMonthNamesMap);

            tmpDatesToIDMap.put("Week of " + tmpMonthName + " " + tmpStartDay, ID);
            tmpMonthNamesMap.put(tmpMonthName, tmpDatesToIDMap);
            returnMap.put(tmpYear, tmpMonthNamesMap);
            System.out.println("ReturnMap: " + returnMap);
        }
        return returnMap;
    }

    public List<Map<String, Integer>> getEntryData(String entryID){
        return globalDBManager.getEntry(entryID);
    }

    public String getFormattedEntryData(String entryID) {
        //Create DecimalFormatter to round numbers to 2 digits
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.HALF_UP);
        String returnString = "";
        List<Map<String, Integer>> returnedData = globalDBManager.getEntry(entryID);
        Map<String, Integer> data = returnedData.get(0);
        returnString = "Entry info:\n";
        returnString += "Week Dates: " + data.get("Start_Month") + "/" + data.get("Start_Day") + "/" + data.get("Start_Year") + " - "
                + data.get("End_Month") + "/" + data.get("End_Day") + "/" + data.get("End_Year") + "\n";
        returnString += "Deposit payment date: " + data.get("Pay_Month") + "/" + data.get("Pay_Day") + "/" + data.get("Pay_Year") + "\n";
        returnString += "Gross pay: $" + df.format(data.get("Gross_Pay") / 100.0) + "\n";
        returnString += "F/W: $" + df.format(data.get("Federal_Withholding") / 100.0) + "\n";
        returnString += "S/W: $" + df.format(data.get("State_Withholding") / 100.0) + "\n";

        return returnString;
    }

    public void deleteEntry(String entryID) {
        globalDBManager.removeEntry(entryID);
    }
}



