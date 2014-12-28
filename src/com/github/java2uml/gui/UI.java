package com.github.java2uml.gui;

import com.github.java2uml.core.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class UI {
    private JFrame mainFrame;
    private JPanel panelForButtons, panelForGeneratedCode, panelForPath, panelForPathAndButtons, panelForDiagram;
    private JButton browse;
    private JTabbedPane tabs;
    private JMenuBar menu;
    private JMenu file, help, typeOfDiagramMenu, options, direction;
    private JMenuItem helpItem, saveItem, exitItem, aboutItem, generateItem, chooseItem, newItem;
    JRadioButtonMenuItem horizontalDirectionRadioItem, verticalDirectionRadioItem, classDiagramRadioItem,
            sequenceDiagramRadioItem;
    ButtonGroup directionGroup;
    ButtonGroup typeOfDiagramGroup;

    private JScrollPane scrollPane, scrollPaneForDiagram;
    private JButton generatePlantUML;
    private JComboBox setDirectionOfDiagram, setTypeOfDiagram;
    private JTextField path;
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

    private static UI ui;

    public JTextField getPath() {
        return path;
    }

    public JButton getGeneratePlantUML() {
        return generatePlantUML;
    }

    public JTextArea getGeneratedCode() {
        return generatedCode;
    }

    private UI(){

    }

    public static UI getInstance(){
        if (ui == null){
            ui = new UI();
        }
        return ui;
    }

    private JMenuBar initMenu(){
        menu = new JMenuBar();
        file = new JMenu("File");
        help = new JMenu("Help");
        typeOfDiagramMenu = new JMenu("Type will be...");

        options = new JMenu("Options");
        direction = new JMenu("Direction will be...");
        helpItem = new JMenuItem("Help");
        saveItem = new JMenuItem("Save");
        exitItem = new JMenuItem("Exit");

        aboutItem = new JMenuItem("About");
        generateItem = new JMenuItem("Generate");
        chooseItem = new JMenuItem("Choose dir");
        newItem = new JMenuItem("New");

        directionGroup = new ButtonGroup();
        typeOfDiagramGroup = new ButtonGroup();

        horizontalDirectionRadioItem = new JRadioButtonMenuItem("Horizontal");
        verticalDirectionRadioItem = new JRadioButtonMenuItem("Vertical");
        directionGroup.add(horizontalDirectionRadioItem);
        directionGroup.add(verticalDirectionRadioItem);

        classDiagramRadioItem = new JRadioButtonMenuItem("Class");
        sequenceDiagramRadioItem = new JRadioButtonMenuItem("Sequence");
        typeOfDiagramGroup.add(classDiagramRadioItem);
        typeOfDiagramGroup.add(sequenceDiagramRadioItem);


        file.add(newItem);
        file.add(chooseItem);
        file.add(generateItem);
        file.add(saveItem);
        file.add(exitItem);

        options.add(direction);
        direction.add(horizontalDirectionRadioItem);
        direction.add(verticalDirectionRadioItem);
        options.add(typeOfDiagramMenu);
        typeOfDiagramMenu.add(classDiagramRadioItem);
        typeOfDiagramMenu.add(sequenceDiagramRadioItem);

        help.add(helpItem);
        help.add(aboutItem);

        menu.add(file);
        menu.add(options);
        menu.add(help);

        return menu;
    }

    public JFrame initUI() {
        mainFrame = new JFrame("Java2UML");
        panelForButtons = new JPanel();
        panelForGeneratedCode = new JPanel();
        panelForDiagram = new JPanel();
        browse = new JButton("Choose dir");
        generatePlantUML = new JButton("Generate");
        labelForDiagram = new JLabel();
        setTypeOfDiagram = new JComboBox();

        setDirectionOfDiagram = new JComboBox();
        generatedCode = new JTextArea();
        path = new JTextField();
        panelForPath = new JPanel();
        panelForPathAndButtons = new JPanel();
        progressBar = new JProgressBar();
        progressBar.setBorder(new EmptyBorder(0, 3, 0, 3));

        scrollPaneForDiagram = new JScrollPane(labelForDiagram);
        scrollPaneForDiagram.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneForDiagram.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        tabs = new JTabbedPane();
        tabs.addTab("PlantUML code", panelForGeneratedCode);


        tabs.addTab("Diagram", panelForDiagram);


        path.setToolTipText("Enter path here");

        separatorBetweenPathAndButtons = new JSeparator();
        separatorBetweenButtonsAndProgressBar = new JSeparator();
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        setTypeOfDiagram.addItem(CLASS_DIAGRAM);
        setTypeOfDiagram.addItem(SEQUENCE_DIAGRAM);
        setDirectionOfDiagram.addItem(VERTICAL_DIRECTION);
        setDirectionOfDiagram.addItem(HORIZONTAL_DIRECTION);

        panelForButtons.add(browse);
        panelForButtons.add(setTypeOfDiagram);
        panelForButtons.add(setDirectionOfDiagram);
        panelForButtons.add(generatePlantUML);
//        panelForButtons.setBorder(new EmptyBorder(0, 3, 0, 3));

        panelForButtons.setLayout(new BoxLayout(panelForButtons, BoxLayout.X_AXIS));

        panelForPath.setLayout(new GridLayout(1, 1));
        panelForPath.add(path);

        panelForPathAndButtons.setLayout(new BoxLayout(panelForPathAndButtons, BoxLayout.Y_AXIS));
        panelForPathAndButtons.add(panelForPath);
        panelForPathAndButtons.add(separatorBetweenPathAndButtons);
        panelForPathAndButtons.add(panelForButtons);
        panelForPathAndButtons.add(separatorBetweenButtonsAndProgressBar);
        panelForPathAndButtons.add(progressBar);



        scrollPane = new JScrollPane(generatedCode);


        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panelForGeneratedCode.setLayout(new BoxLayout(panelForGeneratedCode, BoxLayout.Y_AXIS));

        panelForGeneratedCode.add(scrollPane);
        panelForGeneratedCode.setBorder(new EmptyBorder(0,5,5,5));



        mainFrame.setJMenuBar(initMenu());
        mainFrame.add(tabs);
        mainFrame.add(BorderLayout.NORTH, panelForPathAndButtons);
//        mainFrame.add(BorderLayout.CENTER, panelForGeneratedCode);
        mainFrame.setSize(500, 500);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        progressBar.setStringPainted(true);
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);

        return mainFrame;
    }



    public File addActionListenerToChooseFile(){

        browse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progressBar.setValue(0);
                int resultOfChoice = fileChooser.showOpenDialog(mainFrame);
                if (resultOfChoice == JFileChooser.APPROVE_OPTION){
                    chosenDirectory = new File(fileChooser.getSelectedFile().getPath());
                    path.setText(chosenDirectory.toString());
                    Main.setPath(chosenDirectory.toString());

                }
            }
        });

        return chosenDirectory;
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

    public void showDiagram(){
        try {
            diagram = ImageIO.read(new File("diagram.png"));
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

}
