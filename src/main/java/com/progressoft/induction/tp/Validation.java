package com.progressoft.induction.tp;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public
class Validation {

    private TransactionForm form;


    public Validation(TransactionForm form) {
        this.form = form;
    }

    public List<Violation> isValid() {
        String amt = form.getAmount().trim();
        String type = form.getType().trim();
        String narration = form.getNarration();
        List<Violation> errorsArray = new ArrayList<>();

        try {
            BigDecimal amount = new BigDecimal(amt);

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                Violation error = new Violation(form.getIndex(), "amount", "amount must be greater than zero");
                errorsArray.add(error);
            }
        } catch (NumberFormatException e) {
            Violation error = new Violation(form.getIndex(), "amount", "amount is not a number");
            errorsArray.add(error);
        }

        if (!type.equals("D") && !type.equals("C")) {
            Violation error = new Violation(form.getIndex(), "type", "type is wrong value");
            errorsArray.add(error);
        }

        if (narration == null || narration.trim().length() == 0) {
            Violation error = new Violation(form.getIndex(), "narration", "narration is empty");
            errorsArray.add(error);
        }
        return errorsArray;
    }


}
