package PDFImportDataManager;

import java.io.File;
import java.text.DateFormatSymbols;
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
    private TripleDate dates = null;
    private static double differenceThreshold = 0.10;

    public boolean checkAmounts() {
        Map<String, Integer> testmap = reportData.get(0);
        calculateDeposit((double)testmap.get("Gross_Pay") / 100.0, (double)testmap.get("Federal_Withholding") / 100.0);
        double checkDepositValue = (((double)testmap.get("Social_Security_Employee_Withholding") / 100.0 * 2.0) +
                ((double)testmap.get("Medicare_Employee_Withholding") / 100.0 * 2) + (double)testmap.get("Federal_Withholding") / 100.0);
        return (Math.abs(checkDepositValue - calculatedDeposit) < differenceThreshold );
    }

    public TripleDate getDates(){
        return globalImportedDataManager.getDates();
    }

    public boolean createDatabase(File DBFile){
        //TODO add checks for file path existence, or do it in controller?
        return globalDBManager.createDatabase(DBFile);
    }

    public boolean openDatabase(File DBFile){
        return globalDBManager.openDatabase(DBFile);

    }

    public boolean importPDF(File PDFFile){
        //return (checkValidFile(PDFFile, false) &&
        return globalImportedDataManager.importPDF(PDFFile);//);
        //TODO add checks for file path existence, or do it in controller?
        //return globalImportedDataManager.importPDF(PDFFile);
    }

    public boolean generateReport(File PDFFile){
        if (reportData == null || dates == null || calculatedDeposit == 0){
            return false;
        }
        Map<String, Integer> basicEntries = reportData.get(0);

        Map companyInfo = globalDBManager.getCompanyInfo();
        globalReportGenerator.setData(companyInfo.get("Company_Name").toString(), companyInfo.get("Company_EIN").toString(),
                companyInfo.get("Company_PIN").toString(), reportData.get(1), (double)basicEntries.get("Gross_Pay") / 100.0,
                (double)basicEntries.get("Federal_Withholding") / 100.0, (double)basicEntries.get("State_Withholding") / 100.0,
                calculatedDeposit, taxPercent, dates);
        return globalReportGenerator.outputReport(PDFFile);
    }

    private boolean checkValidFile(String filename, boolean isNew){
        File toBeChecked = new File(filename);
        return toBeChecked.canWrite();

    }

    private void calculateDeposit(double grossPay, double federalWithholding){
        calculatedDeposit = grossPay * taxPercent + federalWithholding;
    }

    public Map<Integer, Map<String, Map<String, String>>> getOverview() {

        //Map<Year, Map<MonthName, Map<Week, entryID>>>>
        Map <Integer, Map<String, Map<String, String>>> returnMap = new HashMap<>();
        for (String ID: globalDBManager.getEntryList()) {
            Map<String, String> tmpDatesToIDMap = new HashMap<>();
            Map<String, Map <String, String>> tmpMonthNamesMap = new HashMap<>();
            Map<String, Integer> tmpEntryInfo = globalDBManager.getEntryInfo(ID);
            int tmpMonth = tmpEntryInfo.get("Month");
            int tmpYear = tmpEntryInfo.get("Year");
            int tmpStartDay = tmpEntryInfo.get("Start_Day");
            int tmpEndDay = tmpEntryInfo.get("End_Day");
            tmpDatesToIDMap.put(tmpStartDay + "- " + tmpEndDay, ID);
            tmpMonthNamesMap.put(new DateFormatSymbols().getMonths()[tmpMonth-1], tmpDatesToIDMap);
            returnMap.put(tmpYear, tmpMonthNamesMap);
        }
        return returnMap;
    }
}
