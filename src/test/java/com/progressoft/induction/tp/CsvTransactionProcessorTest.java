package com.progressoft.induction.tp;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertEquals;

public class CsvTransactionProcessorTest {

    private TransactionProcessor csvTransactionProcessor;

    @Before
    public void setUp() {
        csvTransactionProcessor = new CSVProcessor();// replace the null with your CSV implementation class
    }

    @Test
    public void givenValidCsvStream_WhenImport_ThenReturnTheExpectedTransactions() {
        InputStream is = asStream("C,1000,salary\nD,200,rent\nD,800,other");
        csvTransactionProcessor.importTransactions(is);
        List<Transaction> transactions = csvTransactionProcessor.getImportedTransactions();

        assertThat(transactions, containsInAnyOrder(
                newTransaction("D", new BigDecimal(200), "rent"),
                newTransaction("C", new BigDecimal(1000), "salary"),
                newTransaction("D", new BigDecimal(800), "other")
        ));
    }

    @Test
    public void givenBalancedCsvStream_WhenImportAndCheckIfBalanced_ThenReturnTrue() throws Exception {
        InputStream is = asStream("C,1000.50,salary\nD,200,rent\nD,800.50,other");
        csvTransactionProcessor.importTransactions(is);

        assertEquals(true, csvTransactionProcessor.isBalanced());
    }

    @Test
    public void givenImbalancedCsvStream_WhenImportAndCheckIfBalanced_ThenReturnFalse() throws Exception {
        InputStream is = asStream("C,1000,salary\nD,400,rent\nD,750,other");
        csvTransactionProcessor.importTransactions(is);

        assertEquals(false, csvTransactionProcessor.isBalanced());
    }

    @Test
    public void givenCsvStreamWithAnInvalidTransaction_WhenCallingValidate_ThenReportTheProperViolations() throws Exception {
        InputStream is = asStream("C,1000,salary\nX,400,rent\nD,750,other");
        csvTransactionProcessor.importTransactions(is);
        List<Violation> violations = csvTransactionProcessor.validate();

        assertThat(violations, containsInAnyOrder(new Violation(2,"type")));
    }

    @Test
    public void givenCsvStreamWithMultipleInvalidTransactions_WhenCallingValidate_ThenReportTheProperViolations() throws Exception {
        InputStream is = asStream("C,one thousand,salary\nX,400,rent\nD,750,other");
        csvTransactionProcessor.importTransactions(is);
        List<Violation> violations = csvTransactionProcessor.validate();

        assertThat(violations, containsInAnyOrder(new Violation(2,"type"),new Violation(1,"amount")));
    }

    @Test
    public void givenCsvStreamWithMultipleErrorsInSameTransaction_WhenCallingValidate_ThenReportTheProperViolations() throws Exception {
        InputStream is = asStream("C,one thousand,salary\nX,0,rent\nD,750,other");
        csvTransactionProcessor.importTransactions(is);
        List<Violation> violations = csvTransactionProcessor.validate();

        assertThat(violations, containsInAnyOrder(new Violation(2,"type"),new Violation(2,"amount"),new Violation(1,"amount")));
    }

    private Transaction newTransaction(String type, BigDecimal amount, String narration) {
        return new Transaction(type, amount, narration);
    }

    private InputStream asStream(String s) {
        return new ByteArrayInputStream(s.getBytes());
    }

    public static class CsvTransactionProcessorTest1000 {

        private TransactionProcessor csvTransactionProcessor;
        long lStartTime;
        long lEndTime;
        long totalTime;
        static long finalTime = 0;

        @Before
        public void setUp() {
            csvTransactionProcessor = new CSVProcessor();// replace the null with your CSV implementation class
        }

        @AfterClass
        public static void printOut() {
            System.out.println("Total time taken for processor methods: " + finalTime);
        }

        @Test
        public void givenValidCsvStream_WhenImport_ThenReturnTheExpectedTransactions() throws Exception {
            String isS = "C,1000,salary\nD,200,rent\nD,800,other\nD,801,salary\nD,802,other";
            for (int i = 0; i < 1999; i++) {
                isS = isS + "\nC,1000,salary\nD,200,rent\nD,800,other\nD,801,salary\nD,802,other";
            }
            InputStream is = asStream(isS);

            lStartTime = System.currentTimeMillis();
            csvTransactionProcessor.importTransactions(is);
            List<Transaction> transactions = csvTransactionProcessor.getImportedTransactions();
            lEndTime = System.currentTimeMillis();
            totalTime = lEndTime - lStartTime;
            System.out.println("Test 1: "+totalTime);
            finalTime = finalTime + totalTime;
            List<Transaction> listForCompare = new ArrayList<>();
            for (int i = 0; i < 2000; i++) {
                listForCompare.add(newTransaction("D", new BigDecimal(200), "rent"));
                listForCompare.add(newTransaction("C", new BigDecimal(1000), "salary"));
                listForCompare.add(newTransaction("D", new BigDecimal(800), "other"));
                listForCompare.add(newTransaction("D", new BigDecimal(801), "salary"));
                listForCompare.add(newTransaction("D", new BigDecimal(802), "other"));
            }
            assertThat(transactions, containsInAnyOrder(listForCompare.toArray()));
        }

        @Test
        public void givenBalancedCsvStream_WhenImportAndCheckIfBalanced_ThenReturnTrue() throws Exception {
            String isS = "C,1000.50,salary\nD,200,rent\nD,800.50,other\nC,800.50,other\nD,800.50,rent";
            for (int i = 0; i < 1999; i++) {
                isS = isS + "\nC,1000.50,salary\nD,200,rent\nD,800.50,other\nC,800.50,other\nD,800.50,rent";
            }
            InputStream is = asStream(isS);
            lStartTime = System.currentTimeMillis();
            csvTransactionProcessor.importTransactions(is);
            assertEquals(true, csvTransactionProcessor.isBalanced());
            lEndTime = System.currentTimeMillis();
            totalTime = lEndTime - lStartTime;
            System.out.println("Test 2: "+totalTime);
            finalTime = finalTime + totalTime;
        }

        @Test
        public void givenImbalancedCsvStream_WhenImportAndCheckIfBalanced_ThenReturnFalse() throws Exception {
            String isS = "C,1000,salary\nD,400,rent\nD,750,other\nD,750,other\nD,750,other";
            for (int i = 0; i < 1999; i++) {
                isS = isS + "\nC,1000,salary\nD,400,rent\nD,750,other\nD,750,other\nD,750,other";
            }
            InputStream is = asStream(isS);
            lStartTime = System.currentTimeMillis();
            csvTransactionProcessor.importTransactions(is);

            assertEquals(false, csvTransactionProcessor.isBalanced());
            lEndTime = System.currentTimeMillis();
            totalTime = lEndTime - lStartTime;
            System.out.println("Test 3: "+totalTime);
            finalTime = finalTime + totalTime;
        }

        @Test
        public void givenCsvStreamWithAnInvalidTransaction_WhenCallingValidate_ThenReportTheProperViolations() throws Exception {
            String isS = "C,1000,salary\nX,400,rent\nD,750,other\nC,400,rent\nD,750,other";
            for (int i = 0; i < 1999; i++) {
                isS = isS + "\nC,1000,salary\nC,400,rent\nD,750,other\nC,400,rent\nD,750,other";
            }
            InputStream is = asStream(isS);
            lStartTime = System.currentTimeMillis();
            csvTransactionProcessor.importTransactions(is);
            List<Violation> violations = csvTransactionProcessor.validate();
            lEndTime = System.currentTimeMillis();
            totalTime = lEndTime - lStartTime;
            System.out.println("Test 4: "+totalTime);
            finalTime = finalTime + totalTime;
            assertThat(violations, containsInAnyOrder(new Violation(2, "type")));
        }

        @Test
        public void givenCsvStreamWithMultipleInvalidTransactions_WhenCallingValidate_ThenReportTheProperViolations() throws Exception {
            String isS = "C,one thousand,salary\nX,400,rent\nD,750,other\nC,400,rent\nD,750,other";
            for (int i = 0; i < 1999; i++) {
                isS = isS + "\nC,one thousand,salary\nX,400,rent\nD,750,other\nC,400,rent\nD,750,other";
            }
            InputStream is = asStream(isS);
            lStartTime = System.currentTimeMillis();
            csvTransactionProcessor.importTransactions(is);
            List<Violation> violations = csvTransactionProcessor.validate();

            List<Violation> listToCompare = new ArrayList<>();
            lEndTime = System.currentTimeMillis();
            totalTime = lEndTime - lStartTime;
            System.out.println("Test 5: "+totalTime);
            finalTime = finalTime + totalTime;
            for (int i = -1; i<1999; i++){
                listToCompare.add(new Violation((i+1)*6-(i),"amount"));
                listToCompare.add(new Violation((i+1)*6-(i)+1,"type"));
            }

            assertThat(violations, containsInAnyOrder(listToCompare.toArray()));
        }

        @Test
        public void givenCsvStreamWithMultipleErrorsInSameTransaction_WhenCallingValidate_ThenReportTheProperViolations() throws Exception {
            String isS = "C,one thousand,salary\nX,0,rent\nD,750,other\nD,400,rent\nD,750,other";
            for (int i = 0; i < 1999; i++) {
                isS = isS + "\nC,one thousand,salary\nX,0,rent\nD,750,other\nD,400,rent\nD,750,other";
            }
            InputStream is = asStream(isS);
            lStartTime = System.currentTimeMillis();
            csvTransactionProcessor.importTransactions(is);
            List<Violation> violations = csvTransactionProcessor.validate();
            lEndTime = System.currentTimeMillis();
            totalTime = lEndTime - lStartTime;
            System.out.println("Test 6: "+totalTime);
            finalTime = finalTime + totalTime;
            List<Violation> listToCompare = new ArrayList<>();

            for (int i = -1; i<1999; i++){
                listToCompare.add(new Violation((i+1)*6-(i),"amount"));
                listToCompare.add(new Violation((i+1)*6-(i)+1,"type"));
                listToCompare.add(new Violation((i+1)*6-(i)+1,"amount"));
            }

            assertThat(violations, containsInAnyOrder(listToCompare.toArray()));

        }

        private Transaction newTransaction(String type, BigDecimal amount, String narration) {
            return new Transaction(type, amount, narration);
        }

        private InputStream asStream(String s) {
            return new ByteArrayInputStream(s.getBytes());
        }
    }
}
