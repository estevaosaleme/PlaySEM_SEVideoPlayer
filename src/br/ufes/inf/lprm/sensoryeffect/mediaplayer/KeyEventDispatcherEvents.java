package br.ufes.inf.lprm.sensoryeffect.mediaplayer;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.Timer;

import br.ufes.inf.lprm.sensoryeffect.mediaplayer.timer.TimeLine;

public class KeyEventDispatcherEvents implements KeyEventDispatcher {
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		 if (e.getID() == KeyEvent.KEY_PRESSED) {
			 switch (e.getKeyCode()){
				case KeyEvent.VK_F : { 
					VideoPlayer.mediaPlayerActions.fullScreen();
					}
				break;
				case KeyEvent.VK_S :{
					VideoPlayer.mediaPlayerActions.stopVideo();
				}
				break;
				case KeyEvent.VK_P :{
					VideoPlayer.mediaPlayerActions.playPauseVideo();
				}
				break;
				case KeyEvent.VK_O :{
					VideoPlayer.mediaPlayerActions.openVideo();
				break;
				}
				case KeyEvent.VK_T :{
					// Synchronization tests
					TimeLine timeLine = new TimeLine();
					timeLine.setDuration(VideoPlayer.mediaPlayerComponent.getMediaPlayer().getMediaMeta().getLength());
					
					
					timeLine.play(VideoPlayer.mediaPlayerComponent.getMediaPlayer().getTime());
					VideoPlayer.mediaPlayerActions.playPauseVideo();
					
					int interval = 1;
			        Timer timer = new Timer();
			        timer.scheduleAtFixedRate(timeLine, new Date(), interval);
			        try {
			        	for (int i =0; i<11; i++){
							Thread.sleep(3000);
							if (i % 2 == 0)
								timeLine.pause(VideoPlayer.mediaPlayerComponent.getMediaPlayer().getTime());
							else
								timeLine.play(VideoPlayer.mediaPlayerComponent.getMediaPlayer().getTime());
							VideoPlayer.mediaPlayerActions.playPauseVideo();
			        	}
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
				break;
			}
		 }
		 return false;
	}
}
