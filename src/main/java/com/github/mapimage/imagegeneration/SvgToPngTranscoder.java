package com.github.mapimage.imagegeneration;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import java.io.*;

import static org.apache.batik.transcoder.SVGAbstractTranscoder.KEY_HEIGHT;

public class SvgToPngTranscoder {

    public void transcode(final File svgFile, final String imageOutputPath, final float outputHeightPx) throws IOException, TranscoderException {
        var pngTranscoder = new PNGTranscoder();

        // KEY_MM_PER_PIXEL to specify the number of millimeters in each pixel.
        // pngTranscoder.addTranscodingHint(KEY_MM_PER_PIXEL);

        // see ImageTranscoder
        // Two transcoding hints (KEY_WIDTH and KEY_HEIGHT) can be used to respectively specify the image width and the image height. If only one of these keys is specified, the transcoder preserves the aspect ratio of the original image.

        pngTranscoder.addTranscodingHint(KEY_HEIGHT, outputHeightPx);

        try(var inputStreamReader = new InputStreamReader(new FileInputStream(svgFile))) {
            // Create the transcoder input
            var input = new TranscoderInput(inputStreamReader);

            // Create the transcoder output
            try (var fileOutputStream = new FileOutputStream(imageOutputPath)) {
                var output = new TranscoderOutput(fileOutputStream);

                // Perform the transcoding
                pngTranscoder.transcode(input, output);
            }
        }
    }
}
