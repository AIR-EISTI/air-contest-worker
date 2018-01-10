package fr.aireisti.aircontest.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import fr.aireisti.aircontest.worker.lib.AbstractRunner;
import fr.aireisti.aircontest.worker.lib.RunnableInfo;
import fr.aireisti.aircontest.worker.lib.RunnerResult;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Consumer extends DefaultConsumer {

    public Consumer(Channel channel) {
        super(channel);
    }

    private AbstractRunner getRunner(String language) {
        String runnerClassName = Config.get("runners." + language);
        if (runnerClassName == null)
            return null;
        try {
            AbstractRunner runner = (AbstractRunner) Class.forName(runnerClassName).newInstance();
            Logger.getLogger(Consumer.class.getName()).log(Level.INFO, "Getting runner '" + runnerClassName + "' for language '" + language + "'.");
            return runner;
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
            throws java.io.IOException{
        String message = new String(body, "UTF-8");
        Logger.getLogger(Consumer.class.getName()).log(Level.INFO, "Received '" + message + "'");

        ObjectMapper mapper = new ObjectMapper();
        RunnableInfo runnableInfo;
        AbstractRunner runner;
        RunnerResult runnerResult;
        try {
            runnableInfo = mapper.readValue(body, RunnableInfo.class);
            runner = this.getRunner(runnableInfo.getLanguage());
            runner.setInfo(runnableInfo);
            runnerResult = runner.run();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            runnerResult = new RunnerResult(RunnerResult.COULD_NOT_READ_RUNNABLE_INFO_VALUE, e.getMessage());
        }

        AMQP.BasicProperties.Builder propsBuilder = new AMQP.BasicProperties.Builder();
        propsBuilder.correlationId(properties.getCorrelationId());

        byte[] bodyReply = mapper.writeValueAsBytes(runnerResult);

        getChannel().queueDeclare(properties.getReplyTo(), false, false, false, null);
        getChannel().basicPublish("", properties.getReplyTo(), propsBuilder.build(), bodyReply);
        getChannel().basicAck(envelope.getDeliveryTag(), false);
    }
}
