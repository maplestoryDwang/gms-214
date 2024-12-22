package net.swordie.ms.handlers.threadpool;

/**
 * 加载文件
 */

import java.util.concurrent.*;

public class LoaderExecutor {
    static int coreSize = Runtime.getRuntime().availableProcessors();
    static long keepAliveTime = 60; // 空闲线程存活时间
    // 创建线程池
    static ThreadPoolExecutor executor = new ThreadPoolExecutor(
            coreSize,
            coreSize,
            keepAliveTime,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(Integer.MAX_VALUE)

    );

    public static void load(Runnable task) {
        executor.submit(task);
//        // 并发处理文件
//        Arrays.stream(files).forEach(file -> executor.submit(new Runnable() {
//                                                                 @Override
//                                                                 public void run() {
//                                                                     processFile(file);
//                                                                 }
//                                                             }
//
//        ));

        // 关闭线程池
//        executor.shutdown();
    }


}
