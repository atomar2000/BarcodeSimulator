package org.anurag.components;

import org.anurag.BarcodeSimulator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class RecentBarcodes extends JPanel {
    public static ArrayList<String> recentBarcodes;
    public static final String BARCODES_CACHE_FILE_PATH = "./recentBarcodesCache.txt";
    private static final RecentBarcodes instance = new RecentBarcodes();
    public JPanel recentBarcodesPanel;
    private static int PREVIOUS_HEIGHT = 0;
    private RecentBarcodes() {
        updateRecentBarcodeStrings();
        recentBarcodesPanel = new JPanel();
        addComponentsToPanel();
    }

    public JPanel getRecentBarcodesPanel(){
        updateRecentBarcodeStrings();
        return recentBarcodesPanel;
    }

    public ArrayList<String> getRecentBarcodes(){
        return recentBarcodes;
    }

    public static RecentBarcodes getInstance(){
        return instance;
    }

    public void updateCache(final ArrayList<String> newBarcodes) throws IOException {
        FileWriter recentBarcodesCacheWrite = new FileWriter(BARCODES_CACHE_FILE_PATH);
        for(String barcode : newBarcodes) {
            recentBarcodesCacheWrite.write(barcode + '\n');
        }
        recentBarcodesCacheWrite.close();
        updateRecentBarcodeStrings();
        addComponentsToPanel();
    }
    public void updateRecentBarcodeStrings(){
        recentBarcodes = new ArrayList<>();
        File recentBarcodesCache = new File(BARCODES_CACHE_FILE_PATH);
        if(recentBarcodesCache.exists()){
            Scanner readFromFile = null;
            try {
                readFromFile = new Scanner(recentBarcodesCache);
            } catch (FileNotFoundException fileNotFoundException){
                fileNotFoundException.printStackTrace();
            }
            while(readFromFile != null && readFromFile.hasNextLine()){
                recentBarcodes.add(readFromFile.nextLine());
            }
        }
    }
    public void addComponentsToPanel(){
        recentBarcodesPanel.removeAll();
        recentBarcodesPanel.add(new JLabel("Recent Barcodes"));
        ArrayList<JButton> recentBarcodeButtons = new ArrayList<>();
        for (String recentBarcode : recentBarcodes) {
            recentBarcodeButtons.add(new JButton(recentBarcode));
        }
        for(JButton button : recentBarcodeButtons) {
            button.setPreferredSize(new Dimension(300,20));
            button.addActionListener(action -> {
                String populateBarcodeText = action.getActionCommand();
                updateTextInInputArea(populateBarcodeText);
            });
            recentBarcodesPanel.add(button);
        }
        recentBarcodesPanel.setLayout(new GridLayout(recentBarcodes.size()+1, 1));
        recentBarcodesPanel.setPreferredSize(new Dimension(400,(recentBarcodeButtons.size()*20)+20));
        BarcodeSimulator.frame.setSize(BarcodeSimulator.frame.getWidth(), BarcodeSimulator.frame.getHeight()-PREVIOUS_HEIGHT+recentBarcodesPanel.getHeight()); // -(x-1)*30 + x*30 = -30x-30+30x
        PREVIOUS_HEIGHT = recentBarcodesPanel.getHeight();
        recentBarcodesPanel.setVisible(true);
        recentBarcodesPanel.revalidate();
    }

    private void updateTextInInputArea(final String newBarcodeText){
        InputArea inputArea = InputArea.getInstance();
        Component[] components = inputArea.getInputJpanel().getComponents();
        JPanel inputAreaPanel = (JPanel) components[0];
        Component[] components1 = inputAreaPanel.getComponents();
        JTextField inputTextField = (JTextField) components1[0];
        inputTextField.setText(newBarcodeText);
    }
}
