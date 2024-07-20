package com.github.mapimage.imagegeneration;

import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.renderable.DeferRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.RedRable;
import org.apache.batik.ext.awt.image.spi.AbstractRegistryEntry;
import org.apache.batik.ext.awt.image.spi.JDKRegistryEntry;
import org.apache.batik.ext.awt.image.spi.MagicNumberRegistryEntry;
import org.apache.batik.ext.awt.image.spi.URLRegistryEntry;
import org.apache.batik.util.ParsedURL;

import java.awt.*;
import java.util.Base64;

public class Base64ImageUrlRegistryEntry extends AbstractRegistryEntry implements URLRegistryEntry {
    /**
     * The priority of this entry.
     * This entry should in most cases be the last entry.
     * but if one wishes one could set a priority higher and be called afterwords
     */
    public static final float PRIORITY =
            (1000 * MagicNumberRegistryEntry.PRIORITY) - 1;
    public static final String[] MIME_TYPES = new String[]{"image/gif", "image/jpg", "image/jpeg", "image/png"};

    public JDKRegistryEntry delegate = new JDKRegistryEntry();

    public Base64ImageUrlRegistryEntry() {
        super("Base64", PRIORITY, new String[0], MIME_TYPES);
    }

    @Override
    public boolean isCompatibleURL(ParsedURL parsedURL) {
        return parsedURL.getContentEncoding() != null && parsedURL.getContentEncoding().equalsIgnoreCase("base64");
    }

    @Override
    public Filter handleURL(ParsedURL parsedURL, boolean needRawData) {
        var tk = Toolkit.getDefaultToolkit();
        var imageBytes = Base64.getDecoder().decode(parsedURL.getPath());
        var img = tk.createImage(imageBytes);

        var dr = new DeferRable();
        var ri = delegate.loadImage(img, dr);

        if (ri != null) {
            return new RedRable(GraphicsUtil.wrap(ri));
        }
        return null;
    }
}
