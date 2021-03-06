package fr.aireisti.aircontest.worker;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import fr.aireisti.aircontest.worker.lib.RunnerResult;

import java.io.IOException;
import java.util.Observable;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReplyHandler extends Observable {
    private final static String REPLY_QUEUE_NAME = Config.get("workers.replyQueueName", DefaultConfig.REPLY_QUEUE_NAME);
    private Connection connection = null;
    private Channel channel = null;

    public static void main(String[] argv) {
        ReplyHandler handler = new ReplyHandler();
        handler.start();
    }

    public void start() {
        Logger.getLogger(ReplyHandler.class.getName()).log(Level.INFO, "Initializing connection to Job Message Queue.");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Config.get("workers.rabbitmqHost", DefaultConfig.RABBITMQ_HOST));

        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(REPLY_QUEUE_NAME, false, false, false, null);
            channel.basicConsume(REPLY_QUEUE_NAME, true, new RepplyConsumer(this, channel));
        } catch (IOException | TimeoutException e) {
            Logger.getLogger(ReplyHandler.class.getName()).log(Level.SEVERE, "Failed to initialize connection to RabbitMQ.", e);
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            if (channel != null && channel.isOpen())
                channel.close();
            if (connection != null && connection.isOpen())
                connection.close();
        } catch (IOException | TimeoutException e) {
            Logger.getLogger(ReplyHandler.class.getName()).log(Level.SEVERE, "Failed to close connection to RabbitMQ", e);
        }
    }

    public void notifyNewMessage(RunnerResult runnerResult) {
        setChanged();
        try {
            notifyObservers(runnerResult);
        } catch (Exception e) {
            e.printStackTrace();
            clearChanged();
        }
    }
}
