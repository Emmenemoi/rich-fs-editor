package com.emmenemoi.application.components;

import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.function.SerializableComparator;
import com.vaadin.flow.function.ValueProvider;
import org.slf4j.LoggerFactory;
import org.vaadin.filesystemdataprovider.FileSelect;

class Tree<T> extends TreeGrid<T> {

    public Column<T> addHierarchyColumn(ValueProvider<T, ?> valueProvider, ValueProvider<T, ?> iconProvider, ValueProvider<T, ?> titleProvider) {
        Column<T> column = addColumn(TemplateRenderer
                .<T> of("<vaadin-grid-tree-toggle title='[[item.title]]'"
                        + "leaf='[[item.leaf]]' expanded='{{expanded}}' level='[[level]]'>"
                        + "<vaadin-icon icon='vaadin:[[item.icon]]' style='padding-right: 5px'></vaadin-icon>"
                        + "[[item.name]]"
                        + "</vaadin-grid-tree-toggle>")
                .withProperty("leaf",
                        item -> !getDataCommunicator().hasChildren(item))
                .withProperty("title", item -> String.valueOf(titleProvider.apply(item)))
                .withProperty("icon", item -> fixIconName(String.valueOf(iconProvider.apply(item))))
                .withProperty("name", item -> String.valueOf(valueProvider.apply(item))));
        final SerializableComparator<T> comparator =
                (a, b) -> compareMaybeComparables(valueProvider.apply(a),
                        valueProvider.apply(b));
        column.setComparator(comparator);

        return column;
    }

    private String fixIconName(String name) {
        String trimmed;
        trimmed = name.toLowerCase();
        trimmed = trimmed.replace("_", "-");
        //LoggerFactory.getLogger(Tree.class).info("icon: {}", trimmed);
        return trimmed;
    }
}
