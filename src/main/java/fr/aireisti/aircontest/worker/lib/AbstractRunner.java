package fr.aireisti.aircontest.worker.lib;

public abstract class AbstractRunner {
    protected RunnableInfo info;

    public AbstractRunner (RunnableInfo info) {
        this.info = info;
    }

    public abstract RunnerResult run ();
}
