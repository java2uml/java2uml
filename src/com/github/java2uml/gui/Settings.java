package com.github.java2uml.gui;

import com.github.java2uml.plugin.idea.PluginSettings;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBTabbedPane;

import javax.swing.*;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Андрей on 03.02.2015.
 */
public class Settings extends JFrame{
    private static Settings settings;

    private javax.swing.JPanel diagramSettingsPanel;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JComboBox languageComboBox;
    private javax.swing.JComboBox diagramGeneratingMethodsComboBox;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JComboBox jComboBox4;
    private javax.swing.JLabel languageLabel;
    private javax.swing.JLabel diagramGeneratingMethodsLabel;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
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
        localeLabels = ResourceBundle.getBundle("GUILabels", Locale.getDefault());
        jTabbedPane1 = new JBTabbedPane();
        mainSettingsPanel = new JPanel();
        languageLabel = new JLabel();
        languageComboBox = new ComboBox();
        diagramGeneratingMethodsLabel = new JLabel();
        diagramGeneratingMethodsComboBox = new ComboBox();
        diagramSettingsPanel = new JPanel();
        jLabel3 = new JLabel();
        jComboBox3 = new ComboBox();
        jLabel4 = new JLabel();
        jComboBox4 = new ComboBox();
        jLabel5 = new JLabel();
        jCheckBox1 = new JCheckBox();
        jSeparator1 = new JSeparator();
        jCheckBox2 = new JCheckBox();
        jCheckBox3 = new JCheckBox();
        jSeparator2 = new JSeparator();
        jCheckBox4 = new JCheckBox();
        jCheckBox5 = new JCheckBox();

        setBackground(new java.awt.Color(204, 204, 204));

        mainSettingsPanel.setName("mainSettingsPanel"); // NOI18N

        languageLabel.setText(localeLabels.getString("languageMenu"));

        languageComboBox.setModel(new DefaultComboBoxModel(new String[]{localeLabels.getString("englishLanguage"),
                localeLabels.getString("russianLanguage")}));

        diagramGeneratingMethodsLabel.setText(localeLabels.getString("iWantToParseMenuLabel"));

        diagramGeneratingMethodsComboBox.setModel(new DefaultComboBoxModel(new String[]{localeLabels.getString("javaFilesMenuLabel"), localeLabels.getString("classFilesMenuLabel")}));

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

        jLabel3.setText(localeLabels.getString("directionWillBeMenuLabel"));

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[]{localeLabels.getString("directionHorizontalLabel"),
                localeLabels.getString("directionVerticalLabel")}));

        jLabel4.setText(localeLabels.getString("diagramExtensionLabel"));

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel(new String[]{localeLabels.getString("pngExtensionLabel"),
                localeLabels.getString("svgExtensionLabel")}));

        jLabel5.setText(localeLabels.getString("relationsMenuLabel"));

        jCheckBox1.setText(localeLabels.getString("aggregationMenuLabel"));

        jCheckBox2.setText(localeLabels.getString("associationMenuLabel"));

        jCheckBox3.setText(localeLabels.getString("compositionMenuLabel"));

        jCheckBox4.setText(localeLabels.getString("showLollipopMenuLabel"));

        jCheckBox5.setText(localeLabels.getString("enableDiagramLabel"));


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
                                                                .addComponent(jLabel3)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(GroupLayout.Alignment.LEADING, diagramSettingsPanelLayout.createSequentialGroup()
                                                                .addComponent(jLabel4)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(jComboBox4, GroupLayout.PREFERRED_SIZE, 147, GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(jSeparator1))
                                                .addContainerGap())
                                        .addGroup(diagramSettingsPanelLayout.createSequentialGroup()
                                                .addGroup(diagramSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jCheckBox5)
                                                        .addComponent(jCheckBox4)
                                                        .addComponent(jCheckBox3)
                                                        .addComponent(jCheckBox2)
                                                        .addComponent(jCheckBox1)
                                                        .addComponent(jLabel5))
                                                .addGap(0, 0, Short.MAX_VALUE))))
        );
        diagramSettingsPanelLayout.setVerticalGroup(
                diagramSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(diagramSettingsPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(diagramSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel3, GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jComboBox3, GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(diagramSettingsPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel4, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jComboBox4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jCheckBox4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox5)
                                .addContainerGap(37, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Диаграмма", diagramSettingsPanel);

//        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
//        this.setLayout(layout);
//        layout.setHorizontalGroup(
//                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//                        .addGroup(layout.createSequentialGroup()
//                                .addGap(35, 35, 35)
//                                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE)
//                                .addContainerGap(236, Short.MAX_VALUE))
//        );
//        layout.setVerticalGroup(
//                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//                        .addGroup(layout.createSequentialGroup()
//                                .addGap(41, 41, 41)
//                                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
//                                .addContainerGap(259, Short.MAX_VALUE))
//        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("Основные");
        add(jTabbedPane1);
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

}
