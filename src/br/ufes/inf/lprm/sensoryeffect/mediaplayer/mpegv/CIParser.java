package br.ufes.inf.lprm.sensoryeffect.mediaplayer.mpegv;

import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.iso.mpeg.mpegv._2010.cidl.ControlInfoType;
import org.iso.mpeg.mpegv._2010.cidl.SensoryDeviceCapabilityBaseType;

public class CIParser {
	
	public Vector<SensoryDeviceCapabilityBaseType> getSensoryDevicesCapabilities(String textOfControlInformation) throws JAXBException, IOException {
		Vector<SensoryDeviceCapabilityBaseType> sensoryDevices = new Vector<SensoryDeviceCapabilityBaseType>();
		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(ControlInfoType.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			StringReader reader = new StringReader(textOfControlInformation);
			JAXBElement<ControlInfoType> jaxbElementControlInfo = unmarshaller.unmarshal(new StreamSource(reader), ControlInfoType.class);
			ControlInfoType controlInfo = jaxbElementControlInfo.getValue();
			if (controlInfo.getSensoryDeviceCapabilityList() != null && controlInfo.getSensoryDeviceCapabilityList().getSensoryDeviceCapability() != null){
				for (int i=0;i<controlInfo.getSensoryDeviceCapabilityList().getSensoryDeviceCapability().size();i++){
					sensoryDevices.add(controlInfo.getSensoryDeviceCapabilityList().getSensoryDeviceCapability().get(i));
				}
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (Exception e) {
			throw new JAXBException("Unexpected error: " + e.getMessage());
		}		
		return sensoryDevices;
	}
}