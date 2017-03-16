package br.ufes.inf.lprm.sensoryeffect.mediaplayer;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import br.ufes.inf.lprm.sensoryeffect.mediaplayer.upnp.SearchSERendererDevice;

public class VideoPlayerWindowListenerEvents implements WindowListener {
	@Override
	public void windowActivated(WindowEvent arg0) {
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		VideoPlayer.mediaPlayerActions.stopVideo();
		if (SearchSERendererDevice.upnpService != null)
			SearchSERendererDevice.upnpService.shutdown();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	
	}
}
