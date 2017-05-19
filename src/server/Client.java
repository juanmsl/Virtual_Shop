package server;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import Negocio.Product;
import Utilities.Utils;

public class Client implements Serializable {
	private static final long serialVersionUID = 1L;
	private String username;
	private String password;
	private Map<Integer, Product> products;
	
	public Client(String username, String password) {
		this.username = username;
		this.password = Utils.getHash(password);
		this.products = new HashMap<>();
		System.out.println(this.username + " - " + this.password);
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public Map<Integer, Product> getProducts() {
		return this.products;
	}
	
	public boolean validatePassword(String password) {
		return this.password.equals(Utils.getHash(password));
	}
	
	public void addProduct(Product product) {
		int ID = product.getID();
		if (this.products.containsKey(ID)) {
			this.products.get(ID).addExistence(product.getExistence());
		} else {
			this.products.put(product.getID(), product);
		}
	}
	
}
