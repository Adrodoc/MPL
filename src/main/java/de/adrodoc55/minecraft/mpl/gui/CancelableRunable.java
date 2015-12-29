package de.adrodoc55.minecraft.mpl.gui;

class CancelableRunable implements Runnable {

    private final Runnable runnable;
    private boolean canceled;

    public CancelableRunable(Runnable runnable) {
        super();
        this.runnable = runnable;
    }

    public void cancel() {
        canceled = true;
    }

    @Override
    public void run() {
        runnable.run();
    }

}