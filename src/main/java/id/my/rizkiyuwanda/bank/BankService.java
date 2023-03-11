package id.my.rizkiyuwanda.bank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class BankService {
    @Autowired
    private BankRepository bankRepository;
    public Bank save(Bank bank) {
        return bankRepository.save(bank);
    }
    public void deleteById(String id) {bankRepository.deleteById(id);}
    public Optional<Bank> findById(String id) {return bankRepository.findById(id);}
    public Iterable<Bank> findAll() {
        return bankRepository.findAll();
    }

}
