package client;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import Negocio.Product;
import net.miginfocom.swing.MigLayout;

public class PanelProducts extends JScrollPane {
	
	private static final long serialVersionUID = 1L;
	private Client client;
	private JPanel panelProducts;
	private List<ProductView> productsViews;
	
	public PanelProducts(ClientGUI clientGUI, Client client) {
		this.client = client;
		this.panelProducts = new JPanel();
		this.panelProducts.setBackground(Color.DARK_GRAY);
		this.panelProducts.setLayout(new MigLayout("", "[grow][][grow]", "[]"));
		this.setViewportView(this.panelProducts);
		
		JPanel controlPanel = new JPanel();
		controlPanel.setBackground(Color.GRAY);
		controlPanel.setLayout(new MigLayout("", "[][grow][][grow][]", "[]"));
		this.setColumnHeaderView(controlPanel);
		
		JButton btnVerMisProductos = new JButton("Ver mis productos");
		btnVerMisProductos.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clientGUI.loadPanelMyProducts();
			}
		});
		controlPanel.add(btnVerMisProductos, "cell 0 0");
		
		JButton btnComprar = new JButton("Comprar");
		btnComprar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PanelProducts.this.checkOut();
			}
		});
		controlPanel.add(btnComprar, "cell 2 0,alignx left,aligny top");
		
		JButton btnCerrarSesin = new JButton("Cerrar sesi\u00F3n");
		controlPanel.add(btnCerrarSesin, "cell 4 0,alignx left,aligny top");
		btnCerrarSesin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clientGUI.logout();
			}
		});
	}
	
	public void checkOut() {
		Map<Integer, Product> cart = new HashMap<>();
		for (ProductView productView : this.productsViews) {
			Product nestedProduct = productView.getNestedProduct();
			cart.put(nestedProduct.getID(), nestedProduct);
		}
		this.client.setCart(cart);
		try {
			this.client.commit();
		}
		catch (RemoteException event) {
			System.out.println("Error: [" + event.getMessage() + "]");
			event.printStackTrace();
		}
	}
	
	public void loadProducts() {
		this.panelProducts.removeAll();
		this.productsViews = new ArrayList<>();
		Map<Integer, Product> products;
		try {
			products = this.client.getProducts();
			Set<Integer> keys = products.keySet();
			int i = 0;
			for (int id : keys) {
				Product product = products.get(id);
				if (product.getExistence() > 0) {
					ProductView productView = new ProductView(product);
					this.productsViews.add(productView);
					this.panelProducts.add(productView, "cell 1 " + i++ + ",grow");
				}
			}
			this.panelProducts.updateUI();
		}
		catch (RemoteException event) {
			System.out.println("Error: [" + event.getMessage() + "]");
		}
	}
	
}
