package client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import Negocio.Product;
import net.miginfocom.swing.MigLayout;

public class ProductView extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private Product product;
	private int solicitados;
	private JLabel inCart;
	private JLabel existences;
	private JLabel valor;
	
	public ProductView(Product product) {
		this.setBackground(Color.WHITE);
		this.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		this.product = product;
		this.solicitados = 0;
		this.setLayout(new MigLayout("", "[][][grow][][][][][][][][]", "[][]"));
		
		JLabel label_product_id = new JLabel("ID: ");
		label_product_id.setFont(new Font("Tahoma", Font.BOLD, 11));
		this.add(label_product_id, "flowx,cell 0 0");
		
		Component horizontalStrut_3 = Box.createHorizontalStrut(20);
		this.add(horizontalStrut_3, "cell 1 0");
		
		JLabel lblPrecioUnitario = new JLabel("Precio unitario: ");
		lblPrecioUnitario.setFont(new Font("Tahoma", Font.BOLD, 11));
		this.add(lblPrecioUnitario, "flowx,cell 2 0");
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		this.add(horizontalStrut, "cell 3 0");
		
		JLabel lblExistencias = new JLabel("Existencias");
		lblExistencias.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblExistencias.setHorizontalAlignment(SwingConstants.RIGHT);
		this.add(lblExistencias, "cell 4 0");
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		this.add(horizontalStrut_1, "cell 5 0");
		
		JLabel lblNewLabel_1 = new JLabel("En el carro");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(lblNewLabel_1, "cell 6 0");
		
		JButton btnAadirAlCarrito = new JButton("Uno más");
		btnAadirAlCarrito.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ProductView.this.addProduct();
			}
		});
		
		Component horizontalStrut_2 = Box.createHorizontalStrut(20);
		this.add(horizontalStrut_2, "cell 7 0");
		
		JLabel lblValorAPagar = new JLabel("Valor a pagar");
		lblValorAPagar.setFont(new Font("Tahoma", Font.BOLD, 11));
		this.add(lblValorAPagar, "cell 8 0");
		
		Component horizontalStrut_4 = Box.createHorizontalStrut(20);
		this.add(horizontalStrut_4, "cell 9 0");
		this.add(btnAadirAlCarrito, "cell 10 0,growx,aligny center");
		
		JLabel product_name = new JLabel(product.getName());
		product_name.setFont(new Font("Tahoma", Font.BOLD, 20));
		this.add(product_name, "flowx,cell 0 1 3 1");
		
		JLabel product_id = new JLabel("" + product.getID());
		this.add(product_id, "cell 0 0,grow");
		
		this.existences = new JLabel("" + product.getExistence());
		this.existences.setFont(new Font("Tahoma", Font.PLAIN, 16));
		this.existences.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(this.existences, "cell 4 1,grow");
		
		this.inCart = new JLabel("" + this.solicitados);
		this.inCart.setHorizontalAlignment(SwingConstants.CENTER);
		this.inCart.setFont(new Font("Tahoma", Font.PLAIN, 16));
		this.add(this.inCart, "cell 6 1,grow");
		
		JButton btnUnoMenos = new JButton("Uno menos");
		btnUnoMenos.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ProductView.this.removeOne();
			}
		});
		
		this.valor = new JLabel("$ " + (product.getPrice() * this.solicitados));
		this.valor.setFont(new Font("Tahoma", Font.PLAIN, 16));
		this.valor.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(this.valor, "cell 8 1,grow");
		this.add(btnUnoMenos, "cell 10 1,growx,aligny center");
		
		JLabel price = new JLabel("$ " + NumberFormat.getInstance().format(product.getPrice()));
		this.add(price, "cell 2 0,grow");
		
	}
	
	private void removeOne() {
		if (this.solicitados > 0) {
			this.solicitados--;
			NumberFormat nf = NumberFormat.getInstance();
			this.valor.setText("$ " + nf.format(this.product.getPrice() * this.solicitados));
			this.existences.setText("" + (this.product.getExistence() - this.solicitados));
			this.inCart.setText("" + this.solicitados);
		}
		
	}
	
	private void addProduct() {
		if (this.solicitados < this.product.getExistence()) {
			this.solicitados++;
			NumberFormat nf = NumberFormat.getInstance();
			this.valor.setText("$ " + nf.format(this.product.getPrice() * this.solicitados));
			this.existences.setText("" + (this.product.getExistence() - this.solicitados));
			this.inCart.setText("" + this.solicitados);
		}
	}
	
	public Product getNestedProduct() {
		return new Product(this.product.getID(), this.product.getName(), this.solicitados, this.product.getPrice());
	}
}
