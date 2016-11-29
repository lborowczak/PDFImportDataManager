package PDFImportDataManager;

import java.time.LocalDate;

public class TripleDate {
    private LocalDate BeginDate;
    private LocalDate EndDate;
    private LocalDate PayDate;

    public TripleDate(LocalDate DateOne, LocalDate DateTwo, LocalDate DateThree) {
        BeginDate = DateOne;
        EndDate = DateTwo;
        PayDate = DateThree;
    }


    public LocalDate getBeginDate() {
        return BeginDate;
    }

    public LocalDate getEndDate() {
        return EndDate;
    }

    public LocalDate getPayDate() {
        return PayDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!TripleDate.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final TripleDate objToTest = (TripleDate) obj;
        if (this.getBeginDate().equals(objToTest.getBeginDate()) &&
            this.getEndDate().equals(objToTest.getEndDate()) &&
            this.getPayDate().equals(objToTest.getPayDate()))
        {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString(){
        return (this.getBeginDate() + ", " + this.getEndDate() + ", " + this.getPayDate());
    }
}
