package PDFImportDataManager;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;


public class PDFImporterTest {

    private PDFImporter testPortraitImporter = new TabulaPDFImporter();
    private PDFImporter testLandscapeImporter = new TabulaPDFImporter();
    private List<List<List<String>>> portraitData = asList(
            asList(
                asList("Employee Wages", "Taxes and Adjustments", "", "", "", "", ""),
                asList("Gross Pay","", "", "", "", ""),
                asList("Hourly Rate", "20", "50.00", "1,000.00", "10", ""),
                asList("Overtime Pay", "", "75.00", "0.00", "10", ""),
                asList("Total Gross Pay", "20", "", "1,000.00", "20", ""),
                asList("Adjusted Gross Pay", "20", "", "1,000.00", "20", ""),
                asList("Taxes Withheld", "", "", "", "", ""),
                asList("Federal Withholding", "", "", "-158.00", "", ""),
                asList("Medicare Employee", "", "", "-14.50", "", ""),
                asList("Social Security Employee", "", "", "-62.00", "", ""),
                asList("IL - Withholding", "", "", "-37.50", "", ""),
                asList("Medicare Employee Addl Tax", "", "", "0.00", "", ""),
                asList("Total Taxes Withheld", "", "", "-272.00", "", ""),
                asList("Net Pay", "20", "", "728.00", "20", ""),
                asList("Employer Taxes and Contributions", "", "", "", "", ""),
                asList("Federal Unemployment", "", "", "0.00", "", ""),
                asList("Medicare Company", "", "", "14.50", "", ""),
                asList("Social Security Company", "", "", "62.00", "", ""),
                asList("IL - Unemployment Company", "", "", "5.50", "", ""),
                asList("Total Employer Taxes and Contributions", "", "", "82.00", "", ""),
                asList("", "", "", "", "", "Page 1")),
            asList(
                asList("Employee Wages", "Taxes and Adjustments", "", "", "", "", ""),
                asList("Gross Pay", "", "", "", "", ""),
                asList("Hourly Rate", "50.00", "500.00", "20", "25.00", ""),
                asList("Overtime Pay", "50.00", "500.00", "30", "50.00", ""),
                asList("Total Gross Pay", "", "1,000.00", "50", "", ""),
                asList("Adjusted Gross Pay", "", "1,000.00", "50", "", ""),
                asList("Taxes Withheld", "", "", "", "", ""),
                asList("Federal Withholding", "", "-243.00", "", "", ""),
                asList("Medicare Employee", "", "-14.50", "", "", ""),
                asList("Social Security Employee", "", "-62.00", "", "", ""),
                asList("IL - Withholding", "", "-37.50", "", "", ""),
                asList("Medicare Employee Addl Tax", "", "0.00", "", "", ""),
                asList("Total Taxes Withheld", "", "-357.00", "", "", ""),
                asList("Net Pay", "", "643.00", "50", "", ""),
                asList("Employer Taxes and Contributions", "", "", "", "", ""),
                asList("Federal Unemployment", "", "0.00", "", "", ""),
                asList("Medicare Company", "", "14.50", "", "", ""),
                asList("Social Security Company", "", "62.00", "", "", ""),
                asList("IL - Unemployment Company", "", "5.50", "", "", ""),
                asList("Total Employer Taxes and Contributions", "", "82.00", "", "", ""),
                asList("", "", "", "", "", "Page 2")),
            asList(
                asList("Employee Wages", "Taxes and Adjustments", "", "", "", "", ""),
                asList("Gross Pay", "", "", "", "", ""),
                asList("Hourly Rate", "500.00", "31.5", "24.00", "756.00", ""),
                asList("Overtime Pay", "1,500.00", "12.5", "36.00", "450.00", ""),
                asList("Total Gross Pay", "2,000.00", "44", "", "1,206.00", ""),
                asList("Adjusted Gross Pay", "2,000.00", "44", "", "1,206.00", ""),
                asList("Taxes Withheld", "", "", "", "", ""),
                asList("Federal Withholding", "-194.00", "", "", "-182.00", ""),
                asList("Medicare Employee", "-29.00", "", "", "-17.49", ""),
                asList("Social Security Employee", "-124.00", "", "", "-74.77", ""),
                asList("IL - Withholding", "-75.00", "", "", "-45.23", ""),
                asList("Medicare Employee Addl Tax", "0.00", "", "", "0.00", ""),
                asList("Total Taxes Withheld", "-422.00", "", "", "-319.49", ""),
                asList("Net Pay", "1,578.00", "44", "", "886.51", ""),
                asList("Employer Taxes and Contributions", "", "", "", "", ""),
                asList("Federal Unemployment", "0.00", "", "", "0.00", ""),
                asList("Medicare Company", "29.00", "", "", "17.49", ""),
                asList("Social Security Company", "124.00", "", "", "74.77", ""),
                asList("IL - Unemployment Company", "11.00", "", "", "6.63", ""),
                asList("Total Employer Taxes and Contributions", "164.00", "", "", "98.89", ""),
                asList("", "", "", "", "", "Page 3")),
            asList(
                asList("Employee Wages", "Taxes and Adjustments", "", "", "", "", ""),
                asList("Gross Pay", "", "", "", "", ""),
                asList("Hourly Rate", "43", "18.00", "774.00", "16", ""),
                asList("Overtime Pay", "3", "27.00", "81.00", "32", ""),
                asList("Total Gross Pay", "46", "", "855.00", "48", ""),
                asList("Adjusted Gross Pay", "46", "", "855.00", "48", ""),
                asList("Taxes Withheld", "", "", "", "", ""),
                asList("Federal Withholding", "", "", "-89.00", "", ""),
                asList("Medicare Employee", "", "", "-12.40", "", ""),
                asList("Social Security Employee", "", "", "-53.01", "", ""),
                asList("IL - Withholding", "", "", "-32.06", "", ""),
                asList("Medicare Employee Addl Tax", "", "", "0.00", "", ""),
                asList("Total Taxes Withheld", "", "", "-186.47", "", ""),
                asList("Net Pay", "46", "", "668.53", "48", ""),
                asList("Employer Taxes and Contributions", "", "", "", "", ""),
                asList("Federal Unemployment", "", "", "0.00", "", ""),
                asList("Medicare Company", "", "", "12.40", "", ""),
                asList("Social Security Company", "", "", "53.01", "", ""),
                asList("IL - Unemployment Company", "", "", "4.70", "", ""),
                asList("Total Employer Taxes and Contributions", "", "", "70.11", "", ""),
                asList("", "", "", "", "", "Page 4")),
            asList(
                asList("Employee Wages", "Taxes and Adjustments", "", "", "", ""),
                asList("Gross Pay", "", "", "", ""),
                asList("Hourly Rate", "14.00", "224.00", "140.50", ""),
                asList("Overtime Pay", "21.00", "672.00", "87.50", ""),
                asList("Total Gross Pay", "", "896.00", "228.00", ""),
                asList("Adjusted Gross Pay", "", "896.00", "228.00", ""),
                asList("Taxes Withheld", "", "", "", ""),
                asList("Federal Withholding", "", "-222.00", "", ""),
                asList("Medicare Employee", "", "-13.25", "", ""),
                asList("Social Security Employee", "", "-55.66", "", ""),
                asList("IL - Withholding", "", "-33.80", "", ""),
                asList("Medicare Employee Addl Tax", "", "0.00", "", ""),
                asList("Total Taxes Withheld", "", "-324.71", "", ""),
                asList("Net Pay", "", "571.29", "228.00", ""),
                asList("Employer Taxes and Contributions", "", "", "", ""),
                asList("Federal Unemployment", "", "0.00", "", ""),
                asList("Medicare Company", "", "13.25", "", ""),
                asList("Social Security Company", "", "55.66", "", ""),
                asList("IL - Unemployment Company", "", "4.93", "", ""),
                asList("Total Employer Taxes and Contributions", "", "73.84", "", ""),
                asList("", "", "", "", "Page 5")),
            asList(
                asList("Employee Wages", "Taxes and Adjustments", "", ""),
                asList("Gross Pay", "", ""),
                asList("Hourly Rate", "3,754.00", ""),
                asList("Overtime Pay", "3,203.00", ""),
                asList("Total Gross Pay", "6,957.00", ""),
                asList("Adjusted Gross Pay", "6,957.00", ""),
                asList("Taxes Withheld", "", ""),
                asList("Federal Withholding", "-1,088.00", ""),
                asList("Medicare Employee", "-101.14", ""),
                asList("Social Security Employee", "-431.44", ""),
                asList("IL - Withholding", "-261.09", ""),
                asList("Medicare Employee Addl Tax", "0.00", ""),
                asList("Total Taxes Withheld", "-1,881.67", ""),
                asList("Net Pay", "5,075.33", ""),
                asList("Employer Taxes and Contributions", "", ""),
                asList("Federal Unemployment", "0.00", ""),
                asList("Medicare Company", "101.14", ""),
                asList("Social Security Company", "431.44", ""),
                asList("IL - Unemployment Company", "38.26", ""),
                asList("Total Employer Taxes and Contributions", "570.84", ""),
                asList("", "", "Page 6"))
            );

    private List<List<List<String>>> landscapeData = asList(
            asList(
                asList("Employee Wages", "Taxes and Adjustments", "", "", "", "", "", "", ""),
                asList("Gross Pay", "", "", "", "", "", "", ""),
                asList("Hourly Rate", "20", "50.00", "1,000.00", "10", "50.00", "500.00", ""),
                asList("Overtime Pay", "", "75.00", "0.00", "10", "50.00", "500.00", ""),
                asList("Total Gross Pay", "20", "", "1,000.00", "20", "", "1,000.00", ""),
                asList("Adjusted Gross Pay", "20", "", "1,000.00", "20", "", "1,000.00", ""),
                asList("Taxes Withheld", "", "", "", "", "", "", ""),
                asList("Federal Withholding", "", "", "-158.00", "", "", "-243.00", ""),
                asList("Medicare Employee", "", "", "-14.50", "", "", "-14.50", ""),
                asList("Social Security Employee", "", "", "-62.00", "", "", "-62.00", ""),
                asList("IL - Withholding", "", "", "-37.50", "", "", "-37.50", ""),
                asList("Medicare Employee Addl Tax", "", "", "0.00", "", "", "0.00", ""),
                asList("Total Taxes Withheld", "", "", "-272.00", "", "", "-357.00", ""),
                asList("Net Pay", "20", "", "728.00", "20", "", "643.00", ""),
                asList("Employer Taxes and Contributions", "", "", "", "", "", "", ""),
                asList("Federal Unemployment", "", "", "0.00", "", "", "0.00", ""),
                asList("Medicare Company", "", "", "14.50", "", "", "14.50", ""),
                asList("Social Security Company", "", "", "62.00", "", "", "62.00", ""),
                asList("IL - Unemployment Company", "", "", "5.50", "", "", "5.50", ""),
                asList("Total Employer Taxes and Contributions", "", "", "82.00", "", "", "82.00", ""),
                asList("", "", "", "", "", "", "", "Page 1")),
            asList(
                asList("Employee Wages", "Taxes and Adjustments", "", "", "", "", "", "", ""),
                asList("Gross Pay", "", "", "", "", "", "", ""),
                asList("Hourly Rate", "20", "25.00", "500.00", "31.5", "24.00", "756.00", ""),
                asList("Overtime Pay", "30", "50.00", "1,500.00", "12.5", "36.00", "450.00", ""),
                asList("Total Gross Pay", "50", "", "2,000.00", "44", "", "1,206.00", ""),
                asList("Adjusted Gross Pay", "50", "", "2,000.00", "44", "", "1,206.00", ""),
                asList("Taxes Withheld", "", "", "", "", "", "", ""),
                asList("Federal Withholding", "", "", "-194.00", "", "", "-182.00", ""),
                asList("Medicare Employee", "", "", "-29.00", "", "", "-17.49", ""),
                asList("Social Security Employee", "", "", "-124.00", "", "", "-74.77", ""),
                asList("IL - Withholding", "", "", "-75.00", "", "", "-45.23", ""),
                asList("Medicare Employee Addl Tax", "", "", "0.00", "", "", "0.00", ""),
                asList("Total Taxes Withheld", "", "", "-422.00", "", "", "-319.49", ""),
                asList("Net Pay", "50", "", "1,578.00", "44", "", "886.51", ""),
                asList("Employer Taxes and Contributions", "", "", "", "", "", "", ""),
                asList("Federal Unemployment", "", "", "0.00", "", "", "0.00", ""),
                asList("Medicare Company", "", "", "29.00", "", "", "17.49", ""),
                asList("Social Security Company", "", "", "124.00", "", "", "74.77", ""),
                asList("IL - Unemployment Company", "", "", "11.00", "", "", "6.63", ""),
                asList("Total Employer Taxes and Contributions", "", "", "164.00", "", "", "98.89", ""),
                asList("", "", "", "", "", "", "", "Page 2")),
            asList(
                asList("Employee Wages", "Taxes and Adjustments", "", "", "", "", "", "", ""),
                asList("Gross Pay", "", "", "", "", "", "", ""),
                asList("Hourly Rate", "43", "18.00", "774.00", "16", "14.00", "224.00", ""),
                asList("Overtime Pay", "3", "27.00", "81.00", "32", "21.00", "672.00", ""),
                asList("Total Gross Pay", "46", "", "855.00", "48", "", "896.00", ""),
                asList("Adjusted Gross Pay", "46", "", "855.00", "48", "", "896.00", ""),
                asList("Taxes Withheld", "", "", "", "", "", "", ""),
                asList("Federal Withholding", "", "", "-89.00", "", "", "-222.00", ""),
                asList("Medicare Employee", "", "", "-12.40", "", "", "-13.25", ""),
                asList("Social Security Employee", "", "", "-53.01", "", "", "-55.66", ""),
                asList("IL - Withholding", "", "", "-32.06", "", "", "-33.80", ""),
                asList("Medicare Employee Addl Tax", "", "", "0.00", "", "", "0.00", ""),
                asList("Total Taxes Withheld", "", "", "-186.47", "", "", "-324.71", ""),
                asList("Net Pay", "46", "", "668.53", "48", "", "571.29", ""),
                asList("Employer Taxes and Contributions", "", "", "", "", "", "", ""),
                asList("Federal Unemployment", "", "", "0.00", "", "", "0.00", ""),
                asList("Medicare Company", "", "", "12.40", "", "", "13.25", ""),
                asList("Social Security Company", "", "", "53.01", "", "", "55.66", ""),
                asList("IL - Unemployment Company", "", "", "4.70", "", "", "4.93", ""),
                asList("Total Employer Taxes and Contributions", "", "", "70.11", "", "", "73.84", ""),
                asList("", "", "", "", "", "", "", "Page 3")),
            asList(
                asList("Employee Wages", "Taxes and Adjustments", "", "", ""),
                asList("Gross Pay", "", "", ""),
                asList("Hourly Rate", "140.50", "3,754.00", ""),
                asList("Overtime Pay", "87.50", "3,203.00", ""),
                asList("Total Gross Pay", "228.00", "6,957.00", ""),
                asList("Adjusted Gross Pay", "228.00", "6,957.00", ""),
                asList("Taxes Withheld", "", "", ""),
                asList("Federal Withholding", "", "-1,088.00", ""),
                asList("Medicare Employee", "", "-101.14", ""),
                asList("Social Security Employee", "", "-431.44", ""),
                asList("IL - Withholding", "", "-261.09", ""),
                asList("Medicare Employee Addl Tax", "", "0.00", ""),
                asList("Total Taxes Withheld", "", "-1,881.67", ""),
                asList("Net Pay", "228.00", "5,075.33", ""),
                asList("Employer Taxes and Contributions", "", "", ""),
                asList("Federal Unemployment", "", "0.00", ""),
                asList("Medicare Company", "", "101.14", ""),
                asList("Social Security Company", "", "431.44", ""),
                asList("IL - Unemployment Company", "", "38.26", ""),
                asList("Total Employer Taxes and Contributions", "", "570.84", ""),
                asList("", "", "", "Page 4"))
    );


    @Before
    public void setUp() throws Exception {
        assertEquals(true, testPortraitImporter.importPDF("tst/Example_report_sent_in_portrait.pdf"));
        assertEquals(true, testLandscapeImporter.importPDF("tst/Example_report_sent_in_landscape.pdf"));
    }

    @After
    public void tearDown() throws Exception {


    }

    @Test
    public void checkPortraitDate() throws Exception{
        assertEquals("October 2 - 8, 2016", testPortraitImporter.getDatesString());
    }

    @Test
    public void checkLandscapeDate() throws Exception{
        assertEquals("October 2 - 8, 2016", testLandscapeImporter.getDatesString());
    }

    @Test
    public void checkPortraitData() throws Exception{
        assertEquals(portraitData, testPortraitImporter.getData());
        //assertTrue(Arrays.deepEquals(portraitData.toArray(), testPortraitImporter.getData().toArray()));
    }

    @Test
    public void checkLandscapeData() throws Exception{
        assertEquals(landscapeData.toString(), testLandscapeImporter.getData().toString());
    }


}