package id.my.rizkiyuwanda.bank;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import id.my.rizkiyuwanda.utility.LSFormLayout;
import id.my.rizkiyuwanda.views.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Bank")
@Route(value = "bank", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class BankView extends VerticalLayout {

    private final BankService bankService;

    private final LSFormLayout formLayout = new LSFormLayout("400px", 2);
    private final TextField searchTextField = new TextField();
    private final Button refreshButton = new Button(VaadinIcon.REFRESH.create());
    private final Button addButton = new Button(VaadinIcon.PLUS_CIRCLE.create());
    private final List<Bank> banks = new ArrayList<>();
    private final Grid<Bank> bankGrid = new Grid<>(Bank.class, false);

    public BankView(@Autowired BankService bankService) {
        this.bankService = bankService;
        //addClassName("bank-view");

        initLayouts();
        initEvents();
        loadData();
    }

    private void initLayouts() {
        searchTextField.setMinWidth("80px");
        searchTextField.setPlaceholder("Search");
        searchTextField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchTextField.setValueChangeMode(ValueChangeMode.EAGER);

        refreshButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout layout1 = new HorizontalLayout();
        layout1.setWidthFull();
        layout1.addAndExpand(searchTextField);
        layout1.add(addButton);

        initGrid();
        formLayout.add(layout1, 8);
        formLayout.add(bankGrid, 8);

        add(formLayout);
    }

    private void delete(String id, ConfirmDialog confirmDialog) {
        bankService.deleteById(id);
        Notification.show("Delete success", 3000, Notification.Position.MIDDLE);
        refreshButton.click();
        confirmDialog.close();
    }

    private ConfirmDialog createDeleteDialogConfirm(Bank bank) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Delete");
        dialog.setText("Are you sure want to delete data '" + bank.getName() + "'?");

        dialog.setCancelable(true);
        dialog.setCancelText("Cancel");
        dialog.addCancelListener(event -> dialog.close());

        dialog.setConfirmText("Delete");
        dialog.addConfirmListener(event -> delete(bank.getId(), dialog));

        return dialog;
    }

    private void initGrid() {
        UI.getCurrent().getPage().retrieveExtendedClientDetails(ecd -> {
            int width = ecd.getWindowInnerWidth();
            if (width < 500) {
                int height = ecd.getWindowInnerHeight() - 220;
                bankGrid.setHeight(height + "px");
            } else {
                int height = ecd.getWindowInnerHeight() - 180;
                bankGrid.setHeight(height + "px");
            }
        });
        bankGrid.addComponentColumn((bank) -> {
            Button menuButton = new Button(VaadinIcon.MENU.create());
            ContextMenu contextMenu = new ContextMenu(menuButton);
            contextMenu.setOpenOnClick(true);
            contextMenu.addItem("Update", e -> new BankDialog(bankService, bank, refreshButton).open());
            contextMenu.addItem("Delete", e -> createDeleteDialogConfirm(bank).open());

            return menuButton;
        }).setHeader(refreshButton).setFlexGrow(0).setWidth("60px").setFrozen(true);

        bankGrid.addColumn((bank) -> {
            return bank.getName();
        }).setHeader("Bank Name").setSortable(true).setResizable(true).setAutoWidth(true);

        GridListDataView<Bank> dataView = bankGrid.setItems(banks);
        searchTextField.addValueChangeListener(e -> dataView.refreshAll());

        dataView.addFilter(bank -> {
            String searchTerm = searchTextField.getValue().trim();
            if (searchTerm.isEmpty()) {
                return true;
            }

            boolean nameSearch = matchesTerm(bank.getName(), searchTerm);
            //boolean cariProdukNama = matchesTerm(produkSampahKita.getProduk().getNama(), searchTerm);

            //return cariKategoriNama || cariProdukNama;
            return nameSearch;
        });
    }

    private boolean matchesTerm(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }

    private void initEvents() {
        addButton.addClickListener(event -> new BankDialog(bankService, null, refreshButton).open());
        refreshButton.addClickListener(event -> loadData());
    }

    private void loadData() {
        searchTextField.clear();
        banks.clear();
        bankService.findAll().forEach(banks::add);
        bankGrid.getListDataView().refreshAll();
    }


}
