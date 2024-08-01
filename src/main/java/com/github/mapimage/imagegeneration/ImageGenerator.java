package com.github.mapimage.imagegeneration;
import com.github.mapimage.domain.Party;

import java.awt.image.WritableRaster;
import java.util.Set;

/**
 * Generate an image using a {@link WritableRaster}
 */
public interface ImageGenerator{
	/** Generate an image using a {@link WritableRaster} */
	void generate(WritableRaster raster, Set<Party> partyFilter);
}