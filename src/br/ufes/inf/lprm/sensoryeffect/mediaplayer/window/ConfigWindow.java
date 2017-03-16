package br.ufes.inf.lprm.sensoryeffect.mediaplayer.window;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import br.ufes.inf.lprm.sensoryeffect.mediaplayer.VideoPlayer;

public class ConfigWindow extends JFrame {

	private static final long serialVersionUID = -697723265849658156L;
	
	private File configFile = new File("config.properties");
	private Properties configProps;
	
	private final JPanel contentPanel = new JPanel();
	private JTextField textVlcPath;
	private JTextField textThemePath;

	public ConfigWindow() {
		VideoPlayer.setIconApp(this);
		setTitle("Properties Config");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 385, 124);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JLabel lblVlcPath = new JLabel("VLC Path:");
		lblVlcPath.setBounds(10, 11, 58, 14);
		contentPanel.add(lblVlcPath);
		{
			JLabel lblThemePath = new JLabel("Theme Path:");
			lblThemePath.setBounds(10, 36, 75, 14);
			contentPanel.add(lblThemePath);
		}
		
		textVlcPath = new JTextField();
		textVlcPath.setBounds(85, 8, 284, 20);
		contentPanel.add(textVlcPath);
		textVlcPath.setColumns(10);
		
		textThemePath = new JTextField();
		textThemePath.setColumns(10);
		textThemePath.setBounds(85, 33, 284, 20);
		contentPanel.add(textThemePath);
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnSave = new JButton("Save");
				btnSave.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							saveProperties();
							JOptionPane.showMessageDialog(null, "Properties were saved successfully!");		
						} catch (IOException ex) {
							JOptionPane.showMessageDialog(null, "Error saving properties file: " + ex.getMessage());		
						}
					}
				});
				buttonPane.add(btnSave);
			}
		}
		
		setVisible(true);
		
		try {
			loadProperties();
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this, "The file 'config.properties' does not exist, default properties were loaded.");
		}
		textVlcPath.setText(configProps.getProperty("vlc_path"));
		textThemePath.setText(configProps.getProperty("theme_path"));
		
		setLocationRelativeTo(null);
		setResizable(false);
	}
	
	private void loadProperties() throws IOException {
		Properties defaultProps = new Properties();
		defaultProps.setProperty("vlc_path", "C:\\Program Files\\VideoLAN\\VLC");
		defaultProps.setProperty("theme_path", "themes\\DarkGrey.theme");
		configProps = new Properties(defaultProps);
		InputStream inputStream = new FileInputStream(configFile);
		configProps.load(inputStream);
		inputStream.close();
	}
	
	private void saveProperties() throws IOException {
		configProps.setProperty("vlc_path", textVlcPath.getText());
		configProps.setProperty("theme_path", textThemePath.getText());
		OutputStream outputStream = new FileOutputStream(configFile);
		configProps.store(outputStream, "Video Player settings");
		outputStream.close();
		
		VideoPlayer.vlcPath = textVlcPath.getText();
		VideoPlayer.themePath = textThemePath.getText();
	}
}
