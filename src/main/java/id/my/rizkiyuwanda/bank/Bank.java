package id.my.rizkiyuwanda.bank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table (name = "bank")
public class Bank {
    @Id
    @Column(length = 5)
    private String id;

    @Column(length = 100, nullable = false, unique = true)
    private String name;
}
