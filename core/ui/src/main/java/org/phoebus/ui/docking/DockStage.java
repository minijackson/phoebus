/*******************************************************************************
 * Copyright (c) 2017 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.phoebus.ui.docking;

import static org.phoebus.ui.docking.DockPane.logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;

/** Helper for stage that uses docking
 *
 *  <p>All stages should have a known layout with {@link DockPane} etc.,
 *  but since the initial Stage is passed to the application,
 *  it cannot simply be replaced with a "DockStage extends Stage".
 *
 *  <p>This is thus not a class that extends Stage, but a helper
 *  meant to be called to configure and later interface with
 *  each Stage.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DockStage
{
    /** Property key for the stage ID */
    public static final String KEY_ID = "ID";

    /** The KEY_ID property is a unique stage ID,
     *  except for the main window that this ID.
     */
    public static final String ID_MAIN = "DockStage_MAIN";

    /** Helper to configure a Stage for docking
     *
     *  <p>Adds a Scene with a BorderPane layout and a DockPane in the center
     *
     *  @param stage Stage, should be empty
     *  @param tabs Zero or more initial {@link DockItem}s
     *  @throws Exception on error
     *
     *  @return {@link DockPane} that was added to the {@link Stage}
     */
    public static DockPane configureStage(final Stage stage, final DockItem... tabs)
    {
        stage.getProperties().put(KEY_ID, "DockStage_" + UUID.randomUUID().toString().replace('-', '_'));

        final DockPane tab_pane = new DockPane(tabs);

        final BorderPane layout = new BorderPane(tab_pane);

        final Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Phoebus");
        try
        {
            stage.getIcons().add(new Image(DockStage.class.getResourceAsStream("/icons/logo.png")));
        }
        catch (Exception ex)
        {
            logger.log(Level.WARNING, "Cannot set application icon", ex);
        }

        // Track active pane via focus
        stage.focusedProperty().addListener((prop, old, focus) ->
        {
            if (focus)
                DockPane.setActiveDockPane(tab_pane);
        });

        stage.setOnCloseRequest(event ->
        {
            if (! isStageOkToClose(stage))
                event.consume();
        });

        return getDockPane(stage);
    }

    /** @return Unique ID of this stage */
    public static String getID(final Stage stage)
    {
        return (String) stage.getProperties().get(KEY_ID);
    }

    /** @param id Unique ID of a stage
     *  @return That Stage or <code>null</code> if not found
     */
    public static Stage getDockStageByID(final String id)
    {
        for (Window window : Window.getWindows())
            // id.equals(null) is OK, will return false
            if (id.equals(window.getProperties().get(KEY_ID)))
                return (Stage) window;
        return null;
    }

    /** @return All currently open dock stages (safe copy) */
    public static List<Stage> getDockStages()
    {
        final List<Stage> dock_windows = new ArrayList<>();
        // Having a KEY_ID property implies that the Window
        // is a Stage that was configured as a DockStage
        for (Window window : Window.getWindows())
            if (window.getProperties().containsKey(KEY_ID))
                dock_windows.add((Stage) window);

        return dock_windows;
    }

    /** Gracefully close all DockItems when stage closes
     *
     *  <p>When user closes the stage,
     *  offer dock items a chance to save changes,
     *  or abort the close request.
     *
     *  @param stage {@link Stage} with {@link DockPane}
     */
    public static boolean isStageOkToClose(final Stage stage)
    {
        final DockPane tab_pane = getDockPane(stage);
        // List of tabs changes as we close tabs; get save copy
        final List<Tab> copy = new ArrayList<>(tab_pane.getTabs());
        for (Tab tab : copy)
        {
            if (tab instanceof DockItem  &&
                ! ((DockItem) tab).close())
            {   // Abort the close request
                return false;
            }
        }

        // All tabs either saved or don't care to save,
        // so this stage will be closed
        return true;
    }

    /** @param stage Stage that supports docking
     *  @return {@link BorderPane} layout of that stage
     */
    public static BorderPane getLayout(final Stage stage)
    {
        final Parent layout = stage.getScene().getRoot();
        if (layout instanceof BorderPane)
            return (BorderPane) layout;
        throw new IllegalStateException("Expect BorderPane, got " + layout);
    }

    /** @param stage Stage that supports docking
     *  @return {@link DockPane} of that stage
     */
    public static DockPane getDockPane(final Stage stage)
    {
        final Node dock_pane = getLayout(stage).getCenter();
        if (dock_pane instanceof DockPane)
            return (DockPane) dock_pane;
        throw new IllegalStateException("Expect DockPane, got " + dock_pane);
    }

    /** @param stage Stage that supports docking which should become the active stage */
    public static void setActiveDockPane(final Stage stage)
    {
        final DockPane dock_pane = getDockPane(stage);
        DockPane.setActiveDockPane(Objects.requireNonNull(dock_pane));
    }
}
