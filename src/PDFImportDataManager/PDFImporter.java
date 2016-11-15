package PDFImportDataManager;


import java.util.List;
import java.util.Map;

public interface PDFImporter {


    public boolean importPDF(String PDFFile);
    public String getDatesString();
    public List<List<List<String>>> getData();
}
