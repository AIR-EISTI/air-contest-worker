package fr.aireisti.aircontest.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.impl.AMQBasicProperties;
import fr.aireisti.aircontest.worker.lib.RunnerResult;

import java.io.IOException;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RepplyConsumer extends DefaultConsumer {

    private ReplyHandler replyHandler;

    public RepplyConsumer(ReplyHandler replyHandler, Channel channel) {
        super(channel);
        this.replyHandler = replyHandler;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        RunnerResult runnerResult = null;
        Logger.getLogger(ReplyHandler.class.getName()).log(Level.INFO,new String(body, "UTF-8"));
        try {
            runnerResult = mapper.readValue(body, RunnerResult.class);
            this.replyHandler.notifyNewMessage(runnerResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
