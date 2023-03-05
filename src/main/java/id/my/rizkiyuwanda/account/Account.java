package id.my.rizkiyuwanda.account;

import id.my.rizkiyuwanda.bank.Bank;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "account")
@Data
public class Account {
    @Id
    @Column(length = 50)
    private String id;

    @JoinColumn(name = "bank_id", nullable = false)
    @ManyToOne
    private Bank bank;

    @Column(length = 100, nullable = false)
    private String name;


}
