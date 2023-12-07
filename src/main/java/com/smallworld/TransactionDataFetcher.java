package com.smallworld;

import com.smallworld.data.Transaction;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TransactionDataFetcher {

    ObjectMapper objectMapper = new ObjectMapper();
    List<Transaction> transactionList = new ArrayList<>();

    {
        try {
            Transaction[] transactions = objectMapper.readValue(new File("../transactions.json"), Transaction[].class);
            transactionList = Arrays.asList(transactions);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the sum of the amounts of all transactions
     */
    public double getTotalTransactionAmount() {
        try {
            return transactionList.stream()
                    .mapToDouble(x -> x.getAmount())
                    .sum();

        } catch (Exception e) {
            e.printStackTrace();
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Returns the sum of the amounts of all transactions sent by the specified client
     */
    public double getTotalTransactionAmountSentBy(String senderFullName) {
        try {
            return transactionList.stream()
                    .filter(transaction -> senderFullName.equals(transaction.getSenderFullName()))
                    .mapToDouble(Transaction::getAmount)
                    .sum();
        } catch (Exception e) {
            e.printStackTrace();
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Returns the highest transaction amount
     */
    public double getMaxTransactionAmount() {
        try {
            return transactionList.stream()
                    .mapToDouble(x -> x.getAmount())
                    .max()
                    .orElse(0.0);
        } catch (Exception e) {
            e.printStackTrace();
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Counts the number of unique clients that sent or received a transaction
     */
    public long countUniqueClients() {

        try {
            return transactionList.stream()
                    .flatMap(transaction -> List.of(transaction.getSenderFullName(), transaction.getBeneficiaryFullName()).stream())
                    .distinct()
                    .count();
        } catch (Exception e) {
            e.printStackTrace();
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Returns whether a client (sender or beneficiary) has at least one transaction with a compliance
     * issue that has not been solved
     */
    public boolean hasOpenComplianceIssues(String clientFullName) {
        try {
            return transactionList.stream()
                    .filter(transaction -> clientFullName.equals(transaction.getSenderFullName()) || clientFullName.equals(transaction.getBeneficiaryFullName()))
                    .anyMatch(transaction -> transaction.getIssueSolved() != null && !transaction.getIssueSolved());
        } catch (Exception e) {
            e.printStackTrace();
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Returns all transactions indexed by beneficiary name
     */
    public Map<String, List<Transaction>> getTransactionsByBeneficiaryName() {
        return transactionList.stream()
                .collect(Collectors.groupingBy(Transaction::getBeneficiaryFullName));
    }

    /**
     * Returns the identifiers of all open compliance issues
     */
    public Set<Integer> getUnsolvedIssueIds() {
        return transactionList.stream()
                .filter(transaction -> transaction.getIssueSolved() != null && !transaction.getIssueSolved())
                .map(Transaction::getIssueId)
                .collect(Collectors.toSet());
    }

    /**
     * Returns a list of all solved issue messages
     */
    public List<String> getAllSolvedIssueMessages() {
        return transactionList.stream()
                .filter(transaction -> transaction.getIssueSolved() != null && transaction.getIssueSolved())
                .map(Transaction::getIssueMessage)
                .collect(Collectors.toList());
    }

    /**
     * Returns the 3 transactions with the highest amount sorted by amount descending
     */
    public List<Transaction> getTop3TransactionsByAmount() {
        return transactionList.stream()
                .sorted(Comparator.comparing(Transaction::getAmount).reversed())
                .limit(3)
                .collect(Collectors.toList());
    }

    /**
     * Returns the senderFullName of the sender with the most total sent amount
     */
    public Optional<String> getTopSender() {
        return transactionList.stream()
                .collect(Collectors.groupingBy(Transaction::getSenderFullName, Collectors.summingDouble(Transaction::getAmount)))
                .entrySet().stream()
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .map(Map.Entry::getKey);
    }
}
