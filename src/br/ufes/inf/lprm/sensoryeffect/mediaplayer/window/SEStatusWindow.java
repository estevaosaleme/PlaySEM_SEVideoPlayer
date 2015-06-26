package br.ufes.inf.lprm.sensoryeffect.mediaplayer.window;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.iso.mpeg.mpegv._2010.cidl.SensoryDeviceCapabilityBaseType;

import br.ufes.inf.lprm.sensoryeffect.mediaplayer.VideoPlayer;
import br.ufes.inf.lprm.sensoryeffect.mediaplayer.mpegv.CIParser;
import br.ufes.inf.lprm.sensoryeffect.mediaplayer.upnp.CommandSERendererDevice;
import br.ufes.inf.lprm.sensoryeffect.mediaplayer.upnp.SearchSERendererDevice;

public class SEStatusWindow extends JFrame {

	private static final long serialVersionUID = -697723265849658156L;
	
	private final JPanel contentPanel = new JPanel();
	private JTable capabilities;
	private JTable information;
	
	private Object informationRows[][] = {
		      {"ID", ""},
		      {"Type", ""},
		      {"Version", ""},
		      {"Manufacturer", ""},
		      {"Model", ""},
		      {"Status", ""},
		      {"Current Time", ""},
		    };
	private final Object headers[] = {"Property", "Value"};
	public static boolean online = false;
	
	public SEStatusWindow() {
		try {
			CommandSERendererDevice.getCurrentTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		setTitle("SE Device Status");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 595, 506);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		capabilities = new JTable();
		capabilities.setBounds(12, 247, 565, 207);
		
		JScrollPane scrollPane = new JScrollPane(capabilities);
		scrollPane.setBounds(12, 216, 565, 207);
		contentPanel.add(scrollPane, BorderLayout.CENTER);
		
		{
			information = new JTable(informationRows, headers);
			information.setBounds(12, 35, 565, 176);
			
			JScrollPane scrollPaneI = new JScrollPane(information);
			scrollPaneI.setBounds(12, 35, 565, 144);
			contentPanel.add(scrollPaneI, BorderLayout.CENTER);
		}
		{
			JLabel lblInformation = new JLabel("Device Information:");
			lblInformation.setBounds(12, 13, 133, 16);
			contentPanel.add(lblInformation);
		}
		{
			JLabel lblDeviceCapabilities = new JLabel("Device Capabilities:");
			lblDeviceCapabilities.setBounds(12, 192, 133, 16);
			contentPanel.add(lblDeviceCapabilities);
		}
		
		populateInformation();
		populateCapabilities();
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnClose = new JButton("Close");
				btnClose.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				
				JButton btnRefresh = new JButton("Refresh");
				btnRefresh.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						populateInformation();
					}
				});
				buttonPane.add(btnRefresh);
				buttonPane.add(btnClose);
			}
		}

		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
	}
	
	private void populateInformation(){
		if (SearchSERendererDevice.selectedDevice != null){
			try {
				CommandSERendererDevice.getCurrentTime();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			informationRows[0][1] = SearchSERendererDevice.selectedDevice.getIdentity().getUdn().toString();
			informationRows[1][1] = SearchSERendererDevice.selectedDevice.getType().toString();
			informationRows[2][1] = SearchSERendererDevice.selectedDevice.getVersion().getMajor()+"."+SearchSERendererDevice.selectedDevice.getVersion().getMinor();
			informationRows[3][1] = SearchSERendererDevice.selectedDevice.getDetails().getManufacturerDetails().getManufacturer();
			informationRows[4][1] = SearchSERendererDevice.selectedDevice.getDetails().getModelDetails().getModelDescription();
			if (online)
				informationRows[5][1] = "Online";
			else
				informationRows[5][1] = "Offline";
			informationRows[6][1] = VideoPlayer.seDeviceCurrentTime;
			information.repaint();
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void populateCapabilities(){
		if (!VideoPlayer.seDeviceSelectedCapabilities.equals("")){
			CIParser ciParser = new CIParser();
			Vector<SensoryDeviceCapabilityBaseType> sensoryDevicesCapabilities = null;
			try {
				sensoryDevicesCapabilities = ciParser.getSensoryDevicesCapabilities(VideoPlayer.seDeviceSelectedCapabilities);
			} catch (JAXBException | IOException e1) {
				e1.printStackTrace();
			}
			
			DefaultTableModel aModel = new DefaultTableModel() {
				private static final long serialVersionUID = 1435345L;
				@Override
				public boolean isCellEditable(int row, int column) {
				return false;
				}
			};
			Object[] tableColumnNames = new Object[2];
			tableColumnNames[0] = "Property";
			tableColumnNames[1] = "Value";
			aModel.setColumnIdentifiers(tableColumnNames);
			if (sensoryDevicesCapabilities == null) {
				this.capabilities.setModel(aModel);
			} else {
				Object[] rows = new Object[2];
				ListIterator<SensoryDeviceCapabilityBaseType> listIterator = sensoryDevicesCapabilities.listIterator();
				while (listIterator.hasNext()) {
					SensoryDeviceCapabilityBaseType sensoryDevice = listIterator.next();
					rows[0] = sensoryDevice.getClass().getSimpleName() + " - id";
					rows[1] = sensoryDevice.getId();
					aModel.addRow(rows);
					rows[0] = sensoryDevice.getClass().getSimpleName() + " - locator";
					rows[1] = sensoryDevice.getLocator();
					aModel.addRow(rows);
					rows[0] = sensoryDevice.getClass().getSimpleName() + " - firstOrderDelayTime";
					rows[1] = sensoryDevice.getFirstOrderDelayTime();
					aModel.addRow(rows);
					rows[0] = sensoryDevice.getClass().getSimpleName() + " - zerothOrderDelayTime";
					rows[1] = sensoryDevice.getZerothOrderDelayTime();
					aModel.addRow(rows);
				    try {
				    	for (Field field : sensoryDevice.getClass().getDeclaredFields()) {
				    		field.setAccessible(true);
				    		rows[0] = sensoryDevice.getClass().getSimpleName() + " - " + field.getName();
				    		if (field.get(sensoryDevice) instanceof List){
								List list = (List)field.get(sensoryDevice);
								rows[1] = "";
				    			for (int z =0;z < list.size();z++){
				    				if (list.get(z) instanceof JAXBElement){
				    					List listTwo = (List)((JAXBElement)list.get(z)).getValue();
				    					rows[1] += listTwo.get(0) + ", ";
				    				}
				    				else
				    					rows[1] += list.get(z) + ", ";
				    			}
				    			if (!rows[1].toString().isEmpty())
				    				rows[1] = rows[1].toString().substring(0, rows[1].toString().length() -2);
				    		} else
				    			rows[1] = field.get(sensoryDevice);
							aModel.addRow(rows);
						}
				    } catch (SecurityException e) {
				      System.out.println(e);
				    } catch (IllegalAccessException e) {
				      System.out.println(e);
				    }
				}
				capabilities.setModel(aModel);
				capabilities.repaint();
			}
		}
	}
}
