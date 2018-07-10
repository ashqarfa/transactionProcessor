package com.progressoft.induction.tp;

import java.io.InputStream;
import java.util.List;

public interface TransactionProcessor {
    void importTransactions(InputStream is);

    List<Transaction> getImportedTransactions();

    List<Violation> validate();

    boolean isBalanced();
}
