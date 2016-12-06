package PDFImportDataManager;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class DataManager {

    private DatabaseManager globalDBManager = new SQLiteDatabaseManager();
    private ImportedDataManager globalImportedDataManager = new ImportedDataManager();
    private ReportGenerator globalReportGenerator = new PDFReportGenerator();


    public TripleDate getDates(){
        return globalImportedDataManager.getDates();
    }

    public boolean createDatabase(String DBFile){
        //TODO add checks for file path existence, or do it in controller?
        return globalDBManager.createDatabase(DBFile);
    }

    public boolean openDatabase(String DBFile){
        //TODO add checks for file path existence, or do it in controller?
        return globalDBManager.openDatabase(DBFile);
    }

    public boolean importPDF(String PDFFile){
        return (checkValidFilename(PDFFile, false) &&
                globalImportedDataManager.importPDF(PDFFile));
        //TODO add checks for file path existence, or do it in controller?
        //return globalImportedDataManager.importPDF(PDFFile);
    }

    public boolean generateReport(String PDFFile){
        //globalReportGenerator.setData();
        return globalReportGenerator.outputReport(PDFFile);
    }

    private boolean checkValidFilename(String filename, boolean isNew){
        File toBeChecked = new File(filename);
        return toBeChecked.canWrite();

    }

}
