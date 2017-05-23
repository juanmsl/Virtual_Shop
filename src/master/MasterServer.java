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
	private boolean proccesing;
	
	public MasterServer() throws RemoteException, UnknownHostException {
		this.users = new HashMap<>();
		this.servers = new Stack<>();
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
					if (transaction.validate()) {
						InterfaceClient user = MasterServer.this.users.get(transaction.getHost());
						while (this.servers.size() > 0) {
							InterfaceServer server = this.servers.peek();
							try {
								server.commit(user.getUsername(), transaction.getCart());
								this.sincronizedServers(server);
								user.getItems(true);
								break;
							}
							catch (RemoteException event) {
								System.out.println("Error: [" + event.getMessage() + "]");
								this.servers.pop();
							}
						}
						if (this.servers.size() == 0) {
							user.getItems(true);
						}
					}
				}
			} else {
				this.proccesing = false;
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
	
	public void sincronizedServers(InterfaceServer mainServer) throws RemoteException {
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
