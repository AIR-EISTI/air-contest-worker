package fr.aireisti.aircontest.worker.runners;

import fr.aireisti.aircontest.worker.lib.AbstractRunner;
import fr.aireisti.aircontest.worker.lib.RunnerResult;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class RunnerPython extends AbstractRunner {

    @Override
    public RunnerResult call() {
        String stdoutStr = "";

        try {
            Files.write(Paths.get("input"), info.getInput().getBytes("UTF-8"));
        } catch (IOException e) {
            return new RunnerResult(info.getJobId(), RunnerResult.UNABLE_TO_WRITE_INPUT_FILE, e.getMessage());
        }


        ProcessBuilder processBuilder = new ProcessBuilder("python", "-c", info.getCode());
        Process process = null;
        RunnerResult runnerResult;

        try {
            process = processBuilder.start();
            InputStream stdout = process.getInputStream ();

            BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
            process.waitFor();
            stdoutStr = reader.lines().collect(Collectors.joining("\n"));
            runnerResult = new RunnerResult(info.getJobId(), stdoutStr, process.exitValue());
        } catch (IOException e) {
            process.destroy();
            runnerResult = new RunnerResult(info.getJobId(), RunnerResult.PROCESS_COULD_NOT_START, e.getMessage());
        } catch (java.lang.InterruptedException e) {
            process.destroy();
            runnerResult = new RunnerResult(info.getJobId(), RunnerResult.INTERRUPTED, e.getMessage());
        }

        return runnerResult;
    }
}
