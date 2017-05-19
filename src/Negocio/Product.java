package Negocio;

import java.io.Serializable;

public class Product implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static int CONSECUTIVE = 0;
	
	private int ID;
	private String name;
	private int existence;
	private int price;
	
	public Product(String name, int existence, int price) {
		this.ID = ++Product.CONSECUTIVE;
		this.name = name;
		this.existence = existence;
		this.price = price;
	}
	
	public Product(int ID, String name, int existence, int price) {
		this.ID = ID;
		this.name = name;
		this.existence = existence;
		this.price = price;
	}
	
	public int getID() {
		return this.ID;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getExistence() {
		return this.existence;
	}
	
	public int getPrice() {
		return this.price;
	}
	
	public static int getCONSECUTIVE() {
		return Product.CONSECUTIVE;
	}
	
	public boolean have(int n) {
		return this.existence >= n;
	}
	
	public void setExistence(int existence) {
		this.existence = existence;
	}
	
	public void addExistence(int existence) {
		this.existence += existence;
	}
	
	@Override
	public boolean equals(Object obj) {
		Product product = (Product) obj;
		return product.name.equals(this.name) && (product.existence == this.existence) && (product.ID == this.ID);
	}
	
	@Override
	public String toString() {
		return "Product [ID=" + this.ID + ", name=" + this.name + ", existence=" + this.existence + ", price=" + this.price + "]";
	}
}
