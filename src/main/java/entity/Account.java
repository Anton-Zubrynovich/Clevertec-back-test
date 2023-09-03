package entity;


import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * The type Account.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Account implements Serializable {
    private Long accountNumber;
    private Double balance;
    private Long customerId;
    private Long bankId;

}
