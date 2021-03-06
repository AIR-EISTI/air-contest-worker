package fr.aireisti.aircontest.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import com.sun.org.apache.regexp.internal.RE;
import fr.aireisti.aircontest.worker.lib.RunnableInfo;
import fr.aireisti.aircontest.worker.lib.RunnerResult;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class Sender {
    private final static String TASK_QUEUE_NAME = Config.get("workers.taskQueueName", DefaultConfig.TASK_QUEUE_NAME);
    private final static String REPLY_QUEUE_NAME = Config.get("workers.replyQueueName", DefaultConfig.REPLY_QUEUE_NAME);

    private Channel channel;
    private Connection connection;
    private RunnableInfo msg;

    public Sender() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Config.get("workers.rabbitmqHost", DefaultConfig.RABBITMQ_HOST));
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
    }

    public static void main(String[] argv) throws java.io.IOException, java.util.concurrent.TimeoutException {
        UUID uuid = UUID.randomUUID();
        RunnableInfo msg = new RunnableInfo(uuid.toString(),"python", "import time\ntime.sleep(7)\nprint('plop')", "");
        Sender sender = new Sender();
        try {
            sender.call(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void call(RunnableInfo msg) throws IOException, TimeoutException, InterruptedException {
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .correlationId(msg.getJobId())
                .contentType("application/json")
                .deliveryMode(2)
                .replyTo(REPLY_QUEUE_NAME)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        channel.basicPublish("", TASK_QUEUE_NAME, props, mapper.writeValueAsBytes(msg));
        System.out.println(" [x] Sent " + msg.getJobId());

        channel.close();
        connection.close();
    }

    public void call() {
        try {
            call(msg);
        } catch (IOException | TimeoutException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String prepare(String language, String code, String input) {
        String uuid = UUID.randomUUID().toString();
        msg = new RunnableInfo(uuid, language, code, input);
        return uuid;
    }
}
