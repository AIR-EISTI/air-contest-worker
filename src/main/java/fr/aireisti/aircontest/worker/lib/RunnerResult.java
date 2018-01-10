package fr.aireisti.aircontest.worker.lib;

public class RunnerResult {
    private Integer returnCode;
    private String stdout;
    private String jobId;

    public RunnerResult () {

    }

    public RunnerResult (String jobId, Integer returnCode, String stdout) {
        this.jobId = jobId;
        this.returnCode = returnCode;
        this.stdout = stdout;
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
}
