package com.wuhao.test;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class DynamicThreadPoolTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(DynamicThreadPoolTestApplication.class, args);
    }


//    @Bean
//    public ApplicationRunner applicationRunner(Map<String, ThreadPoolExecutor> map) {
//        ThreadPoolExecutor threadPoolExecutor01 = map.get("threadPoolExecutor01");
//        return args -> {
//            int taskNumber = 0;
//            while (true){
//                try {
//                    threadPoolExecutor01.execute(() -> {
//                        try {
//                            // 模拟任务执行时间
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            Thread.currentThread().interrupt();
//                        }
//                    });
//                } catch (RejectedExecutionException e) {
//                    taskNumber++;
//                    System.out.println("Task " + taskNumber + " was rejected: " + e);
//                }
//            }
//        };
//    }

//    @Bean
//    public ApplicationRunner applicationRunners(Map<String, ThreadPoolExecutor> map) {
//        ThreadPoolExecutor threadPoolExecutor02 = map.get("threadPoolExecutor02");
//        return args -> {
//            int taskNumber = 0;
//            while (true){
//                try {
//                    threadPoolExecutor02.execute(() -> {
//                        try {
//                            // 模拟任务执行时间
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            Thread.currentThread().interrupt();
//                        }
//                    });
//                } catch (RejectedExecutionException e) {
//                    taskNumber++;
//                    System.out.println("Task " + taskNumber + " was rejected: " + e);
//                }
//            }
//        };
//    }


}
