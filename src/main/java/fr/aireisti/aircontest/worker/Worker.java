package fr.aireisti.aircontest.worker;

import com.rabbitmq.client.*;

import java.io.IOException;

public class Worker {
    private final static String TASK_QUEUE_NAME = Config.get("workers.taskQueueName", DefaultConfig.TASK_QUEUE_NAME);

    public static void main(String[] argv) throws java.io.IOException, java.lang.InterruptedException, java.util.concurrent.TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Config.get("workers.rabbitmqHost", DefaultConfig.RABBITMQ_HOST));
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
        System.out.println("-> Waiting for messages. To exit press CTRL+C...");

        channel.basicQos(1);

        Consumer consumer = new Consumer(channel);
        try {
            channel.basicConsume(TASK_QUEUE_NAME, false, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
