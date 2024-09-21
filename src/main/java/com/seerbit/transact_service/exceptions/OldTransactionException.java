package com.seerbit.transact_service.exceptions;

public class OldTransactionException extends RuntimeException {
    public OldTransactionException(String message) {
        super(message);
    }
}
