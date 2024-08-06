package org.anurag;

import javax.swing.*;


import com.formdev.flatlaf.FlatLightLaf;
import org.anurag.components.BarcodeImage;
import org.anurag.components.InputArea;
import org.anurag.components.RecentBarcodes;

import java.awt.*;

public class BarcodeSimulator {
    public static JFrame frame = null;
    private static void createAndShowGUI() {
        frame = new JFrame("BARCODE SIMULATOR");
        Point location = MouseInfo.getPointerInfo().getLocation();
        frame.setLocation(location);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        InputArea inputPanel = InputArea.getInstance();
        RecentBarcodes recentBarcodesPanel = RecentBarcodes.getInstance();
        BarcodeImage barcodeImagePanel = BarcodeImage.getInstance();
        BarcodeImage.getInstance().generateBarcodeImage(null,null);

        frame.getContentPane().add(inputPanel.getInputJpanel());
        frame.getContentPane().add(barcodeImagePanel.getBarcodeImageJpanel());
        frame.getContentPane().add(recentBarcodesPanel.getRecentBarcodesPanel());
        frame.getContentPane().setLayout(
                new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS)
        );
        int maxWidth = Math.max(inputPanel.getInputJpanel().getWidth(), Math.max(RecentBarcodes.getInstance().recentBarcodesPanel.getWidth(), BarcodeImage.getInstance().barcodeImageJpanel.getWidth()));
        int maxHeight = Math.max(inputPanel.getInputJpanel().getHeight(), Math.max(RecentBarcodes.getInstance().recentBarcodesPanel.getHeight(), BarcodeImage.getInstance().barcodeImageJpanel.getHeight()));
        frame.setSize(new Dimension(maxWidth, maxHeight));
        frame.pack();
        frame.setVisible(true);
        frame.setAlwaysOnTop(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();
        javax.swing.SwingUtilities.invokeLater(BarcodeSimulator::createAndShowGUI);
    }
}