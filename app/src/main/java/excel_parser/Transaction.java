package excel_parser;

import java.time.LocalDate;
import java.util.Objects;

public class Transaction {
    private final String transactionType;
    private final double amount;
    private final LocalDate transactionDate;

    Transaction(double amount, String transactionType, LocalDate transactionDate) {
        this.amount = amount;
        this.transactionType = Objects.requireNonNull(transactionType);
        this.transactionDate = Objects.requireNonNull(transactionDate);
    }


    public double getAmount() {
        return amount;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    @Override
    public String toString() {
        return transactionType + "\t" + amount + "\t" + transactionDate.format(Utils.dateTimeFormatter);
    }

}
