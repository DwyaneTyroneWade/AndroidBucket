package com.wangjie.androidbucket.services.network;

import com.wangjie.androidbucket.services.network.exception.HippoException;
import com.wangjie.androidbucket.services.network.exception.RetryFailedException;

/**
 * @author Hubert He
 * @version V1.0
 * @Description
 * @Createdate 14-9-24 16:35
 */
public abstract class HippoRequest<T> implements Comparable<HippoRequest> {

    /**
     * Sequence
     */
    protected int seq;

    private boolean cancel = false;

    private boolean finish = false;

    protected static final String DEFAULT_RESPONSE_ENCODING = "UTF-8";

    protected RetryPolicy retryPolicy;


    /**
     * 成功返回监听器
     */
    protected HippoResponse.Listener<T> listener;

    /**
     * 失败返回监听器
     */
    protected HippoResponse.ErrorListener errorListener;


    public HippoRequest(HippoResponse.Listener<T> listener, HippoResponse.ErrorListener errorListener) {
        this.listener = listener;
        this.errorListener = errorListener;
        this.retryPolicy = new RetryPolicy();
    }

    /**
     * 解析HttpResponse
     *
     * @return
     */
    public abstract void parseResponse(NetworkResponse response);

    public void retry(HippoException e) throws HippoException {
        retryPolicy.retry(e);
    }


    @Override
    public int compareTo(HippoRequest hippoRequest) {
        return this.seq - hippoRequest.seq;
    }

    public void cancel(boolean isCancel) {
        synchronized (this) {
            this.cancel = isCancel;
        }
    }

    public boolean isCancel() {
        return cancel;
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public int getSeq() {
        return seq;
    }

    private class RetryPolicy {

        private static final int DEFAULT_RETRY_COUNT = 3;

        private RetryPolicy() {
            this(DEFAULT_RETRY_COUNT);
        }

        private RetryPolicy(int currentCount) {
            this.currentCount = currentCount;
        }

        private int currentCount;

        public int getCurrentCount() {
            return currentCount;
        }

        public void retry(HippoException e) throws RetryFailedException {
            if (currentCount <= 0) {
                throw new RetryFailedException("Retry request failed.", e);
            }
            currentCount--;
        }
    }

    @Override
    public String toString() {
        return "HippoRequest{" +
                "seq=" + seq +
                ", cancel=" + cancel +
                ", finish=" + finish +
                ", retryPolicy=" + retryPolicy +
                ", listener=" + listener +
                ", errorListener=" + errorListener +
                '}';
    }
}
