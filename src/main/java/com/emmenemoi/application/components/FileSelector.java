package com.emmenemoi.application.components;

import com.emmenemoi.application.data.provider.AutoFilesystemDataProvider;
import com.emmenemoi.application.data.service.BlogDocumentService;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.selection.SingleSelect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.filesystemdataprovider.FileTypeResolver;
import org.vaadin.filesystemdataprovider.FilesystemData;
import org.vaadin.filesystemdataprovider.FilesystemDataProvider;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.function.Consumer;
import java.util.regex.Pattern;

@Tag("div")
@CssImport("themes/fileeditor/file-selector.css")
public class FileSelector extends AbstractField<FileSelector, File> implements HasSize, HasComponents, SingleSelect<FileSelector, File> {
    static Logger logger = LoggerFactory.getLogger(FileSelector.class);

    private final Tree<File> tree;
    private final File rootFile;
    private File selectedFile;
    private final FileFilter filter;
    private final FilesystemData root;

    protected Consumer<File> renameFileConsumer, addFileConsumer, addFolderConsumer, deleteConsumer = null;

    public FileSelector(File rootFile) {
        this(rootFile, null, null, null, null, null);
    }

    public FileSelector(File rootFile, String filter){
        this(rootFile, filter, null, null, null, null);
    }

    public FileSelector(File rootFile, String filter, Consumer<File> renameFileConsumer, Consumer<File> addFileConsumer, Consumer<File> addFolderConsumer, Consumer<File> deleteConsumer) {
        super((File)null);
        this.selectedFile = null;
        this.rootFile = rootFile;
        this.filter = filter == null ? null : new FileFilter(filter);
        if (!rootFile.isDirectory()) {
            throw new IllegalArgumentException(rootFile.getAbsolutePath()+" must be a directory");
        }
        this.root = new FilesystemData(this.rootFile, false);
        if (this.filter != null) {
            this.root.setFilter(this.filter);
        }
        setRenameFileConsumer(renameFileConsumer);
        setAddFileConsumer(addFileConsumer);
        setAddFolderConsumer(addFolderConsumer);
        setDeleteConsumer(deleteConsumer);
        logger.info("init tree at {} / {} children (loaded items: {})", rootFile.getAbsolutePath(),  rootFile.listFiles().length, this.root.getChildren(this.rootFile).size());
        this.tree = this.setupTree(this.root);
        this.add(new Component[]{this.tree});;
    }

    public void setRenameFileConsumer(Consumer<File> renameFileConsumer) {
        this.renameFileConsumer = renameFileConsumer;
    }
    public void setAddFileConsumer(Consumer<File> addFileConsumer) {
        this.addFileConsumer = addFileConsumer;
    }
    public void setAddFolderConsumer(Consumer<File> addFolderConsumer) {
        this.addFolderConsumer = addFolderConsumer;
    }
    public void setDeleteConsumer(Consumer<File> deleteConsumer) {
        this.deleteConsumer = deleteConsumer;
    }

    public FilesystemDataProvider getDataProvider(){
        return (FilesystemDataProvider)tree.getDataProvider();
    }

    public FileFilter getFilter(){
        return filter;
    }

    private Tree<File> setupTree(FilesystemData root) {
        FilesystemDataProvider fileSystem = new AutoFilesystemDataProvider(root);
        Tree<File> tree = new Tree<>();
        tree.setDataProvider(fileSystem);
        tree.addHierarchyColumn(File::getName, FileTypeResolver::getIcon, this::getFileDescription);

        String template = "";
        if (renameFileConsumer != null) {
            template += "<vaadin-button theme='icon' @click='${renameItem}' class='action'><vaadin-icon icon='vaadin:pencil'></vaadin-icon></vaadin-button>";
        }
        if (addFileConsumer != null) {
            template += "<vaadin-button theme='icon' @click='${addFile}' class='action'><vaadin-icon icon='vaadin:file-add'></vaadin-icon></vaadin-button>";
        }
        if (addFolderConsumer != null) {
            template += "<vaadin-button theme='icon' @click='${addDirectory}' class='action'><vaadin-icon icon='vaadin:folder-add'></vaadin-icon></vaadin-button>";
        }
        if (deleteConsumer != null) {
            template += "<vaadin-button theme='icon' @click='${deleteItem}' class='action'><vaadin-icon icon='vaadin:trash'></vaadin-icon></vaadin-button>";
        }

        LitRenderer<File> litTemplate = LitRenderer.<File>of(template);
        if (renameFileConsumer != null) {
            litTemplate.withFunction("renameItem", file -> {
                renameFileConsumer.accept(file);
            });
        }
        if (addFileConsumer != null) {
            litTemplate.withFunction("addFile", file -> {
                addFileConsumer.accept(file);
            });
        }
        if (addFolderConsumer != null) {
            litTemplate.withFunction("addDirectory", file -> {
                addFolderConsumer.accept(file);
            });
        }
        if (deleteConsumer != null) {
            litTemplate.withFunction("deleteItem", file -> {
                deleteConsumer.accept(file);
            });
        }

        tree.addColumn(litTemplate)
        .setFlexGrow(0)
        .setWidth("90px")
        .setResizable(false);

        tree.setSelectionMode(Grid.SelectionMode.SINGLE);
        tree.addThemeVariants(new GridVariant[]{GridVariant.LUMO_NO_ROW_BORDERS});
        tree.setWidth("100%");
        tree.setHeight("100%");
        tree.getStyle().set("border", "1px var(--lumo-primary-color) solid");
        tree.getStyle().set("background", "var(--lumo-contrast-10pct)");

        tree.addSelectionListener((event) -> {
            this.selectedFile = null;
            event.getFirstSelectedItem().ifPresent((file) -> {
                this.selectedFile = file;
                this.setValue(this.selectedFile);
            });
        });
        if(fileSystem.getTreeData().getRootItems().size()>0) {
            tree.expand(fileSystem.getTreeData().getRootItems().get(0));
        }
        return tree;
    }

    private String getFileDescription(File file) {
        String desc = "";
        if (!file.isDirectory()) {
            Date date = new Date(file.lastModified());
            long size = file.length();
            String unit = "";
            if (size > 1073741824L) {
                size /= 1073741824L;
                unit = "GB";
            } else if (size > 1048576L) {
                size /= 1048576L;
                unit = "MB";
            } else if (size > 1024L) {
                size /= 1024L;
                unit = "KB";
            } else {
                unit = "B";
            }

            desc = file.getName() + ", " + date + ", " + size + " " + unit;
        } else {
            desc = this.root.getChildrenFromFilesystem(file).size() + " fichiers";
        }

        return desc;
    }

    protected void setPresentationValue(File value) {
        this.tree.select(value);
    }


    static class FileFilter implements FilenameFilter, Serializable {
        private final Pattern filter;
        private final String pattern;

        public FileFilter(String pathMatcher) {
            logger.info("FileFilter set for {}", pathMatcher);
            pattern = pathMatcher;
            this.filter = Pattern.compile(pathMatcher, Pattern.CASE_INSENSITIVE);
        }

        public String getPattern() {
            return pattern;
        }

        public boolean accept(File dir, String name) {
            Path p = dir != null ? Paths.get(dir.toPath().toString(), name) : Paths.get(name);
            //return this.filter.matches(p) || p.toFile().isDirectory();
            return this.filter.matcher(name).matches() || p.toFile().isDirectory();
        }
    }
}
