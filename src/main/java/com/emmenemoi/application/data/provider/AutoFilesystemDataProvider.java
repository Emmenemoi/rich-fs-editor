package com.emmenemoi.application.data.provider;

import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import org.vaadin.filesystemdataprovider.FilesystemData;
import org.vaadin.filesystemdataprovider.FilesystemDataProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AutoFilesystemDataProvider extends FilesystemDataProvider {
    protected final FilesystemData fsData;

    public AutoFilesystemDataProvider(FilesystemData treeData) {
        super(treeData);
        this.fsData = treeData;
        refreshAll(); // because setFilter doesn't update cached data
    }


    @Override
    public void refreshAll() {
        List<File> rootItems = this.fsData.getRootItems();
        rootItems.forEach(this::removeChildren );
        rootItems.forEach(root -> this.fetchChildren( new HierarchicalQuery<>(null, root)));
        super.refreshAll();
    }

    protected void removeChildren(File file) {
        (new ArrayList<File>(this.fsData.getChildren(file))).forEach((child) -> {
            this.fsData.removeItem(child);
        });
    }

    @Override
    public void refreshItem(File item) {
        //refreshAll();
        File parent = this.fsData.getParent(item);
        removeChildren(parent);
        this.fetchChildren( new HierarchicalQuery<>(null, parent));
        super.refreshItem(parent,true);
    }
}
