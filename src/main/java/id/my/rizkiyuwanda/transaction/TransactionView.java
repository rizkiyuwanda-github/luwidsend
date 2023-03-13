package id.my.rizkiyuwanda.transaction;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import id.my.rizkiyuwanda.account.Account;
import id.my.rizkiyuwanda.account.AccountService;
import id.my.rizkiyuwanda.api.AccountWebClientService;
import id.my.rizkiyuwanda.api.TransactionWebClientService;
import id.my.rizkiyuwanda.bank.Bank;
import id.my.rizkiyuwanda.bank.BankService;
import id.my.rizkiyuwanda.utility.LSFormLayout;
import id.my.rizkiyuwanda.utility.LSVariable;
import id.my.rizkiyuwanda.views.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Transaction")
@Route(value = "transaction", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "USER"})
public class TransactionView extends VerticalLayout {

    private final BankService bankService;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final AccountWebClientService accountWebClientService;
    private final TransactionWebClientService transactionWebClientService;

    private final String TRANSFER_TO = "Transfer to";
    private final String TRANSFER_METHOD = "Choose a transfer method";
    private final String TRANSFER_DETAILS = "Transfer details";
    private final Accordion accordion = new Accordion();
    private final LSFormLayout receiverFormLayout = new LSFormLayout("400px", 2);
    private final LSFormLayout transferMethodFormLayout = new LSFormLayout("400px", 2);
    private final LSFormLayout transferDetailsFormLayout = new LSFormLayout("400px", 2);
    private final AccordionPanel receiverAccordionPanel = accordion.add(TRANSFER_TO, receiverFormLayout);
    private final AccordionPanel transferMethodAccordionPanel = accordion.add(TRANSFER_METHOD, transferMethodFormLayout);
    private final AccordionPanel transferDetailsAccordionPanel = accordion.add(TRANSFER_DETAILS, transferDetailsFormLayout);
    private final ComboBox<Bank> receiverBankComboBox = new ComboBox<>("Bank");
    private final ComboBox<Account> receiverAccountComboBox = new ComboBox<>("Account Number (Example, not real)");
    private final BigDecimalField amountBigDecimalField = new BigDecimalField("Transfer Amount");
    private final Button openTransferMethodButton = new Button("Next");
    private final ComboBox<Account> luwidSendReceiverAccountComboBox = new ComboBox<>("Bank");
    private final BigDecimalField amountPlusFeeBigDecimalField = new BigDecimalField("Total (Amount + Fee)");
    private final ComboBox<Account> senderAccountComboBox = new ComboBox<>("I Transfer Via");
    private final Button cancelButton = new Button("Cancel");
    private final Button openTransferDetailsButton = new Button("Process");
    private final H4 detailsH4 = new H4("Please transfer to the LuwidSend account to be forwarded to: ");
    private final Label detailsLabel = new Label();
    private final H4 statusH4 = new H4("Status: "+ LSVariable.TRANSACTION_STATUS_PENDING);
    private final TextField idTransferToLuwidSendReceiverAccount = new TextField("ID Transfer");
    private final Button checkMyTransferButton = new Button("I Have Transferred");


    @Autowired
    public TransactionView(BankService bankService, AccountService accountService, TransactionService transactionService, AccountWebClientService accountWebClientService, TransactionWebClientService transactionWebClientService) {
        this.bankService = bankService;
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.accountWebClientService = accountWebClientService;
        this.transactionWebClientService = transactionWebClientService;
        initLayouts();
        initEvents();

    }

    private void initLayouts() {
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        transferMethodAccordionPanel.setEnabled(false);
        transferDetailsAccordionPanel.setEnabled(false);


        List<Bank> banks = new ArrayList<>();
        Iterable<Bank>bankIterable = bankService.findAll();
        bankIterable.forEach(banks::add);
        receiverBankComboBox.setItems(banks);
        receiverBankComboBox.setItemLabelGenerator(Bank::getName);

        receiverAccountComboBox.setItemLabelGenerator(account -> account.getId() + " - " + account.getName());

        amountBigDecimalField.setHelperText("Fee: Rp. "+LSVariable.TRANSACTION_FEE);
        amountBigDecimalField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        openTransferMethodButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        luwidSendReceiverAccountComboBox.setItems(accountService.getList());
        luwidSendReceiverAccountComboBox.setItemLabelGenerator(account -> account.getBank().getName() +" - "+account.getName());
        amountPlusFeeBigDecimalField.setEnabled(false);

        senderAccountComboBox.setItemLabelGenerator(account -> account.getBank().getName() +" - "+account.getName());
        senderAccountComboBox.setHelperText("(Example only, in real case the user has to transfer separately from this app)");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        openTransferDetailsButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        checkMyTransferButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        receiverFormLayout.add(receiverBankComboBox, 2);
        receiverFormLayout.add(receiverAccountComboBox, amountBigDecimalField);
        receiverFormLayout.add(openTransferMethodButton, new Label());

        transferMethodFormLayout.add(luwidSendReceiverAccountComboBox, 2);
        transferMethodFormLayout.add(amountPlusFeeBigDecimalField, new Label());
        transferMethodFormLayout.add(senderAccountComboBox, 2);
        transferMethodFormLayout.add(cancelButton, openTransferDetailsButton);

        transferDetailsFormLayout.add(detailsH4, 2);
        transferDetailsFormLayout.add(detailsLabel, 2);
        transferDetailsFormLayout.add(statusH4, 2);
        transferDetailsFormLayout.add(idTransferToLuwidSendReceiverAccount, checkMyTransferButton);

        add(accordion);
    }

    private void initEvents() {
        receiverBankComboBox.addValueChangeListener(event -> loadReceiverAccount());
        openTransferMethodButton.addClickListener(event -> openTransferMethodFormLayout());
        luwidSendReceiverAccountComboBox.addValueChangeListener(event -> loadSenderAccount());
        openTransferDetailsButton.addClickListener(event -> openTransferDetailsFormLayout());
        cancelButton.addClickListener(event -> refresh());
    }

    private void loadReceiverAccount() {
        if (receiverBankComboBox.getValue() != null) {
            List<Account>accounts = new ArrayList<>();
            List<Account>accountsFromAPI = accountWebClientService.findAllAccountByBankId(receiverBankComboBox.getValue().getId());
            for(Account account : accountsFromAPI){
                if(account.getName().equals("LuwidSend") == false){
                    accounts.add(account);
                }
            }
            receiverAccountComboBox.setItems(accounts);
        }
    }

    private void loadSenderAccount(){
        if(luwidSendReceiverAccountComboBox.getValue() != null){
            List<Account>accounts = new ArrayList<>();
            List<Account>accountsFromAPI = accountWebClientService.findAllAccountByBankId(luwidSendReceiverAccountComboBox.getValue().getBank().getId());
            for(Account account : accountsFromAPI){
                if(account.getName().equals("LuwidSend") == false){
                    accounts.add(account);
                }
            }
            senderAccountComboBox.setItems(accounts);
        }
    }

    private void openTransferMethodFormLayout() {
        //1. CHECK RECEIVER ACCOUNT
        if (receiverBankComboBox.getValue() == null) {
            Notification.show("Bank cannot be empty", 5000, Notification.Position.MIDDLE);
        } else if (receiverAccountComboBox.getValue() == null) {
            Notification.show("Receiver account cannot be empty", 5000, Notification.Position.MIDDLE);
        } else if (amountBigDecimalField.getValue() == null) {
            Notification.show("Amount cannot be empty", 5000, Notification.Position.MIDDLE);
        } else if (amountBigDecimalField.getValue().compareTo(BigDecimal.ZERO) != 1) {
            Notification.show("Amount must be greater than 0", 5000, Notification.Position.MIDDLE);
        } else {
            BigDecimal amountPlusFee = amountBigDecimalField.getValue().add(LSVariable.TRANSACTION_FEE);
            amountPlusFeeBigDecimalField.setValue(amountPlusFee);
            receiverFormLayout.setEnabled(false);
            receiverAccordionPanel.setOpened(false);
            transferMethodAccordionPanel.setEnabled(true);
            transferMethodAccordionPanel.setOpened(true);
        }
    }
    private void openTransferDetailsFormLayout() {
        //1. CHECK LUWIDSEND SENDER ACCOUNT
        if (luwidSendReceiverAccountComboBox.getValue() == null) {
            Notification.show("Bank cannot be empty", 5000, Notification.Position.MIDDLE);
        } else if (senderAccountComboBox.getValue() == null) {
            Notification.show("Transfer via cannot be empty", 5000, Notification.Position.MIDDLE);
        } else{
            createTransaction();
            detailsH4.setText("Please transfer to the LuwidSend account to be forwarded to: "+receiverAccountComboBox.getValue().getName());
            detailsLabel.setText(luwidSendReceiverAccountComboBox.getValue().getBank().getName() +" - "+luwidSendReceiverAccountComboBox.getValue().getId()
                    +" - "+luwidSendReceiverAccountComboBox.getValue().getName());
            transferMethodFormLayout.setEnabled(false);
            transferMethodAccordionPanel.setOpened(false);
            transferDetailsAccordionPanel.setEnabled(true);
            transferDetailsAccordionPanel.setOpened(true);
        }
    }
    
    private void refresh(){
        receiverBankComboBox.clear();
        receiverAccountComboBox.clear();
        amountBigDecimalField.clear();
        receiverFormLayout.setEnabled(true);
        
        luwidSendReceiverAccountComboBox.clear();
        amountPlusFeeBigDecimalField.clear();
        senderAccountComboBox.clear();
        transferMethodFormLayout.setEnabled(true);

        transferMethodAccordionPanel.setEnabled(false);
        transferMethodAccordionPanel.setOpened(false);
        receiverAccordionPanel.setEnabled(true);
        receiverAccordionPanel.setOpened(true);
    }

    private void createTransaction(){
        Transaction transaction = new Transaction();
        transaction.setSenderBankId(senderAccountComboBox.getValue().getBank().getId());
        transaction.setSenderBankName(senderAccountComboBox.getValue().getBank().getName());
        transaction.setSenderAccountId(senderAccountComboBox.getValue().getId());
        transaction.setSenderAccountName(senderAccountComboBox.getValue().getName());

        transaction.setReceiverBankId(receiverAccountComboBox.getValue().getBank().getId());
        transaction.setReceiverBankName(receiverAccountComboBox.getValue().getBank().getName());
        transaction.setReceiverAccountId(receiverAccountComboBox.getValue().getId());
        transaction.setReceiverAccountName(receiverAccountComboBox.getValue().getName());

        transaction.setAmount(amountBigDecimalField.getValue());
        transaction.setFee(LSVariable.TRANSACTION_FEE);
        transaction.setNote("");
        transaction.setStatus(LSVariable.TRANSACTION_STATUS_PENDING);
        transaction.setReferenceId("");

        transactionService.save(transaction);

        //1. SENDER TRANSFER TO LUWIDSEND  RECEIVER (Amount + Fee)
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setSenderBankId(senderAccountComboBox.getValue().getBank().getId());
        transactionDTO.setSenderAccountId(senderAccountComboBox.getValue().getId());
        transactionDTO.setReceiverBankId(luwidSendReceiverAccountComboBox.getValue().getBank().getId());
        transactionDTO.setReceiverAccountId(luwidSendReceiverAccountComboBox.getValue().getId());
        transactionDTO.setAmount(amountPlusFeeBigDecimalField.getValue()); //Amount + Fee
        transactionDTO.setNote(transaction.getNote());

        Transaction transactionResponseSenderToLuwidSendReceiver = transactionWebClientService.transfer(transactionDTO);
        if(transactionResponseSenderToLuwidSendReceiver != null){
            idTransferToLuwidSendReceiverAccount.setValue(transactionResponseSenderToLuwidSendReceiver.getId());
        }
        //============================================
    }

    private void checkMyTransfer(){
        if(idTransferToLuwidSendReceiverAccount.getValue() == null){
            Notification.show("ID transfer cannot be empty", 5000, Notification.Position.MIDDLE);
        }else{
            //2. check on the luwidsend account is have received it from the sender (ID + (Amount + Fee))
            Transaction transactionFromAPI = transactionWebClientService.findByIdAndReceiverAccountIdAndAmount(
                    idTransferToLuwidSendReceiverAccount.getValue(),
                    luwidSendReceiverAccountComboBox.getValue().getId(),
                    amountPlusFeeBigDecimalField.getValue()
            );
            if(transactionFromAPI == null){
                Notification.show("Your ID transfer "+idTransferToLuwidSendReceiverAccount.getValue()+
                        " with amount + fee "+amountPlusFeeBigDecimalField.getValue()+" not found",
                        5000, Notification.Position.MIDDLE);
            }else{
                //3. LUWIDSEND SENDER TRANSFER TO RECEIVER (Amount Only)
                bujuri dibawah ini cari luwid send sender sesuai bank receiver
                TransactionDTO transactionDTO = new TransactionDTO();
                transactionDTO.setSenderBankId(luw.getValue().getBank().getId());
                transactionDTO.setSenderAccountId(senderAccountComboBox.getValue().getId());
                transactionDTO.setReceiverBankId(luwidSendReceiverAccountComboBox.getValue().getBank().getId());
                transactionDTO.setReceiverAccountId(luwidSendReceiverAccountComboBox.getValue().getId());
                transactionDTO.setAmount(amountPlusFeeBigDecimalField.getValue()); //Amount + Fee
                transactionDTO.setNote(transaction.getNote());
            }
        }
    }
}
