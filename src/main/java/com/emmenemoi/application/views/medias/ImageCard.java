package com.emmenemoi.application.views.medias;

import com.emmenemoi.application.data.service.FileSystemStorageService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.vaadin.olli.ClipboardHelper;

import java.io.File;

public class ImageCard extends Div {
    protected File image;
    protected FileSystemStorageService fileSystemStorageService;
    protected HorizontalLayout toolbar = new HorizontalLayout();

    public ImageCard(File image, FileSystemStorageService fileSystemStorageService) {
        super();
        this.image = image;
        this.fileSystemStorageService = fileSystemStorageService;
        String src = fileSystemStorageService.getSrc(image);
        Image img = new Image(src, image.getName());
        img.setWidthFull();
        toolbar.setWidthFull();
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        Button copyBtn = new Button(VaadinIcon.COPY.create());
        copyBtn.addClickListener(e -> Notification.show(src+" copied into clipboard"));

        ClipboardHelper clipboardHelper = new ClipboardHelper(src, copyBtn);

        toolbar.add(clipboardHelper);
        add(img, toolbar);
        setClassName("image-card");
    }

    public ImageCard asFull(){
        setSizeFull();
        return this;
    }

}
