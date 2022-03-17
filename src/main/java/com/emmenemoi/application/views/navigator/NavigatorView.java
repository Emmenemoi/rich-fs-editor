package com.emmenemoi.application.views.navigator;

import com.emmenemoi.application.components.ConfirmDialog;
import com.emmenemoi.application.components.FileSelector;
import com.emmenemoi.application.data.Role;
import com.emmenemoi.application.data.StorageProperties;
import com.emmenemoi.application.data.entity.BlogDocument;
import com.emmenemoi.application.data.service.BlogDocumentService;
import com.emmenemoi.application.data.service.FileSystemStorageService;
import com.emmenemoi.application.components.CKEditor;
import com.emmenemoi.application.security.AuthenticatedUser;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@PageTitle("Content")
@Route(value = "/", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class NavigatorView extends Div {
    static Logger logger = LoggerFactory.getLogger(NavigatorView.class);

    private FileSelector fileSelect;

    CKEditor ckEditorLocal;
    TextArea frontmatter;
    Div frontmatterDiv;
    final Div     editor;
    Component buttons;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BlogDocument blogDocument;

    private final BlogDocumentService blogDocumentService;
    private final FileSystemStorageService fileSystemStorageService;

    @Autowired
    public NavigatorView(BlogDocumentService blogDocumentService,
                         FileSystemStorageService fileSystemStorageService,
                         StorageProperties storageProperties,
                         AuthenticatedUser authenticatedUser) {
        this.blogDocumentService = blogDocumentService;
        this.fileSystemStorageService = fileSystemStorageService;
        addClassNames("navigator-view", "flex", "flex-col", "h-full");
        ckEditorLocal = new CKEditor(fileSystemStorageService);

        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout, storageProperties, authenticatedUser);
        editor = createEditorLayout(splitLayout);

        add(splitLayout);

        fileSelect.addValueChangeListener(event -> {
            File file = fileSelect.getValue();
            //Date date = new Date(file.lastModified());
            if (!file.isDirectory()) {
                Notification.show("Loading ... "+file.getPath());

                if (event.getValue() != null) {
                    Optional<BlogDocument> blogDocumentFromBackend = blogDocumentService.get(Path.of(file.getPath()));

                    if (blogDocumentFromBackend.isPresent()) {
                        populateForm(blogDocumentFromBackend.get());
                    } else {
                        Notification.show(
                                String.format("The requested blog document was not found, ID = %s", file.getName()), 3000,
                                Notification.Position.BOTTOM_START);
                        // when a row is selected but the data is no longer available,
                        // refresh grid
                    }

                } else {
                    populateForm(null);
                    UI.getCurrent().navigate(NavigatorView.class);
                }

            } else {
                Notification.show(file.getPath());
            }
        });

        cancel.addClickListener(e -> {
            populateForm(null);
        });

        save.addClickListener(e -> {
            try {
                if (this.blogDocument == null) {
                    this.blogDocument = new BlogDocument();
                }
                this.blogDocument.setFrontmatter(frontmatter.getValue());
                this.blogDocument.setContent(ckEditorLocal.getValue());
                blogDocumentService.update(this.blogDocument);
                Notification.show(blogDocument.getFilename()+" details stored.");
            } catch (IOException validationException) {
                Notification.show("An exception happened while trying to store the blog document.");
            }
        });
        populateForm(null);
    }

    protected void renameFileAction(File file) {
        new ConfirmDialog(null, file.getName(), file.getName(), fileSelect.getFilter(), name -> {
            try {
                Path dest = file.toPath().resolveSibling(name);
                Files.move(file.toPath(), dest);
                fileSelect.getDataProvider().refreshItem(file);
                logger.info("renamed {} to {}", file.toPath().toAbsolutePath(), dest.toAbsolutePath());
            } catch (IOException ex) {
                Notification.show(ex.getMessage());
            }
        } ).open();
    }

    protected void addFileAction(File file) {
        new ConfirmDialog(null, "Nouveau fichier", null, fileSelect.getFilter(), name -> {
            try {
                File dest = file.isDirectory() ? file.toPath().resolve(name).toFile() : file.toPath().getParent().resolve(name).toFile();
                blogDocumentService.newDocument(dest.toPath());
                fileSelect.getDataProvider().refreshAll();
                logger.info("new file at {}", dest.getAbsolutePath());
            } catch (IOException ex) {
                Notification.show(ex.getMessage());
            }
        } ).open();
    }

    protected void addFolderAction(File file) {
        new ConfirmDialog(null, "Nouveau dossier", null, fileSelect.getFilter(), name -> {
            try {
                File dest = file.isDirectory() ? file.toPath().resolve(name).toFile() : file.toPath().getParent().resolve(name).toFile();
                dest.mkdir();
                fileSelect.getDataProvider().refreshItem(file);
                logger.info("new directory at {}", dest.getAbsolutePath());
            } catch (Exception ex) {
                Notification.show(ex.getMessage());
            }
        }).open();
    }

    protected void deleteItemAction(File file) {
        new ConfirmDialog("Etes vous certain de vouloir effacer '"+file.getName()+"'", null, null, fileSelect.getFilter(), name -> {
            try {
                blogDocumentService.delete(file.toPath());
                fileSelect.getDataProvider().refreshItem(file);
                logger.info("delete file at {}", file.getAbsolutePath());
            } catch (Exception ex) {
                Notification.show(ex.getMessage());
            }
        } ).open();
    }

        private Div createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("flex flex-col");
        //editorLayoutDiv.setWidth("400px");
        editorLayoutDiv.setWidthFull();

        frontmatterDiv = new Div();
        frontmatterDiv.setClassName("p-l");
        frontmatterDiv.setHeight("200px");
        frontmatterDiv.setMinHeight("200px");
        frontmatterDiv.getStyle().set("paddingTop", "0");
        frontmatterDiv.getStyle().set("paddingBottom", "0");
        frontmatter = new TextArea();
        frontmatter.setSizeFull();
        frontmatter.setLabel("Frontmatter");
        frontmatterDiv.add(frontmatter);
        editorLayoutDiv.add(frontmatterDiv);

        Div editorDiv = new Div();
        editorDiv.setClassName("p-l flex-grow");
        editorLayoutDiv.add(editorDiv);

        ckEditorLocal.setSizeFull();
        editorDiv.add( ckEditorLocal);
        buttons = createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
        return editorLayoutDiv;
    }

    private Component createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("w-full flex-wrap bg-contrast-5 py-s px-l");
        buttonLayout.setSpacing(true);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
        return buttonLayout;
    }

    private Div createGridLayout(SplitLayout splitLayout, StorageProperties storageProperties, AuthenticatedUser authenticatedUser) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        wrapper.setWidth("500px");
        splitLayout.addToPrimary(wrapper);
        fileSelect = new FileSelector(fileSystemStorageService.getRootFs().toFile(), storageProperties.getFilterFiles(),
                this::renameFileAction, this::addFileAction, this::addFolderAction, this::deleteItemAction);
        fileSelect.setWidthFull();
        //fileSelect.getElement().getStyle().set("flexGrow", "1");
        //fileSelect.setHeightFull();
        wrapper.add(fileSelect);
        if (fileSystemStorageService.hasGit() && authenticatedUser.get().isPresent() && authenticatedUser.get().get().getRoles().contains(Role.ADMIN)) {
            Button publish = new Button("Publish all", e -> {
                try {
                    fileSystemStorageService.publish();
                } catch (Exception ex) {
                    logger.warn("git publish problem", ex);
                }
            });
            wrapper.add(publish);
        }
        return wrapper;
    }

    public void gitCommit(){

    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(BlogDocument value) {
        this.blogDocument = value;
        logger.debug("Edit {}", blogDocument);
        if(blogDocument != null) {
            //ckEditor.setValue(blogDocument.getContent());
            frontmatter.setValue(blogDocument.getFrontmatter());
            frontmatterDiv.setVisible(!blogDocument.getFrontmatter().isEmpty());
            ckEditorLocal.setVisible(true);
            buttons.setVisible(true);
            ckEditorLocal.setValue(blogDocument.getContent());
        } else {
            ckEditorLocal.clear();
            frontmatter.clear();
            //ckEditorLocal.setVisible(false);
            frontmatterDiv.setVisible(false);
            buttons.setVisible(false);
        }
    }
}