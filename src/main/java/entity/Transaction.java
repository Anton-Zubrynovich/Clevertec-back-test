package entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.SimpleDateFormat;

import lombok.*;

/**
 * The type Transaction.
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Transaction implements Serializable {
    private Long id;
    private String dateOfTransaction;
    private String timeOfTransaction;
    private String transactionType;
    private Double amount;
    private Long senderAccount;
    private Long receiverAccount;

    /**
     * Instantiates a new Transaction.
     *
     * @param dateOfTransaction the date of transaction
     * @param timeOfTransaction the time of transaction
     * @param transactionType   the transaction type
     * @param amount            the amount
     * @param senderAccount     the sender account
     * @param receiverAccount   the receiver account
     */
    public Transaction(String dateOfTransaction,
                       String timeOfTransaction,
                       String transactionType,
                       Double amount,
                       Long senderAccount,
                       Long receiverAccount) {
        this.dateOfTransaction = dateOfTransaction;
        this.timeOfTransaction = timeOfTransaction;
        this.transactionType = transactionType;
        this.amount = amount;
        this.senderAccount = senderAccount;
        this.receiverAccount = receiverAccount;
    }
}
