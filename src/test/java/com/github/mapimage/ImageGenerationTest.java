package com.github.mapimage;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.mapimage.domain.CandidateResult;
import com.github.mapimage.domain.CandidateResults;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.mapimage.imagegeneration.ConstituencyVoteDistributionImageGenerator;
import com.github.mapimage.imagegeneration.ImageGenerator;

public class ImageGenerationTest {

	private static final CandidateResult CANDIDATE_RESULT_CON = new CandidateResult(Color.BLUE, 30);
	private static final CandidateResult CANDIDATE_RESULT_LAB = new CandidateResult(Color.RED, 20);
	private static final CandidateResult CANDIDATE_RESULT_SNP = new CandidateResult(Color.YELLOW, 10);
	private static final CandidateResult CANDIDATE_RESULT_OTHER = new CandidateResult(Color.WHITE, 5);

	private static CandidateResults candidates;
	private static CandidateResult noVoteCandidateResult;
	
	private WritableRaster raster;
	private ImageGenerator imageGenerator;
	
	@BeforeAll
	public static void setUpBeforeClass() {
		List<CandidateResult> candidateResultList = new ArrayList<>( asList(CANDIDATE_RESULT_CON, CANDIDATE_RESULT_LAB, CANDIDATE_RESULT_SNP, CANDIDATE_RESULT_OTHER) );
		noVoteCandidateResult = CandidateResult.createNoVoteCandidate(candidateResultList);
		candidateResultList.add(noVoteCandidateResult);
		candidates = new CandidateResults( candidateResultList.toArray( new CandidateResult[candidateResultList.size()] ) );
	}

	@BeforeEach
	public void setUp() {
		raster = new BufferedImage(200, 500, BufferedImage.TYPE_INT_ARGB).getRaster();
		imageGenerator = new ConstituencyVoteDistributionImageGenerator(candidates);
	}

	@Test
	public void testImagePixelCountNotMultipleOfHundred() {
		assertThrows(IllegalStateException.class, () -> imageGenerator.generate( new BufferedImage(10, 5, BufferedImage.TYPE_INT_ARGB).getRaster() ));
	}
	
	@Test
	public void testNoVoteCandidatePercentageCalculation() {
		int noVotePercentage = 100;
		for (CandidateResult candidateResult : candidates.getCandidateResults()) {
			if(candidateResult != noVoteCandidateResult) {
				noVotePercentage -= candidateResult.getPercentageOfTotalElectorate();
			}
		}
		
		assertEquals(noVoteCandidateResult.getPercentageOfTotalElectorate(), noVotePercentage);
	}
	
	@Test
	public void testGenerateImagePixelPercentageAccurate() {
		imageGenerator.generate(raster);

		int width = raster.getWidth();
		int height = raster.getHeight();
		int pixelCount = width * height;
		
		Map<Color, AtomicInteger> pixelColorCountMap = new HashMap<>();
		int[] argbColor = new int[4];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				raster.getPixel(x, y, argbColor);
				Color pixelColor = new Color(argbColor[0], argbColor[1], argbColor[2], argbColor[3]);
				AtomicInteger count = pixelColorCountMap.get(pixelColor);
				if(count == null){
					count = new AtomicInteger(1);
					pixelColorCountMap.put(pixelColor, count);
				}else{
					count.incrementAndGet();
				}
			}
		}

		for (CandidateResult candidateResult : candidates.getCandidateResults()) {
			AtomicInteger pixelColorCount = pixelColorCountMap.remove( candidateResult.getColor() );
			assertNotNull(pixelColorCount, "Can't find pixelColorCount in map [candidate=" + candidateResult + ", pixelColorCountMap=" + pixelColorCountMap + "]");
			int actualPixelCount = (int) (pixelColorCount.get() / (double)pixelCount * 100);
			assertEquals(actualPixelCount, candidateResult.getPercentageOfTotalElectorate(), "pixel percentage incorrect [candidate=" + candidateResult + "]");
		}
	}
}