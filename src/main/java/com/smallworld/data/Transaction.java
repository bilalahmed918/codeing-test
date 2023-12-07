package com.smallworld.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transaction {
    // Represent your transaction data here.
    Integer mtn;
    Float amount;
    String senderFullName;
    Integer senderAge;
    String beneficiaryFullName;
    Integer beneficiaryAge;
    Integer issueId;
    Boolean issueSolved;
    String issueMessage;
}
