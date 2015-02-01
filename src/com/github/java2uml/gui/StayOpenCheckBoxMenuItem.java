package com.github.java2uml.gui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Created by mac on 29.12.14.
 */
public class StayOpenCheckBoxMenuItem extends JCheckBoxMenuItem {
    private static MenuElement[] path;

    {
        getModel().addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if (getModel().isArmed() && isShowing()) {
                    path = MenuSelectionManager.defaultManager().getSelectedPath();
                }
            }
        });
    }

    /**
     * @see JCheckBoxMenuItem#JCheckBoxMenuItem()
     */
    public StayOpenCheckBoxMenuItem() {
        super();
    }

    /**
     * @see JCheckBoxMenuItem#JCheckBoxMenuItem(Action)
     */
    public StayOpenCheckBoxMenuItem(Action a) {
        super(a);
    }

    /**
     * @see JCheckBoxMenuItem#JCheckBoxMenuItem(Icon)
     */
    public StayOpenCheckBoxMenuItem(Icon icon) {
        super(icon);
    }

    /**
     * @see JCheckBoxMenuItem#JCheckBoxMenuItem(String)
     */
    public StayOpenCheckBoxMenuItem(String text) {
        super(text);
    }

    /**
     * @see JCheckBoxMenuItem#JCheckBoxMenuItem(String, boolean)
     */
    public StayOpenCheckBoxMenuItem(String text, boolean selected) {
        super(text, selected);
    }

    /**
     * @see JCheckBoxMenuItem#JCheckBoxMenuItem(String, Icon)
     */
    public StayOpenCheckBoxMenuItem(String text, Icon icon) {
        super(text, icon);
    }

    /**
     * @see JCheckBoxMenuItem#JCheckBoxMenuItem(String, Icon, boolean)
     */
    public StayOpenCheckBoxMenuItem(String text, Icon icon, boolean selected) {
        super(text, icon, selected);
    }

    /**
     * Overridden to reopen the menu.
     *
     * @param pressTime the time to "hold down" the button, in milliseconds
     */
    @Override
    public void doClick(int pressTime) {
        super.doClick(pressTime);
        MenuSelectionManager.defaultManager().setSelectedPath(path);
    }
}
