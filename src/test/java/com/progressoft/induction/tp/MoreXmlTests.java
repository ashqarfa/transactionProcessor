package com.progressoft.induction.tp;

import org.hamcrest.collection.IsIterableContainingInAnyOrder;
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

public class MoreXmlTests {

    private TransactionProcessor xmlTransactionProcessor;
    private static long totalTime = 0;

    @Before
    public void setUp() {
        xmlTransactionProcessor = new XMLProcessor();
    }

    @AfterClass
    public static void printResults(){
        System.out.println("Total time taken for processor methods : " + totalTime + " Milliseconds");
    }

    @Test
    public void givenValidXmlStream_WhenImport_ThenReturnTheExpectedTransactions() {

        String transactionString = "<TransactionList>\n";
        for (int i = 0; i < 2000; i++)
            transactionString = transactionString + "<Transaction type=\"C\" amount=\"1000\" narration=\"salary\" />\n" +
                    "    <Transaction type=\"D\" amount=\"200\" narration=\"rent\" />\n" +
                    "    <Transaction type=\"D\" amount=\"800\" narration=\"other\" />\n" +
                    "    <Transaction type=\"C\" amount=\"500\" narration=\"other\" />\n" +
                    "    <Transaction type=\"C\" amount=\"600\" narration=\"tax\" />\n";

        transactionString = transactionString + "</TransactionList>";

        InputStream is = asStream(transactionString);


        long start = System.currentTimeMillis();

        xmlTransactionProcessor.importTransactions(is);
        List<Transaction> actualTransactions = xmlTransactionProcessor.getImportedTransactions();
        List<Transaction> expectedTransactions = new ArrayList<>();

        long end = System.currentTimeMillis();

        System.out.println("test 1 : " + (end - start));

        totalTime = totalTime + (end - start);

        for (int i = 0; i < 2000; i++) {
            expectedTransactions.add(new Transaction("D", new BigDecimal(200), "rent"));
            expectedTransactions.add(new Transaction("C", new BigDecimal(1000), "salary"));
            expectedTransactions.add(new Transaction("D", new BigDecimal(800), "other"));
            expectedTransactions.add(new Transaction("C", new BigDecimal(500), "other"));
            expectedTransactions.add(new Transaction("C", new BigDecimal(600), "tax"));
        }
        assertThat(actualTransactions, containsInAnyOrder(expectedTransactions.toArray()));
    }

    @Test
    public void givenBalancedXmlStream_WhenImportAndCheckIfBalanced_ThenReturnTrue() throws Exception {

        String transactionString = "<TransactionList>\n";
        for (int i = 0; i < 2000; i++)
            transactionString = transactionString + "<Transaction type=\"C\" amount=\"1000.50\" narration=\"salary\" />\n" +
                    "    <Transaction type=\"D\" amount=\"200\" narration=\"rent\" />\n" +
                    "    <Transaction type=\"D\" amount=\"800.50\" narration=\"other\" />\n" +
                    "    <Transaction type=\"D\" amount=\"600.30\" narration=\"other\" />\n" +
                    "    <Transaction type=\"C\" amount=\"600.30\" narration=\"other\" />\n";

        transactionString = transactionString + "</TransactionList>";

        InputStream is = asStream(transactionString);

        long start = System.currentTimeMillis();

        xmlTransactionProcessor.importTransactions(is);

        assertEquals(true, xmlTransactionProcessor.isBalanced());

        long end = System.currentTimeMillis();

        System.out.println("test 2 : " + (end - start));

        totalTime = totalTime + (end - start);

    }

    @Test
    public void givenImbalancedXmlStream_WhenImportAndCheckIfBalanced_ThenReturnFalse() throws Exception {

        String transactionString = "<TransactionList>\n";

        for (int i = 0; i < 2000; i++)
            transactionString = transactionString + "<Transaction type=\"C\" amount=\"1000\" narration=\"salary\" />\n" +
                    "    <Transaction type=\"D\" amount=\"400\" narration=\"rent\" />\n" +
                    "    <Transaction type=\"D\" amount=\"750\" narration=\"other\" />\n" +
                    "    <Transaction type=\"D\" amount=\"600\" narration=\"other\" />\n" +
                    "    <Transaction type=\"D\" amount=\"1340\" narration=\"other\" />\n";

        transactionString = transactionString + "</TransactionList>";


        InputStream is = asStream(transactionString);

        long start = System.currentTimeMillis();
        xmlTransactionProcessor.importTransactions(is);

        assertEquals(false, xmlTransactionProcessor.isBalanced());

        long end = System.currentTimeMillis();

        totalTime = totalTime + (end - start);

        System.out.println("test 3 : " + (end - start));
    }

    @Test
    public void givenXmlStreamWithAnInvalidTransaction_WhenCallingValidate_ThenReportTheProperViolations() throws Exception {


        String transactionString = "<TransactionList>\n";

        for(int i = 0; i < 1000; i++)
            transactionString = transactionString + "<Transaction type=\"C\" amount=\"1000\" narration=\"salary\" />\n" +
                    "    <Transaction type=\"C\" amount=\"1000\" narration=\"salary\" />\n" +
                    "    <Transaction type=\"D\" amount=\"750\" narration=\"other\" />\n" +
                    "    <Transaction type=\"D\" amount=\"400\" narration=\"rent\" />\n" +
                    "    <Transaction type=\"C\" amount=\"760\" narration=\"tax\" />\n";

        transactionString = transactionString + " <Transaction type=\"L\" amount=\"400\" narration=\"rent\" />\n";

        for(int i = 0; i < 1000; i++)
            transactionString = transactionString +  " <Transaction type=\"C\" amount=\"1000\" narration=\"salary\" />\n" +
                    "    <Transaction type=\"D\" amount=\"750\" narration=\"other\" />\n" +
                    "    <Transaction type=\"D\" amount=\"400\" narration=\"rent\" />\n" +
                    "    <Transaction type=\"C\" amount=\"760\" narration=\"tax\" />\n" +
                    "    <Transaction type=\"D\" amount=\"3000\" narration=\"tuition\" />\n";

        transactionString = transactionString + "</TransactionList>";

        InputStream is = asStream(transactionString);

        long start = System.currentTimeMillis();
        xmlTransactionProcessor.importTransactions(is);
        List<Violation> violations = xmlTransactionProcessor.validate();
        long end = System.currentTimeMillis();

        System.out.println("test 4 : " + (end - start));
        totalTime = totalTime + (end - start);
        assertThat(violations, containsInAnyOrder(new Violation(5001, "type")));
    }

    @Test
    public void givenXmlStreamWithMultipleInvalidTransactions_WhenCallingValidate_ThenReportTheProperViolations() throws Exception {

        String transactionString = "<TransactionList>\n";

        for(int i = 0; i < 2000; i++)
            transactionString = transactionString + " <Transaction type=\"C\" amount=\"one thousand\" narration=\"salary\" />\n" +
                    "    <Transaction type=\"X\" amount=\"400\" narration=\"rent\" />\n" +
                    "    <Transaction type=\"D\" amount=\"750\" narration=\"other\" />\n" +
                    "    <Transaction type=\"C\" amount=\"550\" narration=\"tax\" />\n" +
                    "    <Transaction type=\"D\" amount=\"900\" narration=\"other\" />\n";

        transactionString = transactionString + "</TransactionList>";

        InputStream is = asStream(transactionString);

        long start = System.currentTimeMillis();
        xmlTransactionProcessor.importTransactions(is);
        List<Violation> violations = xmlTransactionProcessor.validate();

        long end = System.currentTimeMillis();

        System.out.println("test 5 : " + (end - start));
        totalTime = totalTime + (end - start);

        List<Violation> expectedViolations = new ArrayList<>();

        for(int i = 0; i < 2000; i++){
            expectedViolations.add(new Violation( 1+5*i, "amount"));
            expectedViolations.add(new Violation(2+5*i, "type"));
        }
        assertThat(violations, containsInAnyOrder(expectedViolations.toArray()));
    }

    @Test
    public void givenXmlStreamWithMultipleErrorsInSameTransaction_WhenCallingValidate_ThenReportTheProperViolations() throws Exception {

        String transactionString = "<TransactionList>\n";

        for(int i = 0; i < 2000; i++)
            transactionString = transactionString + "<Transaction type=\"C\" amount=\"one thousand\" narration=\"salary\" />\n" +
                "    <Transaction type=\"X\" amount=\"0\" narration=\"rent\" />\n" +
                "    <Transaction type=\"D\" amount=\"750\" narration=\"other\" />\n" +
                " <Transaction type=\"C\" amount=\"600\" narration=\"other\" />\n" +
                "<Transaction type=\"D\" amount=\"300\" narration=\"tax\" />\n";

        transactionString = transactionString + "</TransactionList>";

        InputStream is = asStream(transactionString);

        long start = System.currentTimeMillis();
        xmlTransactionProcessor.importTransactions(is);
        List<Violation> violations = xmlTransactionProcessor.validate();
        long end = System.currentTimeMillis();

        System.out.println("test 6 : " + (end - start));
        totalTime = totalTime + (end - start);

        List<Violation> expectedViolations = new ArrayList<>();

        for(int i = 0; i < 2000; i++){
            expectedViolations.add(new Violation(1+5*i, "amount"));
            expectedViolations.add(new Violation(2+5*i, "amount"));
            expectedViolations.add(new Violation(2+5*i, "type"));

        }
        assertThat(violations, containsInAnyOrder(expectedViolations.toArray()));
    }

    private Transaction newTransaction(String type, BigDecimal amount, String narration) {
        return new Transaction(type, amount, narration);
    }

    private InputStream asStream(String s) {
        return new ByteArrayInputStream(s.getBytes());
    }
}
