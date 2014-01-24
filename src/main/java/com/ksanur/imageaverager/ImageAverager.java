package com.ksanur.imageaverager;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * User: bobacadodl
 * Date: 1/23/14
 * Time: 11:08 PM
 */
public class ImageAverager {

    public static void main(String[] args) throws IOException {
        System.out.println("Starting...");
        String webUrl = "http://dinnerbone.com/media/uploads/2014-01/files/skins.html";
        URL url = new URL(webUrl);
        URLConnection connection = url.openConnection();

        InputStream is = connection.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        HTMLEditorKit htmlKit = new HTMLEditorKit();
        HTMLDocument htmlDoc = (HTMLDocument) htmlKit.createDefaultDocument();
        HTMLEditorKit.Parser parser = new ParserDelegator();
        HTMLEditorKit.ParserCallback callback = htmlDoc.getReader(0);
        parser.parse(br, callback, true);

        List<BufferedImage> images = new ArrayList<BufferedImage>();

        System.out.println("Iterating through all images...");
        int idx = 0;
        for (HTMLDocument.Iterator iterator = htmlDoc.getIterator(HTML.Tag.IMG); iterator.isValid(); iterator.next()) {
            AttributeSet attributes = iterator.getAttributes();
            String imgSrc = (String) attributes.getAttribute(HTML.Attribute.SRC);

            String imgUrl = webUrl;

            if (!(imgSrc.startsWith("http"))) {
                imgUrl += imgSrc;
            } else {
                imgUrl = imgSrc;
            }

            BufferedImage image = ImageIO.read(new URL(imgUrl));
            images.add(image);
            System.out.println("Added image[" + idx++ + "] " + imgUrl);
        }
        System.out.println("Total # of images: " + images.size());
        System.out.println("Averaging images...");

        int height = images.get(0).getHeight();
        int width = images.get(0).getWidth();
        System.out.println("Columns: " + height);
        System.out.println("Rows: " + width);
        BufferedImage averageImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int red = 0;
                int green = 0;
                int blue = 0;
                for (BufferedImage image : images) {
                    Color c = new Color(image.getRGB(x, y));
                    blue += c.getBlue();
                    red += c.getRed();
                    green += c.getGreen();
                }

                int rgbaverage = new Color(red / images.size(), green / images.size(), blue / images.size()).getRGB();
                averageImage.setRGB(x, y, rgbaverage);
            }
            System.out.println("Row " + x + " complete...");
        }

        System.out.println("Processing Complete!");
        File completedImages = new File("CompletedImages");
        completedImages.mkdirs();

        ImageIO.write(averageImage, "png", new File(completedImages, "output.png"));

        ImageIcon icon = new ImageIcon(averageImage);
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(width + 10, height + 10);
        JLabel lbl = new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
