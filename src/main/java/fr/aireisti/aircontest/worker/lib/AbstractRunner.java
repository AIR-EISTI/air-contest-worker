package fr.aireisti.aircontest.worker.lib;

import java.util.concurrent.Callable;

public abstract class AbstractRunner implements Callable<RunnerResult> {
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

    public abstract RunnerResult call ();
}
