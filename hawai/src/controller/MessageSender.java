package controller;

import java.io.IOException;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

public class MessageSender {

	private final String INVOICE_QUEUE = "invoice";
	private final String WAWISION_QUEUE = "waWision";
	private final String SUGAR_QUEUE = "sugar";
	
	private Channel channel;
	private ConnectionFactory factory;
	private Connection connection;
	
	public MessageSender(String host) {
		createConnection(host);
		declareQueue();
	}

	private void declareQueue() {
		try {
			channel.queueDeclare(INVOICE_QUEUE, false, false, false, null);
			channel.queueDeclare(WAWISION_QUEUE, false, false, false, null);
			channel.queueDeclare(SUGAR_QUEUE, false, false, false, null);
		} catch (IOException e) {
			System.out.println("Channel declaration fehlgeschlagen");
		}	
	}

	private void createConnection(String host) {
		this.factory = new ConnectionFactory();
		factory.setHost(host);
		try {
			this.connection = factory.newConnection();
		} catch (IOException e) {
			System.out.println("Verbindung erstellen fehlgeschlagen");
		}
	    try {
			this.channel = connection.createChannel();
		} catch (IOException e) {
			System.out.println("Create Channel fehlgeschlagen");
		}
	}
	
//	private void closeConnection() {
//		try {
//			channel.close();
//		} catch (IOException e) {
//			System.out.println("Close Channel fehlgeschlagen");
//		}
//		try {
//			connection.close();
//		} catch (IOException e) {
//			System.out.println("Close Connection fehlgeschlagen");
//		}
//	}
	
	/* Nach jeder gesendeten Nachricht wird Connection geschlossen */
	public void sendeNachricht(String ziel, String nachricht) {
		switch (ziel) {
		case "IN":
				this.sendToInvoiceNinja(nachricht);
			break;
		case "WW":
				this.sendToWawision(nachricht);
			break;
		case "SG":
				this.sendToSugarCrm(nachricht);
			break;
		default:
			break;
		}
//		closeConnection();
	}

	private void sendToInvoiceNinja(String message) {
		try {
			channel.basicPublish("", INVOICE_QUEUE, null, message.getBytes());
		} catch (IOException e) {
			System.out.println("Nachrichte an Invoice Ninja fehlgeschlagen");
		}
	}

	private void sendToWawision(String message) {
		try {
			channel.basicPublish("", WAWISION_QUEUE, null, message.getBytes());
		} catch (IOException e) {
			System.out.println("Nachrichte an WaWision fehlgeschlagen");
		}
	}

	private void sendToSugarCrm(String message) {
		try {
			channel.basicPublish("", SUGAR_QUEUE, null, message.getBytes());
		} catch (IOException e) {
			System.out.println("Nachrichte an Sugar-CRM fehlgeschlagen");
		}
	}
	
	public static void main(String[] args) {
//		MessageSender ms = new MessageSender("localhost");
//		ms.sendeNachricht("IN", "TEST");
	}
}
