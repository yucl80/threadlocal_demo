package com.yucl.demo;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        ThreadLocalMap.put("a","aa");
        InheritableThreadLocalMap.put("test","test");
        ThreadPoolExecutor executor= new ThreadPoolExecutor(1,2,1000, TimeUnit.SECONDS,new LinkedBlockingDeque<Runnable>());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println(ThreadLocalMap.get("a"));
                String thhreadLocals= ThreadLocalUtil.dump(Thread.currentThread());
                System.out.println(thhreadLocals);
            }
        });
        try {
            executor.awaitTermination(3,TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
    }
}
