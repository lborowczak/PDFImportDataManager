package PDFImportDataManager;

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
    private static double socialSecurityPercent = 0.0620;
    private static double medicarePercent = 0.01450;
    private static double statePercent = 0.03750;
    //private TripleDate dates = null;
    private static double differenceThreshold = 0.10;

    public boolean checkAmounts(List<Map<String, Integer>> data) {
        boolean isDataCorrect = true;
        double checkGrossValue = 0.0;
        double checkSocialSecurity = 0.0;
        double checkMedicare = 0.0;
        double checkStateWithholding = 0.0;
        Map<String, Integer> valuesData = data.get(0);
        Map<String, Integer> extraData = data.get(1);
        int storedGross = valuesData.get("Gross_Pay");
        int storedSocialSecurity = valuesData.get("Social_Security_Employee_Withholding");
        int storedMedicare = valuesData.get("Medicare_Employee_Withholding");
        int storedFederalWithholding = valuesData.get("Federal_Withholding");
        int storedStateWithholding = valuesData.get("State_Withholding");

        //Check that Social Security matches
        checkSocialSecurity = (double)storedGross * socialSecurityPercent;
        isDataCorrect = isDataCorrect && (Math.abs(checkSocialSecurity - (double)storedSocialSecurity) < (differenceThreshold * 100.0));

        //Check that Medicare matches
        checkMedicare = (double)storedGross * medicarePercent;
        isDataCorrect = isDataCorrect && (Math.abs(checkMedicare - (double)storedMedicare) < (differenceThreshold * 100.0));

        //Check that State Withholding matches
        checkStateWithholding = (double)storedStateWithholding * statePercent;
        isDataCorrect = isDataCorrect && (Math.abs(checkMedicare - (double)storedMedicare) < (differenceThreshold * 100.0));


        //Check that deposit values match
        calculateDeposit(
                (double)storedGross / 100.0, (double)storedFederalWithholding / 100.0
        );

        double checkDepositValue = (((double)storedSocialSecurity / 100.0 * 2.0) +
            ((double)storedMedicare / 100.0 * 2) + (double)storedFederalWithholding / 100.0);

        isDataCorrect = isDataCorrect && (Math.abs(checkDepositValue - calculatedDeposit) < differenceThreshold);

        //Check that custom items add up to total gross
        for(Map.Entry<String, Integer> entry : extraData.entrySet()) {
            checkGrossValue += entry.getValue();
        }
        isDataCorrect = isDataCorrect && (Math.abs(checkGrossValue - storedGross) < (differenceThreshold * 100.0));



        return isDataCorrect;
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
        if (!globalDBManager.addEntry(globalImportedDataManager.getParsedData())){
            return 'i';
        }
        if (!checkAmounts(globalImportedDataManager.getParsedData())){
            return 'c';
        }
        return 'g';
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



