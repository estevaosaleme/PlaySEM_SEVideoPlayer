/*
 * This file is part of SEVino.
 *
 * SEVino is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SEVino is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SEVino.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright 2008, 2009, 2010, 2011, 2012 Alpen-Adria-Universitaet Klagenfurt, Markus Waltl.
 */
package br.ufes.inf.lprm.sensoryeffect.mediaplayer.autoextraction;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * @author Markus Waltl (Alpen-Adria-Universitaet Klagenfurt)
 * 
 * Thanks to Mathias Lux (mathias@juggle.at) for his advice
 */
public class Histogram {
    public static final int NUMBER_OF_BINS = 64;  

    private int[] pixel = new int[3];
    private int[] histogram;
    private int[] averageColor = new int[3];

    /**
     * Default constructor
     */
    public Histogram() {
        histogram = new int[NUMBER_OF_BINS];
    }

    /**
     * Extracts the color histogram from the given image.
     *
     * @param image
     */
    public void extract(BufferedImage image) {
        if (image.getColorModel().getColorSpace().getType() != ColorSpace.TYPE_RGB)
            throw new UnsupportedOperationException("Color space not supported. Only RGB.");
        
        WritableRaster raster = image.getRaster();
        int numValues = 0;
        long[] tmpColor = new long[3];
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                raster.getPixel(x, y, pixel);
                
                int curValue = quant(pixel);
                histogram[curValue]++;
                    
                // think about it because of higher colors
                //normalize(pixel);
                
                if (curValue > 0) {
                	tmpColor[0] += pixel[0];
                	tmpColor[1] += pixel[1];
                	tmpColor[2] += pixel[2];
                	numValues++;
                }
            }
        }
        
        // calculate average color
        if (numValues > 0) {
        	averageColor[0] = (int)(tmpColor[0]/numValues);
        	averageColor[1] = (int)(tmpColor[1]/numValues);
        	averageColor[2] = (int)(tmpColor[2]/numValues);
        }
    
        normalize(histogram);
    }

    private void normalize(int[] histogram) {
        // find max:
        int max = 0;
        for (int i = 0; i < histogram.length; i++) {
            max = Math.max(histogram[i], max);
        }
        for (int i = 0; i < histogram.length; i++) {
        	if (max > 0)
        		histogram[i] = (histogram[i] * 255) / max;
        	else
        		histogram[i] = (histogram[i] * 255);
        }
    }

    private int quant(int[] pixel) {
    	assert(pixel.length == 3);
    	int maxValue = 256;
        double quant = (maxValue * maxValue * maxValue) / (double) histogram.length;
        
        // B || G || R
        // 8 bit || 8 bit || 8 bit
        return (int) ((pixel[0] + pixel[1] * maxValue + pixel[2] * maxValue * maxValue) / quant);
    }

    public float getDistance(Histogram ch) {
        if ((ch.histogram.length != histogram.length))
            throw new UnsupportedOperationException("Histogram lengths do not match");

        return (float) distL2(histogram, ch.histogram);
    }

    /**
     * Euclidean distance
     */
    private static double distL2(int[] h1, int[] h2) {
        double sum = 0d;
        for (int i = 0; i < h1.length; i++) {
            sum += (h1[i] - h2[i]) * (h1[i] - h2[i]);
        }
        return Math.sqrt(sum);
    }

    public int[] getAverageColor() {
    	return averageColor;
    }
}
