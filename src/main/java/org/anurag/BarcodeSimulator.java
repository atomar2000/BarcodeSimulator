package org.anurag;

import javax.swing.*;


import org.anurag.components.BarcodeImage;
import org.anurag.components.InputArea;
import org.anurag.components.RecentBarcodes;

public class BarcodeSimulator {
    public static JFrame frame = null;
    private static void createAndShowGUI() {
        UIManager.setLookAndFeel(WebLookAndFeel.class.getCanonicalName());
        WebLookAndFeel.initializeManagers();
        frame = new JFrame("BARCODE SIMULATOR");
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
//        frame.setPreferredSize(new Dimension(450, 450));
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(BarcodeSimulator::createAndShowGUI);
    }
}