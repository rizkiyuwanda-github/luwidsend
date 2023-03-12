package id.my.rizkiyuwanda.api;

import id.my.rizkiyuwanda.account.Account;
import id.my.rizkiyuwanda.utility.LSWebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class AccountWebClientService {
    @Autowired
    private LSWebClient lsWebClient;

    public List<Account> findAllAccountByBankId(String bankId) {
        Flux<Account> accountFlux = lsWebClient.getWebClient().get()
                .uri("/account/findallbybankId/{bankid}", bankId)
                .retrieve()
                .bodyToFlux(Account.class);
        //EXECUTE
        return accountFlux.collectList().block();
    }
}
