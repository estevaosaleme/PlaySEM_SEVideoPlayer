package br.ufes.inf.lprm.sensoryeffect.mediaplayer;

import java.awt.BorderLayout;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import br.ufes.inf.lprm.sensoryeffect.mediaplayer.timer.TimeLine;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.FullScreenStrategy;
import uk.co.caprica.vlcj.player.embedded.windows.Win32FullScreenStrategy;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class VideoPlayer {

	public static EmbeddedMediaPlayerComponent mediaPlayerComponent;
    public static JFrame frame;
    public static JMenuBar menuBar;
    public static MediaPlayerActions mediaPlayerActions;
    
    public static String vlcPath = "";
    public static String themePath = "";
    public static boolean stats = false;
    public static boolean autoPlay = false;
    
    public static JPanel panelControlInformation = new JPanel();
    public static JLabel lblStatus = new JLabel(" Stopped ");
    public static JLabel lblSeDevice = new JLabel(" Please select a SE Device ");
    public static JSlider jslider = new JSlider();
    
    public static boolean existsSem = false;
    public static String sem = "";
    public static boolean seDeviceSelected = false;
    public static String seDeviceSelectedCapabilities = "";
    public static String seDeviceCurrentTime = "";
    
    public static TimeLine timeLine = new TimeLine();
    
    public static boolean autoColorExtraction = false;
    public static String playSemVersion = "PlaySEM - SE Video Player v1.1";
    
    private static final String iconResource = "br/ufes/inf/lprm/sensoryeffect/mediaplayer/icon.png";
    
    public static void main(final String[] args) {
    	try {
			MediaPlayerActions.loadProperties();
			// It sets a timer
	       int interval = 50;
	       Timer timer = new Timer();
	       timer.scheduleAtFixedRate(timeLine, new Date(), interval);
		} catch (IOException e) {
			e.printStackTrace();
		}

    	if (!new File(vlcPath).exists()){
    		JOptionPane.showMessageDialog(null, "Invalid VLC path or it is not installed. Please set up the file 'config.properties' properly.", "Error",JOptionPane.ERROR_MESSAGE);
    		System.exit(1);
    	}
    	
	    NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), vlcPath);
	    Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);

	    VideoPlayerTheme.setTheme(VideoPlayer.themePath);
	    
	    SwingUtilities.invokeLater(new Runnable() {
	        @Override
	        public void run() {
	            new VideoPlayer(args);
	        }
	    });
    }

    @SuppressWarnings("serial")
	private VideoPlayer(String[] args) {
        frame = new JFrame(playSemVersion);
        frame.addWindowListener(new VideoPlayerWindowListenerEvents());
        
        setIconApp(frame);
        
        mediaPlayerComponent = new EmbeddedMediaPlayerComponent() {
            @Override
            protected FullScreenStrategy onGetFullScreenStrategy() {
                return new Win32FullScreenStrategy(frame);
            }
        };
        mediaPlayerActions = new MediaPlayerActions(mediaPlayerComponent);
        
        frame.setContentPane(mediaPlayerComponent);
        
        frame.setLocation(100, 100);
        frame.setSize(720, 480);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        mediaPlayerComponent.getMediaPlayer().addMediaPlayerEventListener(new MediaPlayerEvents());   
        
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new KeyEventDispatcherEvents());
        
        /* Status bar */
        JSplitPane splitPane = new JSplitPane();
        splitPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
        splitPane.setLeftComponent(lblStatus);
        lblSeDevice.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent arg0) {
        		mediaPlayerActions.seStatusWindowOrSearchDevice();
        	}
        });
        splitPane.setRightComponent(lblSeDevice);
        splitPane.setDividerSize(1);
        splitPane.setDividerLocation(240);
        jslider.addChangeListener(new ChangeListener() {
        	public void stateChanged(ChangeEvent arg0) {
        		JSlider source = (JSlider)arg0.getSource();
        		if (source.getValueIsAdjusting()) {
        			mediaPlayerActions.positionVideoAt(source.getValue());
        		}
        	}
        });
        jslider.setValue(0);
        panelControlInformation.setLayout(new BorderLayout());
        panelControlInformation.add(jslider, BorderLayout.NORTH);
        panelControlInformation.add(splitPane, BorderLayout.SOUTH);
        frame.getContentPane().add(panelControlInformation, BorderLayout.SOUTH);
        
        /* Menu bar */
        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
        JMenu mnOpen = new JMenu("File");
        menuBar.add(mnOpen);
        
        JMenuItem mntmNewMenuItem = new JMenuItem("Open Video");
        mntmNewMenuItem.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		mediaPlayerActions.openVideo();
        	}
        });
        mntmNewMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.ALT_MASK));
        mnOpen.add(mntmNewMenuItem);
        
        JMenuItem mntSearchRendererDevices = new JMenuItem("Select a SE Renderer Device");
        mntSearchRendererDevices.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		mediaPlayerActions.searchRendererDevices();
        	}
        });
        mntSearchRendererDevices.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.ALT_MASK));
        mnOpen.add(mntSearchRendererDevices);
        
        JMenuItem mntConfig = new JMenuItem("Config");
        mntConfig.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		mediaPlayerActions.config();
        	}
        });
        mntConfig.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_MASK));
        mnOpen.add(mntConfig);
        
        JMenuItem mntExit = new JMenuItem("Exit");
        mntExit.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		mediaPlayerActions.exit();
        	}
        });
        mntExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.ALT_MASK));
        mnOpen.add(mntExit);
        
        JMenu mnControls = new JMenu("Controls");
        menuBar.add(mnControls);
        
        JMenuItem mntmPlaypause = new JMenuItem("Play/Pause");
        mntmPlaypause.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		mediaPlayerActions.playPauseVideo();
        	}
        });
        mntmPlaypause.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.ALT_MASK));
        mnControls.add(mntmPlaypause);
        
        JMenuItem mntmStop = new JMenuItem("Stop");
        mntmStop.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		mediaPlayerActions.stopVideo();
        	}
        });
        mntmStop.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_MASK));
        mnControls.add(mntmStop);
        
        JMenuItem mntmFullscreen = new JMenuItem("FullScreen");
        mntmFullscreen.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		mediaPlayerActions.fullScreen();
        	}
        });
        mntmFullscreen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.ALT_MASK));
        mnControls.add(mntmFullscreen);
        
        final JCheckBoxMenuItem mntmAutoplay = new JCheckBoxMenuItem("Autoplay");
        mntmAutoplay.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		autoPlay = mntmAutoplay.getState();
        	}
        });
        mntmAutoplay.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.ALT_MASK));
        mnControls.add(mntmAutoplay);
        
        JMenu mnAbout = new JMenu("About");
        menuBar.add(mnAbout);
        JMenuItem mntAbout = new JMenuItem("About");
        mntAbout.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		mediaPlayerActions.about();
        	}
        });
        mnAbout.add(mntAbout);
    }
    
    public static BufferedImage getFrame() {
		if (mediaPlayerComponent.getMediaPlayer() != null)
			return mediaPlayerComponent.getMediaPlayer().getVideoSurfaceContents();
		else
			return null;
	}
    
    public static void setIconApp(JFrame jFrame){
    	ImageIcon image = null;
		try {
			image = new ImageIcon(ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream(iconResource)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (image != null)
			jFrame.setIconImage(image.getImage());
    }
    
    public static void setIconApp(JDialog jDialog){
    	ImageIcon image = null;
		try {
			image = new ImageIcon(ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream(iconResource)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (image != null)
			jDialog.setIconImage(image.getImage());
    }
}
