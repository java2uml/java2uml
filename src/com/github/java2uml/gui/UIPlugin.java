package com.github.java2uml.gui;


import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
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

public class UIPlugin implements ExceptionListener{
    private JPanel mainFrame;
    private JPanel panelForOptions, panelForGeneratedCode, panelForPath, panelForPathAndButtons, panelForDiagram, panelForProgressBarAndCancel, panelForClearAndCopyToClipboard, panelForSaveAndOpenDiagram;
    private JButton browse;
    private JButton generatePlantUML;
    private JButton copyToClipboard;
    private JButton saveDiagram;
    private JButton cancelLoading;
    private JButton clearCode;
    private JButton openDiagram;
    private JButton openOnPlantUMLServer;
    private JTabbedPane tabs;
    private ButtonGroup directionGroup;
    private ButtonGroup typeOfDiagramGroup;
    private ButtonGroup languageGroup;
    private ButtonGroup diagramExtensionGroup;
    private ButtonGroup parsingMethod;
//    private About about;
//    private static Help helpWindow;
//    private static HelpRu helpRu;
    private JTextArea generatedCode;
    private JProgressBar progressBar;

    private JSeparator separatorBetweenPathAndButtons, separatorBetweenButtonsAndProgressBar;
    private JFileChooser fileChooser, fileSaver;
    private File chosenDirectory;
    private BufferedImage diagram;
    private JLabel labelForDiagram;


    private JScrollPane scrollPane;
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
        return ResourceBundle.getBundle("GUILabels", Locale.getDefault());
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

    private static class UIHolder {
        static final UIPlugin UI_INSTANCE = new UIPlugin();
    }

    private UIPlugin() {
    }

    public JPanel getMainFrame() {
        return mainFrame;
    }

    public static UIPlugin getInstance() {
        return UIHolder.UI_INSTANCE;
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
        StringBuilder stringBuilder = new StringBuilder("We've got an error, breathe deeply, invisible little dwarves are trying to fix it right now... \n Error message:\n\n" + exception.getMessage());
        generatedCode.setText(stringBuilder.toString());
    }

    public JPanel initUI() {
        localeLabels = ResourceBundle.getBundle("GUILabels", Locale.getDefault());
        mainFrame = new JBPanel(new BorderLayout());
//        mainFrame = new JBPanel();
//        mainFrame.setLayout(new BoxLayout(mainFrame, BoxLayout.Y_AXIS));
        panelForOptions = new JPanel();
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
        tabs = new JBTabbedPane();
        scrollPaneForDiagram = new JBScrollPane(labelForDiagram);
        separatorBetweenPathAndButtons = new JSeparator();
        separatorBetweenButtonsAndProgressBar = new JSeparator();
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

                fileSaver.setCurrentDirectory(new File(path.getText()));
                fileSaver.setSelectedFile(new File("diagram.png"));
                fileSaver.setFileFilter(new FileNameExtensionFilter("PNG image", "png"));

                    CopyOption[] options = new CopyOption[]{
                            StandardCopyOption.REPLACE_EXISTING,
                    };
                    if (fileSaver.showSaveDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
                        File file = fileSaver.getSelectedFile();
                        try {
                                Files.copy(new File("diagram.png").toPath(), file.toPath(), options);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
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
        JLabel jLabel = null;

//            BufferedImage bufferedImage = ImageIO.read(getClass().getClassLoader().getResource("logo.png"));
//            bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.ULTRA_QUALITY, 350, 86);
        Image image = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("logo.png"));
        jLabel = new JLabel(new ImageIcon(image));
        panelForPathAndButtons.add(jLabel);
        panelForPathAndButtons.add(panelForPath);
        panelForPathAndButtons.add(separatorBetweenPathAndButtons);
        panelForPathAndButtons.add(panelForOptions);
        panelForPathAndButtons.add(separatorBetweenButtonsAndProgressBar);
        panelForPathAndButtons.add(panelForProgressBarAndCancel);

        scrollPane = new JBScrollPane(generatedCode);

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
                    final boolean isPng = true;
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {

                            String diagram = isPng ? "diagram.png" : "diagram.svg";

                            if (System.getProperty("os.name").contains("Windows")) {
                                try {
                                    if (viewerProc != null) {
                                        viewerProc.destroy();
                                        viewerProc = null;
                                    }
                                    viewerProc = Runtime.getRuntime().exec("java -jar lib/diagram_viewer.jar " + diagram);
                                } catch (IOException ioe) {
                                    ioe.printStackTrace();
                                }
                            } else {
                                try {
                                    Desktop.getDesktop().open(new File(diagram));
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    });
            }
        });
        mainFrame.add(panelForPathAndButtons, BorderLayout.NORTH);
        mainFrame.add(tabs, BorderLayout.CENTER);
        mainFrame.setSize(600, 650);
        //mainFrame.setLocationRelativeTo(null);
//        mainFrame.setResizable(false);
        return mainFrame;
    }

    public JButton getCancelLoading() {
        return cancelLoading;
    }

    public void setCancelLoading(JButton cancelLoading) {
        this.cancelLoading = cancelLoading;
    }
    public void settingLocaleLabels(ResourceBundle localeLabels) {
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

    public JButton getOpenOnPlantUMLServer() {
        return openOnPlantUMLServer;
    }

    public class ChooseFileActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            getProgressBar().setString("0%");
            getProgressBar().setValue(0);
            int resultOfChoice = fileChooser.showOpenDialog(mainFrame);
            if (resultOfChoice == JFileChooser.APPROVE_OPTION) {
                chosenDirectory = new File(fileChooser.getSelectedFile().getPath());
                path.setText(chosenDirectory.toString());
            }
        }
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
                diagram = ImageIO.read(resource);
                diagram = Scalr.resize(diagram, 500);
                labelForDiagram = new JLabel(new ImageIcon(diagram));
                panelForDiagram.removeAll();
                scrollPaneForDiagram.removeAll();
                scrollPaneForDiagram = new JBScrollPane(labelForDiagram);
                panelForDiagram.add(scrollPaneForDiagram, new GridBagConstraints(0, 0, 1, 2, 1, 7, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
                panelForDiagram.add(panelForSaveAndOpenDiagram, new GridBagConstraints(0, 2, 1, 1, 1, 0, GridBagConstraints.SOUTH, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
                tabs.removeTabAt(1);
                tabs.addTab(getLocaleLabels().getString("diagramTabLabel"), panelForDiagram);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                this.getInstance().handleExceptionAndShowDialog(throwable);
            }
        }
}
