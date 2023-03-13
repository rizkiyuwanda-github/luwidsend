package id.my.rizkiyuwanda.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@Transactional
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    public Transaction save (Transaction transaction){
        LocalDateTime ldt = LocalDateTime.now();
        if(transaction.getId() == null) {
            transaction.setId("LST" + ldt.getYear() + ldt.getMonthValue() + ldt.getDayOfMonth() + ldt.getHour() + ldt.getMinute() + ldt.getSecond() + ldt.getNano());
        }
        transaction.setTime(ldt);
        return transactionRepository.save(transaction);
    }
}
