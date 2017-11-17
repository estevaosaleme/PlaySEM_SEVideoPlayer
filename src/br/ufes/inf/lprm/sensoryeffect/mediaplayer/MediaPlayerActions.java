package br.ufes.inf.lprm.sensoryeffect.mediaplayer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import br.ufes.inf.lprm.sensoryeffect.mediaplayer.upnp.CommandSERendererDevice;
import br.ufes.inf.lprm.sensoryeffect.mediaplayer.upnp.SearchSERendererDevice;
import br.ufes.inf.lprm.sensoryeffect.mediaplayer.window.AboutWindow;
import br.ufes.inf.lprm.sensoryeffect.mediaplayer.window.ConfigWindow;
import br.ufes.inf.lprm.sensoryeffect.mediaplayer.window.SEStatusWindow;
import br.ufes.inf.lprm.sensoryeffect.mediaplayer.window.SelectSERendererDeviceWindow;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

public class MediaPlayerActions {
	
	static JFileChooser fileChooser = new JFileChooser();
	static EmbeddedMediaPlayerComponent embeddedMediaPlayerComponent;
	private static final File configFile = new File("config.properties");
	static Properties configProps;
	private static File selectedFile;
	
	private int playlistCurrentIndex = -1;
	public int getPlaylistCurrentIndex() {
		return playlistCurrentIndex;
	}
	public void setPlaylistCurrentIndex(int playlistCurrentIndex) {
		this.playlistCurrentIndex = playlistCurrentIndex;
	}
	
	private ArrayList<File> playList = new ArrayList<File>();
	public ArrayList<File> getPlayList() {
		return playList;
	}
	public void setPlayList(ArrayList<File> playList) {
		this.playList = playList;
	}

	public static JDialog semWaitMessage = null;
	private static Timer timerSemWaitMessage = null;
	
	public MediaPlayerActions(EmbeddedMediaPlayerComponent arg0){
		embeddedMediaPlayerComponent = arg0;
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		fileChooser.setMultiSelectionEnabled(true);
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(
			    "Video files", "mkv", "flv", "mov", "avi","mpg","mpeg", "ts", "qt", "wmv", "asf", "mp4", "m2v", "m4p", "m4v", "3gp", "mp2", "mpe"));
		fileChooser.setAcceptAllFileFilterUsed(false);
	}
	
	public void openVideo(){
		int result = fileChooser.showOpenDialog(embeddedMediaPlayerComponent);
		if (result == JFileChooser.APPROVE_OPTION) {
			if (fileChooser.getSelectedFiles() != null && fileChooser.getSelectedFiles().length > 0) {
				VideoPlayer.frame.setTitle(VideoPlayer.playSemVersion);
				setPlayList(new ArrayList<File>());
				playlistCurrentIndex = fileChooser.getSelectedFiles().length -1;
				for (int i = 0; i < fileChooser.getSelectedFiles().length; i++)
					playList.add(fileChooser.getSelectedFiles()[i]);
				Collections.sort(playList);
				Collections.reverse(playList);
				prepareMedia(playList.get(playlistCurrentIndex));
			}
		}
	}
	
	public void prepareMedia(int index) {
		prepareMedia(playList.get(index));
	}
	
	public void prepareMedia(File file) {
		selectedFile = file;
	    embeddedMediaPlayerComponent.getMediaPlayer().prepareMedia(file.getAbsolutePath());
	    embeddedMediaPlayerComponent.getMediaPlayer().parseMedia();
	    
	    File semFileXml = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf('.')) + ".xml");
		File semFileSem = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf('.')) + ".sem");
		
		if (semFileXml.exists() || semFileSem.exists()){
	    	VideoPlayer.existsSem = true;
	    	 if (semFileXml.exists())
				try {
					VideoPlayer.sem = readFile(semFileXml.getAbsolutePath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			else
				try {
					VideoPlayer.sem = readFile(semFileSem.getAbsolutePath());
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	 if (VideoPlayer.seDeviceSelected)
	    		 prepareSEDevice();
	    	 else {
	    		 embeddedMediaPlayerComponent.getMediaPlayer().playMedia(file.getAbsolutePath());
	    		 embeddedMediaPlayerComponent.getMediaPlayer().stop();
	    		 VideoPlayer.lblStatus.setText(" Ready to play ");
	    	 }
	    } 
	    else {
	    	VideoPlayer.existsSem = false;
	    	VideoPlayer.sem = "";
	    	VideoPlayer.autoColorExtraction = false;
	    	embeddedMediaPlayerComponent.getMediaPlayer().playMedia(file.getAbsolutePath());
	    	embeddedMediaPlayerComponent.getMediaPlayer().stop();
	    	VideoPlayer.lblStatus.setText(" Ready to play ");
	    }
	}
	
	private void prepareSEDevice(){
		try {
			CommandSERendererDevice.activeReceiveEvents();
			Thread.sleep(100);
			
			timerSemWaitMessage = new Timer(30000, new ActionListener(){
	            public void actionPerformed(ActionEvent e) {
	            	finishFailLoadSEM();
	            }
	        });
			timerSemWaitMessage.start();
			
			CommandSERendererDevice.setSem(VideoPlayer.sem, embeddedMediaPlayerComponent.getMediaPlayer().getMediaMeta().getLength());

			JOptionPane pane = new JOptionPane("Loading Sensory Effect Metadata (SEM) to SE Device...", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
			semWaitMessage = pane.createDialog("Please wait");
			semWaitMessage.setModal(true);
			VideoPlayer.setIconApp(semWaitMessage);
			semWaitMessage.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			semWaitMessage.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void finishSucessLoadSEM(){
		semWaitMessage.setVisible(false);
		try {
			CommandSERendererDevice.getLightAutoExtraction();
		} catch (Exception e) {
			e.printStackTrace();
		}
		embeddedMediaPlayerComponent.getMediaPlayer().playMedia(selectedFile.getAbsolutePath());
		embeddedMediaPlayerComponent.getMediaPlayer().stop();
		VideoPlayer.lblStatus.setText(" Ready to play ");
		if (timerSemWaitMessage != null)
			timerSemWaitMessage.stop();
	}
	
	public static void finishFailLoadSEM(){	
		semWaitMessage.dispose();
		JOptionPane pane = new JOptionPane("Loading failed. Please restart the application and try again.", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
		semWaitMessage = pane.createDialog("Error");
		semWaitMessage.setModal(false);
		VideoPlayer.setIconApp(semWaitMessage);
		semWaitMessage.setVisible(true);	
		if (timerSemWaitMessage != null)
			timerSemWaitMessage.stop();
	}
	
	private String readFile(String fileName) throws IOException {
	    BufferedReader br = new BufferedReader(new FileReader(fileName));
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        return sb.toString();
	    } finally {
	        br.close();
	    }
	}
	
	public void stopVideo(){
		if (embeddedMediaPlayerComponent.getMediaPlayer().getTime() > 0){
			embeddedMediaPlayerComponent.getMediaPlayer().stop();
			if (VideoPlayer.existsSem && VideoPlayer.seDeviceSelected){
				try {
					CommandSERendererDevice.setStop();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}	
			
		}
	}
	
	public void playPauseVideo(){
		if (embeddedMediaPlayerComponent.getMediaPlayer().isPlaying()){
			embeddedMediaPlayerComponent.getMediaPlayer().pause();
			if (VideoPlayer.existsSem && VideoPlayer.seDeviceSelected){
				try {
					CommandSERendererDevice.setPause(embeddedMediaPlayerComponent.getMediaPlayer().getTime());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		else{
			embeddedMediaPlayerComponent.getMediaPlayer().play();
			if (VideoPlayer.existsSem && VideoPlayer.seDeviceSelected){
				try {
					CommandSERendererDevice.setPlay(embeddedMediaPlayerComponent.getMediaPlayer().getTime());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	
	public void positionVideoAt(float percentual){
		if (VideoPlayer.existsSem && VideoPlayer.seDeviceSelected){
			try {
				if (embeddedMediaPlayerComponent.getMediaPlayer().isPlaying())
					playPauseVideo();
				CommandSERendererDevice.setCurrentTime(embeddedMediaPlayerComponent.getMediaPlayer().getTime());
				if (embeddedMediaPlayerComponent.getMediaPlayer().isPlaying())
					playPauseVideo();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		embeddedMediaPlayerComponent.getMediaPlayer().setPosition(percentual / 100);
	}
	
	public void fullScreen(){
		if (embeddedMediaPlayerComponent.getMediaPlayer().isFullScreen()){
			embeddedMediaPlayerComponent.getMediaPlayer().setFullScreen(false);
			VideoPlayer.menuBar.setVisible(true);
			VideoPlayer.panelControlInformation.setVisible(true);
		}
		else{
			embeddedMediaPlayerComponent.getMediaPlayer().setFullScreen(true);
			VideoPlayer.menuBar.setVisible(false);
			VideoPlayer.panelControlInformation.setVisible(false);
		}
	}
	
	public void exit(){
		if(JOptionPane.showConfirmDialog(null, "Are you sure?", "Exit?", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
			try {
				stopVideo();
				if (SearchSERendererDevice.upnpService != null)
					SearchSERendererDevice.upnpService.shutdown();
			}
			catch(Exception ex) {}
			System.exit(0);
		}
	}
	
	public void searchRendererDevices(){
		@SuppressWarnings("unused")
		SelectSERendererDeviceWindow searchSERendererDeviceWindow = new SelectSERendererDeviceWindow();
	}
	
	public void config(){
		@SuppressWarnings("unused")
		ConfigWindow configWindow = new ConfigWindow();
	}
		
	public static void loadProperties() throws IOException {
		Properties defaultProps = new Properties();
		if (configFile.exists()){
			InputStream inputStream = new FileInputStream(configFile);
			configProps = new Properties(defaultProps);
			configProps.load(inputStream);
			VideoPlayer.vlcPath = configProps.getProperty("vlc_path");
			VideoPlayer.themePath = configProps.getProperty("theme_path");
			if ("1".equalsIgnoreCase(configProps.getProperty("stats")) || "yes".equalsIgnoreCase(configProps.getProperty("stats")) || 
                    "true".equalsIgnoreCase(configProps.getProperty("stats")) || "on".equalsIgnoreCase(configProps.getProperty("stats")))
				VideoPlayer.stats = true;
			else
				VideoPlayer.stats = false;
			inputStream.close();
		}
		else {
			configProps = new Properties(defaultProps);
			configProps.setProperty("vlc_path", "C:\\Program Files\\VideoLAN\\VLC");
			configProps.setProperty("theme_path", "my.theme");
			configProps.setProperty("stats", "false");
			VideoPlayer.vlcPath = configProps.getProperty("vlc_path");
			VideoPlayer.themePath = configProps.getProperty("theme_path");
			VideoPlayer.stats = Boolean.parseBoolean(configProps.getProperty("stats"));
			OutputStream outputStream = new FileOutputStream(configFile);
			configProps.store(outputStream, "PlaySEM Video Player - Settings");
			outputStream.close();
		}
	}
	
	public void about(){
		@SuppressWarnings("unused")
		AboutWindow aboutWindow = new AboutWindow();
	}
	
	public void seStatusWindowOrSearchDevice(){
		if (VideoPlayer.seDeviceSelected){
			@SuppressWarnings("unused")
			SEStatusWindow seStatusWindow = new SEStatusWindow();
		}
		else
			searchRendererDevices();
	}
}
