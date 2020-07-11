package com.halo.redpacket.okhttp;

public interface ProgressListener {

    /**
     * Called periodically as the verification progresses.
     *
     * @param progress  the approximate percentage of the
     *        verification that has been completed, ranging from 0
     *        to 100 (inclusive).
     */
    public void onProgress(int progress);
}
