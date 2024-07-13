package excel_parser;

import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;


import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class Main {
    private final static String DEFAULT_PATH = "C:\\Users\\fidor\\IdeaProjects\\transactionCalculatorFromExcel\\Test.xlsx";
    private static Map<Integer, List<String>> readExcel(String fileLocation) throws IOException {
        Map<Integer, List<String>> data = new HashMap<>();

        try (FileInputStream file = new FileInputStream(fileLocation); ReadableWorkbook wb = new ReadableWorkbook(file)) {
            Sheet sheet = wb.getFirstSheet();
            try (Stream<Row> rows = sheet.openStream()) {
                rows.forEach(r -> {
                    data.put(r.getRowNum(), new ArrayList<>());

                    for (Cell cell : r) {
                        data.get(r.getRowNum()).add(cell.getRawValue());
                    }
                });
            }
        }

        return data;
    }

    private static List<TransactionList> split(List<Transaction> transactions, int delimiter) {
        List<TransactionList> result = new ArrayList<>();
        TransactionList currentTransactionMonth = new TransactionList();
        Transaction currentTransaction = transactions.getFirst();
        int currentYear = currentTransaction.getTransactionDate().getYear();
        int currentMonthIndex = currentTransaction.getTransactionDate().getMonthValue();
        for (Transaction transaction : transactions) {
            if(transaction.getTransactionDate().getMonthValue() != currentMonthIndex && transaction.getTransactionDate().getDayOfMonth() >= delimiter) {
                if (transaction.getTransactionDate().getYear()>currentYear) {
                    ++currentYear;
                    currentMonthIndex = 1;
                }
                else {
                    currentMonthIndex++;
                }
                result.add(currentTransactionMonth);
                currentTransactionMonth = new TransactionList();
            }
            currentTransactionMonth.add(transaction);
        }
        result.add(currentTransactionMonth);
        return result;
    }

    private static void showList(List<TransactionList> list) {
        JFrame frame = new JFrame();
        String[] columnNames = {"Month", "Positive", "Negative", "Amount"};
        String[][] data = new String[list.size()+1][columnNames.length];
        data[0] = columnNames;
        for (int i = 1; i < data.length; i++) {
            data[i][0] = list.get(i-1).getFirst().getTransactionDate().getMonth().toString();
            data[i][1] = String.format("%.2f", list.get(i-1).getPositiveValues());
            data[i][2] = String.format("%.2f", list.get(i-1).getNegativeValues());
            data[i][3] = String.format("%.2f", list.get(i-1).getValue());
        }
        JTable jTable = new JTable(data, columnNames);
        frame.add(jTable);
        frame.setVisible(true);
        frame.setTitle("Transaction List");
        frame.setSize(new Dimension(400, 300));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
    }

    private static class TransactionList extends ArrayList<Transaction> {
        public Double getValue(){
            double result = 0.0;
            for (Transaction transaction : this) {
                result+=transaction.getAmount();
            }
            return result;
        }

        public Double getPositiveValues(){
            double result = 0.0;
            for (Transaction transaction : this) {
                if(transaction.getAmount()>0){
                    result+=transaction.getAmount();
                }
            }
            return result;
        }

        public Double getNegativeValues(){
            double result = 0.0;
            for (Transaction transaction : this) {
                if(transaction.getAmount()<0){
                    result+=transaction.getAmount();
                }
            }
            return result;
        }
    }

    public static void main(String[] args) throws IOException {
        String fileLocation;
        if(args.length>1){
            fileLocation = args[1];
        }else {
            fileLocation = DEFAULT_PATH;
        }
        Map<Integer, List<String>> excel = readExcel(fileLocation);

        List<Transaction> transactions = new ArrayList<>();
        int readingStart = 9;
        for (int i = readingStart; i <= excel.size(); i++) {
            List<String> row = excel.get(i);
            transactions.add(new Transaction(Double.parseDouble(row.get(3)), row.get(2), LocalDate.of(1899, Month.DECEMBER, 30).plusDays(Integer.parseInt(row.get(0)))));
        }
        transactions = transactions.reversed();

        Scanner scanner = new Scanner(System.in);
        System.out.print("On which day of the month do you want to split?");
        int dayDelimiter = scanner.nextInt();
        scanner.close();

        List<TransactionList> splitedTransactionPerMonths = split(transactions, dayDelimiter);

        showList(splitedTransactionPerMonths);
    }
}
