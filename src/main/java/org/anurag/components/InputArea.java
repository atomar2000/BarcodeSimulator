package org.anurag.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputArea extends JPanel {
    public JPanel inputPanel;
    public final JTextField barcodeTextField = new JTextField(27);
    public static InputArea inputArea = new InputArea();
    public static char ALT_REPLACEMENT_CHAR = '\u001B';
    public static char CTRL_REPLACEMENT_CHAR = '\u001C';
    public static char GS_REPLACEMENT_CHAR = '\u001D';

    private static final int KEY_DELAY = 1;

    private final HashMap<Character, Integer> spCharMap = new HashMap<>();

    private Robot robotObject;

    private static int SUFFIX = KeyEvent.VK_ENTER;

    public static InputArea getInstance(){
        return inputArea;
    }
    private InputArea() {
        inputPanel = new JPanel();
        barcodeTextField.addActionListener(e -> {
            String s = e.getActionCommand();
            if(s != null && !s.isEmpty()) {
                performScan(s);
                writeToCacheFile(s);
                barcodeTextField.setText("");
            }
        });
        ButtonGroup bg = new ButtonGroup();
        JRadioButton enterRadioBtn=new JRadioButton("Enter", true);
        enterRadioBtn.addActionListener(e-> {SUFFIX = KeyEvent.VK_ENTER;});
        JRadioButton tabRadioBtn=new JRadioButton("Tab");
        tabRadioBtn.addActionListener(e -> {SUFFIX = KeyEvent.VK_TAB;});
        bg.add(enterRadioBtn);
        bg.add(tabRadioBtn);
        JButton scanBtn = new JButton("Scan");
        scanBtn.addActionListener(e -> {
            String s = barcodeTextField.getText();
            if(s != null && !s.isEmpty()) {
                performScan(s);
                writeToCacheFile(s);
                barcodeTextField.setText("");
            }
        });
        JButton oneDimBarcodeBtn = new JButton("linear barcode");
        oneDimBarcodeBtn.addActionListener(e -> {
            String s = barcodeTextField.getText();
            if(s != null && !s.isEmpty()) {
                generateOneDimImage(s);
                writeToCacheFile(s);
                barcodeTextField.setText("");
            }
        });
        JButton dataMatrixBarcodeBtn = new JButton("data-matrix barcode");
        dataMatrixBarcodeBtn.addActionListener(e -> {
            String s = barcodeTextField.getText();
            if(s != null && !s.isEmpty()) {
                generateTwoDimImage(s);
                writeToCacheFile(s);
                barcodeTextField.setText("");
            }
        });
        oneDimBarcodeBtn.setPreferredSize(new Dimension(165, 20));
        dataMatrixBarcodeBtn.setPreferredSize(new Dimension(165, 20));
        scanBtn.setPreferredSize(new Dimension(80, 20));
        JPanel inputLabelAndTextPanel = new JPanel();
        inputLabelAndTextPanel.add(barcodeTextField);
        inputLabelAndTextPanel.add(enterRadioBtn);
        inputLabelAndTextPanel.add(tabRadioBtn);
        inputLabelAndTextPanel.setSize(new Dimension(470,20));

        JPanel barcodeImageButtonPanel = new JPanel();
        barcodeImageButtonPanel.add(oneDimBarcodeBtn);
        barcodeImageButtonPanel.add(dataMatrixBarcodeBtn);
        barcodeImageButtonPanel.add(scanBtn);
        barcodeImageButtonPanel.setSize(470,20);

        inputPanel.add(inputLabelAndTextPanel);
        inputPanel.add(barcodeImageButtonPanel);
        inputPanel.setLayout(new GridLayout(2,1));
        inputPanel.setPreferredSize(new Dimension(470,50));
        loadSpCharInMap();
    }

    public JPanel getInputJpanel() {
        return inputPanel;
    }

    public void writeToCacheFile(final String latestBarcodeText){
        RecentBarcodes recentBarcodes = RecentBarcodes.getInstance();
        ArrayList<String> currRecentBarcodes = recentBarcodes.getRecentBarcodes();
        ArrayList<String> newRecentBarcodes = new ArrayList<>();
        newRecentBarcodes.add(latestBarcodeText);
        int count = 0;
        for(String currBarcode : currRecentBarcodes) {
            if(!currBarcode.equalsIgnoreCase(latestBarcodeText)) newRecentBarcodes.add(currBarcode);
            count++;
            if(count == 5) break;
        }
        try {
            recentBarcodes.updateCache(newRecentBarcodes);
        }
        catch(Exception exception) {
            exception.printStackTrace();
        }
        recentBarcodes.updateRecentBarcodeStrings();
    }

    private void generateOneDimImage(final String barcodeText){
        BarcodeImage barcodeImage = BarcodeImage.getInstance();
        String finalBarcodeText = replaceKeyPressesWithChars("<GS>", "\\\\F", barcodeText);
        finalBarcodeText = replaceKeyPressesWithChars("[|]", "\\\\F", finalBarcodeText);
        if(SUFFIX == KeyEvent.VK_ENTER) {
            finalBarcodeText = String.format("%s%s", finalBarcodeText, "\\n");
        } else {
            finalBarcodeText = String.format("%s%s", finalBarcodeText, "\\t");
        }
        barcodeImage.generateBarcodeImage(finalBarcodeText, "one_dim");
    }

    private void generateTwoDimImage(final String barcodeText){
        BarcodeImage barcodeImage = BarcodeImage.getInstance();
        String finalBarcodeText = replaceKeyPressesWithChars("<GS>", "\\\\F", barcodeText);
        finalBarcodeText = replaceKeyPressesWithChars("[|]", "\\\\F", finalBarcodeText);
        if(SUFFIX == KeyEvent.VK_ENTER) {
            finalBarcodeText = String.format("%s%s", finalBarcodeText, "\\n");
        } else {
            finalBarcodeText = String.format("%s%s", finalBarcodeText, "\\t");
        }
        barcodeImage.generateBarcodeImage(finalBarcodeText, "two_dim");
    }

    private void performScan(final String barcodeText){
        String finalBarcodeText = replaceKeyPressesWithChars("<Alt>", Character.toString(ALT_REPLACEMENT_CHAR), barcodeText);
        finalBarcodeText = replaceKeyPressesWithChars("<Ctrl>", Character.toString(CTRL_REPLACEMENT_CHAR), finalBarcodeText);
        finalBarcodeText = replaceKeyPressesWithChars("<GS>", Character.toString(GS_REPLACEMENT_CHAR), finalBarcodeText);
        try {
            robotObject = new Robot();
            robotObject.delay(3000);
            for(int i = 0 ; i < finalBarcodeText.length() ; i++) {
                boolean altExists = false;
                boolean ctrlExists = false;
                while(i < finalBarcodeText.length() && (finalBarcodeText.charAt(i) == ALT_REPLACEMENT_CHAR || finalBarcodeText.charAt(i) == CTRL_REPLACEMENT_CHAR)) {
                    if(finalBarcodeText.charAt(i) == ALT_REPLACEMENT_CHAR) altExists = true;
                    else ctrlExists = true;
                    i++;
                }
                char nextChar = finalBarcodeText.charAt(i);
                // execute key press and key release events
                simulateKeyPress(altExists, ctrlExists, nextChar);
            }
            robotObject.keyPress(SUFFIX);
            robotObject.keyRelease(SUFFIX);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String replaceKeyPressesWithChars(final String regex, final String replacement, final String barcodeText){

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(barcodeText);
        return matcher.replaceAll(replacement);
    }

    private void simulateKeyPress(final boolean altExists, final boolean ctrlExists, final char ch) {
        // add a delay to allow user to move the cursor to correct location.
        if(altExists) {
            robotObject.keyPress(KeyEvent.VK_ALT);
            robotObject.delay(KEY_DELAY);
        }
        if(ctrlExists) {
            robotObject.keyPress(KeyEvent.VK_CONTROL);
            robotObject.delay(KEY_DELAY);
        }
        if(Character.isAlphabetic(ch)){
            if(Character.isUpperCase(ch)){
                simulateUpperCaseKeyPress(ch);
            }
            else {
                simulateLowerKeyPress(ch);
            }
        } else if(Character.isDigit(ch)){
            simulateLowerKeyPress(ch);
        }
        else if(ch == GS_REPLACEMENT_CHAR){
            simulateGroupSeparatorKeyPress();
//            pressUnicode(KeyEvent.getExtendedKeyCodeForChar(ch));
        }
        else{
            simulateSpecialCharacterKeyPress(ch);
        }

        if(altExists) {
            robotObject.keyRelease(KeyEvent.VK_ALT);
            robotObject.delay(KEY_DELAY);
        }
        if(ctrlExists) {
            robotObject.keyRelease(KeyEvent.VK_CONTROL);
            robotObject.delay(KEY_DELAY);
        }
    }

    private void simulateGroupSeparatorKeyPress() {
        // Press Alt + 0 + 0 + 1 + D to insert GS character
        // hold Alt
        robotObject.keyPress(KeyEvent.VK_ALT);
        robotObject.delay(KEY_DELAY);

        // press and release 001d
        robotObject.keyPress(KeyEvent.VK_NUMPAD0);
        robotObject.delay(KEY_DELAY);
        robotObject.keyRelease(KeyEvent.VK_NUMPAD0);
        robotObject.delay(KEY_DELAY);
        robotObject.keyPress(KeyEvent.VK_NUMPAD0);
        robotObject.delay(KEY_DELAY);
        robotObject.keyRelease(KeyEvent.VK_NUMPAD0);
        robotObject.delay(KEY_DELAY);
        robotObject.keyPress(KeyEvent.VK_NUMPAD2);
        robotObject.delay(KEY_DELAY);
        robotObject.keyRelease(KeyEvent.VK_NUMPAD2);
        robotObject.delay(KEY_DELAY);
        robotObject.keyPress(KeyEvent.VK_NUMPAD9);
        robotObject.delay(KEY_DELAY);
        robotObject.keyRelease(KeyEvent.VK_NUMPAD9);
        robotObject.delay(KEY_DELAY);

        // release Alt
        robotObject.keyRelease(KeyEvent.VK_ALT);
        robotObject.delay(KEY_DELAY);
//        robotObject.keyRelease(29);
//        robotObject.delay(KEY_DELAY);
//        robotObject.keyRelease(29);
//        robotObject.delay(KEY_DELAY);
    }

    private void simulateUpperCaseKeyPress(final char ch) {
        int keyCode = KeyEvent.getExtendedKeyCodeForChar(ch);
        robotObject.keyPress(KeyEvent.VK_SHIFT);
        robotObject.delay(KEY_DELAY);
        robotObject.keyPress(keyCode);
        robotObject.delay(KEY_DELAY);
        robotObject.keyRelease(keyCode);
        robotObject.delay(KEY_DELAY);
        robotObject.keyRelease(KeyEvent.VK_SHIFT);
        robotObject.delay(KEY_DELAY);
    }

    private void simulateLowerKeyPress(final char ch) {
        int keyCode = KeyEvent.getExtendedKeyCodeForChar(ch);
        robotObject.keyPress(keyCode);
        robotObject.delay(KEY_DELAY);
        robotObject.keyRelease(keyCode);
        robotObject.delay(KEY_DELAY);
    }

    private void simulateSpecialCharacterKeyPress(final char ch) {
        if(spCharMap.containsKey(ch)){
            int keyCode = spCharMap.get(ch);
            robotObject.keyPress(KeyEvent.VK_SHIFT);
            robotObject.delay(KEY_DELAY);
            robotObject.keyPress(keyCode);
            robotObject.delay(KEY_DELAY);
            robotObject.keyRelease(keyCode);
            robotObject.delay(KEY_DELAY);
            robotObject.keyRelease(KeyEvent.VK_SHIFT);
            robotObject.delay(KEY_DELAY);
        }
        else {
            int keyCode = KeyEvent.getExtendedKeyCodeForChar(ch);
            robotObject.keyPress(keyCode);
            robotObject.delay(KEY_DELAY);
            robotObject.keyRelease(keyCode);
            robotObject.delay(KEY_DELAY);
        }
    }

    private void loadSpCharInMap(){
        spCharMap.put('!', KeyEvent.VK_1);
        spCharMap.put('@', KeyEvent.VK_2);
        spCharMap.put('#', KeyEvent.VK_3);
        spCharMap.put('$', KeyEvent.VK_4);
        spCharMap.put('%', KeyEvent.VK_5);
        spCharMap.put('^', KeyEvent.VK_6);
        spCharMap.put('&', KeyEvent.VK_7);
        spCharMap.put('*', KeyEvent.VK_8);
        spCharMap.put('(', KeyEvent.VK_9);
        spCharMap.put(')', KeyEvent.VK_0);

        spCharMap.put('_', KeyEvent.VK_MINUS);
        spCharMap.put('+', KeyEvent.VK_EQUALS);
        spCharMap.put('{', KeyEvent.VK_OPEN_BRACKET);
        spCharMap.put('}', KeyEvent.VK_CLOSE_BRACKET);
        spCharMap.put(':', KeyEvent.VK_SEMICOLON);
        spCharMap.put('\"', KeyEvent.VK_QUOTE);
        spCharMap.put('|', KeyEvent.VK_BACK_SLASH);
        spCharMap.put('<', KeyEvent.VK_COMMA);
        spCharMap.put('>', KeyEvent.VK_PERIOD);
        spCharMap.put('?', KeyEvent.VK_SLASH);
    }
}
