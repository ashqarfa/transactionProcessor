package com.progressoft.induction.tp;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CSVProcessor extends TransactionProcessorImpl {
    @Override
    public void importTransactions(InputStream is) {
        try (Reader reader = new InputStreamReader(is);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT)) {
            List<CSVRecord> records = new ArrayList<>();
            for (CSVRecord csvRecord : csvParser)
                records.add(csvRecord);

            records.stream().forEach(tx -> transactionList.add(toTransaction(tx)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Transaction toTransaction(CSVRecord csvRecord) {
        String type = csvRecord.get(0);
        String amount = csvRecord.get(1);
        String narration = csvRecord.get(2);
        BigDecimal bdAmount = tryReadAmount(amount);
        return new Transaction(type, bdAmount, narration);
    }
}

