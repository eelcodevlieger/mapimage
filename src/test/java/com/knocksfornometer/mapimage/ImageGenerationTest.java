package com.knocksfornometer.mapimage;

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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.knocksfornometer.mapimage.imagegeneration.ConstituencyVoteDistributionImageGeneratorExact;
import com.knocksfornometer.mapimage.imagegeneration.ImageGenerator;

public class ImageGenerationTest {

	private static final Candidate CANDIDATE_CON = new Candidate(Color.BLUE, 30);
	private static final Candidate CANDIDATE_LAB = new Candidate(Color.RED, 20);
	private static final Candidate CANDIDATE_SNP = new Candidate(Color.YELLOW, 10);
	private static final Candidate CANDIDATE_OTHER = new Candidate(Color.WHITE, 5);

	private static Candidates candidates;
	private static Candidate noVoteCandidate;
	
	private WritableRaster raster;
	private ImageGenerator imageGenerator;
	
	@BeforeAll
	public static void setUpBeforeClass() {
		List<Candidate> candidateList = new ArrayList<>( asList(CANDIDATE_CON, CANDIDATE_LAB, CANDIDATE_SNP, CANDIDATE_OTHER) );
		noVoteCandidate = Candidate.createNoVoteCandidate(candidateList);
		candidateList.add(noVoteCandidate);
		candidates = new Candidates( candidateList.toArray( new Candidate[candidateList.size()] ) );
	}

	@BeforeEach
	public void setUp() {
		raster = new BufferedImage(200, 500, BufferedImage.TYPE_INT_ARGB).getRaster();
		imageGenerator = new ConstituencyVoteDistributionImageGeneratorExact(candidates);
	}

	@Test
	public void testImagePixelCountNotMultipleOfHundred() {
		assertThrows(IllegalStateException.class, () -> imageGenerator.generate( new BufferedImage(10, 5, BufferedImage.TYPE_INT_ARGB).getRaster() ));
	}
	
	@Test
	public void testNoVoteCandidatePercentageCalculation() {
		int noVotePercentage = 100;
		for (Candidate candidate : candidates.getCandidates()) {
			if(candidate != noVoteCandidate) {
				noVotePercentage -= candidate.getPercentage();
			}
		}
		
		assertEquals(noVoteCandidate.getPercentage(), noVotePercentage);
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

		for (Candidate candidate : candidates.getCandidates()) {
			AtomicInteger pixelColorCount = pixelColorCountMap.remove( candidate.getColor() );
			assertNotNull(pixelColorCount, "Can't find pixelColorCount in map [candidate=" + candidate + ", pixelColorCountMap=" + pixelColorCountMap + "]");
			int actualPixelCount = (int) (pixelColorCount.get() / (double)pixelCount * 100);
			assertEquals(actualPixelCount, candidate.getPercentage(), "pixel percentage incorrect [candidate=" + candidate + "]");
		}
	}
}