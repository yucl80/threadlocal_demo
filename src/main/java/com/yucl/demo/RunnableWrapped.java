package com.yucl.demo;

import static com.yucl.demo.ThreadLocalUtil.copyInheritableThreadLocals;
import static com.yucl.demo.ThreadLocalUtil.copyThreadLocals;

public class RunnableWrapped  implements Runnable{
    private final Runnable task;

    private final Thread caller;

    private boolean copyAllThreadLocals = false;

    public RunnableWrapped(Runnable task, Thread caller) {
        this.task = task;
        this.caller = caller;
    }

    public RunnableWrapped(Runnable task, Thread caller,boolean copyAllThreadLocals) {
        this.task = task;
        this.caller = caller;
        this.copyAllThreadLocals = copyAllThreadLocals;
    }

    public void run() {
        Iterable<ThreadLocal<?>> inheritableThreadLocals = null;
        Iterable<ThreadLocal<?>> threadLocals = null;
        try {
            inheritableThreadLocals = copyInheritableThreadLocals(caller);
            if(copyAllThreadLocals) {
                threadLocals = copyThreadLocals(caller);
            }
        } catch (Exception e) {
            throw new RuntimeException("error when coping threadLocal", e);
        }
        try {
            task.run();
        } finally {
            for (ThreadLocal<?> var : inheritableThreadLocals) {
                var.remove();
            }
            if(copyAllThreadLocals) {
                for (ThreadLocal<?> var : threadLocals) {
                    var.remove();
                }
            }
        }
    }

}
