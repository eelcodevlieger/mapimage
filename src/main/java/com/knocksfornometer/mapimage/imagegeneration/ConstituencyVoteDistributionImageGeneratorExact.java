package com.knocksfornometer.mapimage.imagegeneration;

import java.awt.Color;
import java.awt.image.WritableRaster;

import com.knocksfornometer.mapimage.domain.Candidate;
import com.knocksfornometer.mapimage.domain.Candidates;
import com.knocksfornometer.mapimage.utils.CollectionUtils;
import lombok.AllArgsConstructor;

/**
 * Generate a 'voting distribution' image.
 * 
 * For each party percentage add a party colour pixel, then shuffle the resulting pixels.
 * 
 * @author Eelco de Vlieger
 */
@AllArgsConstructor
public class ConstituencyVoteDistributionImageGeneratorExact implements ImageGenerator {

	private final Candidates candidates;

	@Override
	public void generate(WritableRaster raster) {
		int width = raster.getWidth();
		int height = raster.getHeight();
		
		if(width * height % 100 != 0)
			throw new IllegalStateException("Currently restricting pixelcount to multiples of 100 (representing vote percentage)");
		
		float[][] pixels = getVoteDistributionPixels(width, height, candidates.getCandidatesExpanded());
		CollectionUtils.shuffle(pixels); // make the distribution more realistic by shuffling the pixel locations

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int pixelIndex = x * height + y;
				raster.setPixel(x, y, pixels[pixelIndex]);
			}	
		}
	}

	private float[][] getVoteDistributionPixels(int width, int height, Candidate[] candidatesExpanded) {
		float[][] pixels = new float[width * height][];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int pixelIndex = x * height + y;
				pixels[pixelIndex] = getPixel( candidatesExpanded[pixelIndex % 100] );
			}	
		}
		
		return pixels;
	}

	private float[] getPixel(Candidate party) {
		Color color = party.getColor();
		return new float[]{color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()};
	}
}