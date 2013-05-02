package WebServerGUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JToolBar;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import java.awt.FlowLayout;
import java.awt.CardLayout;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

public class MainWindow extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 712, 465);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JToolBar toolBar = new JToolBar();
		contentPane.add(toolBar, BorderLayout.NORTH);
		
		JButton btnServerActions = new JButton("Server Actions");
		toolBar.add(btnServerActions);
		
		JToolBar toolBar_1 = new JToolBar();
		contentPane.add(toolBar_1, BorderLayout.SOUTH);
		
		JSplitPane splitPane = new JSplitPane();
		toolBar_1.add(splitPane);
		
		JProgressBar progressBar = new JProgressBar();
		splitPane.setLeftComponent(progressBar);
		
		JLabel lblProgressStatusLabel = new JLabel("Progress Status Label");
		splitPane.setRightComponent(lblProgressStatusLabel);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane_2 = new JSplitPane();
		panel.add(splitPane_2, BorderLayout.CENTER);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		splitPane_2.setRightComponent(tabbedPane);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("New tab", null, panel_1, null);
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("New tab", null, panel_2, null);
		
		JPanel panel_3 = new JPanel();
		tabbedPane.addTab("New tab", null, panel_3, null);
		
		JPanel panel_4 = new JPanel();
		splitPane_2.setLeftComponent(panel_4);
		panel_4.setLayout(new BorderLayout(0, 0));
		
		JTree tree = new JTree();
		panel_4.add(tree);
	}

}
