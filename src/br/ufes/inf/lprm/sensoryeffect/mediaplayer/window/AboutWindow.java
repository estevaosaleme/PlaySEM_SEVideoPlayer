package br.ufes.inf.lprm.sensoryeffect.mediaplayer.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import br.ufes.inf.lprm.sensoryeffect.mediaplayer.VideoPlayer;

public class AboutWindow extends JDialog {

	private static final long serialVersionUID = -697723265849658156L;
	
	private final JPanel contentPanel = new JPanel();

	public AboutWindow() {
		setTitle("About");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 342, 219);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JTextPane txtpnSePlayer = new JTextPane();
		txtpnSePlayer.setEditable(false);
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		txtpnSePlayer.setBorder(BorderFactory.createCompoundBorder(border, 
	            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		txtpnSePlayer.setText(VideoPlayer.playSemVersion  + " - LPRM/UFES\r\nCreated by Estevao Bissoli Saleme\r\n\r\nFeatures:\r\n - Media Player.\r\n - UPnP Discovery SE Renderer Device.\r\n - UPnP Remote Control SE Renderer Device.\r\n - Supports theme settings.");
		txtpnSePlayer.setBounds(0, 0, 336, 157);
		contentPanel.add(txtpnSePlayer);
		
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
				buttonPane.add(btnClose);
			}
		}

		setLocationRelativeTo(null);
		setModal(true);
		setResizable(false);
		setVisible(true);
		
	}
}
