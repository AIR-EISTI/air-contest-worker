package fr.aireisti.aircontest.worker.lib;

public abstract class AbstractRunner {
    protected RunnableInfo info;

    public AbstractRunner () {

    }

    public AbstractRunner (RunnableInfo info) {
        this.info = info;
    }

    public void setInfo(RunnableInfo info) {
        this.info = info;
    }

    public RunnableInfo getInfo() {
        return info;
    }

    public abstract RunnerResult run ();
}
