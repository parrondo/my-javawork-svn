/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import javax.swing.*;

/**
 * Provides access to various user interface parts
 */
public interface IUserInterface {
    /**
     * Returns tab by unique key which is used by strategies
     * to display some information and is located in the bottom
     * pane with tabs
     *
     * @param key unique key which is mapped to a specific bottom tab
     * @return bottom tab which is mapped to unique key
     */
    public JPanel getBottomTab(String key);

    /**
     * Removes tab by unique key which is used by strategies
     * to display some information and is located in the bottom
     * pane with tabs. If there is no tab mapped to such key no
     * action is taken place.
     *
     * @param key unique key which is mapped to a specific bottom tab
     */
    public void removeBottomTab(String key);


    /**
     * Returns tab by unique key which is used by strategies
     * to display some information and is located in the main
     * panel among charts and strategy source files
     *
     * @param key unique key which is mapped to a specific main tab
     * @return main tab which is mapped to unique key
     */
    public JPanel getMainTab(String key);

    /**
     * Removes tab by unique key which is used by strategies
     * to display some information and is located in the
     * main panel among charts and strategy source files.
     * If there is no tab mapped to such key no action is
     * taken place. 
     *
     * @param key unique key which is mapped to a specific main tab
     */
    public void removeMainTab(String key);

    /**
     * Returns main frame. Can be used as parent for dialogs to show them on the same screen
     * 
     * @return main JFrame
     */
    public JFrame getMainFrame();
}
