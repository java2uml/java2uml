package com.github.java2uml.gui;

import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
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

public class UI implements ExceptionListener {
    private JFrame mainFrame;

    private JPanel panelForDiagram;
    private JPanel panelForSaveAndOpenDiagram;
    private JButton browse;
    private JButton generatePlantUML;
    private JButton copyToClipboard;
    private JButton saveDiagram;
    private JButton cancelLoading;
    private JButton clearCode;
    private JButton openDiagram;
    private JButton openOnPlantUMLServer;
    private JTabbedPane tabs;
    private JMenu file, help, typeOfDiagramMenu, options, direction, diagramGeneratingMethods, whichRelationsAreShown, languageMenu, diagramExtension;
    private JMenuItem helpItem, exitItem, aboutItem, generateItem, chooseItem, quickHelpItem;
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
    private JLabel labelForDiagram;


    private JScrollPane scrollPaneForDiagram;

    private JTextField path;

    ResourceBundle localeLabels;

    public static final String VERTICAL_DIRECTION = "Vertical";
    public static final String HORIZONTAL_DIRECTION = "Horizontal";
    public static final String CLASS_DIAGRAM = "Class Dia";
    public static final String SEQUENCE_DIAGRAM = "Sequence Dia";

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public ResourceBundle getLocaleLabels() {
        if (russianLangItem.getState()) {
            return ResourceBundle.getBundle("GUILabels", new Locale("ru"));
        } else if (englishLangItem.getState()) {
            return ResourceBundle.getBundle("GUILabels", new Locale(""));
        } else return ResourceBundle.getBundle("GUILabels", Locale.getDefault());
    }

    private static class UIHolder {
        static final UI UI_INSTANCE = new UI();
    }

    private UI() {

    }

    public static UI getInstance() {
        return UIHolder.UI_INSTANCE;
    }

    public JCheckBoxMenuItem getEnglishLangItem() {
        return englishLangItem;
    }

    public JCheckBoxMenuItem getRussianLangItem() {
        return russianLangItem;
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

    public void setGenerateItem(JMenuItem generateItem) {
        this.generateItem = generateItem;
    }

    public String getPathOfCurrentDiagram() {
        return pathOfCurrentDiagram;
    }

    public void setPathOfCurrentDiagram(String fileName) {
        this.pathOfCurrentDiagram = fileName;
    }


    public void settingStateForAllOptions() {

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

    public JButton getCancelLoading() {
        return cancelLoading;
    }

    public void setCancelLoading(JButton cancelLoading) {
        this.cancelLoading = cancelLoading;
    }

    public JCheckBoxMenuItem getEnableDiagramItem() {
        return enableDiagramItem;
    }

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

    private JMenuBar initMenu() {
        JMenuBar menu = new JMenuBar();

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
        ButtonGroup parsingMethod = new ButtonGroup();
        ButtonGroup directionGroup = new ButtonGroup();
        ButtonGroup typeOfDiagramGroup = new ButtonGroup();
        ButtonGroup languageGroup = new ButtonGroup();
        ButtonGroup diagramExtensionGroup = new ButtonGroup();

        reflectionCheckboxItem = new StayOpenCheckBoxMenuItem(localeLabels.getString("classFilesMenuLabel"));
        parsingCheckboxItem = new StayOpenCheckBoxMenuItem(localeLabels.getString("javaFilesMenuLabel"));
        parsingMethod.add(reflectionCheckboxItem);
        parsingMethod.add(parsingCheckboxItem);

        languageGroup.add(englishLangItem);
        languageGroup.add(russianLangItem);

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

        horizontalDirectionCheckboxItem = new StayOpenCheckBoxMenuItem(localeLabels.getString("directionHorizontalLabel"));
        verticalDirectionCheckboxItem = new StayOpenCheckBoxMenuItem(localeLabels.getString("directionVerticalLabel"));

        directionGroup.add(horizontalDirectionCheckboxItem);
        directionGroup.add(verticalDirectionCheckboxItem);

        classDiagramCheckboxItem = new StayOpenCheckBoxMenuItem(localeLabels.getString("classDiagramLabel"));
        sequenceDiagramCheckboxItem = new StayOpenCheckBoxMenuItem(localeLabels.getString("sequenceDiagramLabel"));
        typeOfDiagramGroup.add(classDiagramCheckboxItem);
        typeOfDiagramGroup.add(sequenceDiagramCheckboxItem);

        chooseItem.addActionListener(new ChooseFileActionListener());
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        file.add(chooseItem);
        file.add(generateItem);
        file.add(exitItem);

        whichRelationsAreShown.add(showAggregation);
        whichRelationsAreShown.add(showAssociation);
        whichRelationsAreShown.add(showComposition);

        languageMenu.add(englishLangItem);
        languageMenu.add(russianLangItem);

        diagramExtension.add(pngExtensionItem);
        diagramExtension.add(svgExtensionItem);

        diagramExtensionGroup.add(pngExtensionItem);
        diagramExtensionGroup.add(svgExtensionItem);

        options.add(languageMenu);
        options.add(direction);
        options.add(diagramGeneratingMethods);
        options.add(whichRelationsAreShown);

        diagramGeneratingMethods.add(parsingCheckboxItem);
        diagramGeneratingMethods.add(reflectionCheckboxItem);

        direction.add(horizontalDirectionCheckboxItem);
        direction.add(verticalDirectionCheckboxItem);

        options.add(typeOfDiagramMenu);

        typeOfDiagramMenu.add(classDiagramCheckboxItem);
        typeOfDiagramMenu.add(sequenceDiagramCheckboxItem);

        options.add(diagramExtension);
        options.add(showHeader);
        options.add(showLollipops);
        options.add(enableDiagramItem);

        help.add(quickHelpItem);
        help.add(helpItem);
        help.add(aboutItem);

        menu.add(file);
        menu.add(options);
        menu.add(help);

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
        return menu;
    }

    public JButton getOpenOnPlantUMLServer() {
        return openOnPlantUMLServer;
    }

    public JFrame initUI() {
        localeLabels = ResourceBundle.getBundle("GUILabels", Locale.getDefault());
        mainFrame = new JFrame(localeLabels.getString("titleLabel"));
        JPanel panelForOptions = new JPanel();
        JPanel panelForGeneratedCode = new JPanel();
        panelForDiagram = new JPanel();
        JPanel panelForClearAndCopyToClipboard = new JPanel();
        JPanel panelForProgressBarAndCancel = new JPanel();
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
        JPanel panelForPath = new JPanel();
        JPanel panelForPathAndButtons = new JPanel();
        progressBar = new JProgressBar();
        progressBar.setBorder(new EmptyBorder(0, 3, 0, 3));
        tabs = new JTabbedPane();
        scrollPaneForDiagram = new JScrollPane(labelForDiagram);
        JSeparator separatorBetweenPathAndButtons = new JSeparator();
        JSeparator separatorBetweenButtonsAndProgressBar = new JSeparator();
        fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Java archive (.jar)", "jar"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Java project directory", "."));
        fileSaver = new JFileChooser();

        generatedCode.setEditable(false);

        progressBar.setStringPainted(true);
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
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
        scrollPaneForDiagram.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneForDiagram.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        tabs.addTab(localeLabels.getString("plantUMLTabLabel"), panelForGeneratedCode);
        tabs.addTab(localeLabels.getString("diagramTabLabel"), panelForDiagram);
        panelForDiagram.setLayout(new GridBagLayout());
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        panelForPath.setLayout(new GridBagLayout());
        panelForPath.add(browse, new GridBagConstraints(0, 0, 1, 1, 0, 0.5, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        panelForPath.add(path, new GridBagConstraints(1, 0, 5, 1, 30, 0.5, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 3), 0, 0));
        panelForOptions.setLayout(new BoxLayout(panelForOptions, BoxLayout.X_AXIS));
        panelForProgressBarAndCancel.setLayout(new BoxLayout(panelForProgressBarAndCancel, BoxLayout.X_AXIS));
        panelForProgressBarAndCancel.add(cancelLoading);
        panelForProgressBarAndCancel.add(progressBar);
        panelForProgressBarAndCancel.add(generatePlantUML);
        panelForPathAndButtons.setLayout(new BoxLayout(panelForPathAndButtons, BoxLayout.Y_AXIS));
        panelForPathAndButtons.setBorder(new EmptyBorder(3, 1, 3, 1));
        Image image = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("logo.png"));
        JLabel jLabel = new JLabel(new ImageIcon(image));
        panelForPathAndButtons.add(jLabel);
        panelForPathAndButtons.add(panelForPath);
        panelForPathAndButtons.add(separatorBetweenPathAndButtons);
        panelForPathAndButtons.add(panelForOptions);
        panelForPathAndButtons.add(separatorBetweenButtonsAndProgressBar);
        panelForPathAndButtons.add(panelForProgressBarAndCancel);

        JScrollPane scrollPane = new JScrollPane(generatedCode);

        generatedCode.setLineWrap(true);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        panelForGeneratedCode.setLayout(new GridBagLayout());
        panelForGeneratedCode.add(openOnPlantUMLServer, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        panelForGeneratedCode.add(scrollPane, new GridBagConstraints(0, 1, 1, 3, 1, 5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));

        panelForClearAndCopyToClipboard.setLayout(new BoxLayout(panelForClearAndCopyToClipboard, BoxLayout.X_AXIS));
        panelForClearAndCopyToClipboard.add(clearCode);
        panelForClearAndCopyToClipboard.add(copyToClipboard);
        panelForGeneratedCode.add(panelForClearAndCopyToClipboard, new GridBagConstraints(0, 5, 1, 1, 1, 0, GridBagConstraints.SOUTH, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        panelForGeneratedCode.setBorder(new EmptyBorder(0, 5, 5, 5));

        panelForSaveAndOpenDiagram.setLayout(new BoxLayout(panelForSaveAndOpenDiagram, BoxLayout.X_AXIS));
        panelForSaveAndOpenDiagram.add(saveDiagram);
        panelForSaveAndOpenDiagram.add(openDiagram);

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

        mainFrame.setJMenuBar(initMenu());
        mainFrame.add(tabs);
        mainFrame.add(BorderLayout.NORTH, panelForPathAndButtons);
        mainFrame.setSize(600, 650);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        mainFrame.setResizable(false);
        return mainFrame;
    }

    // процесс просмоторщика диаграмм
    private Process viewerProc = null;


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

    public JLabel getLabelForDiagram() {
        return labelForDiagram;
    }

    public void showDiagram(URL resource) {
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
                File chosenDirectory = new File(fileChooser.getSelectedFile().getPath());
                path.setText(chosenDirectory.toString());
            }
        }
    }

}
