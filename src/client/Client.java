package client;

import java.awt.EventQueue;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import Negocio.Product;
import interfaces.InterfaceClient;
import interfaces.InterfaceMaster;

public class Client extends UnicastRemoteObject implements InterfaceClient {
	private static final long serialVersionUID = 1L;
	private InterfaceMaster masterServer;
	private Map<Integer, Product> cart;
	private Map<Integer, Product> products;
	private Map<Integer, Product> clientProducts;
	private String username;
	private String password;
	private String myHost;
	protected ClientGUI frame;
	
	public Client() throws RemoteException, UnknownHostException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		this.logout();
		this.myHost = InetAddress.getLocalHost().getHostAddress();
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					Client.this.frame = new ClientGUI(Client.this);
					Client.this.frame.setVisible(true);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void initClient(String masterServerHost, int port, String domainName) throws RemoteException, NotBoundException {
		String masterServerName = "rmi://" + masterServerHost + ":" + port + "/" + domainName;
		System.out.println("Connecting client to " + masterServerName);
		Registry registry = LocateRegistry.getRegistry(masterServerHost, port);
		this.masterServer = (InterfaceMaster) registry.lookup(domainName);
		this.masterServer.addClient(this.myHost, this);
		System.out.println("Succesfull connected");
	}
	
	@Override
	public void commit() throws RemoteException {
		this.masterServer.commit(this.myHost);
	}
	
	@Override
	public void getItems(boolean update, boolean isOK) throws RemoteException {
		this.products = this.masterServer.getProducts();
		this.clientProducts = this.masterServer.getClientProducts(this.username, this.password);
		this.cart = new HashMap<>();
		if (update) {
			this.frame.loadLists();
			this.frame.notify(isOK);
		}
	}
	
	public boolean signup(String username, String password) throws RemoteException {
		if (this.masterServer.signup(username, password)) {
			this.username = username;
			this.password = password;
			this.getItems(false, true);
			return true;
		}
		return false;
	}
	
	public boolean login(String username, String password) throws RemoteException {
		if (this.masterServer.login(username, password)) {
			this.username = username;
			this.password = password;
			this.getItems(false, true);
			return true;
		}
		return false;
	}
	
	public void logout() {
		this.cart = new HashMap<>();
		this.products = new HashMap<>();
		this.clientProducts = new HashMap<>();
		this.username = null;
		this.password = null;
	}
	
	@Override
	public Map<Integer, Product> getCart() throws RemoteException {
		return this.cart;
	}
	
	@Override
	public Map<Integer, Product> getProducts() throws RemoteException {
		return this.products;
	}
	
	public Map<Integer, Product> getClientProducts() {
		return this.clientProducts;
	}
	
	public void setCart(Map<Integer, Product> cart) {
		this.cart = cart;
	}
	
	@Override
	public String getUsername() throws RemoteException {
		return this.username;
	}
	
	public static void main(String[] args) {
		String host = "localhost";
		if (args.length != 0) {
			host = args[0];
		}
		try {
			System.out.println(host);
			Client client = new Client();
			client.initClient(host, 3000, "Alefi");
		}
		catch (RemoteException | NotBoundException | UnknownHostException | ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException event) {
			System.out.println("Error: [" + event.getMessage() + "]");
			event.printStackTrace();
		}
	}
	
}
