package id.my.rizkiyuwanda.account;

import id.my.rizkiyuwanda.bank.Bank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AccountRepositoryJDBCTemplate {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Account> getList() {
        List<Account> list = null;
        try {
            String sqlSelect = "SELECT account.*, bank.name as bank_name " +
                    "FROM " +
                    "account join bank on account.bank_id = bank.id " +
                    "order by account.name asc";

            //Object[]parameters = {"a", "b"};
            //list = jdbcTemplate.query(sqlSelect, new ProdukRepository.ProdukRowMapper(), parameters);

            list = jdbcTemplate.query(sqlSelect, new AccountRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return list;
    }

    private class AccountRowMapper implements RowMapper<Account> {
        @Override
        public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
            Account account = new Account();

            account.setId(rs.getString("id"));

            Bank bank = new Bank();
            bank.setId(rs.getString("bank_id"));
            bank.setName(rs.getString("bank_name"));
            account.setBank(bank);

            account.setName(rs.getString("name"));
            return account;
        }
    }
}
