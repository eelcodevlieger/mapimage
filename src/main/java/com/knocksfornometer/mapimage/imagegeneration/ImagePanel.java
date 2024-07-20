package com.knocksfornometer.mapimage.imagegeneration;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JPanel;


/**
 * Displays an image on a panel with the image name written over it.
 */
public class ImagePanel extends JPanel {

	private final Image img;
	private final String name;

	public ImagePanel(String name, Image img) {
		this.name = name;
		this.img = img;
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setFont( new Font( "SansSerif", Font.PLAIN, 20 ) );
		g2d.drawImage(img, 0, 0, this);
		Color previousColor = g2d.getColor();
		g2d.setColor( new Color(1f, 1f, 1f, 0.8f) );
		g2d.fillRect(10, 10, 230, 70);
		g2d.setColor(previousColor);
		g2d.drawString(name, 50, 50);
	}

	@Override
	public String toString() {
		return "ImagePanel [img=" + img + ", name=" + name + "]";
	}
}