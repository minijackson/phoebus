/*
 * Copyright (C) 2024 European Spallation Source ERIC.
 */

package org.phoebus.applications.saveandrestore.ui.contextmenu;

import javafx.collections.ObservableList;
import org.phoebus.applications.saveandrestore.Messages;
import org.phoebus.applications.saveandrestore.model.Node;
import org.phoebus.applications.saveandrestore.model.NodeType;
import org.phoebus.applications.saveandrestore.ui.SaveAndRestoreController;
import org.phoebus.ui.javafx.ImageCache;

import java.util.function.Consumer;

public class NewConfigurationMenuItem extends SaveAndRestoreMenuItem {

    public NewConfigurationMenuItem(SaveAndRestoreController saveAndRestoreController,
                                    ObservableList<Node> selectedItemsProperty,
                                    Consumer onAction) {
        super(saveAndRestoreController, selectedItemsProperty, onAction);
        setText(Messages.contextMenuNewConfiguration);
        setGraphic(ImageCache.getImageView(ImageCache.class, "/icons/save-and-restore/configuration.png"));
    }

    @Override
    public void configure() {
        disableProperty().set(saveAndRestoreController.getUserIdentity().isNull().get() ||
                selectedItemsProperty.size() != 1 ||
                !selectedItemsProperty.get(0).getNodeType().equals(NodeType.FOLDER) ||
                selectedItemsProperty.get(0).getUniqueId().equals(Node.ROOT_FOLDER_UNIQUE_ID));
    }
}
