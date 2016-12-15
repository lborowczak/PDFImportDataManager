package PDFImportDataManager.interfaces;


import java.io.File;
import java.util.List;
import java.util.Map;

public interface PDFImporter {


    public boolean importPDF(File PDFFile);
    public String getDatesString();
    public List<List<List<String>>> getData();
    public List<List<List<String>>> getUsersData();
}
