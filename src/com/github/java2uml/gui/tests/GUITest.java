package com.github.java2uml.gui.tests;

import com.github.java2uml.gui.UIEntry;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by mac on 18.01.15.
 */
public class GUITest {
    @Test
    public void testDeletePreviousVersionsOfDiagrams(){
        UIEntry uiEntry = new UIEntry();
        File diagramPNG = new File("diagram.png");
        File diagramSVG = new File("diagram.svg");
        if (!diagramPNG.exists() && !diagramSVG.exists()) {
            try {
                diagramPNG.createNewFile();
                diagramSVG.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Assert.assertTrue(uiEntry.deletePreviousVersionsOfDiagrams());
    }
}
