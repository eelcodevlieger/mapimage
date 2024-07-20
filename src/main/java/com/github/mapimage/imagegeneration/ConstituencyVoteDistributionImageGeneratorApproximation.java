package com.github.mapimage.imagegeneration;

import java.awt.Color;
import java.awt.image.WritableRaster;

import com.github.mapimage.domain.Candidate;
import com.github.mapimage.domain.Candidates;
import lombok.AllArgsConstructor;

/**
 * Generate a 'voting distribution' image.
 * 
 * For each pixel grab a random party colour with the probability matching the voting distribution.
 * Due to the amount of pixels this will produce a texture matching the voting distribution.
 */
@AllArgsConstructor
public class ConstituencyVoteDistributionImageGeneratorApproximation implements ImageGenerator {

	private final Candidates candidates;

	@Override
	public void generate(WritableRaster raster) {
		int width = raster.getWidth();
		int height = raster.getHeight();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				float[] pixel = getPixel( candidates.getNextCandidate() );
				raster.setPixel(x, y, pixel);
			}	
		}
	}

	private float[] getPixel(Candidate party) {
		Color color = party.getColor();
		return new float[]{color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()};
	}
}