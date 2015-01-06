package com.github.java2uml.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class UI implements ExceptionListener {
    private JFrame mainFrame;
    private JPanel panelForButtons, panelForGeneratedCode, panelForPath, panelForPathAndButtons, panelForDiagram, panelForProgressBarAndCancel, panelForClearAndCopyToClipboard;
    private JButton browse, generatePlantUML, copyToClipboard, saveDiagram, cancelLoading, clearCode;
    private JTabbedPane tabs;
    private JMenuBar menu;
    private JMenu file, help, typeOfDiagramMenu, options, direction, diagramGeneratingMethods, whichRelationsAreShown;
    private JMenuItem helpItem, exitItem, aboutItem, generateItem, chooseItem, saveItem;
    JCheckBoxMenuItem horizontalDirectionCheckboxItem, verticalDirectionCheckboxItem, classDiagramCheckboxItem,
            sequenceDiagramCheckboxItem, reflectionCheckboxItem, parsingCheckboxItem, showLollipops, showHeader, showAssociation,
    showComposition, showAggregation;
    ButtonGroup directionGroup;
    ButtonGroup typeOfDiagramGroup;

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(JProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    ButtonGroup parsingMethod;

    private JScrollPane scrollPane, scrollPaneForDiagram;

    private JTextField path;

    public JTextArea getGeneratedCode() {
        return generatedCode;
    }



    private JTextArea generatedCode;
    private JProgressBar progressBar;
    private JSeparator separatorBetweenPathAndButtons, separatorBetweenButtonsAndProgressBar;
    private JFileChooser fileChooser;
    private File chosenDirectory;
    private BufferedImage diagram;
    private JLabel labelForDiagram;

    public static final String VERTICAL_DIRECTION = "Vertical";
    public static final String HORIZONTAL_DIRECTION = "Horizontal";
    public static final String CLASS_DIAGRAM = "Class Dia";
    public static final String SEQUENCE_DIAGRAM = "Sequence Dia";



    private static Help helpWindow;

    public JTextField getPath() {
        return path;
    }

    public JButton getGeneratePlantUML() {
        return generatePlantUML;
    }

    private static class UIHolder {
        static final UI UI_INSTANCE = new UI();
    }

    private UI(){

    }

    public static UI getInstance(){
        return UIHolder.UI_INSTANCE;
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

    private JMenuBar initMenu(){
        menu = new JMenuBar();

        file = new JMenu("File");
        help = new JMenu("Help");
        typeOfDiagramMenu = new JMenu("Type will be...");

        options = new JMenu("Options");
        direction = new JMenu("Direction will be...");
        diagramGeneratingMethods = new JMenu("I want to parse...");
        whichRelationsAreShown = new JMenu("Relations to be shown...");

        showAssociation = new StayOpenCheckBoxMenuItem("Association");
        showAssociation.setState(true);
        showAggregation = new StayOpenCheckBoxMenuItem("Aggregation");
        showComposition = new StayOpenCheckBoxMenuItem("Composition");

        showHeader = new StayOpenCheckBoxMenuItem("Show header");
        showLollipops = new StayOpenCheckBoxMenuItem("Show lollipop interfaces");

        helpItem = new JMenuItem("Help");
        exitItem = new JMenuItem("Exit");

        aboutItem = new JMenuItem("About");
        generateItem = new JMenuItem("Generate");
        chooseItem = new JMenuItem("Choose dir");

        parsingMethod = new ButtonGroup();
        directionGroup = new ButtonGroup();
        typeOfDiagramGroup = new ButtonGroup();

        reflectionCheckboxItem = new StayOpenCheckBoxMenuItem(".class files");
        parsingCheckboxItem = new StayOpenCheckBoxMenuItem(".java files");
        parsingMethod.add(reflectionCheckboxItem);
        parsingMethod.add(parsingCheckboxItem);


        horizontalDirectionCheckboxItem = new StayOpenCheckBoxMenuItem("Horizontal");

        verticalDirectionCheckboxItem = new StayOpenCheckBoxMenuItem("Vertical");

        directionGroup.add(horizontalDirectionCheckboxItem);
        directionGroup.add(verticalDirectionCheckboxItem);

        classDiagramCheckboxItem = new StayOpenCheckBoxMenuItem("Class");
        sequenceDiagramCheckboxItem = new StayOpenCheckBoxMenuItem("Sequence");
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
        options.add(showHeader);
        options.add(showLollipops);

        help.add(helpItem);
        help.add(aboutItem);

        menu.add(file);
        menu.add(options);
        menu.add(help);

        helpItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (!Help.helpIsNull()) {
                    if (!helpWindow.isVisible()) {
                        helpWindow.setVisible(true);
                    } else {
                        helpWindow.toFront();
                        helpWindow.repaint();
                    }
                } else helpWindow = Help.getInstance();
            }
        });


        return menu;
    }

    public void settingStateForAllOptions(){

        classDiagramCheckboxItem.setState(true);
        verticalDirectionCheckboxItem.setState(true);
        classDiagramCheckboxItem.setEnabled(false);
        sequenceDiagramCheckboxItem.setEnabled(false);
        classDiagramCheckboxItem.setState(true);
        reflectionCheckboxItem.setState(true);
        showAggregation.setState(true);
        showAssociation.setState(true);
        showComposition.setState(true);
        showLollipops.setState(true);





    }

    public JButton getCancelLoading() {
        return cancelLoading;
    }

    public void setCancelLoading(JButton cancelLoading) {
        this.cancelLoading = cancelLoading;
    }

    public JFrame initUI() {
        mainFrame = new JFrame("Java2UML");
        panelForButtons = new JPanel();
        panelForGeneratedCode = new JPanel();
        panelForDiagram = new JPanel();
        panelForClearAndCopyToClipboard = new JPanel();
        panelForProgressBarAndCancel = new JPanel();
        browse = new JButton("Select files");
        saveDiagram = new JButton("Save image as");
        generatePlantUML = new JButton("Generate");
        cancelLoading = new JButton("Cancel");
        labelForDiagram = new JLabel();
        clearCode = new JButton("Clear");

        copyToClipboard = new JButton("Copy to clipboard");

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

        progressBar.setStringPainted(true);
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);


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
                Clipboard clipboard = Toolkit.getDefaultToolkit ().getSystemClipboard ();
                clipboard.setContents (new StringSelection(getGeneratedCode().getText()), null);
            }
        });

        scrollPaneForDiagram.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneForDiagram.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        tabs.addTab("PlantUML code", panelForGeneratedCode);
        tabs.addTab("Diagram", panelForDiagram);
        path.setToolTipText("Enter path here");

        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);


        panelForPath.setLayout(new GridBagLayout());
        panelForPath.add(browse, new GridBagConstraints(0,0,1,1,0,0.5,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0),0,0));
        panelForPath.add(path, new GridBagConstraints(20,0,4,1,15,0.5,GridBagConstraints.EAST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,3),0,0));



        panelForButtons.setLayout(new BoxLayout(panelForButtons, BoxLayout.X_AXIS));

        panelForProgressBarAndCancel.setLayout(new BoxLayout(panelForProgressBarAndCancel, BoxLayout.X_AXIS));


        panelForProgressBarAndCancel.add(generatePlantUML);
        panelForProgressBarAndCancel.add(progressBar);
        panelForProgressBarAndCancel.add(cancelLoading);

        panelForPathAndButtons.setLayout(new BoxLayout(panelForPathAndButtons, BoxLayout.Y_AXIS));
        panelForPathAndButtons.setBorder(new EmptyBorder(3, 1, 3, 1));
        panelForPathAndButtons.add(panelForPath);
        panelForPathAndButtons.add(separatorBetweenPathAndButtons);
        panelForPathAndButtons.add(panelForButtons);
        panelForPathAndButtons.add(separatorBetweenButtonsAndProgressBar);
        panelForPathAndButtons.add(panelForProgressBarAndCancel);




        scrollPane = new JScrollPane(generatedCode);

        generatedCode.setLineWrap(true);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        panelForGeneratedCode.setLayout(new GridBagLayout());
        panelForGeneratedCode.add(scrollPane, new GridBagConstraints(0, 0, 1, 2, 1, 7, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));

        panelForClearAndCopyToClipboard.setLayout(new BoxLayout(panelForClearAndCopyToClipboard, BoxLayout.X_AXIS));
        panelForClearAndCopyToClipboard.add(clearCode);
        panelForClearAndCopyToClipboard.add(copyToClipboard);
        panelForGeneratedCode.add(panelForClearAndCopyToClipboard, new GridBagConstraints(0, 2, 1, 1, 1, 0, GridBagConstraints.SOUTH, GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));
        panelForGeneratedCode.setBorder(new EmptyBorder(0, 5, 5, 5));

        mainFrame.setJMenuBar(initMenu());
        mainFrame.add(tabs);
        mainFrame.add(BorderLayout.NORTH, panelForPathAndButtons);
        mainFrame.setSize(600, 600);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        return mainFrame;
    }


    public int increaseProgressBarForTwenty(){
        int value = progressBar.getValue() + 20;
        int maximum = progressBar.getMaximum();
        if (value > maximum) value = maximum;
        progressBar.setValue(value);

        return value;
    }

    public void setProgressBarComplete(){
        progressBar.setValue(100);
    }

    public JLabel getLabelForDiagram() {
        return labelForDiagram;
    }

    public void setLabelForDiagram(JLabel labelForDiagram) {
        this.labelForDiagram = labelForDiagram;
    }

    public void showDiagram(String diagramName){
        try {
            diagram = ImageIO.read(new File(diagramName));
//            System.out.println(diagram.getWidth() + "" + "" + diagram.getHeight());
//
            labelForDiagram = new JLabel(new ImageIcon(diagram));

            panelForDiagram.removeAll();
            scrollPaneForDiagram.removeAll();
            scrollPaneForDiagram = new JScrollPane(labelForDiagram);
            panelForDiagram.add(scrollPaneForDiagram);

            tabs.removeTabAt(1);


            tabs.addTab("Diagram", scrollPaneForDiagram);



        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void handleExceptionAndShowDialog(Exception exception) {

        JOptionPane.showMessageDialog(mainFrame, exception.getMessage(), "It's an error, breathe deeply", JOptionPane.ERROR_MESSAGE);
    }


    @Override
    public void handleExceptionAndDisplayItInCodeArea(Exception exception) {
        StringBuilder stringBuilder = new StringBuilder("We've got an error, breathe deeply, invisible little dwarves are trying to fix it right now... \n Error message:\n\n" + exception.getMessage());
        generatedCode.setText(stringBuilder.toString());
    }

    public class ChooseFileActionListener implements ActionListener {


        @Override
        public void actionPerformed(ActionEvent e) {

            getProgressBar().setString("0%");
            getProgressBar().setValue(0);

            int resultOfChoice = fileChooser.showOpenDialog(mainFrame);
            if (resultOfChoice == JFileChooser.APPROVE_OPTION){
                chosenDirectory = new File(fileChooser.getSelectedFile().getPath());
                path.setText(chosenDirectory.toString());


            }
        }
    }

}
