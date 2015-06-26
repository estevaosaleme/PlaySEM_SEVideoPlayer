package br.ufes.inf.lprm.sensoryeffect.mediaplayer.upnp;

import java.util.ArrayList;
import java.util.List;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.model.message.header.DeviceTypeHeader;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.types.DeviceType;

public class SearchSERendererDevice {

	public static UpnpService upnpService = null;
	@SuppressWarnings("rawtypes")
	public static List<Device> seRendererDeviceList = new ArrayList<Device>();
    @SuppressWarnings("rawtypes")
	public static Device selectedDevice = null; 
	
	public static void search() throws Exception {
        System.out.println("Starting Upnp service...");
        upnpService = new UpnpServiceImpl(new RegistryListenerEvents());

	    DeviceType type = new DeviceType("schemas-upnp-org", "SensoryEffectRenderer", 1);
	    upnpService.getControlPoint().search(new DeviceTypeHeader(type));
    }

}
