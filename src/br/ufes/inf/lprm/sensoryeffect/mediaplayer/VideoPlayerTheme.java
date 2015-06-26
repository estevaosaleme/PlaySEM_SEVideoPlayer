package br.ufes.inf.lprm.sensoryeffect.mediaplayer;

import java.io.File;

import javax.swing.UIManager;

import com.nilo.plaf.nimrod.NimRODLookAndFeel;
import com.nilo.plaf.nimrod.NimRODTheme;

public class VideoPlayerTheme {

	public static void setTheme(String path){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
   			NimRODTheme nt = null;
   			File f = new File(path);
   			if (f.exists()){
   				nt = new NimRODTheme(path);
	   			NimRODLookAndFeel nf = new NimRODLookAndFeel();
	   	   		NimRODLookAndFeel.setCurrentTheme(nt);
				UIManager.setLookAndFeel(nf);
   			}
		} catch (Exception e) {
			e.printStackTrace();		
		}
	}
}
