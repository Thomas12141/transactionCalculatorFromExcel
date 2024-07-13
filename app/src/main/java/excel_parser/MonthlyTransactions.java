package excel_parser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class MonthlyTransactions {
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final ArrayList<Transaction> transactions;


    public MonthlyTransactions(LocalDate startDate, LocalDate endDate) {
        this.startDate = Objects.requireNonNull(startDate);
        this.endDate = Objects.requireNonNull(endDate);
        transactions = new ArrayList<>();
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(Objects.requireNonNull(transaction));
    }
}
