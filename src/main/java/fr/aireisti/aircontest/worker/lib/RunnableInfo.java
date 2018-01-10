package fr.aireisti.aircontest.worker.lib;

public class RunnableInfo {
    private String language;
    private String code;
    private String input;
    private String jobId;

    public RunnableInfo () {

    }

    public RunnableInfo(String jobId, String language, String code, String input) {
        this.jobId = jobId;
        this.language = language;
        this.code = code;
        this.input = input;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String newLanguage) {
        language = newLanguage;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String newCode) {
        code = newCode;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String newInput) {
        input = newInput;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
}
