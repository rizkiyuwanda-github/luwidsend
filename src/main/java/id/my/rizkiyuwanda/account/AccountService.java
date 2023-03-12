package id.my.rizkiyuwanda.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountRepositoryJDBCTemplate accountRepositoryJDBCTemplate;

    //USE THIS IS VERY SLOW
    public Iterable<Account> findAll() {
        return accountRepository.findAll();
    }

    public List<Account> getList(){
        return accountRepositoryJDBCTemplate.getList();
    }
}
