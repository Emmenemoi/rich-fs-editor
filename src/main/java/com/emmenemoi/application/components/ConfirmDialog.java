package com.emmenemoi.application.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.util.function.Consumer;

public class ConfirmDialog extends Dialog {
    public ConfirmDialog(String title, String placeholder, String value, FileSelector.FileFilter filter, Consumer<String> consumer) {
        if (title != null) {
            getElement().setAttribute("aria-label", title);
            add(new Label(title));
        }
        VerticalLayout dialogLayout = new VerticalLayout();
        final TextField tf = new TextField();;
        if (placeholder != null) {
            tf.setPlaceholder(placeholder);
        }
        if(value!=null){
            tf.setValue(value);
        }
        if(value!= null || placeholder!=null) {
            dialogLayout.add(tf);
            if (filter != null) {
                tf.setHelperText("Accepted text: "+filter.getPattern());
                tf.setPattern(filter.getPattern());
            }
        }
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setWidthFull();
        buttons.add(
                new Button("Annuler", e -> close()),
                new Button("Ok", e -> {
                    if (filter != null && tf.isInvalid()) {
                        tf.setErrorMessage("Can't create file. Respect filter");
                        return;
                    }
                    if (consumer != null) {
                        consumer.accept(tf.getValue());
                    }
                    close();
                })
        );
        dialogLayout.add(buttons);
        dialogLayout.setWidthFull();
        add(dialogLayout);
    }
}
