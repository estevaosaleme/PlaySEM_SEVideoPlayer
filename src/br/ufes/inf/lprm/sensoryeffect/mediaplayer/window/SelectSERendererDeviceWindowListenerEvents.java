package br.ufes.inf.lprm.sensoryeffect.mediaplayer.window;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import br.ufes.inf.lprm.sensoryeffect.mediaplayer.upnp.SearchSERendererDevice;

public class SelectSERendererDeviceWindowListenerEvents implements WindowListener {
	@Override
	public void windowActivated(WindowEvent arg0) {	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		SelectSERendererDeviceWindow.timer.stop();
	}

	@Override
	public void windowClosing(WindowEvent arg0) { }

	@Override
	public void windowDeactivated(WindowEvent arg0) { }

	@Override
	public void windowDeiconified(WindowEvent arg0) { }

	@Override
	public void windowIconified(WindowEvent arg0) { }

	@Override
	public void windowOpened(WindowEvent arg0) {
		try {
			if (SearchSERendererDevice.upnpService == null)
				SearchSERendererDevice.search();
			SelectSERendererDeviceWindow.timer.start();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
	}
}
