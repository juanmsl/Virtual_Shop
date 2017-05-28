package client;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class ClientGUI extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private Client client;
	private JPanel contentPane;
	private PanelLogin panelLogin;
	private PanelProducts panelProducts;
	private PanelMyProducts panelMyProducts;
	
	public ClientGUI(Client client) {
		this.client = client;
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(100, 100, 914, 556);
		this.setLocationRelativeTo(null);
		this.contentPane = new JPanel();
		this.contentPane.setBackground(Color.DARK_GRAY);
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.contentPane.setLayout(new BorderLayout(0, 0));
		this.setContentPane(this.contentPane);
		
		this.panelLogin = new PanelLogin(this, client);
		this.panelProducts = new PanelProducts(this, client);
		this.panelMyProducts = new PanelMyProducts(this, client);
		
		this.contentPane.add(this.panelLogin, BorderLayout.CENTER);
	}
	
	public void loadPanelProducts() {
		this.contentPane.removeAll();
		this.contentPane.add(this.panelProducts, BorderLayout.CENTER);
		this.contentPane.updateUI();
	}
	
	public void loadPanelMyProducts() {
		this.contentPane.removeAll();
		this.contentPane.add(this.panelMyProducts, BorderLayout.CENTER);
		this.contentPane.updateUI();
	}
	
	public void loadLists() {
		this.panelProducts.loadProducts();
		this.panelMyProducts.loadClientProducts();
	}
	
	public void logout() {
		this.client.logout();
		this.setTitle("");
		this.contentPane.removeAll();
		this.contentPane.add(this.panelLogin, BorderLayout.CENTER);
		this.contentPane.updateUI();
	}
	
	public void notify(boolean isOK) {
		/* if (isOK) { JOptionPane.showMessageDialog(this, "Compra realizada"); } else { JOptionPane.showMessageDialog(this, "Surgio un problema, vuelva a intentar"); } */
		System.out.println("Respondio");
	}
}
