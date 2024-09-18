package Tracker;

import java.math.BigInteger;

public class Deposit {
    private BigInteger blockNumber;
    private String blockTimestamp;
    private Double fee;
    private String transactionHash;
    private String pubkey;


    public Deposit(BigInteger blockNumber, String blockTimestamp, Double fee, String transactionHash, String pubkey) {
        this.blockNumber=blockNumber;
        this.blockTimestamp=blockTimestamp;
        this.fee =fee;
        this.transactionHash=transactionHash;
        this.pubkey=pubkey;
    }

    // Getters and setters
    public BigInteger getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(BigInteger blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getBlockTimestamp() {
        return blockTimestamp;
    }

    public void setBlockTimestamp(String blockTimestamp) {
        this.blockTimestamp = blockTimestamp;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public String getPubkey() {
        return pubkey;
    }

    public void setPubkey(String pubkey) {
        this.pubkey = pubkey;
    }


    @Override
    public String toString() {
        return "Deposit{" +
                "blockNumber=" + blockNumber +
                ", blockTimestamp=" + blockTimestamp +
                ", fee=" + fee +
                ", transactionHash='" + transactionHash + '\'' +
                ", pubkey='" + pubkey + '\'' +
                '}';
    }
}
