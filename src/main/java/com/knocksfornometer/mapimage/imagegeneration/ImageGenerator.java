package com.knocksfornometer.mapimage.imagegeneration;
import java.awt.image.WritableRaster;

/**
 * Generate an image using a {@link WritableRaster}
 * 
 * @author Eelco de Vlieger
 */
public interface ImageGenerator{
	/** Generate an image using a {@link WritableRaster} */
	void generate(WritableRaster raster);
}