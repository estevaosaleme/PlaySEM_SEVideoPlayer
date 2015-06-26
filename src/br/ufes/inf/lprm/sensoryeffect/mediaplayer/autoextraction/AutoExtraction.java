package br.ufes.inf.lprm.sensoryeffect.mediaplayer.autoextraction;

import java.awt.image.BufferedImage;

public class AutoExtraction {
	
	public static String[] autoColorCalculationToHex(BufferedImage img) {
		if (img != null) {
			int splitter = (int)(img.getWidth()/3);
        	BufferedImage frameL = new BufferedImage(splitter, img.getHeight(), BufferedImage.TYPE_INT_RGB);
        	BufferedImage frameM = new BufferedImage(img.getWidth()-(splitter*2), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        	BufferedImage frameR = new BufferedImage(splitter, img.getHeight(), BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < img.getWidth(); x++) {
                for (int y = 0; y < img.getHeight(); y++) {
                   	int[] rgb = img.getRaster().getPixel(x, y, (int[])null);
                    // create a picture ...
                    if (x < splitter)
                    	frameL.getRaster().setPixel(x, y, rgb);
                    else if (x >= splitter && x < (img.getWidth()-splitter))
                    	frameM.getRaster().setPixel(x-splitter, y, rgb);
                    else if (x >= (img.getWidth()-splitter))
                    	frameR.getRaster().setPixel((x-(img.getWidth()-splitter)), y, rgb);
                }
            }
            // calculate left part of frame
			Histogram histo = new Histogram();
			histo.extract(frameL);
		    int[] avgColor = histo.getAverageColor();
		    String hexL = rgbToHex(avgColor[0], avgColor[1], avgColor[2]);
		    
		    //calculate center part of frame
		    histo.extract(frameM);
		    avgColor = histo.getAverageColor();
		    String hexM = rgbToHex(avgColor[0], avgColor[1], avgColor[2]);
		    
		    //calculate right part of frame
		    histo.extract(frameR);
		    avgColor = histo.getAverageColor();
		    String hexR = rgbToHex(avgColor[0], avgColor[1], avgColor[2]);
		    return  new String[] {hexL, hexM, hexR};
		}
		else
			return new String[] {"#FFFFFF", "#FFFFFF", "#FFFFFF"};
	}
	
	private static String rgbToHex(int r, int g, int b) {
		return String.format("#%02x%02x%02x", r, g, b);
	}
}
