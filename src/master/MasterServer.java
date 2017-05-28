package master;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import Negocio.Product;
import interfaces.InterfaceClient;
import interfaces.InterfaceMaster;
import interfaces.InterfaceServer;
import server.Client;

public class MasterServer extends UnicastRemoteObject implements InterfaceMaster {
	private static final long serialVersionUID = 1L;
	private Map<String, InterfaceClient> users;
	private Stack<InterfaceServer> servers;
	private String myHost;
	private Queue<Transaction> transactions;
	private boolean proccesing;
	private Map<Integer, Product> products_copy;
	private Map<String, Client> users_copy;
	private boolean firstTime;
	
	public MasterServer() throws RemoteException, UnknownHostException {
		this.users = new HashMap<>();
		this.servers = new Stack<>();
		this.products_copy = new HashMap<>();
		this.firstTime = true;
		this.users_copy = new HashMap<>();
		this.transactions = new LinkedList<>();
		this.myHost = InetAddress.getLocalHost().getHostAddress();
		this.proccesing = false;
	}
	
	private void proccess() throws RemoteException {
		while (this.proccesing) {
			Transaction transaction = MasterServer.this.transactions.poll();
			System.out.println("Corriendo");
			if (transaction != null) {
				if (!transaction.isProcesed()) {
					System.out.println("Processing transaction " + transaction.getHost());
					InterfaceClient user = MasterServer.this.users.get(transaction.getHost());
					if (transaction.validate()) {
						while (this.servers.size() > 0) {
							InterfaceServer server = this.servers.peek();
							try {
								server.commit(user.getUsername(), transaction.getCart());
								this.sincronizedServers(server);
								user.getItems(true, true);
								break;
							}
							catch (RemoteException event) {
								System.out.println("Error: [" + event.getMessage() + "]");
								this.servers.pop();
							}
						}
						if (this.servers.size() == 0) {
							this.localCommit(user.getUsername(), transaction.getCart());
							user.getItems(true, false);
						}
					} else {
						user.getItems(true, false);
					}
				}
			} else {
				this.proccesing = false;
			}
		}
	}
	
	private void localCommit(String username, Map<Integer, Product> cart) {
		Set<Integer> keyset = cart.keySet();
		Client client = this.users_copy.get(username);
		for (int id : keyset) {
			Product product_original = this.products_copy.get(id);
			Product product_cart = cart.get(id);
			
			int new_existence = product_original.getExistence() - product_cart.getExistence();
			product_original.setExistence(new_existence);
			if (product_cart.getExistence() > 0) {
				client.addProduct(product_cart);
			}
		}
	}
	
	private void initMasterServer(int port, String domainName) throws RemoteException, AlreadyBoundException {
		String masterServerName = "rmi://" + this.myHost + ":" + port + "/" + domainName;
		System.out.println("Init master server in " + masterServerName);
		Registry registry = LocateRegistry.createRegistry(port);
		registry.bind(domainName, this);
		System.out.println("Succesfull initialized");
	}
	
	@Override
	public void addClient(String host, InterfaceClient client) throws RemoteException {
		this.users.put(host, client);
		System.out.println("The client " + host + " was conected");
	}
	
	@Override
	public void addServer(String host, InterfaceServer server) throws RemoteException {
		if (this.servers.size() == 0) {
			this.servers.push(server);
			if (this.firstTime) {
				this.firstTime = false;
				this.products_copy = server.getProducts();
				this.users_copy = server.getUsers();
			} else {
				this.localSincronizedServers();
			}
		} else {
			InterfaceServer mainServer = this.servers.peek();
			this.servers.push(server);
			this.sincronizedServers(mainServer);
		}
		System.out.println("The server " + host + " was conected");
	}
	
	@Override
	public void commit(String host) throws RemoteException {
		InterfaceClient client = this.users.get(host);
		Transaction transaction = new Transaction(host, client.getCart(), client.getProducts(), this);
		this.transactions.add(transaction);
		if (!this.proccesing) {
			this.proccesing = true;
			this.proccess();
		}
		System.out.println("New transaction from " + host);
	}
	
	@Override
	public boolean validate(String host, Map<Integer, Product> products, Map<Integer, Product> cart) throws RemoteException {
		while (this.servers.size() > 0) {
			try {
				InterfaceServer server = this.servers.peek();
				return server.validate(products, cart);
			}
			catch (RemoteException event) {
				System.out.println("Error: [" + event.getMessage() + "]");
				this.servers.pop();
			}
		}
		if (this.servers.size() == 0) {
			Set<Integer> keyset = cart.keySet();
			for (int id : keyset) {
				Product product_copy = products.get(id);
				Product product_original = this.products_copy.get(id);
				Product product_cart = cart.get(id);
				
				if (product_original.equals(product_copy)) {
					if (product_original.getExistence() < product_cart.getExistence()) { return false; }
				} else {
					return false;
				}
			}
		}
		return true;
	}
	
	@Override
	public boolean signup(String username, String password) throws RemoteException {
		boolean result = false;
		while (this.servers.size() > 0) {
			try {
				InterfaceServer server = this.servers.peek();
				result = server.signup(username, password);
				this.sincronizedServers(server);
				return result;
			}
			catch (RemoteException event) {
				System.out.println("Error: [" + event.getMessage() + "]");
				this.servers.pop();
			}
		}
		if (this.servers.size() == 0) {
			if (!this.users_copy.containsKey(username)) {
				Client client = new Client(username, password);
				this.users_copy.put(username, client);
				return true;
			}
		}
		return result;
	}
	
	@Override
	public boolean login(String username, String password) throws RemoteException {
		while (this.servers.size() > 0) {
			try {
				InterfaceServer server = this.servers.peek();
				return server.login(username, password);
			}
			catch (RemoteException event) {
				System.out.println("Error: [" + event.getMessage() + "]");
				this.servers.pop();
			}
		}
		if (this.servers.size() == 0) {
			Client client = this.users_copy.get(username);
			return client.validatePassword(password);
		}
		return false;
	}
	
	@Override
	public Map<Integer, Product> getProducts() throws RemoteException {
		while (this.servers.size() > 0) {
			try {
				InterfaceServer server = this.servers.peek();
				return server.getProducts();
			}
			catch (RemoteException event) {
				System.out.println("Error: [" + event.getMessage() + "]");
				this.servers.pop();
			}
		}
		if (this.servers.size() == 0) { return this.products_copy; }
		return null;
	}
	
	@Override
	public Map<Integer, Product> getClientProducts(String username, String password) throws RemoteException {
		while (this.servers.size() > 0) {
			try {
				InterfaceServer server = this.servers.peek();
				return server.getClientProducts(username, password);
			}
			catch (RemoteException event) {
				System.out.println("Error: [" + event.getMessage() + "]");
				this.servers.pop();
			}
		}
		if (this.servers.size() == 0) {
			if (this.login(username, password)) {
				Client client = this.users_copy.get(username);
				return client.getProducts();
			}
		}
		return null;
	}
	
	public void localSincronizedServers() throws RemoteException {
		for (InterfaceServer server : this.servers) {
			server.sincronized(this.products_copy, this.users_copy);
		}
	}
	
	public void sincronizedServers(InterfaceServer mainServer) throws RemoteException {
		this.products_copy = mainServer.getProducts();
		this.users_copy = mainServer.getUsers();
		for (InterfaceServer server : this.servers) {
			server.sincronized(mainServer.getProducts(), mainServer.getUsers());
		}
	}
	
	public static void main(String[] args) {
		try {
			MasterServer masterServer = new MasterServer();
			masterServer.initMasterServer(3000, "Alefi");
		}
		catch (RemoteException | AlreadyBoundException | UnknownHostException event) {
			System.out.println("Error: [" + event.getMessage() + "]");
		}
	}
}
