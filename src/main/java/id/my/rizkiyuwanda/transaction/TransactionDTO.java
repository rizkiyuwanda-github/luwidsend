package id.my.rizkiyuwanda.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    private String id;
    private String senderAccountId;
    private String senderBankId;
    private String receiverAccountId;
    private String receiverBankId;
    private BigDecimal amount;
    private String note;
}
