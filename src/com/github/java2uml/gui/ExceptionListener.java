package com.github.java2uml.gui;

/**
 * Created by mac on 01.01.15.
 */
public interface ExceptionListener {
    public void handleExceptionAndShowDialog(Exception exception);

    public void handleExceptionAndDisplayItInCodeArea(Exception exception);
}
