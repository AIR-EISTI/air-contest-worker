package fr.aireisti.aircontest.worker.lib;

public class RunnerResult {
    private Integer returnCode;
    private String stdout;
    private String jobId;
    private String error;
    private Integer status;

    public final static int COULD_NOT_READ_RUNNABLE_INFO_VALUE = 10;
    public final static int RUNNER_NOT_FOUND = 15;
    public final static int UNABLE_TO_WRITE_INPUT_FILE = 16;
    public final static int PROCESS_COULD_NOT_START = 20;
    public final static int INTERRUPTED = 30;
    public final static int TIMEOUT = 40;
    public final static int SUCCESS = 0;

    public RunnerResult () {

    }

    public RunnerResult (String jobId, String stdout, Integer returnCode) {
        this.jobId = jobId;
        this.returnCode = returnCode;
        this.stdout = stdout;
        this.status = SUCCESS;
    }

    public RunnerResult(String jobId, Integer errorCode, String error) {
        this.error = error;
        this.status = errorCode;
    }

    public Integer getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(Integer returnCode) {
        this.returnCode = returnCode;
    }

    public String getStdout() {
        return stdout;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
