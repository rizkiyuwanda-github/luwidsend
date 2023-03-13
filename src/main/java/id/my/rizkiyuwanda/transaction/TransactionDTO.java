package id.my.rizkiyuwanda.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    private String senderBankId;
    private String senderAccountId;
    private String receiverBankId;
    private String receiverAccountId;
    private BigDecimal amount;
    private String note;
}
