package PDFImportDataManager;

import org.apache.pdfbox.pdmodel.PDDocument;
import technology.tabula.*;
import technology.tabula.extractors.ExtractionAlgorithm;
import technology.tabula.extractors.BasicExtractionAlgorithm;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TabulaPDFImporter implements PDFImporter {


    private ExtractionAlgorithm basicExtractor = new BasicExtractionAlgorithm();
    private ObjectExtractor oe;
    private List<List<List<String>>> extractedData = new ArrayList<List<List<String>>>();
    private String datesString = "";



    @Override
    public boolean importPDF(File PDFFile) {

        //Rectangle PortraitDatesPageArea = new Rectangle(55.0f,150.0f,1000.0f,40.0f);
        //Rectangle LandscapeDatesPageArea = new Rectangle(40.0f,150.0f,1000.0f,40.0f);
        //Rectangle PortraitDataPageArea = new Rectangle();
        //Rectangle LandscapeDataPageArea = new Rectangle();
        //Rectangle PortraitItemsPageArea = new Rectangle();
        //Rectangle LandscapeItemsPageArea = new Rectangle(110.0f,0.0f,1000.0f, 1000.0f);

        //Set areas of the page where the date is.
        Rectangle datesPageArea = new Rectangle(60.0f,150.0f,1000.0f,40.0f);

        //Set area of the page where the data will be
        Rectangle itemsDataPageArea = new Rectangle(110.0f,0.0f,1000.0f, 1000.0f);

        //Open the file for import and check if it exists
        try {
            oe = new ObjectExtractor(PDDocument.load(PDFFile));
        } catch (IOException ie) {return false; }

        PageIterator pageIterator = oe.extract();

        while (pageIterator.hasNext()) {
            Page currPage = pageIterator.next();

            //Extract the date from the PDF
            if (datesString.equals("")) {
                Page tmpDatesArea = currPage.getArea(datesPageArea);
                List<List<String>> datesData = getAreaData(tmpDatesArea);

                //Find the entry in the returned data with the string containing the date, only do this once
                for (List<String> currRow: datesData)
                {
                    for (String currEntry: currRow) {
                        if (currEntry.contains(", 20")) {
                            datesString = currEntry;
                        }
                    }
                }

            }

            //Extract all other page data
            Page tmpItemsDataArea = currPage.getArea(itemsDataPageArea);
            extractedData.add(getAreaData(tmpItemsDataArea));

        }
        try {
            oe.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public String getDatesString() {
        return datesString;
    }

    @Override
    public List<List<List<String>>> getData(){
        //Outer list = list of results of getAreaDate, page by page
        return extractedData;
    }


    private List<List<String>> getAreaData(Page tmpPage) {

        //Breakdown of nested list:
        //Outer list = list of rows found
        //Inner list = list of item objects in the row

        List<List<String>> areaData = new ArrayList<List<String>>();
        List<Table> tmpTables = new ArrayList<Table>();
        tmpTables.addAll(basicExtractor.extract(tmpPage));
        for (Table table : tmpTables) {
            for (List<RectangularTextContainer> row : table.getRows()) {
                List<String> rowData = new ArrayList<String>(row.size());
                for (RectangularTextContainer tc : row) {
                    rowData.add(tc.getText());
                }
                areaData.add(rowData);
            }
        }
        return areaData;

    }

}
