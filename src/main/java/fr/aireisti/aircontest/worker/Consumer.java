package fr.aireisti.aircontest.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import fr.aireisti.aircontest.worker.lib.AbstractRunner;
import fr.aireisti.aircontest.worker.lib.RunnableInfo;
import fr.aireisti.aircontest.worker.lib.RunnerResult;

import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Consumer extends DefaultConsumer {

    private static final Integer MAX_EXEC_TIME = Integer.parseInt(Config.get("workers.maxExecTime", DefaultConfig.MAX_EXEC_TIME));
    private ExecutorService executor = Executors.newFixedThreadPool(4);

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
        RunnableInfo runnableInfo = null;
        AbstractRunner runner;
        RunnerResult runnerResult;
        Future<RunnerResult> future = null;
        try {
            runnableInfo = mapper.readValue(body, RunnableInfo.class);
            runner = this.getRunner(runnableInfo.getLanguage());
            runner.setInfo(runnableInfo);
            future = executor.submit(runner);
            runnerResult = future.get(Consumer.MAX_EXEC_TIME, TimeUnit.SECONDS);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            runnerResult = new RunnerResult(RunnerResult.COULD_NOT_READ_RUNNABLE_INFO_VALUE, e.getMessage());
        } catch (NullPointerException e) {
            e.printStackTrace();
            runnerResult = new RunnerResult(RunnerResult.RUNNER_NOT_FOUND, e.getMessage());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            runnerResult = new RunnerResult(RunnerResult.INTERRUPTED, e.getMessage());
        } catch (TimeoutException e) {
            e.printStackTrace();
            future.cancel(true);
            runnerResult = new RunnerResult(RunnerResult.TIMEOUT, e.getMessage());
        }
        try {
            runnerResult.setJobId(runnableInfo.getJobId());
        } catch (NullPointerException e) {}

        AMQP.BasicProperties.Builder propsBuilder = new AMQP.BasicProperties.Builder();
        propsBuilder.correlationId(properties.getCorrelationId());

        byte[] bodyReply = mapper.writeValueAsBytes(runnerResult);

        getChannel().queueDeclare(properties.getReplyTo(), false, false, false, null);
        getChannel().basicPublish("", properties.getReplyTo(), propsBuilder.build(), bodyReply);
        getChannel().basicAck(envelope.getDeliveryTag(), false);
    }
}
