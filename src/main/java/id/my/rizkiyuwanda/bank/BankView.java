package id.my.rizkiyuwanda.bank;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import id.my.rizkiyuwanda.views.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;

@PageTitle("Bank")
@Route(value = "bank", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class BankView extends Div {

    @Autowired
    private BankService bankService;

    public BankView() {
        //addClassName("bank-view");

        Button buttonAdd = new Button("Add");
        buttonAdd.addClickListener(buttonClickEvent ->
                openBankDialog()
                );
        add(buttonAdd);

    }

    private void openBankDialog(){
        Button refresh = new Button();
        refresh.addClickListener(buttonClickEvent -> Notification.show("Closed"));
        BankDialog bankDialog = new BankDialog(bankService, null, refresh);
        bankDialog.open();

    }
}
