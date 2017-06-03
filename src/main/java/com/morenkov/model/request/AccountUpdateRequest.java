package com.morenkov.model.request;

public class AccountUpdateRequest {
    private AccountOperation operation;
    private String to;
    private String amount;

    public AccountUpdateRequest() {
    }

    public AccountUpdateRequest(AccountOperation operation, String to, String amount) {
        this.operation = operation;
        this.to = to;
        this.amount = amount;
    }

    public AccountOperation getOperation() {
        return operation;
    }

    public void setOperation(AccountOperation operation) {
        this.operation = operation;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
