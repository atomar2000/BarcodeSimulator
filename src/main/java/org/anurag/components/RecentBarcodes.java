package org.anurag.components;

import org.anurag.BarcodeSimulator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class RecentBarcodes extends JPanel {
    public static ArrayList<String> recentBarcodes;
    public static ArrayList<String> prefillBarcodes;
    public static final String BARCODES_CACHE_FILE_PATH = "./recentBarcodesCache.txt";
    public static final String PREFILL_BARCODES_FILE_PATH = "./prefillBarcodes.txt";
    private static final RecentBarcodes instance = new RecentBarcodes();
    public JPanel recentBarcodesPanel;
    public JPanel loadPrefillPanel;
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

    private void updatePrefillBarcodes() {
        prefillBarcodes = new ArrayList<>();
        File prefillBarcodesCache = new File(PREFILL_BARCODES_FILE_PATH);
        if(prefillBarcodesCache.exists()){
            Scanner readFromFile = null;
            try {
                readFromFile = new Scanner(prefillBarcodesCache);
            } catch (FileNotFoundException fileNotFoundException){
                fileNotFoundException.printStackTrace();
            }
            while(readFromFile != null && readFromFile.hasNextLine()){
                prefillBarcodes.add(readFromFile.nextLine());
            }
        }
    }

    public void updateLoadPrefillFile() throws IOException {
        Component[] loadPrefillComponents = loadPrefillPanel.getComponents();
        String value = ((JTextArea)loadPrefillComponents[1]).getText();
        String[] values = value.split("\n");
        prefillBarcodes = new ArrayList<>(Arrays.asList(values).subList(0, Math.min(values.length, 5)));

        FileWriter prefillBarcodesCacheWrite = new FileWriter(PREFILL_BARCODES_FILE_PATH);
        for(String barcode : prefillBarcodes) {
            if(barcode.length() > 0) {
                prefillBarcodesCacheWrite.write(barcode + '\n');
            }
        }
        prefillBarcodesCacheWrite.close();
    }

    public void updateRecentBarcodesToPrefill() throws IOException {
        updateLoadPrefillFile();
        recentBarcodes = prefillBarcodes;
        updateCache(recentBarcodes);
        addComponentsToPanel();
    }

    public void updateFromPrefill(){
        JButton updateFromPrefillBtn = new JButton("— Load Prefill —");
        updateFromPrefillBtn.addActionListener(e -> {
            updatePrefillBarcodes();
            JFrame textFrame = new JFrame("Fill/Set prefill Barcodes");
            loadPrefillPanel = new JPanel();
            Point location = MouseInfo.getPointerInfo().getLocation();
            textFrame.setLocation(location);
            textFrame.setSize(500, 200);
            JLabel prefillBarcodesLabel = new JLabel("prefill barcodes:");
            prefillBarcodesLabel.setSize(new Dimension(480, 20));
            JTextArea textArea = new JTextArea();
            textArea.setPreferredSize(new Dimension(480, 100));

            if (prefillBarcodes.size() > 0) {
                textArea.setText(String.join("\n", prefillBarcodes) + "\n");
            } else {
                textArea.setText("");
            }
            loadPrefillPanel.add(prefillBarcodesLabel);
            loadPrefillPanel.add(textArea);

            JButton loadButton = new JButton("Load");
            JButton updateButton = new JButton("Save");
            JButton cancelButton = new JButton("Cancel");

            loadButton.setSize(new Dimension(480/3 - 5, 20));
            updateButton.setSize(new Dimension(480/3 - 5, 20));
            cancelButton.setSize(new Dimension(480/3 - 5, 20));



            loadPrefillPanel.add(loadButton);
            loadPrefillPanel.add(updateButton);
            loadPrefillPanel.add(cancelButton);


            cancelButton.addActionListener(cancelEvent -> {
                textFrame.dispose();
            });

            updateButton.addActionListener(updateEvent -> {
                try {
                    updateLoadPrefillFile();
                    textFrame.dispose();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });

            loadButton.addActionListener(loadEvent -> {
                try {
                    updateRecentBarcodesToPrefill();
                    textFrame.dispose();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });

            textFrame.add(loadPrefillPanel);
            textFrame.getContentPane().setLayout(
                    new BoxLayout(textFrame.getContentPane(), BoxLayout.Y_AXIS)
            );
            textFrame.setVisible(true);
        });
        recentBarcodesPanel.add(updateFromPrefillBtn);
    }
    public void addComponentsToPanel(){
        recentBarcodesPanel.removeAll();
        JLabel recentBarcodesLabel = new JLabel("Recent Barcodes");
        recentBarcodesLabel.setSize(300,25);
        recentBarcodesPanel.add(recentBarcodesLabel);
        ArrayList<JButton> recentBarcodeButtons = new ArrayList<>();
        for (String recentBarcode : recentBarcodes) {
            recentBarcodeButtons.add(new JButton(recentBarcode));
        }
        for(JButton button : recentBarcodeButtons) {
            button.setSize(new Dimension(300,25));
            button.addActionListener(action -> {
                String populateBarcodeText = action.getActionCommand();
                updateTextInInputArea(populateBarcodeText);
            });
            recentBarcodesPanel.add(button);
        }
        updateFromPrefill();
        recentBarcodesPanel.setLayout(new GridLayout(recentBarcodes.size()+2, 1));
//        recentBarcodesPanel.setSize(new Dimension(400,((recentBarcodeButtons.size()+2)*30)+50));
        recentBarcodesPanel.setSize(new Dimension(400,300));
        //BarcodeSimulator.frame.setSize(BarcodeSimulator.frame.getWidth(), recentBarcodesPanel.getHeight() + InputArea.getInstance().inputPanel.getHeight() + BarcodeImage.getInstance().barcodeImageJpanel.getHeight());
        if(BarcodeSimulator.frame.getHeight() > 0) {
            BarcodeSimulator.frame.setSize(new Dimension(BarcodeSimulator.frame.getWidth(), 100 + BarcodeImage.getInstance().barcodeImageJpanel.getHeight() + ((recentBarcodeButtons.size() + 1) * 25) + 25)); // -(x-1)*30 + x*30 = -30x-30+30x
        }
        PREVIOUS_HEIGHT = ((recentBarcodeButtons.size()+1)*25)+25;
        recentBarcodesPanel.setVisible(true);
        BarcodeSimulator.frame.revalidate();
    }

    private void updateTextInInputArea(final String newBarcodeText){
        InputArea inputArea = InputArea.getInstance();
        inputArea.barcodeTextField.setText(newBarcodeText);
    }
}
