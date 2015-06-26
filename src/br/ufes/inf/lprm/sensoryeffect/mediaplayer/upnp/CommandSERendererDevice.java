package br.ufes.inf.lprm.sensoryeffect.mediaplayer.upnp;

import java.util.Date;
import java.util.Map;

import javax.swing.JOptionPane;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.UnsupportedDataException;
import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.gena.RemoteGENASubscription;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.state.StateVariableValue;

import br.ufes.inf.lprm.sensoryeffect.mediaplayer.MediaPlayerActions;
import br.ufes.inf.lprm.sensoryeffect.mediaplayer.VideoPlayer;
import br.ufes.inf.lprm.sensoryeffect.mediaplayer.window.SEStatusWindow;

public class CommandSERendererDevice {
	
	@SuppressWarnings("rawtypes")
	public static Service service = null;
	
	private static boolean receivingEvents = false;
	
	public static void activeReceiveEvents(){
		if (!receivingEvents){
			SubscriptionCallback subscriptionCallback = new SubscriptionCallback(service, 500) {
			    @Override
			    public void established(@SuppressWarnings("rawtypes") GENASubscription sub) {
			    }
			    @Override
			    protected void failed(@SuppressWarnings("rawtypes") GENASubscription subscription,
			                          UpnpResponse responseStatus,
			                          Exception exception,
			                          String defaultMsg) {
			    	MediaPlayerActions.finishFailLoadSEM();
			    	
			    }
			    @Override
			    public void ended(@SuppressWarnings("rawtypes") GENASubscription sub,
			                      CancelReason reason,
			                      UpnpResponse response) {
			    }
			    @SuppressWarnings("rawtypes")
				@Override
			    public void eventReceived(GENASubscription sub) {
			        @SuppressWarnings("unchecked")
					Map<String, StateVariableValue> values = sub.getCurrentValues();
			        String semPrepared = values.get("SemPrepared").getValue().toString();
			        if ("1".equalsIgnoreCase(semPrepared) || "yes".equalsIgnoreCase(semPrepared) || 
	                        "true".equalsIgnoreCase(semPrepared) || "on".equalsIgnoreCase(semPrepared)){
			        	MediaPlayerActions.finishSucessLoadSEM();
			        	if (VideoPlayer.stats)
			        		System.err.println("/setSem /T4 / " + new Date().getTime());
			        }
			    }
	
			    @SuppressWarnings("rawtypes")
				@Override
			    public void eventsMissed(GENASubscription sub, int numberOfMissedEvents) {
			    	MediaPlayerActions.finishFailLoadSEM();
			    }
			    @Override
			    protected void invalidMessage(RemoteGENASubscription sub,
			                                  UnsupportedDataException ex) {
			    	MediaPlayerActions.finishFailLoadSEM();
			    }
			};
			SearchSERendererDevice.upnpService.getControlPoint().execute(subscriptionCallback);
			receivingEvents = true;
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void getCapabilities() throws Exception {
		if (VideoPlayer.stats){
			System.err.println("/getCapabilities /CTVP / "+ VideoPlayer.mediaPlayerComponent.getMediaPlayer().getTime());
			System.err.println("/getCapabilities /T1 / " + new Date().getTime());
		}
        Action getStatusAction = service.getQueryStateVariableAction();
		ActionInvocation getStatusInvocation = new ActionInvocation(getStatusAction);
        getStatusInvocation.setInput("varName","CapabilitiesMetadata");
        getStatusInvocation.setOutput("return",null);
        ActionCallback getStatusCallback = new ActionCallback(getStatusInvocation) {
			@Override
            public void success(ActionInvocation invocation) {
				if (VideoPlayer.stats){
					System.err.println("/getCapabilities /T3 / " + new Date().getTime());
				}
                ActionArgumentValue status  = invocation.getOutput("return");
                VideoPlayer.seDeviceSelectedCapabilities = (String)status.getValue();
            }
            @Override
            public void failure(ActionInvocation invocation,
                                UpnpResponse operation,
                                String defaultMsg) {
            	VideoPlayer.seDeviceSelectedCapabilities = "";
            	JOptionPane.showMessageDialog(null, "SE Get Capabilities Error: " + defaultMsg);
            }
        };
        SearchSERendererDevice.upnpService.getControlPoint().execute(getStatusCallback);
        if (VideoPlayer.stats){
			System.err.println("/getCapabilities /T2 / " + new Date().getTime());
		}
    }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void getCurrentTime() throws Exception {
		if (VideoPlayer.stats){
			System.err.println("/getCurrentTime /CTVP / "+ VideoPlayer.mediaPlayerComponent.getMediaPlayer().getTime());
			System.err.println("/getCurrentTime /T1 / " + new Date().getTime());
		}
        Action getStatusAction = service.getQueryStateVariableAction();
		ActionInvocation getStatusInvocation = new ActionInvocation(getStatusAction);
        getStatusInvocation.setInput("varName","CurrentTime");
        getStatusInvocation.setOutput("return",null);
        ActionCallback getStatusCallback = new ActionCallback(getStatusInvocation) {
			@Override
            public void success(ActionInvocation invocation) {
				if (VideoPlayer.stats){
					System.err.println("/getCurrentTime /T3 / " + new Date().getTime());
				}
                ActionArgumentValue status  = invocation.getOutput("return");               
                VideoPlayer.seDeviceCurrentTime = (String)status.getValue();
                SEStatusWindow.online = true;
            }
            @Override
            public void failure(ActionInvocation invocation,
                                UpnpResponse operation,
                                String defaultMsg) {
            	VideoPlayer.seDeviceCurrentTime = "";
            	SEStatusWindow.online = false;
            	JOptionPane.showMessageDialog(null, "SE Get Current Time: " + defaultMsg);
            }
        };
        SearchSERendererDevice.upnpService.getControlPoint().execute(getStatusCallback);
        if (VideoPlayer.stats){
			System.err.println("/getCurrentTime /T2 / " + new Date().getTime());
		}
    }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void getLightAutoExtraction() throws Exception {
		if (VideoPlayer.stats){
			System.err.println("/getLightAutoExtraction /CTVP / "+ VideoPlayer.mediaPlayerComponent.getMediaPlayer().getTime());
			System.err.println("/getLightAutoExtraction /T1 / " + new Date().getTime());
		}
        Action getStatusAction = service.getQueryStateVariableAction();
		ActionInvocation getStatusInvocation = new ActionInvocation(getStatusAction);
        getStatusInvocation.setInput("varName","LightAutoExtraction");
        getStatusInvocation.setOutput("return",null);
        ActionCallback getStatusCallback = new ActionCallback(getStatusInvocation) {
			@Override
            public void success(ActionInvocation invocation) {
				if (VideoPlayer.stats){
					System.err.println("/getLightAutoExtraction /T3 / " + new Date().getTime());
				}
                ActionArgumentValue status  = invocation.getOutput("return"); 
                String value = (String)status.getValue();
                if ("1".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) || 
                        "true".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value))
                	VideoPlayer.autoColorExtraction = true;
                else
                	VideoPlayer.autoColorExtraction = false;
            }
            @Override
            public void failure(ActionInvocation invocation,
                                UpnpResponse operation,
                                String defaultMsg) {
            	VideoPlayer.autoColorExtraction = false;
            	JOptionPane.showMessageDialog(null, "SE Light Auto Extraction: " + defaultMsg);
            }
        };
        SearchSERendererDevice.upnpService.getControlPoint().execute(getStatusCallback);
        if (VideoPlayer.stats){
			System.err.println("/getLightAutoExtraction /T2 / " + new Date().getTime());
		}
    }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void setPlay(long currentTime) throws Exception {
		if (VideoPlayer.stats){
			System.err.println("/setPlay /CTVP / "+ VideoPlayer.mediaPlayerComponent.getMediaPlayer().getTime());
			System.err.println("/setPlay /T1 / " + new Date().getTime());
		}
		Action getStatusAction = service.getAction("SetPlay");
		ActionInvocation getStatusInvocation = new ActionInvocation(getStatusAction);
		getStatusInvocation.setInput("CurrentTime", String.valueOf(currentTime));
        ActionCallback getStatusCallback = new ActionCallback(getStatusInvocation) {
			@Override
            public void success(ActionInvocation invocation) {          
				if (VideoPlayer.stats){
					System.err.println("/setPlay /T3 / " + new Date().getTime());
				}
            }
            @Override
            public void failure(ActionInvocation invocation,
                                UpnpResponse operation,
                                String defaultMsg) {
            	JOptionPane.showMessageDialog(null, "SE Play Error: " + defaultMsg);
            }
        };
        SearchSERendererDevice.upnpService.getControlPoint().execute(getStatusCallback);
        if (VideoPlayer.stats){
			System.err.println("/setPlay /T2 / " + new Date().getTime());
		}
    }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void setPause(long currentTime) throws Exception {
		if (VideoPlayer.stats){
			System.err.println("/setPause /CTVP / "+ VideoPlayer.mediaPlayerComponent.getMediaPlayer().getTime());
			System.err.println("/setPause /T1 / " + new Date().getTime());
		}
		Action getStatusAction = service.getAction("SetPause");
		ActionInvocation getStatusInvocation = new ActionInvocation(getStatusAction);
		getStatusInvocation.setInput("CurrentTime", String.valueOf(currentTime));
        ActionCallback getStatusCallback = new ActionCallback(getStatusInvocation) {
			@Override
            public void success(ActionInvocation invocation) {          
				if (VideoPlayer.stats){
					System.err.println("/setPause /T3 / " + new Date().getTime());
				}
            }
            @Override
            public void failure(ActionInvocation invocation,
                                UpnpResponse operation,
                                String defaultMsg) {
            	JOptionPane.showMessageDialog(null, "SE Pause Error: " + defaultMsg);
            }
        };
        SearchSERendererDevice.upnpService.getControlPoint().execute(getStatusCallback);
        if (VideoPlayer.stats){
			System.err.println("/setPause /T2 / " + new Date().getTime());
		}
    }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void setStop() throws Exception {
		if (VideoPlayer.stats){
			System.err.println("/setStop /CTVP / "+ VideoPlayer.mediaPlayerComponent.getMediaPlayer().getTime());
			System.err.println("/setStop /T1 / " + new Date().getTime());
		}
		Action getStatusAction = service.getAction("SetStop");
		ActionInvocation getStatusInvocation = new ActionInvocation(getStatusAction);
        ActionCallback getStatusCallback = new ActionCallback(getStatusInvocation) {
			@Override
            public void success(ActionInvocation invocation) {          
				if (VideoPlayer.stats){
					System.err.println("/setStop /T3 / " + new Date().getTime());
				}
            }
            @Override
            public void failure(ActionInvocation invocation,
                                UpnpResponse operation,
                                String defaultMsg) {
            	JOptionPane.showMessageDialog(null, "SE Stop Error: " + defaultMsg);
            }
        };
        SearchSERendererDevice.upnpService.getControlPoint().execute(getStatusCallback);
        if (VideoPlayer.stats){
			System.err.println("/setStop /T2 / " + new Date().getTime());
		}
    }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void setCurrentTime(long currentTime) throws Exception {
		if (VideoPlayer.stats){
			System.err.println("/setCurrentTime /CTVP / "+ VideoPlayer.mediaPlayerComponent.getMediaPlayer().getTime());
			System.err.println("/setCurrentTime /T1 / " + new Date().getTime());
		}
		Action getStatusAction = service.getAction("SetCurrentTime");
		ActionInvocation getStatusInvocation = new ActionInvocation(getStatusAction);
		getStatusInvocation.setInput("CurrentTime", String.valueOf(currentTime));
        ActionCallback getStatusCallback = new ActionCallback(getStatusInvocation) {
			@Override
            public void success(ActionInvocation invocation) {          
				if (VideoPlayer.stats){
					System.err.println("/setCurrentTime /T3 / " + new Date().getTime());
				}
            }
            @Override
            public void failure(ActionInvocation invocation,
                                UpnpResponse operation,
                                String defaultMsg) {
            	JOptionPane.showMessageDialog(null, "SE Set Current Time Error: " + defaultMsg);
            }
        };
        SearchSERendererDevice.upnpService.getControlPoint().execute(getStatusCallback);
        if (VideoPlayer.stats){
			System.err.println("/setCurrentTime /T2 / " + new Date().getTime());
		}
    }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void setSem(String sensoryEffectMetadata, long duration) throws Exception {
		if (VideoPlayer.stats){
			System.err.println("/setSem /CTVP / "+ VideoPlayer.mediaPlayerComponent.getMediaPlayer().getTime());
			System.err.println("/setSem /T1 / " + new Date().getTime());
		}
		Action getStatusAction = service.getAction("SetSem");
		ActionInvocation getStatusInvocation = new ActionInvocation(getStatusAction);
		getStatusInvocation.setInput("SensoryEffectMetadata", sensoryEffectMetadata);
		getStatusInvocation.setInput("Duration", String.valueOf(duration));
        ActionCallback getStatusCallback = new ActionCallback(getStatusInvocation) {
			@Override
            public void success(ActionInvocation invocation) {          
				if (VideoPlayer.stats){
					System.err.println("/setSem /T3 / " + new Date().getTime());
				}
            }
            @Override
            public void failure(ActionInvocation invocation,
                                UpnpResponse operation,
                                String defaultMsg) {
            	JOptionPane.showMessageDialog(null, "SEM Load Error: " + defaultMsg);
            }
        };
        SearchSERendererDevice.upnpService.getControlPoint().execute(getStatusCallback);
        if (VideoPlayer.stats){
			System.err.println("/setSem /T2 / " + new Date().getTime());
		}
    }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void setLightColors(String hexL, String hexM, String hexR) throws Exception {
		if (VideoPlayer.stats){
			System.err.println("/setLightColors /CTVP / "+ VideoPlayer.mediaPlayerComponent.getMediaPlayer().getTime());
			System.err.println("/setLightColors /T1 / " + new Date().getTime());
		}
		Action getStatusAction = service.getAction("SetLightColors");
		ActionInvocation getStatusInvocation = new ActionInvocation(getStatusAction);
		getStatusInvocation.setInput("HexLeft", hexL);
		getStatusInvocation.setInput("HexCenter", hexM);
		getStatusInvocation.setInput("HexRight", hexR);
        ActionCallback getStatusCallback = new ActionCallback(getStatusInvocation) {
			@Override
            public void success(ActionInvocation invocation) {          
				if (VideoPlayer.stats){
					System.err.println("/setLightColors /T3 / " + new Date().getTime());
				}
            }
            @Override
            public void failure(ActionInvocation invocation,
                                UpnpResponse operation,
                                String defaultMsg) {
            	JOptionPane.showMessageDialog(null, "SE Set Light Colors Error: " + defaultMsg);
            }
        };
        SearchSERendererDevice.upnpService.getControlPoint().execute(getStatusCallback);
        if (VideoPlayer.stats){
			System.err.println("/setLightColors /T2 / " + new Date().getTime());
		}
    }

}
