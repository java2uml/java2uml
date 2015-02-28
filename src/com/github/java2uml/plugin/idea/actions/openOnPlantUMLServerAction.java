package com.github.java2uml.plugin.idea.actions;

import com.github.java2uml.gui.SwingWorkerForBackgroundGenerating;
import com.github.java2uml.gui.UIPluginEntry;
import com.github.java2uml.plugin.idea.PluginSettings;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import net.sourceforge.plantuml.code.Transcoder;
import net.sourceforge.plantuml.code.TranscoderUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by Андрей on 17.02.2015.
 */
public class openOnPlantUMLServerAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
//        if (PluginSettings.getSettings().get("showDiagram") == 1) {
//            SwingUtilities.invokeLater(new Runnable() {
//                @Override
//                public void run() {
//                    if (System.getProperty("os.name").contains("Windows")) {
//                        try {
//                            if (viewerProc != null) {
//                                viewerProc.destroy();
//                                viewerProc = null;
//                            }
//                            viewerProc = Runtime.getRuntime().exec("java -jar lib/diagram_viewer.jar " + UIPluginEntry.getUi().getPathOfCurrentDiagram());
//                        } catch (IOException ioe) {
//                            ioe.printStackTrace();
//                        }
//                    } else {
//                        try {
//                            Desktop.getDesktop().open(new File(UIPluginEntry.getUi().getPathOfCurrentDiagram()));
//                        } catch (IOException e1) {
//                            e1.printStackTrace();
//                        }
//                    }
//                }
//            });
//        } else {
//            JOptionPane.showMessageDialog(UIPluginEntry.getUi().getMainFrame(), UIPluginEntry.getUi().getLocaleLabels().getString("youMustGenerateDiagramFirst"), "Java2UML message", JOptionPane.INFORMATION_MESSAGE);
//        }
        if (SwingWorkerForBackgroundGenerating.getPlantUMLCode() != null && !SwingWorkerForBackgroundGenerating.getPlantUMLCode().equals("") && !UIPluginEntry.getUi().getGeneratedCode().getText().equals(""))
            sendRequestAndShowSvg(SwingWorkerForBackgroundGenerating.getPlantUMLCode());
        else {
            JOptionPane.showMessageDialog(UIPluginEntry.getUi().getMainFrame(), UIPluginEntry.getUi().getLocaleLabels().getString("umlCodeMustNotBeEmpty"), "Java2UML message", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void sendRequestAndShowSvg(String source) {

        Transcoder t = TranscoderUtil.getDefaultTranscoder();
        String url = null;
        try {
            url = t.encode(source);
            url = "http://www.plantuml.com/plantuml/svg/" + url;
            Desktop.getDesktop().browse(new URL(url).toURI());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
