package fr.aireisti.aircontest.worker.runners;

import fr.aireisti.aircontest.worker.lib.AbstractRunner;
import fr.aireisti.aircontest.worker.lib.RunnableInfo;
import fr.aireisti.aircontest.worker.lib.RunnerResult;

import java.io.*;
import java.util.stream.Collectors;

public class RunnerPython extends AbstractRunner {

    public RunnerPython(RunnableInfo info) {
        super(info);
    }

    @Override
    public RunnerResult run() {
        String stdoutStr = "";

        ProcessBuilder processBuilder = new ProcessBuilder("python", "-c", info.getCode());
        Process process = null;
        RunnerResult runnerResult;

        try {
            process = processBuilder.start();
            InputStream stdout = process.getInputStream ();

            BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
            process.waitFor();
            stdoutStr = reader.lines().collect(Collectors.joining("\n"));
            runnerResult = new RunnerResult(info.getJobId(), process.exitValue(), stdoutStr);
        } catch (IOException e) {
            process.destroy();
            runnerResult = new RunnerResult(RunnerResult.PROCESS_COULD_NOT_START, e.getMessage());
        } catch (java.lang.InterruptedException e) {
            process.destroy();
            runnerResult = new RunnerResult(RunnerResult.INTERRUPTED, e.getMessage());
        }

        return runnerResult;
    }
}
