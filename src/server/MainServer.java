package server;

import java.io.BufferedReader;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import Negocio.Product;
import Utilities.FileManager;
import interfaces.InterfaceMaster;
import interfaces.InterfaceServer;

public class MainServer extends UnicastRemoteObject implements InterfaceServer {
	private static final long serialVersionUID = 1L;
	private Map<String, Client> users;
	private Map<Integer, Product> products;
	private InterfaceMaster masterServer;
	private String myHost;
	
	public MainServer() throws RemoteException, UnknownHostException {
		this.users = new HashMap<>();
		this.products = new HashMap<>();
		this.loadProducts();
		this.myHost = InetAddress.getLocalHost().getHostAddress();
	}
	
	private void loadProducts() {
		BufferedReader buffer = FileManager.readFile(new File("products"));
		if (buffer != null) {
			String line = "";
			while ((line = FileManager.readLine(buffer)) != null) {
				StringTokenizer tokenizer = new StringTokenizer(line, ",");
				String name = tokenizer.nextToken().trim();
				int existence = Integer.parseInt(tokenizer.nextToken().trim());
				int price = Integer.parseInt(tokenizer.nextToken().trim());
				this.addProduct(name, existence, price);
			}
			FileManager.closeBuffer(buffer);
		}
	}
	
	private void initServer(String masterServerHost, int port, String domainName) throws RemoteException, NotBoundException {
		String masterServerName = "rmi://" + masterServerHost + ":" + port + "/" + domainName;
		System.out.println("Connecting server to " + masterServerName);
		Registry registry = LocateRegistry.getRegistry(masterServerHost, port);
		this.masterServer = (InterfaceMaster) registry.lookup(domainName);
		this.masterServer.addServer(this.myHost, this);
		System.out.println("Succesfull connected");
	}
	
	private void addProduct(String name, int existence, int price) {
		Product product = new Product(name, existence, price);
		int ID = Product.getCONSECUTIVE();
		this.products.put(ID, product);
	}
	
	@Override
	public boolean signup(String username, String password) {
		if (!this.users.containsKey(username)) {
			Client client = new Client(username, password);
			this.users.put(username, client);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean login(String username, String password) throws RemoteException {
		if (this.users.containsKey(username)) {
			Client client = this.users.get(username);
			return client.validatePassword(password);
		}
		return false;
	}
	
	@Override
	public void commit(String username, Map<Integer, Product> cart) throws RemoteException {
		Set<Integer> keyset = cart.keySet();
		Client client = this.users.get(username);
		for (int id : keyset) {
			Product product_original = this.products.get(id);
			Product product_cart = cart.get(id);
			
			int new_existence = product_original.getExistence() - product_cart.getExistence();
			product_original.setExistence(new_existence);
			if (product_cart.getExistence() > 0) {
				client.addProduct(product_cart);
			}
		}
	}
	
	@Override
	public Map<Integer, Product> getProducts() throws RemoteException {
		return this.products;
	}
	
	@Override
	public boolean validate(Map<Integer, Product> products, Map<Integer, Product> cart) throws RemoteException {
		Set<Integer> keyset = cart.keySet();
		for (int id : keyset) {
			Product product_copy = products.get(id);
			Product product_original = this.products.get(id);
			Product product_cart = cart.get(id);
			
			if (product_original.equals(product_copy)) {
				if (product_original.getExistence() < product_cart.getExistence()) { return false; }
			} else {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public Map<String, Client> getUsers() throws RemoteException {
		return this.users;
	}
	
	@Override
	public Map<Integer, Product> getClientProducts(String username, String password) throws RemoteException {
		if (this.login(username, password)) {
			Client client = this.users.get(username);
			return client.getProducts();
		}
		return null;
	}
	
	@Override
	public void sincronized(Map<Integer, Product> products, Map<String, Client> users) throws RemoteException {
		this.users = users;
		this.products = products;
		System.out.println("Server sincronized");
	}
	
	public static void main(String[] args) {
		String host = "localhost";
		if (args.length != 0) {
			host = args[0];
		}
		try {
			System.out.println(host);
			MainServer server = new MainServer();
			server.initServer(host, 3000, "Alefi");
		}
		catch (RemoteException | NotBoundException | UnknownHostException event) {
			System.out.println("Error: [" + event.getMessage() + "]");
		}
	}
}
