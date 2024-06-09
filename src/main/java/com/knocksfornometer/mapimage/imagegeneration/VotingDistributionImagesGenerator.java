package com.knocksfornometer.mapimage.imagegeneration;

import com.knocksfornometer.mapimage.domain.Candidates;
import com.knocksfornometer.mapimage.domain.ElectionData;
import lombok.AllArgsConstructor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Generate the constituency voting distribution images
 */
@AllArgsConstructor
public class VotingDistributionImagesGenerator {

    private final String targetOutputBaseDir;
    private final String targetOutputImageDir;
    private final String targetOutputImageFormat;
    private final int imageWidth;
    private final int imageHeight;

    public Map<String, BufferedImage> generateImages(final ElectionData electionData) throws InterruptedException {
        final Map<String, Candidates> constituencyNameToPartyCandidates = electionData.constituencyNameToPartyCandidates();
        final ExecutorService imageGenerationThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        final long startTime = System.currentTimeMillis();

        final Map<String, BufferedImage> images = new ConcurrentHashMap<>();
        
        // create output directory if it doesn't yet exist + clean previous output
        final File mapImageDir = new File(targetOutputBaseDir + electionData.electionYearDataSource() + targetOutputImageDir);
        mapImageDir.mkdirs();
        for(File imageFile : mapImageDir.listFiles()) {
            final var deleted = imageFile.delete();
            if(!deleted){
                System.err.println("Failed to delete file: " + imageFile);
            }
        }

        for (Map.Entry<String, Candidates> entry : constituencyNameToPartyCandidates.entrySet()) {
            imageGenerationThreadPool.execute( () -> {
                final String constituencyName = entry.getKey();
                System.out.println("Generate image [constituencyName=" + constituencyName + "]");
                final BufferedImage image = generateImage( new ConstituencyVoteDistributionImageGeneratorExact( entry.getValue() ) );
                final File outputImagePath = new File(mapImageDir, electionData.constituencyKeyGenerator().toKey(constituencyName) + "." + targetOutputImageFormat);
                try {
                    ImageIO.write(image, targetOutputImageFormat, outputImagePath);
                } catch (Exception e) {
                    System.err.println("Problem writing image " + e);
                }
                images.put(constituencyName, image);
            });
        }

        // clean shutdown of thread pool tasks
        imageGenerationThreadPool.shutdown();
        final boolean finished = imageGenerationThreadPool.awaitTermination(10, TimeUnit.MINUTES);
        if(!finished)
            System.err.println("Image generation still running after 10 minutes");
        else{
            System.out.println("Image generation finished [imageCount=" + images.size() + ", durationSeconds=" + (System.currentTimeMillis() - startTime)/1000 + "]");
        }

        return images;
    }

    private BufferedImage generateImage(final ImageGenerator imageGenerator) {
        final BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        imageGenerator.generate( image.getRaster() );
        return image;
    }
}
