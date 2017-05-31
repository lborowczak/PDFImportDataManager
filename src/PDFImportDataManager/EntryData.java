package PDFImportDataManager;

import java.util.List;
import java.util.Map;

public class EntryData {

    private Map<String, Integer> mainDataMap;
    private Map<String, Integer> extraDataMap;


    public EntryData(){

    }

    public int getStartDay(){
        return getMainMapItem("Start_Day");
    }
    public void setStartDay(int startDay){
        mainDataMap.put("Start_Day", startDay);
    }


    public int getStartMonth(){
        return getMainMapItem("Start_Month");
    }
    public void setStartMonth(int startMonth){
        mainDataMap.put("Start_Month", startMonth);
    }


    public int getStartYear(){
        return getMainMapItem("Start_Year");
    }
    public void setStartYear(int startYear){
        mainDataMap.put("Start_Year", startYear);
    }


    public int getEndDay(){
        return getMainMapItem("End_Day");
    }
    public void setEndDay(int endDay){
        mainDataMap.put("End_Day", endDay);
    }


    public int getEndMonth(){
        return getMainMapItem("End_Month");
    }
    public void setEndMonth(int endMonth){
        mainDataMap.put("End_Month", endMonth);
    }


    public int getEndYear(){
        return getMainMapItem("End_Year");
    }
    public void setEndYear(int endYear){
        mainDataMap.put("End_Year", endYear);
    }


    public int getPayDay(){
        return getMainMapItem("Pay_Day");
    }
    public void setPayDay(int payDay){
        mainDataMap.put("Pay_Day", payDay);
    }


    public int getPayMonth(){
        return getMainMapItem("Pay_Month");
    }
    public void setpPayMonth(int payMonth){
        mainDataMap.put("Pay_Month", payMonth);
    }


    public int getPayYear(){
        return getMainMapItem("Pay_Year");
    }
    public void setPayYear(int payYear){
        mainDataMap.put("Pay_Year", payYear);
    }


    public void setGrossPay(int payAmount) {
        mainDataMap.put("Gross_Pay", payAmount);
    }
    public int getGrossPay() {
        return getMainMapItem("Gross_Pay");
    }
    public double getGrossPayDouble() {
        int grossPay = getMainMapItem("Gross_Pay");
        return (double)grossPay / 100.0;
    }


    public void setFederalWithholding(int federalWithholdingAmount) {
        mainDataMap.put("Federal_Withholding", federalWithholdingAmount);
    }
    public int getFederalWithholding() {
        return getMainMapItem("Federal_Withholding");
    }
    public double getFederalWithholdingDouble() {
        int federalWithholding = getMainMapItem("Federal_Withholding");
        return (double)federalWithholding / 100.0;
    }


    public void setStateWithholding(int stateWithholdingAmount) {
        mainDataMap.put("State_Withholding", stateWithholdingAmount);
    }

    public int getStateWithholding() {
        return getMainMapItem("State_Withholding");
    }

    public double getStateWithholdingDouble() {
        int stateWithholding = getMainMapItem("State_Withholding");
        return (double)stateWithholding / 100.0;
    }


    public void setMedicareEmployeeWithholding(int medicareEmployeeWithholdingAmount) {
        mainDataMap.put("Medicare_Employee_Withholding", medicareEmployeeWithholdingAmount);
    }
    public int getMedicareEmployeeWithholding() {
        return getMainMapItem("Medicare_Employee_Withholding");
    }
    public double getMedicareEmployeeWithholdingDouble() {
        int medicareEmployeeWithholding = getMainMapItem("Medicare_Employee_Withholding");
        return (double)medicareEmployeeWithholding / 100.0;
    }


    public void setSocialSecurityEmployeeWithholding(int socialSecurityEmployeeWithholdingAmount) {
        mainDataMap.put("Social_Security_Employee_Withholding", socialSecurityEmployeeWithholdingAmount);
    }
    public int getSocialSecurityEmployeeWithholding() {
        return getMainMapItem("Social_Security_Employee_Withholding");
    }
    public double getSocialSecurityEmployeeWithholdingDouble() {
        int socialSecurityEmployeeWithholding = getMainMapItem("Social_Security_Employee_Withholding");
        return (double)socialSecurityEmployeeWithholding / 100.0;
    }


    private int getMainMapItem(String itemName) {
        return getMapItem(itemName, mainDataMap);
    }

    private int getBreakdownMapItem(String itemName) {
        return getMapItem(itemName, extraDataMap);
    }


    private int getMapItem(String itemName, Map<String, Integer> dataMap) {
        Integer currVal = dataMap.get(itemName);
        if (currVal == null) {
            return 0;
        }
        else {
            return (int)currVal;
        }

    }
}
