package br.ufes.inf.lprm.sensoryeffect.mediaplayer.upnp;

import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;

import br.ufes.inf.lprm.sensoryeffect.mediaplayer.VideoPlayer;

public class RegistryListenerEvents implements RegistryListener {

	public void remoteDeviceDiscoveryStarted(Registry registry,
            RemoteDevice device) {
	}
	
	public void remoteDeviceDiscoveryFailed(Registry registry,
	           RemoteDevice device,
	           Exception ex) {
		SearchSERendererDevice.seRendererDeviceList.remove(device);
	}
	
	public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
		SearchSERendererDevice.seRendererDeviceList.add(device);
	}
	
	public void remoteDeviceUpdated(Registry registry, RemoteDevice device) {
		SearchSERendererDevice.seRendererDeviceList.indexOf(device);
		SearchSERendererDevice.seRendererDeviceList.remove(device);
		SearchSERendererDevice.seRendererDeviceList.add(device);
	}
	
	public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
		SearchSERendererDevice.seRendererDeviceList.remove(device);
		if (device == SearchSERendererDevice.selectedDevice)
			VideoPlayer.lblSeDevice.setText(" Please select a SE Device ");
	}
	
	public void localDeviceAdded(Registry registry, LocalDevice device) {
		SearchSERendererDevice.seRendererDeviceList.add(device);
	}
	
	public void localDeviceRemoved(Registry registry, LocalDevice device) {
		SearchSERendererDevice.seRendererDeviceList.remove(device);
		if (device == SearchSERendererDevice.selectedDevice)
			VideoPlayer.lblSeDevice.setText(" Please select a SE Device ");
	}
	
	public void beforeShutdown(Registry registry) {
	}
	
	public void afterShutdown() {
	}

}
