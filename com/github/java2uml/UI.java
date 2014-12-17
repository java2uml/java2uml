package com.github.java2uml;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class UI {
    private JFrame mainFrame;

    private JPanel panelForButtons, panelForGeneratedCode, panelForPath, panelForPathAndButtons;
    private JButton browse;

    private JScrollPane scrollPane;

    private JButton generatePlantUML;
    private JComboBox setDirectionOfDiagram;
    private JTextField path;

    private JTextArea generatedCode;
    private JProgressBar progressBar;

    private JSeparator separatorBetweenProgressBarAndGeneratedCode, separatorBetweenPathAndButtons, separatorBetweenButtonsAndProgressBar;
    private JFileChooser fileChooser;
    private File chosenDirectory;

    public File getChosenDirectory() {
        return chosenDirectory;
    }

    public void setChosenDirectory(File chosenDirectory) {
        this.chosenDirectory = chosenDirectory;
    }


    public JButton getGeneratePlantUML() {
        return generatePlantUML;
    }

    public void setGeneratePlantUML(JButton generatePlantUML) {
        this.generatePlantUML = generatePlantUML;
    }

    public JTextArea getGeneratedCode() {
        return generatedCode;
    }

    public void setGeneratedCode(JTextArea generatedCode) {
        this.generatedCode = generatedCode;
    }


    public static final String VERTICAL_DIRECTION = "Vertical";
    public static final String HORIZONTAL_DIRECTION = "Horizontal";

    public JFrame initUI() {
        mainFrame = new JFrame("Java2UML");
        panelForButtons = new JPanel();
        panelForGeneratedCode = new JPanel();
        browse = new JButton("Choose dir");
        generatePlantUML = new JButton("Generate");


        setDirectionOfDiagram = new JComboBox();
        generatedCode = new JTextArea();
        path = new JTextField();
        panelForPath = new JPanel();
        panelForPathAndButtons = new JPanel();
        progressBar = new JProgressBar();
        separatorBetweenProgressBarAndGeneratedCode = new JSeparator();
        separatorBetweenPathAndButtons = new JSeparator();
        separatorBetweenButtonsAndProgressBar = new JSeparator();
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);


        setDirectionOfDiagram.addItem(VERTICAL_DIRECTION);
        setDirectionOfDiagram.addItem(HORIZONTAL_DIRECTION);

        panelForButtons.add(browse);

        panelForButtons.add(setDirectionOfDiagram);
        panelForButtons.add(generatePlantUML);
        panelForButtons.setLayout(new BoxLayout(panelForButtons, BoxLayout.X_AXIS));

        panelForPath.setLayout(new GridLayout(1, 1));
        panelForPath.add(path);

        panelForPathAndButtons.setLayout(new BoxLayout(panelForPathAndButtons, BoxLayout.Y_AXIS));
        panelForPathAndButtons.add(panelForPath);
        panelForPathAndButtons.add(separatorBetweenPathAndButtons);
        panelForPathAndButtons.add(panelForButtons);
        panelForPathAndButtons.add(separatorBetweenButtonsAndProgressBar);


        scrollPane = new JScrollPane(generatedCode);
//        scrollPane.setBorder(new LineBorder(Color.GRAY, 1, true));

        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panelForGeneratedCode.setLayout(new BoxLayout(panelForGeneratedCode, BoxLayout.Y_AXIS));
        panelForGeneratedCode.add(progressBar);
//        panelForGeneratedCode.add(separatorBetweenProgressBarAndGeneratedCode);
        panelForGeneratedCode.add(scrollPane);
        panelForGeneratedCode.setBorder(new EmptyBorder(0,5,5,5));





        mainFrame.add(BorderLayout.NORTH, panelForPathAndButtons);
        mainFrame.add(BorderLayout.CENTER, panelForGeneratedCode);
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



}
