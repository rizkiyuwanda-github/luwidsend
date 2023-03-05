package id.my.rizkiyuwanda.bank;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import id.my.rizkiyuwanda.utility.LSFormLayout;

public class BankDialog extends Dialog {

    private final BankService bankService;

    private final Bank bankForUpdate;
    private final Button refresh;

    private final TextField id = new TextField("Id");
    private final TextField name = new TextField("Bank Name");
    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final Binder<Bank> binder = new Binder<>(Bank.class);

    public BankDialog(BankService bankService, Bank bankForUpdate, Button refresh) {
        this.bankService = bankService;
        this.bankForUpdate = bankForUpdate;
        this.refresh = refresh;
        initLayouts();
        initEvents();
    }

    private void initLayouts(){
        setWidth("500px");
        setModal(true);
        setCloseOnEsc(false);
        setCloseOnOutsideClick(false);


        LSFormLayout lsFormLayout = new LSFormLayout("400px", 2);
        lsFormLayout.add(id, name);
        Scroller scroller = new Scroller(lsFormLayout);
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);

        binder.bindInstanceFields(this);
//        binder.bind(id, Bank::getId, Bank::setId);
//        binder.bind(name, Bank::getName, Bank::setName);

        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancel.getStyle().set("margin-right", "auto");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(scroller);

        getFooter().add(cancel, save);

        if (bankForUpdate == null) {
            setHeaderTitle("Create");
        } else {
            setHeaderTitle("Update");
            binder.readBean(bankForUpdate);
            id.setEnabled(false);
        }
    }

    private void initEvents(){
        cancel.addClickListener(buttonClickEvent -> close());
        save.addClickListener(buttonClickEvent -> saveBank());
    }

    private void saveBank() {
        if (id.isEmpty()) {
            Notification.show("Id cannot be empty", 3000, Notification.Position.MIDDLE);
        } else if (name.isEmpty()) {
            Notification.show("Name cannot be empty", 3000, Notification.Position.MIDDLE);
        } else {
            Bank bank= new Bank(id.getValue(), name.getValue());
//            binder.setBean(bank);
//            System.out.println("Data: "+binder.getBean().toString());
            if(bankService.save(bank) != null){
                Notification.show("Bank saved", 3000, Notification.Position.MIDDLE);
                refresh.click();
                this.close();
            }else{
                Notification.show("Error", 3000, Notification.Position.MIDDLE);
            }
        }
    }

}
