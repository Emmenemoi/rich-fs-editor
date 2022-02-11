package com.emmenemoi.application.views.medias;

import com.emmenemoi.application.components.CKEditor;
import com.emmenemoi.application.components.FileSelector;
import com.emmenemoi.application.data.StorageProperties;
import com.emmenemoi.application.data.entity.BlogDocument;
import com.emmenemoi.application.data.service.BlogDocumentService;
import com.emmenemoi.application.data.service.FileSystemStorageService;
import com.emmenemoi.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.PermitAll;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

@PageTitle("Medias")
@Route(value = "/medias", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class MediasView extends Div {
    static Logger logger = LoggerFactory.getLogger(MediasView.class);

    private FileSelector fileSelect;
    private Div medias;
    private final FileSystemStorageService fileSystemStorageService;

    private File current;

    @Autowired
    public MediasView(FileSystemStorageService fileSystemStorageService, StorageProperties storageProperties) {
        this.fileSystemStorageService = fileSystemStorageService;
        addClassNames("navigator-view", "flex", "flex-col", "h-full");
        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout, storageProperties);
        medias = createMediasLayout(splitLayout);

        add(splitLayout);

        fileSelect.addValueChangeListener(event -> {
            File file = fileSelect.getValue();
            //Date date = new Date(file.lastModified());
            Notification.show("Loading ... "+file.getPath());
            populateForm(file);
        });

        populateForm(null);
    }

    protected void save() {

    }

    private Div createMediasLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("flex flex-col");
        //editorLayoutDiv.setWidth("400px");
        editorLayoutDiv.setWidthFull();


        Div mediasDiv = new Div();
        mediasDiv.setClassName("p-l flex-grow");
        editorLayoutDiv.add(mediasDiv);


        splitLayout.addToSecondary(editorLayoutDiv);
        return mediasDiv;
    }

    private Div createGridLayout(SplitLayout splitLayout, StorageProperties storageProperties) {
        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidth("500px");
        splitLayout.addToPrimary(wrapper);
        fileSelect = new FileSelector(fileSystemStorageService.getUploadRootFs().toFile(), storageProperties.getFilterImages());
        fileSelect.setWidthFull();
        fileSelect.setHeightFull();
        wrapper.add(fileSelect);
        return wrapper;
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(File value) {
        this.current = value;
        logger.debug("View {}", current);
        medias.removeAll();
        if(current != null) {
            if (current.isDirectory()) {
                Arrays.asList(current.listFiles()).forEach(f ->{
                    medias.add(new ImageCard(f, fileSystemStorageService));
                });
            } else {
                medias.add(new ImageCard(current, fileSystemStorageService).asFull());
            }
            medias.setVisible(true);
        } else {
            medias.setVisible(false);
        }
    }
}