package com.github.mapimage.imagegeneration;

import java.awt.Color;
import java.awt.image.WritableRaster;
import java.util.Set;

import com.github.mapimage.domain.CandidateResult;
import com.github.mapimage.domain.CandidateResults;
import com.github.mapimage.domain.Party;
import com.github.mapimage.utils.CollectionUtils;
import lombok.AllArgsConstructor;

/**
 * Generate a 'voting distribution' image.
 * 
 * For each party percentage add a party colour pixel, then shuffle the resulting pixels.
 */
@AllArgsConstructor
public class ConstituencyVoteDistributionImageGenerator implements ImageGenerator {

	private final CandidateResults candidates;

	@Override
	public void generate(final WritableRaster raster, final Set<Party> partyFilter) {
		final int width = raster.getWidth();
		final int height = raster.getHeight();
		
		if(width * height % 100 != 0) {
			throw new IllegalStateException("Currently restricting pixel count to multiples of 100 (representing vote percentage)");
		}
		
		final float[][] pixels = getVoteDistributionPixels(width, height, candidates.getCandidateResultsExpanded(), partyFilter);
		CollectionUtils.shuffle(pixels); // make the distribution more realistic by shuffling the pixel locations

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int pixelIndex = x * height + y;
				raster.setPixel(x, y, pixels[pixelIndex]);
			}	
		}
	}

	private float[][] getVoteDistributionPixels(final int width, final int height, final CandidateResult[] candidatesExpanded, final Set<Party> partyFilter) {
		final float[][] pixels = new float[width * height][];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				final int pixelIndex = x * height + y;
				final CandidateResult candidateResult = candidatesExpanded[pixelIndex % 100];

				final float[] pixel = partyFilter.isEmpty() || partyFilter.contains(candidateResult.getParty()) ?
						getPixel(candidateResult) : getColorComponentsRGBA(Color.WHITE);

				pixels[pixelIndex] = pixel;
			}	
		}
		
		return pixels;
	}

	private float[] getPixel(final CandidateResult candidateResult) {
		var color = candidateResult.getParty().color;
		return getColorComponentsRGBA(color);
	}

	private static float[] getColorComponentsRGBA(final Color color) {
		return new float[]{color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()};
	}
}