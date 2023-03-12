package id.my.rizkiyuwanda.transaction;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import id.my.rizkiyuwanda.account.Account;
import id.my.rizkiyuwanda.account.AccountService;
import id.my.rizkiyuwanda.api.AccountWebClientService;
import id.my.rizkiyuwanda.bank.Bank;
import id.my.rizkiyuwanda.bank.BankService;
import id.my.rizkiyuwanda.utility.LSFormLayout;
import id.my.rizkiyuwanda.views.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@PageTitle("Transaction")
@Route(value = "transaction", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "USER"})
public class TransactionView extends VerticalLayout {

    private final BankService bankService;
    private final AccountService accountService;
    private final AccountWebClientService accountWebClientService;

    private final String TRANSFER_TO = "Transfer To";
    private final String TRANSFER_VIA = "Transfer Via";
    private final String TRANSFER_DETAILS = "Transfer Details";
    private final Accordion accordion = new Accordion();
    private final LSFormLayout transferToFormLayout = new LSFormLayout("400px", 2);
    private final LSFormLayout transferViaFormLayout = new LSFormLayout("400px", 2);
    private final LSFormLayout transferDetailsFormLayout = new LSFormLayout("400px", 2);
    private final AccordionPanel transferToAccordionPanel = accordion.add(TRANSFER_TO, transferToFormLayout);
    private final AccordionPanel transferViaAccordionPanel = accordion.add(TRANSFER_VIA, transferViaFormLayout);
    private final AccordionPanel transferDetailsAccordionPanel = accordion.add(TRANSFER_DETAILS, transferDetailsFormLayout);
    private final ComboBox<Bank> bankComboBox = new ComboBox<>("Bank");
    private final ComboBox<Account> receiverAccountComboBox = new ComboBox<>("Account Number (Example, not real)");
    private final BigDecimalField amountBigDecimalField = new BigDecimalField("Transfer Amount");
    private final Button openTransferViaButton = new Button("Next");
    private final BigDecimalField amountPlusFeeBigDecimalField = new BigDecimalField("Total (Amount + Fee)");

    private final ComboBox<Account> luwidSendSenderAccountComboBox = new ComboBox<>("LuwidSend Account");

    @Autowired
    public TransactionView(BankService bankService, AccountService accountService, AccountWebClientService accountWebClientService) {
        this.bankService = bankService;
        this.accountService = accountService;
        this.accountWebClientService = accountWebClientService;
        initLayouts();
        initEvents();
    }

    private void initLayouts() {
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        transferViaAccordionPanel.setEnabled(false);
        transferDetailsAccordionPanel.setEnabled(false);


        List<Bank> banks = new ArrayList<>();
        Iterable<Bank>bankIterable = bankService.findAll();
        bankIterable.forEach(banks::add);
        bankComboBox.setItems(banks);
        bankComboBox.setItemLabelGenerator(Bank::getName);

        receiverAccountComboBox.setItemLabelGenerator(account -> account.getId() + " - " + account.getName());

        amountBigDecimalField.setHelperText("Fee: Rp. 500");
        amountBigDecimalField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        openTransferViaButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        luwidSendSenderAccountComboBox.setItems(accountService.getList());
        luwidSendSenderAccountComboBox.setItemLabelGenerator(account -> account.getBank().getName() + " - " + account.getName());
        amountPlusFeeBigDecimalField.setEnabled(false);

        transferToFormLayout.add(bankComboBox, 2);
        transferToFormLayout.add(receiverAccountComboBox, amountBigDecimalField);
        transferToFormLayout.add(openTransferViaButton, new Label());

        transferViaFormLayout.add(luwidSendSenderAccountComboBox, 2);
        transferViaFormLayout.add(amountPlusFeeBigDecimalField, new Label());

        add(accordion);
    }

    private void initEvents() {
        bankComboBox.addValueChangeListener(event -> loadReceiverAccount());
        openTransferViaButton.addClickListener(event -> openTransferViaFormLayout());
    }

    private void loadReceiverAccount() {
        if (bankComboBox.getValue() != null) {
            List<Account>accounts = new ArrayList<>();
            List<Account>accountsFromAPI = accountWebClientService.findAllAccountByBankId(bankComboBox.getValue().getId());
            for(Account account : accountsFromAPI){
                if(account.getName().equals("LuwidSend") == false){
                    accounts.add(account);
                }
            }
            receiverAccountComboBox.setItems(accounts);
        }
    }

    private void openTransferViaFormLayout() {
        //1. CHECK RECEIVER ACCOUNT
        if (bankComboBox.getValue() == null) {
            Notification.show("Bank cannot be empty", 5000, Notification.Position.MIDDLE);
        } else if (receiverAccountComboBox.getValue() == null) {
            Notification.show("Receiver account cannot be empty", 5000, Notification.Position.MIDDLE);
        } else if (amountBigDecimalField.getValue() == null) {
            Notification.show("Amount cannot be empty", 5000, Notification.Position.MIDDLE);
        } else if (amountBigDecimalField.getValue().compareTo(BigDecimal.ZERO) != 1) {
            Notification.show("Amount must be greater than 0", 5000, Notification.Position.MIDDLE);
        } else {
            BigDecimal amountPlusFee = amountBigDecimalField.getValue().add(new BigDecimal(500));
            amountPlusFeeBigDecimalField.setValue(amountPlusFee);

            transferToAccordionPanel.setOpened(false);
            transferViaAccordionPanel.setEnabled(true);
            transferViaAccordionPanel.setOpened(true);
        }
    }
}
