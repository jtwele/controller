package controller.recv;

import java.io.IOException;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class MessageReceiverInvoice {
	
	private final String INVOICE_QUEUE = "invoice";
	
	private ConnectionFactory factory;
	private Connection connection;
	private Channel channel;
	private QueueingConsumer consumer;
	private String receivedMessage;
	
	public MessageReceiverInvoice() {
		this.factory  = new ConnectionFactory();
		this.createConnection();
		this.declareQueue();
		this.receiveMessage(); 
	}
	
	public String getReceivedMessage(){
		return this.receivedMessage;
	}
	
	private void createConnection(){
		this.factory = new ConnectionFactory();
		this.factory.setHost("localhost");
		this.factory.setUsername("ninja");
		this.factory.setPassword("ninja");
		this.factory.setVirtualHost("/");
		try {
			this.connection = factory.newConnection();
		} catch (IOException e) {
			System.out.println("Verbindung erstellen fehlgeschlagen");
		}
		try {
			this.channel = connection.createChannel();
		} catch (IOException e) {
			System.out.println("Erstellen des Channels fehlgeschlagen");
		}
	}
	
	private void declareQueue(){
		try {
			this.channel.queueDeclare(INVOICE_QUEUE, false, false, false, null);
		} catch (IOException e) {
			System.out.println("Deklarieren der Queue fehlgeschlagen");
		}
		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
		consumer = new QueueingConsumer(channel);
		try {
			this.channel.basicConsume(INVOICE_QUEUE, true, consumer);
		} catch (IOException e) {
			System.out.println("Lesen der Nachricht fehlgeschlagen");
		}
	}
	
	private void receiveMessage(){
		
		QueueingConsumer.Delivery delivery = null;
		while(true){
			try {
				delivery = consumer.nextDelivery();
			} catch (ShutdownSignalException |ConsumerCancelledException |InterruptedException e) {
				System.out.println("irgendetwas schiefgegangen");
				e.printStackTrace();
			}
			this.receivedMessage = new String(delivery.getBody());
			System.out.println(this.receivedMessage);
		}
		
	}

}
