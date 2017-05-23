package client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.NumberFormat;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import Negocio.Product;
import net.miginfocom.swing.MigLayout;

public class ProductClientView extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	public ProductClientView(Product product) {
		this.setBackground(Color.WHITE);
		this.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		this.setLayout(new MigLayout("", "[][][grow][][]", "[][]"));
		
		JLabel label_product_id = new JLabel("ID: ");
		label_product_id.setFont(new Font("Tahoma", Font.BOLD, 11));
		this.add(label_product_id, "flowx,cell 0 0");
		
		Component horizontalStrut_3 = Box.createHorizontalStrut(20);
		this.add(horizontalStrut_3, "cell 1 0");
		
		JLabel lblPrecioUnitario = new JLabel("Precio unitario: ");
		lblPrecioUnitario.setFont(new Font("Tahoma", Font.BOLD, 11));
		this.add(lblPrecioUnitario, "flowx,cell 2 0");
		
		Component horizontalStrut = Box.createHorizontalStrut(50);
		this.add(horizontalStrut, "cell 3 0");
		
		JLabel lblExistencias = new JLabel("Unidades");
		lblExistencias.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblExistencias.setHorizontalAlignment(SwingConstants.RIGHT);
		this.add(lblExistencias, "cell 4 0");
		
		JLabel product_name = new JLabel(product.getName());
		product_name.setFont(new Font("Tahoma", Font.BOLD, 20));
		this.add(product_name, "flowx,cell 0 1 3 1");
		
		JLabel product_id = new JLabel("" + product.getID());
		this.add(product_id, "cell 0 0,grow");
		
		JLabel existences = new JLabel("" + product.getExistence());
		existences.setFont(new Font("Tahoma", Font.PLAIN, 16));
		existences.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(existences, "cell 4 1,grow");
		
		JLabel price = new JLabel("$ " + NumberFormat.getInstance().format(product.getPrice()));
		this.add(price, "cell 2 0,grow");
	}
}
