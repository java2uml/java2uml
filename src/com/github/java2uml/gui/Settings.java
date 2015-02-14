package com.github.java2uml.gui;

import com.github.java2uml.plugin.idea.PluginSettings;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBTabbedPane;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Андрей on 03.02.2015.
 */
public class Settings extends JFrame{
    private static Settings settings;

    private javax.swing.JPanel diagramSettingsPanel;
    private javax.swing.JCheckBox aggregationCheckBox;
    private javax.swing.JCheckBox associationCheckBox;
    private javax.swing.JCheckBox compositionCheckBox;
    private javax.swing.JCheckBox showLollipopCheckBox;
    private javax.swing.JCheckBox enableDiagramCheckBox;
    private javax.swing.JComboBox languageComboBox;
    private javax.swing.JComboBox diagramGeneratingMethodsComboBox;
    private javax.swing.JComboBox directionComboBox;
    private javax.swing.JComboBox diagramExtensionComboBox;
    private javax.swing.JLabel languageLabel;
    private javax.swing.JLabel diagramGeneratingMethodsLabel;
    private javax.swing.JLabel directionLabel;
    private javax.swing.JLabel diagramExtensionLabel;
    private javax.swing.JLabel relationsLabel;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel mainSettingsPanel;

    ResourceBundle localeLabels;

    public ResourceBundle getLocaleLabels() {
        if (PluginSettings.getSettings().get("language") == 1) {
            return ResourceBundle.getBundle("GUILabels", new Locale("ru"));
        } else if (PluginSettings.getSettings().get("language") == 0) {
            return ResourceBundle.getBundle("GUILabels", new Locale(""));
        } else return ResourceBundle.getBundle("GUILabels", Locale.getDefault());

    }

    private Settings() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(480, 380);
        setResizable(false);
        setVisible(true);
    }

    private void initComponents() {
        if (PluginSettings.getSettings() != null){
            localeLabels = PluginSettings.getSettings().get("language") == PluginSettings.RUSSIAN ?
                    ResourceBundle.getBundle("GUILabels", new Locale("ru")):
                    ResourceBundle.getBundle("GUILabels", new Locale(""));
        } else {
            localeLabels = ResourceBundle.getBundle("GUILabels", Locale.getDefault());
        }
        jTabbedPane1 = new JBTabbedPane();
        mainSettingsPanel = new JPanel();
        languageLabel = new JLabel();
        languageComboBox = new ComboBox();
        diagramGeneratingMethodsLabel = new JLabel();
        diagramGeneratingMethodsComboBox = new ComboBox();
        diagramSettingsPanel = new JPanel();
        directionLabel = new JLabel();
        directionComboBox = new ComboBox();
        diagramExtensionLabel = new JLabel();
        diagramExtensionComboBox = new ComboBox();
        relationsLabel = new JLabel();
        aggregationCheckBox = new JCheckBox();
        jSeparator1 = new JSeparator();
        associationCheckBox = new JCheckBox();
        compositionCheckBox = new JCheckBox();
        jSeparator2 = new JSeparator();
        showLollipopCheckBox = new JCheckBox();
        enableDiagramCheckBox = new JCheckBox();

        setBackground(new java.awt.Color(204, 204, 204));

        mainSettingsPanel.setName("mainSettingsPanel"); // NOI18N

        languageLabel.setText(localeLabels.getString("languageMenu"));

        languageComboBox.setModel(new DefaultComboBoxModel(new String[]{localeLabels.getString("englishLanguage"),
                localeLabels.getString("russianLanguage")}));
        languageComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                languageComboBoxActionPerformed(evt);
            }
        });
        if (PluginSettings.getSettings().get("language") == PluginSettings.RUSSIAN){
            languageComboBox.setSelectedIndex(1);
        }
        diagramGeneratingMethodsLabel.setText(localeLabels.getString("iWantToParseMenuLabel"));

        diagramGeneratingMethodsComboBox.setModel(new DefaultComboBoxModel(new String[]{localeLabels.getString("javaFilesMenuLabel"),
                localeLabels.getString("classFilesMenuLabel")}));
        diagramGeneratingMethodsComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                diagramGeneratingMethodsComboBoxActionPerformed(evt);
            }
        });
        if (PluginSettings.getSettings().get("parseType") == PluginSettings.REFLECTION){
            diagramGeneratingMethodsComboBox.setSelectedIndex(1);
        }
        GroupLayout mainSettingsPanelLayout = new GroupLayout(mainSettingsPanel);
        mainSettingsPanel.setLayout(mainSettingsPanelLayout);
        mainSettingsPanelLayout.setHorizontalGroup(
                mainSettingsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(mainSettingsPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(mainSettingsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(languageLabel)
                                        .addComponent(diagramGeneratingMethodsLabel))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                                .addGroup(mainSettingsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(languageComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(diagramGeneratingMethodsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
        );
        mainSettingsPanelLayout.setVerticalGroup(
                mainSettingsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(mainSettingsPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(mainSettingsPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(languageLabel, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(languageComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(mainSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(diagramGeneratingMethodsLabel, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(diagramGeneratingMethodsComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Основные", mainSettingsPanel);

        diagramSettingsPanel.setPreferredSize(new java.awt.Dimension(324, 300));

        directionLabel.setText(localeLabels.getString("directionWillBeMenuLabel"));

        directionComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{localeLabels.getString("directionHorizontalLabel"),
                localeLabels.getString("directionVerticalLabel")}));
        directionComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                directionComboBoxActionPerformed(evt);
            }
        });
        if (PluginSettings.getSettings().get("direction") == PluginSettings.VERTICAL){
            directionComboBox.setSelectedIndex(1);
        }

        diagramExtensionLabel.setText(localeLabels.getString("diagramExtensionLabel"));

        diagramExtensionComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{localeLabels.getString("pngExtensionLabel"),
                localeLabels.getString("svgExtensionLabel")}));
        diagramExtensionComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                diagramExtensionComboBoxActionPerformed(evt);
            }
        });
        if (PluginSettings.getSettings().get("diagramExtension") == PluginSettings.PNG){
            diagramExtensionComboBox.setSelectedIndex(0);
        }

        relationsLabel.setText(localeLabels.getString("relationsMenuLabel"));

        aggregationCheckBox.setText(localeLabels.getString("aggregationMenuLabel"));
        aggregationCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aggregationCheckBoxActionPerformed(evt);
            }
        });
        if (PluginSettings.getSettings().get("aggregation") == 1){
            aggregationCheckBox.setSelected(true);
        }
        associationCheckBox.setText(localeLabels.getString("associationMenuLabel"));
        associationCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                associationCheckBoxActionPerformed(evt);
            }
        });
        if (PluginSettings.getSettings().get("association") == 1){
            associationCheckBox.setSelected(true);
        }
        compositionCheckBox.setText(localeLabels.getString("compositionMenuLabel"));
        compositionCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                compositionCheckBoxActionPerformed(evt);
            }
        });
        if (PluginSettings.getSettings().get("composition") == 1){
            compositionCheckBox.setSelected(true);
        }
        showLollipopCheckBox.setText(localeLabels.getString("showLollipopMenuLabel"));
        showLollipopCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showLollipopCheckBoxActionPerformed(evt);
            }
        });
        if (PluginSettings.getSettings().get("lollipop") == 1){
            showLollipopCheckBox.setSelected(true);
        }
        enableDiagramCheckBox.setText(localeLabels.getString("enableDiagramLabel"));
        enableDiagramCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enableDiagramCheckBoxActionPerformed(evt);
            }
        });
        if (PluginSettings.getSettings().get("showDiagram") == 1){
            enableDiagramCheckBox.setSelected(true);
        }

        javax.swing.GroupLayout diagramSettingsPanelLayout = new javax.swing.GroupLayout(diagramSettingsPanel);
        diagramSettingsPanel.setLayout(diagramSettingsPanelLayout);
        diagramSettingsPanelLayout.setHorizontalGroup(
                diagramSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(diagramSettingsPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(diagramSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(GroupLayout.Alignment.TRAILING, diagramSettingsPanelLayout.createSequentialGroup()
                                                .addGroup(diagramSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(GroupLayout.Alignment.LEADING, diagramSettingsPanelLayout.createSequentialGroup()
                                                                .addComponent(directionLabel)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(directionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(GroupLayout.Alignment.LEADING, diagramSettingsPanelLayout.createSequentialGroup()
                                                                .addComponent(diagramExtensionLabel)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(diagramExtensionComboBox, GroupLayout.PREFERRED_SIZE, 147, GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(jSeparator1))
                                                .addContainerGap())
                                        .addGroup(diagramSettingsPanelLayout.createSequentialGroup()
                                                .addGroup(diagramSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(enableDiagramCheckBox)
                                                        .addComponent(showLollipopCheckBox)
                                                        .addComponent(compositionCheckBox)
                                                        .addComponent(associationCheckBox)
                                                        .addComponent(aggregationCheckBox)
                                                        .addComponent(relationsLabel))
                                                .addGap(0, 0, Short.MAX_VALUE))))
        );
        diagramSettingsPanelLayout.setVerticalGroup(
                diagramSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(diagramSettingsPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(diagramSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(directionLabel, GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(directionComboBox, GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(diagramSettingsPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(diagramExtensionLabel, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(diagramExtensionComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(relationsLabel, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(aggregationCheckBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(associationCheckBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(compositionCheckBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(showLollipopCheckBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(enableDiagramCheckBox)
                                .addContainerGap(37, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Диаграмма", diagramSettingsPanel);
        jTabbedPane1.getAccessibleContext().setAccessibleName("Основные");
        add(jTabbedPane1);
    }

    private void enableDiagramCheckBoxActionPerformed(ActionEvent evt) {
        JCheckBox box = (JCheckBox)evt.getSource();
        if (box.isSelected()){
            PluginSettings.setSettings("showDiagram", 1);
        } else{
            PluginSettings.setSettings("showDiagram", 0);
        }
    }

    private void showLollipopCheckBoxActionPerformed(ActionEvent evt) {
        JCheckBox box = (JCheckBox)evt.getSource();
        if (box.isSelected()){
            PluginSettings.setSettings("lollipop", 1);
        } else{
            PluginSettings.setSettings("lollipop", 0);
        }
    }

    private void compositionCheckBoxActionPerformed(ActionEvent evt) {
        JCheckBox box = (JCheckBox)evt.getSource();
        if (box.isSelected()){
            PluginSettings.setSettings("composition", 1);
        } else{
            PluginSettings.setSettings("composition", 0);
        }
    }

    private void associationCheckBoxActionPerformed(ActionEvent evt) {
        JCheckBox box = (JCheckBox)evt.getSource();
        if (box.isSelected()){
            PluginSettings.setSettings("association", 1);
        } else{
            PluginSettings.setSettings("association", 0);
        }
    }

    private void aggregationCheckBoxActionPerformed(ActionEvent evt) {
        JCheckBox box = (JCheckBox)evt.getSource();
        if (box.isSelected()){
            PluginSettings.setSettings("aggregation", 1);
        } else{
            PluginSettings.setSettings("aggregation", 0);
        }
    }

    private void diagramExtensionComboBoxActionPerformed(ActionEvent evt) {
        JComboBox box = (JComboBox)evt.getSource();
        String item = (String)box.getSelectedItem();
        if (item.equals(localeLabels.getString("pngExtensionLabel"))){
            PluginSettings.setSettings("diagramExtension", PluginSettings.PNG);
        } else{
            PluginSettings.setSettings("diagramExtension", PluginSettings.SVG);
        }
    }

    private void directionComboBoxActionPerformed(ActionEvent evt) {
        JComboBox box = (JComboBox)evt.getSource();
        String item = (String)box.getSelectedItem();
        if (item.equals(localeLabels.getString("directionVerticalLabel"))){
            PluginSettings.setSettings("direction", PluginSettings.VERTICAL);
        } else{
            PluginSettings.setSettings("direction", PluginSettings.HORIZONTAL);
        }
    }

    private void diagramGeneratingMethodsComboBoxActionPerformed(ActionEvent evt) {
        JComboBox box = (JComboBox)evt.getSource();
        String item = (String)box.getSelectedItem();
        if (item.equals(localeLabels.getString("javaFilesMenuLabel"))){
            PluginSettings.setSettings("parseType", PluginSettings.PARSING);
        } else{
            PluginSettings.setSettings("parseType", PluginSettings.REFLECTION);
        }
    }

    private void languageComboBoxActionPerformed(ActionEvent evt) {
        JComboBox box = (JComboBox)evt.getSource();
        String item = (String)box.getSelectedItem();
        if (item.equals(localeLabels.getString("russianLanguage"))){
            PluginSettings.setSettings("language", PluginSettings.RUSSIAN);
            settingLocaleLabels(ResourceBundle.getBundle("GUILabels", new Locale("ru")));
        } else{
            PluginSettings.setSettings("language", PluginSettings.ENGLISH);
            settingLocaleLabels(ResourceBundle.getBundle("GUILabels", new Locale("")));
        }
    }

    public void settingLocaleLabels(ResourceBundle local) {
        languageLabel.setText(local.getString("languageMenu"));
        languageComboBox.setModel(new DefaultComboBoxModel(new String[]{local.getString("englishLanguage"),
                local.getString("russianLanguage")}));
        if (PluginSettings.getSettings().get("language") == PluginSettings.RUSSIAN){
            languageComboBox.setSelectedIndex(1);
        }
        diagramGeneratingMethodsLabel.setText(local.getString("iWantToParseMenuLabel"));
        diagramGeneratingMethodsComboBox.setModel(new DefaultComboBoxModel(new String[]{local.getString("javaFilesMenuLabel"),
                local.getString("classFilesMenuLabel")}));
        if (PluginSettings.getSettings().get("parseType") == PluginSettings.REFLECTION){
            diagramGeneratingMethodsComboBox.setSelectedIndex(1);
        }
        directionLabel.setText(local.getString("directionWillBeMenuLabel"));
        directionComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{local.getString("directionHorizontalLabel"),
                local.getString("directionVerticalLabel")}));
        if (PluginSettings.getSettings().get("direction") == PluginSettings.VERTICAL){
            directionComboBox.setSelectedIndex(1);
        }
        diagramExtensionLabel.setText(local.getString("diagramExtensionLabel"));
        diagramExtensionComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{local.getString("pngExtensionLabel"),
                local.getString("svgExtensionLabel")}));
        if (PluginSettings.getSettings().get("diagramExtension") == PluginSettings.PNG){
            diagramExtensionComboBox.setSelectedIndex(0);
        }
        relationsLabel.setText(local.getString("relationsMenuLabel"));
        aggregationCheckBox.setText(local.getString("aggregationMenuLabel"));
        associationCheckBox.setText(local.getString("associationMenuLabel"));
        compositionCheckBox.setText(local.getString("compositionMenuLabel"));
        showLollipopCheckBox.setText(local.getString("showLollipopMenuLabel"));
        enableDiagramCheckBox.setText(local.getString("enableDiagramLabel"));
        if (UIPluginEntry.getUi() != null){
            UIPluginEntry.getUi().settingLocaleLabels(local);
        }
    }

    public static boolean settingsAreNull(){
        return settings == null;
    }

    public static Settings getInstance(){
        if (settings == null) {
            settings = new Settings();
        }
        return settings;
    }

    //here will be action listeners
    //для корректной работы класса необходим инициализированный объект UIPluginEntry

}
