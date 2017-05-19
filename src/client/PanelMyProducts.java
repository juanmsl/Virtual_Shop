package client;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import Negocio.Product;
import net.miginfocom.swing.MigLayout;

public class PanelMyProducts extends JScrollPane {
	
	private static final long serialVersionUID = 1L;
	private Client client;
	private JPanel panelMyProduct;
	
	public PanelMyProducts(ClientGUI clientGUI, Client client) {
		this.client = client;
		this.panelMyProduct = new JPanel();
		this.panelMyProduct.setBackground(Color.DARK_GRAY);
		this.panelMyProduct.setLayout(new MigLayout("", "[grow][][grow]", "[]"));
		this.setViewportView(this.panelMyProduct);
		
		JPanel controlPanel = new JPanel();
		controlPanel.setBackground(Color.GRAY);
		controlPanel.setLayout(new MigLayout("", "[][grow][]", "[]"));
		this.setColumnHeaderView(controlPanel);
		
		JButton btnVerMisProductos = new JButton("Ver productos");
		btnVerMisProductos.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clientGUI.loadPanelProducts();
			}
		});
		controlPanel.add(btnVerMisProductos, "cell 0 0");
		
		JButton btnCerrarSesin = new JButton("Cerrar sesi\u00F3n");
		controlPanel.add(btnCerrarSesin, "cell 2 0,alignx left,aligny top");
		btnCerrarSesin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clientGUI.logout();
			}
		});
	}
	
	public void loadClientProducts() {
		this.panelMyProduct.removeAll();
		Map<Integer, Product> clientProducts = this.client.getClientProducts();
		Set<Integer> keys = clientProducts.keySet();
		int i = 0;
		for (int id : keys) {
			Product product = clientProducts.get(id);
			ProductView productView = new ProductView(product);
			this.panelMyProduct.add(productView, "cell 1 " + i++ + ",grow");
		}
		this.panelMyProduct.updateUI();
	}
}
