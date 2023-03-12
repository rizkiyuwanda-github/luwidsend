package id.my.rizkiyuwanda.transaction;

import id.my.rizkiyuwanda.account.Account;
import id.my.rizkiyuwanda.utility.LSWebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@Transactional
public class TransactionService {

    @Autowired
    private LSWebClient lsWebClient;

    @Autowired
    private TransactionRepository transactionRepository;

    public LocalDateTime getLocalDateTime() {
        Mono<LocalDateTime> timeMono = lsWebClient.getWebClient().get()
                .uri("/gettime")
                .retrieve()
                .bodyToMono(LocalDateTime.class);
        //EXECUTE
        return timeMono.block();
    }

    private TransactionDTO transfer(TransactionDTO transactionDTO) {
        return lsWebClient.getWebClient().post()
                .uri("/transaction/transfer")
                .body(Mono.just(transactionDTO), TransactionDTO.class)
                .retrieve()
                .bodyToMono(TransactionDTO.class).block();
    }

    public Transaction transferDifferentBank(Transaction transaction, Account luwidSendReceiverAccount, Account luwidSendSenderAccount) {
        LocalDateTime timeServer = getLocalDateTime();
        int nano1 = timeServer.getNano();
        int nano2 = nano1 + 1;
        String id1 = "T"+timeServer.getYear()+timeServer.getMonthValue()+timeServer.getDayOfMonth()+timeServer.getHour()+timeServer.getMinute()+timeServer.getSecond()+nano1;
        String id2 = "T"+timeServer.getYear()+timeServer.getMonthValue()+timeServer.getDayOfMonth()+timeServer.getHour()+timeServer.getMinute()+timeServer.getSecond()+nano2;

        TransactionDTO transactionDTO_1 = new TransactionDTO();
        transactionDTO_1.setId(id1);
        transactionDTO_1.setSenderAccountId(transaction.getSenderAccountId());
        transactionDTO_1.setSenderBankId(transaction.getSenderBankId());
        transactionDTO_1.setReceiverAccountId(luwidSendReceiverAccount.getId());
        transactionDTO_1.setReceiverBankId(luwidSendReceiverAccount.getBank().getId());
        transactionDTO_1.setAmount(transaction.getAmount().add(transaction.getFee()));//(Amount + Fee)
        transactionDTO_1.setNote(transaction.getNote());

        TransactionDTO transactionDTO_2 = new TransactionDTO();
        transactionDTO_2.setId(id2);
        transactionDTO_2.setSenderAccountId(luwidSendSenderAccount.getId());
        transactionDTO_2.setSenderBankId(luwidSendSenderAccount.getBank().getId());
        transactionDTO_2.setReceiverAccountId(transaction.getReceiverAccountId());
        transactionDTO_2.setReceiverBankId(transaction.getReceiverBankId());
        transactionDTO_2.setAmount(transaction.getAmount());//(Amount Only)
        transactionDTO_2.setNote(transaction.getNote());

        //1. SENDER TRANSFER TO ACCOUNT LUWIDSEND (Amount + Fee)
        //if in real application then the sender has to send himself and input ID himself
        TransactionDTO transactionDTOSender =  transfer(transactionDTO_1);

        //2. check on the luwidsend account is have received it from the sender (ID + (Amount + Fee))
        Transaction transactionToLuwidSend = lsWebClient.getWebClient().get()
                .uri("/findByIdAndReceiverAccountIdAndAmount/{id}/{receiverAccountId}/{amount}",
                        transactionDTOSender.getId(), luwidSendReceiverAccount.getId(), transactionDTO_1.getAmount())
                .retrieve()
                .bodyToMono(Transaction.class).block();

        if(transactionToLuwidSend == null){
            System.out.println("Transfer not found");
            return null;
        }else{
            //3. LUWIDSEND TRANSFER TO RECEIVER (Amount Only), use luwidsend bank account as same with receiver
            TransactionDTO transactionDTO_2Response = transfer(transactionDTO_2);

            if(transactionDTO_2Response != null) {
                System.out.println("Transfer failed");
                return null;
            }else{
                //4. INSERT TO LOCAL DATABASE
                transaction.setId(id1);
                return transactionRepository.save(transaction);

            }
        }
    }
}
