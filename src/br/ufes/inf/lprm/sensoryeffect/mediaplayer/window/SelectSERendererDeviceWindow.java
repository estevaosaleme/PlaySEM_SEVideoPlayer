package br.ufes.inf.lprm.sensoryeffect.mediaplayer.window;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.types.UDAServiceId;

import br.ufes.inf.lprm.sensoryeffect.mediaplayer.VideoPlayer;
import br.ufes.inf.lprm.sensoryeffect.mediaplayer.upnp.CommandSERendererDevice;
import br.ufes.inf.lprm.sensoryeffect.mediaplayer.upnp.SearchSERendererDevice;

public class SelectSERendererDeviceWindow extends JFrame {

	private static final long serialVersionUID = -6837698648965485874L;
	
	private final JPanel contentPanel = new JPanel();
	public static Timer timer;

	public SelectSERendererDeviceWindow() {
		VideoPlayer.setIconApp(this);
		setTitle("Select a SE Renderer Device");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
		setBounds(100, 100, 500, 219);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		addWindowListener(new SelectSERendererDeviceWindowListenerEvents());
		
		@SuppressWarnings("rawtypes")
		final JList lstFoundDevices = new JList();
		lstFoundDevices.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lstFoundDevices.setBounds(10, 31, 574, 116);
		contentPanel.add(lstFoundDevices);
		
		JLabel lblListOfDevices = new JLabel("Active devices:");
		lblListOfDevices.setBounds(10, 11, 100, 14);
		contentPanel.add(lblListOfDevices);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnRefresh = new JButton("Refresh");
				btnRefresh.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						populateFoundDevices(lstFoundDevices);
					}
				});
				buttonPane.add(btnRefresh);
			}
			{
				JButton okButton = new JButton("Set the device");
				okButton.addActionListener(new ActionListener() {
					@SuppressWarnings("rawtypes")
					public void actionPerformed(ActionEvent e) {
						if (lstFoundDevices.getSelectedIndex() > -1) {
							
							Device device = null;
							for (Device i : SearchSERendererDevice.seRendererDeviceList) {
								if ((i.getDisplayString() + " / ID=" + i.getIdentity().getUdn().getIdentifierString()).equalsIgnoreCase(lstFoundDevices.getSelectedValue().toString())){
									device = i;
									break;
								}
							}
							if (device !=null && device.getType().toString().startsWith("urn:schemas-upnp-org:device:SensoryEffectRenderer")){
								SearchSERendererDevice.selectedDevice = device;
								JOptionPane.showMessageDialog(null, SearchSERendererDevice.selectedDevice.getDisplayString() + " selected!");
								VideoPlayer.lblSeDevice.setText(" SE Device: " + SearchSERendererDevice.selectedDevice.getDisplayString() + " (Click here for details)");
								CommandSERendererDevice.service = SearchSERendererDevice.selectedDevice.findService(new UDAServiceId("RendererControl"));
								try {
									CommandSERendererDevice.getCapabilities();
								} catch (Exception e1) {
									e1.printStackTrace();
								}
								VideoPlayer.seDeviceSelected = true;
								if (VideoPlayer.existsSem){
									VideoPlayer.mediaPlayerActions.stopVideo();
									VideoPlayer.mediaPlayerActions.openVideo();
								}
								dispose();
							} else
								JOptionPane.showMessageDialog(null, "Please select a 'SensoryEffectRenderer' device type.");
						}
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		setLocationRelativeTo(null);
		setResizable(false);
		populateFoundDevices(lstFoundDevices);
		
		timer = new Timer(5000, new ActionListener(){
            public void actionPerformed(ActionEvent e) {
            	populateFoundDevices(lstFoundDevices);
            }
        });
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void populateFoundDevices(JList list){
		int selIndex = -1;
		if (list.getSelectedIndex() > -1)
			selIndex = list.getSelectedIndex();
		DefaultListModel model = new DefaultListModel();
        for (Device i : SearchSERendererDevice.seRendererDeviceList){
        	//if (i.getType().toString().startsWith("urn:schemas-upnp-org:device:SensoryEffectRenderer"))
        		model.addElement(i.getDisplayString() + " / ID=" + i.getIdentity().getUdn().getIdentifierString());
        }
        list.setModel(model);
        list.setSelectedIndex(selIndex);
	}
}
