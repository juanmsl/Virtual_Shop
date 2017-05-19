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
import java.util.Stack;

import Negocio.Product;
import interfaces.InterfaceClient;
import interfaces.InterfaceMaster;
import interfaces.InterfaceServer;

public class MasterServer extends UnicastRemoteObject implements InterfaceMaster {
	private static final long serialVersionUID = 1L;
	private Map<String, InterfaceClient> users;
	private Stack<InterfaceServer> servers;
	private String myHost;
	private Queue<Transaction> transactions;
	private Thread checkTransactions;
	
	public MasterServer() throws RemoteException, UnknownHostException {
		this.users = new HashMap<>();
		this.servers = new Stack<>();
		this.transactions = new LinkedList<>();
		this.myHost = InetAddress.getLocalHost().getHostAddress();
		this.checkTransactions = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					while (true) {
						Transaction transaction = MasterServer.this.transactions.poll();
						if (transaction != null) {
							if (!transaction.isProcesed()) {
								if (transaction.validate()) {
									InterfaceClient user = MasterServer.this.users.get(transaction.getHost());
									InterfaceServer server = MasterServer.this.servers.peek();
									server.commit(user.getUsername(), transaction.getCart());
									user.getItems(true);
								}
							}
						} else {
							Thread.sleep(5000);
						}
					}
				}
				catch (RemoteException | InterruptedException event) {
					System.out.println("Error: [" + event.getMessage() + "]");
				}
			}
		});
		this.checkTransactions.start();
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
		this.servers.push(server);
		System.out.println("The server " + host + " was conected");
	}
	
	@Override
	public void commit(String host) throws RemoteException {
		InterfaceClient client = this.users.get(host);
		Transaction transaction = new Transaction(host, client.getCart(), client.getProducts(), this);
		this.transactions.add(transaction);
	}
	
	@Override
	public boolean validate(String host, Map<Integer, Product> products, Map<Integer, Product> cart) throws RemoteException {
		InterfaceServer server = this.servers.peek();
		return server.validate(products, cart);
	}
	
	@Override
	public boolean signup(String username, String password) throws RemoteException {
		boolean result = false;
		for (InterfaceServer server : this.servers) {
			result = server.signup(username, password);
		}
		return result;
	}
	
	@Override
	public boolean login(String username, String password) throws RemoteException {
		InterfaceServer server = this.servers.peek();
		return server.login(username, password);
	}
	
	@Override
	public Map<Integer, Product> getProducts() throws RemoteException {
		InterfaceServer server = this.servers.peek();
		return server.getProducts();
	}
	
	@Override
	public Map<Integer, Product> getClientProducts(String username, String password) throws RemoteException {
		InterfaceServer server = this.servers.peek();
		return server.getClientProducts(username, password);
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
