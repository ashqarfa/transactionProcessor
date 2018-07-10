package com.progressoft.induction.tp;

public class TransactionForm {

    private String type;
    private String amount;
    private String narration;
    private int index;


    public TransactionForm(String type, String amount, String narration, int index) {
        this.type = type;
        this.amount = amount;
        this.narration = narration;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }
}
