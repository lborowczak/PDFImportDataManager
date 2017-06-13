package PDFImportDataManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntryData {

    private Map<String, Integer> mainDataMap;
    private Map<String, Integer> extraDataMap;
    private TripleDate dates;


    public EntryData(){
        mainDataMap = new HashMap<String, Integer>();
        extraDataMap = new HashMap<String, Integer>();

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


    public void setDates(TripleDate datesToSet){
        LocalDate startDate = datesToSet.getBeginDate();
        LocalDate endDate = datesToSet.getEndDate();
        LocalDate payDate = datesToSet.getPayDate();
        setStartDay(startDate.getDayOfMonth());
        setStartMonth(startDate.getMonthValue());
        setStartYear(startDate.getYear());
        setEndDay(endDate.getDayOfMonth());
        setEndMonth(endDate.getMonthValue());
        setEndYear(endDate.getYear());
        setPayDay(payDate.getDayOfMonth());
        setpPayMonth(payDate.getMonthValue());
        setPayYear(payDate.getYear());
        dates = datesToSet;
    }



    public void setExtraData(String itemName, Integer itemAmount){
        extraDataMap.put(itemName, itemAmount);
    }
    public int getExtraData(String itemName){
        return extraDataMap.get(itemName);
    }

    //Temporary function to keep compatibility with older code
    public List<Map<String, Integer>> getMapList(){
        List<Map<String, Integer>> returnList = new ArrayList<Map<String, Integer>>();
        returnList.add(mainDataMap);
        returnList.add(extraDataMap);
        return returnList;
    }

    public TripleDate getTripleDate(){
        if (dates == null){
            generateTripleDate();
        }
        return dates;
    }

    public String getExtraDataString() {
        final StringBuilder extraDataString = new StringBuilder();
        extraDataMap.forEach((k, v) -> extraDataString.append(k + "," + v + ";"));
        return extraDataString.toString();
    }

    private void generateTripleDate(){
        String firstDate = getStartYear() + "-" + String.format("%02d", getStartMonth()) + "-" + String.format("%02d", getStartDay());
        String endDate = getEndYear() + "-" + String.format("%02d", getEndMonth()) + "-" + String.format("%02d", getEndDay());
        String payDate = getPayYear() + "-" + String.format("%02d", getPayMonth()) + "-" + String.format("%02d", getPayDay());
        dates = new TripleDate(LocalDate.parse(firstDate), LocalDate.parse(endDate), LocalDate.parse(payDate));
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
