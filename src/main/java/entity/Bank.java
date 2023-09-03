package entity;

import lombok.*;

import java.io.Serializable;

/**
 * The type Bank.
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Bank implements Serializable {
    private Long id;
    private String bankName;

    /**
     * Instantiates a new Bank.
     *
     * @param bankName the bank name
     */
    public Bank(String bankName){
        this.bankName = bankName;
    }
}

