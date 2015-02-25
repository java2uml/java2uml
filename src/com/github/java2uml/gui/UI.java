package com.github.java2uml.gui;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.imgscalr.Scalr;

import com.github.java2uml.core.Options;
import com.github.java2uml.core.reflection.UMLClassLoader;

public class UI implements ExceptionListener {
    private JFrame mainFrame;
    private JMenuBar menu;
    private JPanel panelForDiagram;
    private JPanel panelForSaveAndOpenDiagram;
    private JPanel panelForOptions;
    private JPanel optionsTypeOfParsing;
    private JPanel optionsDiagramExtension;
    private JPanel optionsTypeOfDiagram;
    private JPanel optionsOthers;
    private JPanel panelForGeneratedCode;
    private JPanel panelForClearAndCopyToClipboard;
    private JPanel panelForProgressBarAndCancel;
    private JPanel panelForPathAndButtons;
    private JPanel panelForPath;
    private ButtonGroup parsingMethod;
    private ButtonGroup directionGroup;
    private ButtonGroup typeOfDiagramGroup;
    private ButtonGroup languageGroup;
    private ButtonGroup diagramExtensionGroup;
    private JButton browse;
    private JButton generatePlantUML;
    private JButton copyToClipboard;
    private JButton saveDiagram;
    private JButton cancelLoading;
    private JButton clearCode;
    private JButton openDiagram;
    private JButton openOnPlantUMLServer;
    private JSeparator separatorBetweenPathAndButtons;
    private JSeparator separatorBetweenButtonsAndProgressBar;
    private JScrollPane scrollPane;
    private JTabbedPane tabs;
    private JMenu file, help, typeOfDiagramMenu, options, direction, diagramGeneratingMethods, whichRelationsAreShown, languageMenu, diagramExtension;
    private JMenuItem helpItem, exitItem, aboutItem, generateItem, chooseItem, quickHelpItem, packageDialogMI;
    private JRadioButton javaFilesOption, classFilesOption, pngExtensionOption, svgExtensionOption, classesDiagramOption, sequenceDiagramOption;
    private JCheckBox showLollipopsOption, showDiagramOption;
    private JCheckBoxMenuItem horizontalDirectionCheckboxItem, verticalDirectionCheckboxItem, classDiagramCheckboxItem,
            sequenceDiagramCheckboxItem, reflectionCheckboxItem, parsingCheckboxItem, showLollipops, showHeader, showAssociation,
            showComposition, showAggregation, russianLangItem, englishLangItem, svgExtensionItem, pngExtensionItem, enableDiagramItem;
    private About about;
    private QuickHelp quickHelp;
    private static Help helpWindow;
    private static HelpRu helpRu;
    private JTextArea generatedCode;
    private JProgressBar progressBar;
    private String pathOfCurrentDiagram;
    private JFileChooser fileChooser, fileSaver;

    private PackageDialog packageDialog;
    private JLabel labelForDiagram, inputFilesTypeOptionsLabel, diagramExtensionOptionsLabel, diagramTypeOptionsLabel, othersOptionsLabel;
    private JScrollPane scrollPaneForDiagram;
    private JTextField path;
    ResourceBundle localeLabels;

    // процесс просмоторщика диаграмм
    private Process viewerProc = null;

//Простейшая и надежнейшая статическая реализация singletone для экземпляра класса UI

    private UI() {
    }

    private static class UIHolder {
        static final UI UI_INSTANCE = new UI();
    }

    public static UI getInstance() {
        return UIHolder.UI_INSTANCE;
    }

    //В зависимости от выбранного языка, или географического положения пользователя, возвращает ResourceBundle со строками
    //соответствующего языка
    public ResourceBundle getLocaleLabels() {
        if (russianLangItem.getState()) {
            return ResourceBundle.getBundle("GUILabels", new Locale("ru"));
        } else if (englishLangItem.getState()) {
            return ResourceBundle.getBundle("GUILabels", new Locale(""));
        } else return ResourceBundle.getBundle("GUILabels", Locale.getDefault());
    }

    public void initLocaleBundle() {
        localeLabels = ResourceBundle.getBundle("GUILabels", Locale.getDefault());
    }

    /**Создает и располагает панель с опциями вверху окна*/
    public JPanel createAndComposeOptionsPanel() {
        inputFilesTypeOptionsLabel = new JLabel(localeLabels.getString("iWantToParseMenuLabel"));
        diagramExtensionOptionsLabel = new JLabel(localeLabels.getString("diagramExtensionLabel"));
        diagramTypeOptionsLabel = new JLabel(localeLabels.getString("typeOfDiagramMenuLabel"));
        diagramTypeOptionsLabel.setEnabled(false);
        othersOptionsLabel = new JLabel(localeLabels.getString("othersLabel"));

        ButtonGroup inputFilesOptionsButtonGroup = new ButtonGroup();
        javaFilesOption = new JRadioButton(localeLabels.getString("javaFilesMenuLabel"));

        javaFilesOption.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                parsingCheckboxItem.setState(javaFilesOption.isSelected());
            }
        });
        classFilesOption = new JRadioButton(localeLabels.getString("classFilesMenuLabel"));
        classFilesOption.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                reflectionCheckboxItem.setState(classFilesOption.isSelected());
            }
        });
        inputFilesOptionsButtonGroup.add(javaFilesOption);
        inputFilesOptionsButtonGroup.add(classFilesOption);

        ButtonGroup diagramExtensionOptionsButtonGroup = new ButtonGroup();
        pngExtensionOption = new JRadioButton(localeLabels.getString("pngExtensionLabel"));
        pngExtensionOption.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                pngExtensionItem.setState(pngExtensionOption.isSelected());
            }
        });
        svgExtensionOption = new JRadioButton(localeLabels.getString("svgExtensionLabel"));
        svgExtensionOption.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                svgExtensionItem.setState(svgExtensionOption.isSelected());
            }
        });
        diagramExtensionOptionsButtonGroup.add(pngExtensionOption);
        diagramExtensionOptionsButtonGroup.add(svgExtensionOption);

        ButtonGroup typeOfDiagramOptionsButtonGroup = new ButtonGroup();
        classesDiagramOption = new JRadioButton(localeLabels.getString("classDiagramLabel"));
        classesDiagramOption.setEnabled(false);
        classesDiagramOption.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                classDiagramCheckboxItem.setState(classFilesOption.isSelected());
            }
        });
        sequenceDiagramOption = new JRadioButton(localeLabels.getString("sequenceDiagramLabel"));
        sequenceDiagramOption.setEnabled(false);
        sequenceDiagramOption.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                sequenceDiagramCheckboxItem.setState(sequenceDiagramOption.isSelected());
            }
        });
        typeOfDiagramOptionsButtonGroup.add(classesDiagramOption);
        typeOfDiagramOptionsButtonGroup.add(sequenceDiagramOption);

        showDiagramOption = new JCheckBox(localeLabels.getString("enableDiagramLabel"));
        showDiagramOption.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                enableDiagramItem.setState(showDiagramOption.isSelected());
            }
        });
        showLollipopsOption = new JCheckBox(localeLabels.getString("showLollipopMenuLabel"));
        showLollipopsOption.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                showLollipops.setState(showLollipopsOption.isSelected());
            }
        });

        panelForOptions.setLayout(new BoxLayout(panelForOptions, BoxLayout.X_AXIS));
        panelForOptions.add(optionsTypeOfParsing);

        optionsTypeOfParsing.setLayout(new BoxLayout(optionsTypeOfParsing, BoxLayout.Y_AXIS));
        optionsTypeOfParsing.add(inputFilesTypeOptionsLabel);
        optionsTypeOfParsing.add(javaFilesOption);
        optionsTypeOfParsing.add(classFilesOption);

        panelForOptions.add(new JSeparator(SwingConstants.VERTICAL));

        panelForOptions.add(optionsDiagramExtension);
        optionsDiagramExtension.setLayout(new BoxLayout(optionsDiagramExtension, BoxLayout.Y_AXIS));
        optionsDiagramExtension.add(diagramExtensionOptionsLabel);
        optionsDiagramExtension.add(pngExtensionOption);
        optionsDiagramExtension.add(svgExtensionOption);

        panelForOptions.add(new JSeparator(SwingConstants.VERTICAL));

        panelForOptions.add(optionsTypeOfDiagram);
        optionsTypeOfDiagram.setLayout(new BoxLayout(optionsTypeOfDiagram, BoxLayout.Y_AXIS));
        optionsTypeOfDiagram.add(diagramTypeOptionsLabel);
        optionsTypeOfDiagram.add(classesDiagramOption);
        optionsTypeOfDiagram.add(sequenceDiagramOption);

        panelForOptions.add(new JSeparator(SwingConstants.VERTICAL));

        panelForOptions.add(optionsOthers);
        optionsOthers.setLayout(new BoxLayout(optionsOthers, BoxLayout.Y_AXIS));
        optionsOthers.add(othersOptionsLabel);
        optionsOthers.add(showDiagramOption);
        optionsOthers.add(showLollipopsOption);

        panelForOptions.setBorder(new EmptyBorder(0, 3, 0, 1));
        return panelForOptions;
    }

    /**Устанавливает для всех опций состояния "по умолчанию" */
    public void settingActualStatesForAllOptions() {

        classDiagramCheckboxItem.setState(true);
        verticalDirectionCheckboxItem.setState(true);
        classDiagramCheckboxItem.setEnabled(false);
        openOnPlantUMLServer.setEnabled(false);
        sequenceDiagramCheckboxItem.setEnabled(false);
        showHeader.setEnabled(false);
        classDiagramCheckboxItem.setState(true);
        reflectionCheckboxItem.setState(true);
        showAggregation.setState(true);
        showAssociation.setState(true);
        showComposition.setState(true);
        showLollipops.setState(true);
        enableDiagramItem.setState(true);
        pngExtensionItem.setState(true);

    }

    /**Срабатывает когда пользователь переключает язык - меняет надписи для всех элементов ГУИ, которые поддаются локализации */
    public void settingLocaleLabels(ResourceBundle localeLabels) {
        file.setText(localeLabels.getString("fileMenuLabel"));
        help.setText(localeLabels.getString("helpMenuLabel"));
        typeOfDiagramMenu.setText(localeLabels.getString("typeOfDiagramMenuLabel"));
        languageMenu.setText(localeLabels.getString("languageMenu"));
        options.setText(localeLabels.getString("optionsMenuLabel"));
        direction.setText(localeLabels.getString("directionWillBeMenuLabel"));
        diagramGeneratingMethods.setText(localeLabels.getString("iWantToParseMenuLabel"));
        whichRelationsAreShown.setText(localeLabels.getString("relationsMenuLabel"));
        englishLangItem.setText(localeLabels.getString("englishLanguage"));
        russianLangItem.setText(localeLabels.getString("russianLanguage"));

        showAssociation.setText(localeLabels.getString("associationMenuLabel"));
        showAggregation.setText(localeLabels.getString("aggregationMenuLabel"));
        showComposition.setText(localeLabels.getString("compositionMenuLabel"));
        showHeader.setText(localeLabels.getString("chooseHeaderMenuLabel"));
        showLollipops.setText(localeLabels.getString("showLollipopMenuLabel"));
        quickHelpItem.setText(localeLabels.getString("quickHelp"));
        helpItem.setText(localeLabels.getString("helpMenuLabel"));
        exitItem.setText(localeLabels.getString("exitMenuLabel"));
        aboutItem.setText(localeLabels.getString("aboutMenuLabel"));
        generateItem.setText(localeLabels.getString("generateLabel"));
        chooseItem.setText(localeLabels.getString("chooseDirLabel"));
        reflectionCheckboxItem.setText(localeLabels.getString("classFilesMenuLabel"));
        parsingCheckboxItem.setText(localeLabels.getString("javaFilesMenuLabel"));
        horizontalDirectionCheckboxItem.setText(localeLabels.getString("directionHorizontalLabel"));
        verticalDirectionCheckboxItem.setText(localeLabels.getString("directionVerticalLabel"));
        classDiagramCheckboxItem.setText(localeLabels.getString("classDiagramLabel"));
        sequenceDiagramCheckboxItem.setText(localeLabels.getString("sequenceDiagramLabel"));
        diagramExtension.setText(localeLabels.getString("diagramExtensionLabel"));
        pngExtensionItem.setText(localeLabels.getString("pngExtensionLabel"));
        svgExtensionItem.setText(localeLabels.getString("svgExtensionLabel"));
        enableDiagramItem.setText(localeLabels.getString("enableDiagramLabel"));
        packageDialogMI.setText(localeLabels.getString("packageTreeMenuLabel"));
        inputFilesTypeOptionsLabel.setText(localeLabels.getString("iWantToParseMenuLabel"));
        diagramExtensionOptionsLabel.setText(localeLabels.getString("diagramExtensionLabel"));
        diagramTypeOptionsLabel.setText(localeLabels.getString("typeOfDiagramMenuLabel"));
        othersOptionsLabel.setText(localeLabels.getString("othersLabel"));
        javaFilesOption.setText(localeLabels.getString("javaFilesMenuLabel"));
        classFilesOption.setText(localeLabels.getString("classFilesMenuLabel"));
        pngExtensionOption.setText(localeLabels.getString("pngExtensionLabel"));
        svgExtensionOption.setText(localeLabels.getString("svgExtensionLabel"));
        classesDiagramOption.setText(localeLabels.getString("classDiagramLabel"));
        sequenceDiagramOption.setText(localeLabels.getString("sequenceDiagramLabel"));
        showDiagramOption.setText(localeLabels.getString("enableDiagramLabel"));
        showLollipopsOption.setText(localeLabels.getString("showLollipopMenuLabel"));

        browse.setText(localeLabels.getString("chooseDirLabel"));
        saveDiagram.setText(localeLabels.getString("saveMenuLabel"));
        openDiagram.setText(localeLabels.getString("openDiagramLabel"));
        generatePlantUML.setText(localeLabels.getString("generateLabel"));
        cancelLoading.setText(localeLabels.getString("cancelLabel"));
        clearCode.setText(localeLabels.getString("clearLabel"));

        copyToClipboard.setText(localeLabels.getString("copyToClipboardLabel"));
        openOnPlantUMLServer.setText(localeLabels.getString("showOnPlantUMLSite"));

        tabs.setTitleAt(0, localeLabels.getString("plantUMLTabLabel"));
        tabs.setTitleAt(1, localeLabels.getString("diagramTabLabel"));


    }

    /**Инициализирует объекты меню и устанавливает им надписи из ResourceBundle соответствующие дефолтному языку */
    protected void createMenuObjects() {
        menu = new JMenuBar();
        file = new JMenu(localeLabels.getString("fileMenuLabel"));
        help = new JMenu(localeLabels.getString("helpMenuLabel"));
        typeOfDiagramMenu = new JMenu(localeLabels.getString("typeOfDiagramMenuLabel"));
        languageMenu = new JMenu(localeLabels.getString("languageMenu"));
        options = new JMenu(localeLabels.getString("optionsMenuLabel"));
        direction = new JMenu(localeLabels.getString("directionWillBeMenuLabel"));
        diagramGeneratingMethods = new JMenu(localeLabels.getString("iWantToParseMenuLabel"));
        whichRelationsAreShown = new JMenu(localeLabels.getString("relationsMenuLabel"));
        diagramExtension = new JMenu(localeLabels.getString("diagramExtensionLabel"));
        pngExtensionItem = new StayOpenCheckBoxMenuItem(localeLabels.getString("pngExtensionLabel"));
        svgExtensionItem = new StayOpenCheckBoxMenuItem(localeLabels.getString("svgExtensionLabel"));
        enableDiagramItem = new StayOpenCheckBoxMenuItem(localeLabels.getString("enableDiagramLabel"));
        englishLangItem = new StayOpenCheckBoxMenuItem(localeLabels.getString("englishLanguage"));
        russianLangItem = new StayOpenCheckBoxMenuItem(localeLabels.getString("russianLanguage"));
        showAssociation = new StayOpenCheckBoxMenuItem(localeLabels.getString("associationMenuLabel"));
        showAssociation.setState(true);
        showAggregation = new StayOpenCheckBoxMenuItem(localeLabels.getString("aggregationMenuLabel"));
        showComposition = new StayOpenCheckBoxMenuItem(localeLabels.getString("compositionMenuLabel"));
        showHeader = new StayOpenCheckBoxMenuItem(localeLabels.getString("chooseHeaderMenuLabel"));
        showLollipops = new StayOpenCheckBoxMenuItem(localeLabels.getString("showLollipopMenuLabel"));
        helpItem = new JMenuItem(localeLabels.getString("helpMenuLabel"));
        quickHelpItem = new JMenuItem(localeLabels.getString("quickHelp"));
        exitItem = new JMenuItem(localeLabels.getString("exitMenuLabel"));
        aboutItem = new JMenuItem(localeLabels.getString("aboutMenuLabel"));
        generateItem = new JMenuItem(localeLabels.getString("generateLabel"));
        chooseItem = new JMenuItem(localeLabels.getString("chooseDirLabel"));
        packageDialogMI = new JMenuItem(localeLabels.getString("packageTreeMenuLabel"));
        parsingMethod = new ButtonGroup();
        directionGroup = new ButtonGroup();
        typeOfDiagramGroup = new ButtonGroup();
        languageGroup = new ButtonGroup();
        diagramExtensionGroup = new ButtonGroup();
        reflectionCheckboxItem = new StayOpenCheckBoxMenuItem(localeLabels.getString("classFilesMenuLabel"));
        parsingCheckboxItem = new StayOpenCheckBoxMenuItem(localeLabels.getString("javaFilesMenuLabel"));
        horizontalDirectionCheckboxItem = new StayOpenCheckBoxMenuItem(localeLabels.getString("directionHorizontalLabel"));
        verticalDirectionCheckboxItem = new StayOpenCheckBoxMenuItem(localeLabels.getString("directionVerticalLabel"));
        classDiagramCheckboxItem = new StayOpenCheckBoxMenuItem(localeLabels.getString("classDiagramLabel"));
        sequenceDiagramCheckboxItem = new StayOpenCheckBoxMenuItem(localeLabels.getString("sequenceDiagramLabel"));
    }

    protected void addActionListenersToMenu() {
        pngExtensionItem.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                pngExtensionOption.setSelected(pngExtensionItem.isSelected());
            }
        });

        svgExtensionItem.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                svgExtensionOption.setSelected(svgExtensionItem.isSelected());
            }
        });

        enableDiagramItem.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                showDiagramOption.setSelected(enableDiagramItem.isSelected());
            }
        });

        showLollipops.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                showLollipopsOption.setSelected(showLollipops.isSelected());
            }
        });


        reflectionCheckboxItem.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                classFilesOption.setSelected(reflectionCheckboxItem.isSelected());
            }
        });


        parsingCheckboxItem.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                javaFilesOption.setSelected(parsingCheckboxItem.isSelected());
            }
        });

        packageDialogMI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (reflectionCheckboxItem.getState() && Options.isClassesLoaded()) {
                    // классы загружены - выводим дерево
                    packageDialog.showDialog();
                } else if (parsingCheckboxItem.getState()) {
                    // вывод дерева без загрузки классов
                    packageDialog.showDialog();
                }
            }
        });


        quickHelpItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (quickHelp != null) {
                    if (!quickHelp.isVisible()) {
                        quickHelp.setVisible(true);
                    } else {
                        quickHelp.toFront();
                        quickHelp.repaint();
                    }
                } else {
                    quickHelp = QuickHelp.getInstance();
                    quickHelp.setVisible(true);
                }
            }
        });

        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (about == null) {
                    about = new About("Java2UML About");
                    about.setVisible(true);
                } else {
                    about.setVisible(true);
                    about.repaint();
                }
            }
        });
        classDiagramCheckboxItem.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                classesDiagramOption.setSelected(classDiagramCheckboxItem.isSelected());
            }
        });


        sequenceDiagramCheckboxItem.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                sequenceDiagramOption.setSelected(sequenceDiagramCheckboxItem.isSelected());
            }
        });
        chooseItem.addActionListener(new ChooseFileActionListener());
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        englishLangItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                settingLocaleLabels(ResourceBundle.getBundle("GUILabels", new Locale("")));
            }
        });

        russianLangItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                settingLocaleLabels(ResourceBundle.getBundle("GUILabels", new Locale("ru")));
            }
        });

        helpItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (englishLangItem.getState()) {
                    if (!Help.helpIsNull()) {
                        if (!helpWindow.isVisible()) {
                            helpWindow.setVisible(true);
                        } else {
                            helpWindow.toFront();
                            helpWindow.repaint();
                        }
                    } else helpWindow = Help.getInstance();
                } else {
                    if (!HelpRu.helpIsNull()) {
                        if (!helpRu.isVisible()) {
                            helpRu.setVisible(true);
                        } else {
                            helpRu.toFront();
                            helpRu.repaint();
                        }
                    } else helpRu = HelpRu.getInstance();
                }
            }
        });
    }

    /**Расставляет менюшки внутрь друг друга и в правильном порядке */
    protected void composeMenu() {
        file.add(chooseItem);
        file.add(generateItem);
        file.add(exitItem);

        parsingMethod.add(reflectionCheckboxItem);
        parsingMethod.add(parsingCheckboxItem);

        languageGroup.add(englishLangItem);
        languageGroup.add(russianLangItem);

        directionGroup.add(horizontalDirectionCheckboxItem);
        directionGroup.add(verticalDirectionCheckboxItem);

        typeOfDiagramGroup.add(classDiagramCheckboxItem);
        typeOfDiagramGroup.add(sequenceDiagramCheckboxItem);

        whichRelationsAreShown.add(showAggregation);
        whichRelationsAreShown.add(showAssociation);
        whichRelationsAreShown.add(showComposition);

        languageMenu.add(englishLangItem);
        languageMenu.add(russianLangItem);

        diagramExtension.add(pngExtensionItem);
        diagramExtension.add(svgExtensionItem);

        diagramExtensionGroup.add(pngExtensionItem);
        diagramExtensionGroup.add(svgExtensionItem);


        options.add(diagramGeneratingMethods);
        options.add(diagramExtension);
        options.add(languageMenu);
        options.add(direction);
        options.add(whichRelationsAreShown);

        diagramGeneratingMethods.add(parsingCheckboxItem);
        diagramGeneratingMethods.add(reflectionCheckboxItem);

        direction.add(horizontalDirectionCheckboxItem);
        direction.add(verticalDirectionCheckboxItem);

        options.add(typeOfDiagramMenu);

        typeOfDiagramMenu.add(classDiagramCheckboxItem);
        typeOfDiagramMenu.add(sequenceDiagramCheckboxItem);

        options.add(packageDialogMI);
        options.add(showHeader);
        options.add(showLollipops);
        options.add(enableDiagramItem);

        help.add(quickHelpItem);
        help.add(helpItem);
        help.add(aboutItem);

        menu.add(file);
        menu.add(options);
        menu.add(help);
    }

    /**Инициализирует все объекты во фрейме (кроме меню), и присваивает им локализованные надписи */
    protected void createAllUIObjects() {
        mainFrame = new JFrame(localeLabels.getString("titleLabel"));
        panelForOptions = new JPanel();
        optionsDiagramExtension = new JPanel();
        optionsTypeOfParsing = new JPanel();
        optionsTypeOfDiagram = new JPanel();
        optionsOthers = new JPanel();
        panelForGeneratedCode = new JPanel();
        panelForDiagram = new JPanel();
        panelForClearAndCopyToClipboard = new JPanel();
        panelForProgressBarAndCancel = new JPanel();
        panelForSaveAndOpenDiagram = new JPanel();
        browse = new JButton(localeLabels.getString("chooseDirLabel"));
        saveDiagram = new JButton(localeLabels.getString("saveMenuLabel"));
        generatePlantUML = new JButton(localeLabels.getString("generateLabel"));
        cancelLoading = new JButton(localeLabels.getString("cancelLabel"));
        labelForDiagram = new JLabel();
        clearCode = new JButton(localeLabels.getString("clearLabel"));
        openOnPlantUMLServer = new JButton(localeLabels.getString("showOnPlantUMLSite"));
        openDiagram = new JButton(localeLabels.getString("openDiagramLabel"));
        copyToClipboard = new JButton(localeLabels.getString("copyToClipboardLabel"));
        generatedCode = new JTextArea();
        path = new JTextField();
        panelForPath = new JPanel();
        panelForPathAndButtons = new JPanel();
        progressBar = new JProgressBar();
        progressBar.setBorder(new EmptyBorder(0, 3, 0, 3));
        tabs = new JTabbedPane();
        scrollPaneForDiagram = new JScrollPane(labelForDiagram);
        separatorBetweenPathAndButtons = new JSeparator();
        separatorBetweenButtonsAndProgressBar = new JSeparator();
        fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Java archive (.jar)", "jar"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Java project directory", "."));
        fileSaver = new JFileChooser();
        scrollPane = new JScrollPane(generatedCode);

        packageDialog = new PackageDialog(mainFrame);

        generatedCode.setEditable(false);

        progressBar.setStringPainted(true);
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
    }

    /**Устанавливает ActionListener-ы для всех кнопок во врейме */
    protected void addActionListenersToButtons() {
        saveDiagram.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (enableDiagramItem.getState()) {
                    fileSaver.setCurrentDirectory(new File(path.getText()));
                    if (pngExtensionItem.getState()) {
                        fileSaver.setSelectedFile(new File("diagram.png"));
                        fileSaver.setFileFilter(new FileNameExtensionFilter("PNG image", "png"));
                    } else {
                        fileSaver.setSelectedFile(new File("diagram.svg"));
                        fileSaver.setFileFilter(new FileNameExtensionFilter("SVG image", "svg"));
                    }

                    CopyOption[] options = new CopyOption[]{
                            StandardCopyOption.REPLACE_EXISTING,
                    };
                    if (fileSaver.showSaveDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
                        File file = fileSaver.getSelectedFile();
                        try {
                            if (pngExtensionItem.getState()) {
                                Files.copy(new File("diagram.png").toPath(), file.toPath(), options);
                            } else {
                                Files.copy(new File("diagram.svg").toPath(), file.toPath(), options);
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(mainFrame, getLocaleLabels().getString("youMustGenerateDiagramFirst"), "Java2UML message", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        clearCode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getGeneratedCode().setText("");
            }
        });
        browse.addActionListener(new ChooseFileActionListener());
        copyToClipboard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generatedCode.setFocusable(true);
                generatedCode.selectAll();

                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(new StringSelection(getGeneratedCode().getText()), null);
            }
        });
        openDiagram.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (enableDiagramItem.getState()) {
                    final boolean isPng = getPngExtensionItem().getState();
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            if (System.getProperty("os.name").contains("Windows")) {
                                try {
                                    if (viewerProc != null) {
                                        viewerProc.destroy();
                                        viewerProc = null;
                                    }
                                    viewerProc = Runtime.getRuntime().exec("java -jar lib/diagram_viewer.jar " + pathOfCurrentDiagram);
                                } catch (IOException ioe) {
                                    ioe.printStackTrace();
                                }
                            } else {
                                try {
                                    Desktop.getDesktop().open(new File(pathOfCurrentDiagram));
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    });
                } else {
                    JOptionPane.showMessageDialog(mainFrame, getLocaleLabels().getString("youMustGenerateDiagramFirst"), "Java2UML message", JOptionPane.INFORMATION_MESSAGE);
                }

            }
        });
    }

    protected void settingParametersToUIObjects() {
        mainFrame.setJMenuBar(menu);
        generatedCode.setEditable(false);
        progressBar.setStringPainted(true);
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        scrollPaneForDiagram.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneForDiagram.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        tabs.addTab(localeLabels.getString("plantUMLTabLabel"), panelForGeneratedCode);
        tabs.addTab(localeLabels.getString("diagramTabLabel"), panelForDiagram);
        panelForDiagram.setLayout(new GridBagLayout());
        panelForProgressBarAndCancel.setLayout(new BoxLayout(panelForProgressBarAndCancel, BoxLayout.X_AXIS));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        panelForPath.setLayout(new GridBagLayout());
        panelForPathAndButtons.setLayout(new BoxLayout(panelForPathAndButtons, BoxLayout.Y_AXIS));
        panelForPathAndButtons.setBorder(new EmptyBorder(3, 1, 3, 1));
        generatedCode.setLineWrap(true);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panelForGeneratedCode.setLayout(new GridBagLayout());
        panelForClearAndCopyToClipboard.setLayout(new BoxLayout(panelForClearAndCopyToClipboard, BoxLayout.X_AXIS));
        panelForGeneratedCode.setBorder(new EmptyBorder(0, 5, 5, 5));
        panelForSaveAndOpenDiagram.setLayout(new BoxLayout(panelForSaveAndOpenDiagram, BoxLayout.X_AXIS));
    }

    /**Располагает все объекты во фрейме */
    protected JFrame composeObjectsInFrame() {
        panelForPath.add(browse, new GridBagConstraints(0, 0, 1, 1, 0, 0.5, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        panelForPath.add(path, new GridBagConstraints(1, 0, 5, 1, 30, 0.5, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 3), 0, 0));
        panelForProgressBarAndCancel.add(cancelLoading);
        panelForProgressBarAndCancel.add(progressBar);
        panelForProgressBarAndCancel.add(generatePlantUML);
        Image image = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("logo.png"));
        JLabel jLabel = new JLabel(new ImageIcon(image));
        panelForPathAndButtons.add(jLabel);
        panelForPathAndButtons.add(panelForPath);
        panelForPathAndButtons.add(separatorBetweenPathAndButtons);
        panelForPathAndButtons.add(createAndComposeOptionsPanel());
        panelForPathAndButtons.add(separatorBetweenButtonsAndProgressBar);
        panelForPathAndButtons.add(panelForProgressBarAndCancel);

        panelForGeneratedCode.add(openOnPlantUMLServer, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        panelForGeneratedCode.add(scrollPane, new GridBagConstraints(0, 1, 1, 3, 1, 5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));

        panelForClearAndCopyToClipboard.add(clearCode);
        panelForClearAndCopyToClipboard.add(copyToClipboard);
        panelForGeneratedCode.add(panelForClearAndCopyToClipboard, new GridBagConstraints(0, 5, 1, 1, 1, 0, GridBagConstraints.SOUTH, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

        panelForSaveAndOpenDiagram.add(saveDiagram);
        panelForSaveAndOpenDiagram.add(openDiagram);

        mainFrame.add(BorderLayout.CENTER, tabs);
        mainFrame.add(BorderLayout.NORTH, panelForPathAndButtons);

        mainFrame.setSize(600, 650);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);

        return mainFrame;
    }

    public int validateProgressBarTo(int progress) {
        int value = progress * 20;
        int maximum = progressBar.getMaximum();
        if (value > maximum) value = maximum;
        progressBar.setValue(value);
        return value;
    }

    public void setProgressBarComplete() {
        progressBar.setValue(100);
    }

    /**Отображает уменьшенную копию диаграммы в окне предпросмотра*/
    public void showDiagramInReducedSize(URL resource) {
        try {
            BufferedImage diagram = ImageIO.read(resource);
            diagram = Scalr.resize(diagram, 500);
            labelForDiagram = new JLabel(new ImageIcon(diagram));
            panelForDiagram.removeAll();
            scrollPaneForDiagram.removeAll();
            scrollPaneForDiagram = new JScrollPane(labelForDiagram);
            panelForDiagram.add(scrollPaneForDiagram, new GridBagConstraints(0, 0, 1, 2, 1, 7, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
            panelForDiagram.add(panelForSaveAndOpenDiagram, new GridBagConstraints(0, 2, 1, 1, 1, 0, GridBagConstraints.SOUTH, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
            tabs.removeTabAt(1);
            tabs.addTab(getLocaleLabels().getString("diagramTabLabel"), panelForDiagram);

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            this.handleExceptionAndShowDialog(throwable);
        }
    }

    @Override
    public void handleExceptionAndShowDialog(Throwable throwable) {
        JOptionPane.showMessageDialog(mainFrame, throwable.getMessage(), "It's an error, breathe deeply", JOptionPane.ERROR_MESSAGE);
        labelForDiagram.setIcon(null);
        generatePlantUML.setEnabled(true);
        progressBar.setString("0%");
        progressBar.setValue(0);
    }

    @Override
    public void handleExceptionAndDisplayItInCodeArea(Exception exception) {
        generatedCode.setText("We've got an error, breathe deeply, invisible little dwarves are trying to fix it right now... \n Error message:\n\n" + exception.getMessage());
    }

    public class ChooseFileActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (new File(path.getText()).exists() && !path.getText().equals("")) {
                fileChooser.setCurrentDirectory(new File(path.getText()));
            }
            getProgressBar().setString("0%");
            getProgressBar().setValue(0);
            int resultOfChoice = fileChooser.showOpenDialog(mainFrame);
            if (resultOfChoice == JFileChooser.APPROVE_OPTION) {
                File chosenDirectory = new File(fileChooser.getSelectedFile()
                        .getPath());
                path.setText(chosenDirectory.toString());
                if (reflectionCheckboxItem.getState()) {
                    // стираем данные по предыдущим классам и пакетам
                    Options.clearClassesAndPackages();

                    // загрузка классов из выбранного пути
                    UMLClassLoader ecl = new UMLClassLoader();
                    try {
                        Options.setClasses(ecl.loadClasses(path.getText()));
                        Options.setPath(path.getText());
                        packageDialog.initDialog();
                    } catch(IOException ioe) {
                        ioe.printStackTrace();
                    } catch(ClassNotFoundException cnfe) {
                        cnfe.printStackTrace();
                    }
                } else if (parsingCheckboxItem.getState()) {
                	Options.clearClassesAndPackages();
                	Options.setPath(path.getText());
                }
                
            }
        }
    }

    public JCheckBoxMenuItem getEnglishLangItem() {
        return englishLangItem;
    }

    public JCheckBoxMenuItem getRussianLangItem() {
        return russianLangItem;
    }

    public JButton getCancelLoading() {
        return cancelLoading;
    }

    public JCheckBoxMenuItem getEnableDiagramItem() {
        return enableDiagramItem;
    }

    public JCheckBoxMenuItem getPngExtensionItem() {
        return pngExtensionItem;
    }

    public JCheckBoxMenuItem getSvgExtensionItem() {
        return svgExtensionItem;
    }

    public JTextArea getGeneratedCode() {
        return generatedCode;
    }

    public JTextField getPath() {
        return path;
    }

    public JButton getGeneratePlantUML() {
        return generatePlantUML;
    }


    public JFrame getMainFrame() {
        return mainFrame;
    }

    public JCheckBoxMenuItem getHorizontalDirectionCheckboxItem() {
        return horizontalDirectionCheckboxItem;
    }

    public JCheckBoxMenuItem getVerticalDirectionCheckboxItem() {
        return verticalDirectionCheckboxItem;
    }

    public JCheckBoxMenuItem getClassDiagramCheckboxItem() {
        return classDiagramCheckboxItem;
    }

    public JCheckBoxMenuItem getSequenceDiagramCheckboxItem() {
        return sequenceDiagramCheckboxItem;
    }

    public JCheckBoxMenuItem getReflectionCheckboxItem() {
        return reflectionCheckboxItem;
    }

    public JCheckBoxMenuItem getParsingCheckboxItem() {
        return parsingCheckboxItem;
    }

    public JCheckBoxMenuItem getShowLollipops() {
        return showLollipops;
    }

    public JCheckBoxMenuItem getShowHeader() {
        return showHeader;
    }

    public JCheckBoxMenuItem getShowAssociation() {
        return showAssociation;
    }

    public JCheckBoxMenuItem getShowComposition() {
        return showComposition;
    }

    public JCheckBoxMenuItem getShowAggregation() {
        return showAggregation;
    }

    public JMenuItem getGenerateItem() {
        return generateItem;
    }

    public JLabel getLabelForDiagram() {
        return labelForDiagram;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public String getPathOfCurrentDiagram() {
        return pathOfCurrentDiagram;
    }

    public JButton getOpenOnPlantUMLServer() {
        return openOnPlantUMLServer;
    }

    public void setCancelLoading(JButton cancelLoading) {
        this.cancelLoading = cancelLoading;
    }

    public void setGenerateItem(JMenuItem generateItem) {
        this.generateItem = generateItem;
    }

    public void setPathOfCurrentDiagram(String fileName) {
        this.pathOfCurrentDiagram = fileName;
    }


}