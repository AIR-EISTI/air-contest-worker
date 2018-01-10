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

        try {
            process = processBuilder.start();
            InputStream stdout = process.getInputStream ();

            BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
            process.waitFor();
            stdoutStr = reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException | java.lang.InterruptedException e) {
            e.printStackTrace();
            process.destroy();
        }

        return new RunnerResult(info.getJobId(), process.exitValue(), stdoutStr);
    }
}
