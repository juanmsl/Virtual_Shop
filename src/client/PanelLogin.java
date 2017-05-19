package client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

public class PanelLogin extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private JTextField username;
	private JPasswordField password;
	private JLabel result;
	private ClientGUI clientGUI;
	private Client client;
	
	public PanelLogin(ClientGUI clientGUI, Client client) {
		this.clientGUI = clientGUI;
		this.client = client;
		this.setBackground(Color.DARK_GRAY);
		this.setLayout(new MigLayout("", "[grow][][][grow]", "[grow][][][][][][][][grow]"));
		
		JLabel lblUsuario = new JLabel("Usuario");
		lblUsuario.setForeground(Color.WHITE);
		lblUsuario.setFont(new Font("Tahoma", Font.BOLD, 14));
		this.add(lblUsuario, "cell 1 1 2 1,growx");
		
		this.username = new JTextField();
		this.add(this.username, "cell 1 2 2 1,growx");
		this.username.setColumns(10);
		
		Component horizontalStrut = Box.createHorizontalStrut(125);
		this.add(horizontalStrut, "cell 1 3");
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(125);
		this.add(horizontalStrut_1, "cell 2 3");
		
		JLabel lblContrasea = new JLabel("Contrase\u00F1a");
		lblContrasea.setForeground(Color.WHITE);
		lblContrasea.setFont(new Font("Tahoma", Font.BOLD, 14));
		this.add(lblContrasea, "cell 1 4 2 1,growx");
		
		this.password = new JPasswordField();
		this.add(this.password, "cell 1 5 2 1,growx");
		
		JButton btnRegistrarme = new JButton("Registrarme");
		btnRegistrarme.setFont(new Font("Tahoma", Font.BOLD, 11));
		this.add(btnRegistrarme, "flowx,cell 1 7,growx");
		btnRegistrarme.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PanelLogin.this.singup();
			}
		});
		
		this.result = new JLabel("");
		this.result.setForeground(Color.WHITE);
		this.result.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(this.result, "flowx,cell 0 6 4 1,alignx center");
		
		Component horizontalStrut_2 = Box.createHorizontalStrut(5);
		this.add(horizontalStrut_2, "flowx,cell 2 7");
		
		JButton btnIngresar = new JButton("Ingresar");
		btnIngresar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PanelLogin.this.login();
			}
		});
		btnIngresar.setFont(new Font("Tahoma", Font.BOLD, 11));
		this.add(btnIngresar, "cell 2 7,growx");
		
		Component horizontalStrut_3 = Box.createHorizontalStrut(5);
		this.add(horizontalStrut_3, "cell 1 7");
		
		Component verticalStrut = Box.createVerticalStrut(30);
		this.add(verticalStrut, "cell 2 6");
	}
	
	public void login() {
		String username = this.username.getText();
		String password = String.copyValueOf(this.password.getPassword());
		try {
			if (this.client.login(username, password)) {
				this.clientGUI.loadPanelProducts();
				this.clientGUI.loadLists();
			} else {
				this.result.setText("Your username or your password are wrong, or it doesn't exists");
			}
		}
		catch (RemoteException event) {
			System.out.println("Error: [" + event.getMessage() + "]");
		}
	}
	
	public void singup() {
		String username = this.username.getText();
		String password = String.copyValueOf(this.password.getPassword());
		try {
			if (this.client.signup(username, password)) {
				this.clientGUI.loadPanelProducts();
				this.clientGUI.loadLists();
			} else {
				this.result.setText("This username already exists");
			}
		}
		catch (RemoteException event) {
			System.out.println("Error: [" + event.getMessage() + "]");
		}
	}
}
