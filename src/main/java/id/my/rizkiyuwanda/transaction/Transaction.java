package id.my.rizkiyuwanda.transaction;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@Data
public class Transaction {
    @Id
    @Column(length = 50)
    private String id;

    @Column(name = "sender_bank_id", length = 5)
    private String senderBankId;

    @Column(name = "sender_bank_name", length = 100)
    private String senderBankName;

    @Column(name = "sender_account_id", length = 50)
    private String senderAccountId;

    @Column(name = "sender_account_name", length = 100)
    private String senderAccountName;

    @Column(name = "receiver_bank_id", length = 5)
    private String receiverBankId;

    @Column(name = "receiver_bank_name", length = 100)
    private String receiverBankName;

    @Column(name = "receiver_account_id", length = 50)
    private String receiverAccountId;

    @Column(name = "receiver_account_name", length = 100)
    private String receiverAccountName;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime time;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private BigDecimal fee;

    private String note;
    private String status;

    @Column(name = "reference_id", length = 50, nullable = true)
    private String referenceId;

}
