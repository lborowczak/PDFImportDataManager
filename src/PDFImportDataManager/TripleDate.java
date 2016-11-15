package PDFImportDataManager;

import java.time.LocalDate;

public class TripleDate<LocalDate> {
    private LocalDate BeginDate;
    private LocalDate EndDate;
    private LocalDate PayDate;

    public TripleDate(LocalDate DateOne, LocalDate DateTwo, LocalDate DateThree) {
        BeginDate = DateOne;
        EndDate = DateTwo;
        PayDate = DateThree;
    }


    public LocalDate getBeginDate(){
        return BeginDate;
    }

    public LocalDate getEndDate(){
        return EndDate;
    }

    public LocalDate getPayDate(){
        return PayDate;
    }

}
