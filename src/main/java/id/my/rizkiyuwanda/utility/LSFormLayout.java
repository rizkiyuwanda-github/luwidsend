package id.my.rizkiyuwanda.utility;

import com.vaadin.flow.component.formlayout.FormLayout;

public class LSFormLayout extends FormLayout {
    public LSFormLayout(String width, Integer columnQty) {
        setResponsiveSteps(new ResponsiveStep("0", 1, ResponsiveStep.LabelsPosition.TOP), //APABILA UKURAN FORM LAYOUT LEBARNYA < 380 PX (DI UKURAN APLIKASI 380PX KARENA 40 SPACE DIKANAN, 40 SPACE DIKIRI, JADI LAYOUT 380 PX) MAKA AKAN 1 KOLOM
                new ResponsiveStep(width, columnQty, ResponsiveStep.LabelsPosition.ASIDE));  //APABILA UKURAN FORM LAYOUT LEBARNYA >= 300 PX (DI UKURAN APLIKASI 308PX KARENA 40 SPACE DIKANAN, 40 SPACE DIKIRI, JADI LAYOUT 380 PX) MAKA AKAN 2 KOLOM

    }

}
