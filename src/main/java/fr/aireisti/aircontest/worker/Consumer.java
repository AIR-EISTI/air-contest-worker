package fr.aireisti.aircontest.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import fr.aireisti.aircontest.worker.lib.AbstractRunner;
import fr.aireisti.aircontest.worker.lib.RunnableInfo;
import fr.aireisti.aircontest.worker.lib.RunnerResult;
import fr.aireisti.aircontest.worker.runners.RunnerPython;

public class Consumer extends DefaultConsumer {

    public Consumer(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
            throws java.io.IOException{
        String message = new String(body, "UTF-8");
        System.out.println(" [x] Received '" + message + "'");

        ObjectMapper mapper = new ObjectMapper();
        RunnableInfo runnableInfo = null;
        try {
            runnableInfo = mapper.readValue(body, RunnableInfo.class);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        AbstractRunner runner = new RunnerPython(runnableInfo);
        RunnerResult runnerResult = runner.run();
        System.out.println(" [.] Exit code" + runnerResult.getReturnCode());
        System.out.println(" [.] Stdout\n" + runnerResult.getStdout() + "\n [.]");

        AMQP.BasicProperties.Builder propsBuilder = new AMQP.BasicProperties.Builder();
        propsBuilder.correlationId(properties.getCorrelationId());

        byte[] bodyReply = mapper.writeValueAsBytes(runnerResult);

        System.out.println(properties.getReplyTo());
        getChannel().queueDeclare(properties.getReplyTo(), false, false, false, null);
        getChannel().basicPublish("", properties.getReplyTo(), propsBuilder.build(), bodyReply);
        getChannel().basicAck(envelope.getDeliveryTag(), false);
    }
}
