package org.anurag.components;

import org.anurag.BarcodeSimulator;
import org.anurag.utility.CopyImagetoClipBoard;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class BarcodeImage extends JPanel{
    public JPanel barcodeImageJpanel;
    private static final BarcodeImage instance = new BarcodeImage();
    private static final String IMG_NOT_FOUND_PATH = "";//"https://static.thenounproject.com/png/2002633-200.png";
    public BarcodeImage(){
        barcodeImageJpanel = new JPanel();
    }
    public static BarcodeImage getInstance(){
        return instance;
    }
    public JPanel getBarcodeImageJpanel(){
        return this.barcodeImageJpanel;
    }
    private static int PREVIOUS_HEIGHT = 0;
    private BufferedImage image = null;
    private JButton dataMatrixImgBtn = null;
    private JLabel clickToCopyImage = new JLabel("Click the image to copy");

    public void generateBarcodeImage(String barcodeText, final String barcodeType){
        barcodeImageJpanel.removeAll();
        if(barcodeText == null) return;
        try {
            barcodeText = URLEncoder.encode(barcodeText, String.valueOf(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        final String pathPrefix = "https://barcode.tec-it.com/barcode.ashx?data=";
        final String pathSuffix = "one_dim".equals(barcodeType) ? "&code=GS1-128&translate-esc=on" : "&code=DataMatrix&dmsize=Default";
        String path = barcodeText != null && barcodeText.length() > 0 ? String.format("%s%s%s", pathPrefix, barcodeText, pathSuffix) : IMG_NOT_FOUND_PATH;
        if(path.equals(IMG_NOT_FOUND_PATH)) return;
        URL url = null;
        int widthImg = 0;
        int heightImg = 0;
        try {
            url = new URL(path);
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            assert url != null;
            image = ImageIO.read(url);
            dataMatrixImgBtn = new JButton(new ImageIcon(image));
            dataMatrixImgBtn.addActionListener(e -> new CopyImagetoClipBoard(image));
            dataMatrixImgBtn.setPreferredSize(new Dimension(image.getWidth()+20, image.getHeight()+10));
            widthImg = image.getWidth()+20;
            heightImg = image.getHeight()+30;
            barcodeImageJpanel.add(dataMatrixImgBtn);
        } catch (IOException e) {
            e.printStackTrace();
        }
        barcodeImageJpanel.setPreferredSize(new Dimension(widthImg, heightImg));
        barcodeImageJpanel.add(clickToCopyImage);
        barcodeImageJpanel.revalidate();
        BarcodeSimulator.frame.setSize(new Dimension(Math.max(widthImg+10,500), BarcodeSimulator.frame.getHeight()-PREVIOUS_HEIGHT+heightImg));
        PREVIOUS_HEIGHT = heightImg;
        BarcodeSimulator.frame.revalidate();
    }
}
