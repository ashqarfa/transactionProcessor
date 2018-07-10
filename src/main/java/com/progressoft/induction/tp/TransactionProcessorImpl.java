package com.progressoft.induction.tp;

import org.beanio.BeanReader;
import org.beanio.StreamFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.math.BigDecimal.ZERO;

public abstract class TransactionProcessorImpl implements TransactionProcessor {

    protected ArrayList<Transaction> transactionList = new ArrayList<>();

    @Override
    public List<Transaction> getImportedTransactions() {
        return transactionList;
    }

    @Override
    public List<Violation> validate() {


        ArrayList<Violation> allViolations = new ArrayList<>();

        for(int i = 0; i < transactionList.size(); i++){

            Transaction transaction = transactionList.get(i);

            String type = transaction.getType();
            String amount = transaction.getAmount().toString();
            String narration = transaction.getNarration();
            int index = i+1;

            TransactionForm transactionForm = new TransactionForm(type, amount, narration, index);

            Validation myValidation = new Validation(transactionForm);

            List<Violation> violations = myValidation.isValid();

            allViolations.addAll(violations);
        }

        return allViolations;
    }

    @Override
    public boolean isBalanced() {

        boolean balanced = false;
        BigDecimal sumDebit = new BigDecimal(0);
        BigDecimal sumCredit = new BigDecimal(0);

        for(Transaction transaction : transactionList){

            if(transaction.getType().equals("C")){
                sumCredit = sumCredit.add(transaction.getAmount());
            }

            if(transaction.getType().equals("D")){
                sumDebit = sumDebit.add(transaction.getAmount());
            }
        }

        balanced = (sumCredit.compareTo(sumDebit) == 0);

        return balanced;
    }

    protected BigDecimal tryReadAmount(String amount) {
        BigDecimal bdAmount = ZERO;
        try{
            bdAmount = new BigDecimal(amount);
        }
        catch (NumberFormatException e){
        }
        return bdAmount;
    }
}
