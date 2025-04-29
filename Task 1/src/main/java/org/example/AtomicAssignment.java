package org.example;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AtomicAssignment implements Runnable{
    private static final SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD:mm: ss:SSS");
    
    private static Map<String,String> configuration = new HashMap<String,String>();
    
    public void run(){
        for (int i = 0; i < 1000; i++) {
            Map<String,String>currConfig = configuration;
            String value1 = currConfig.get("key-1");
            String value2 = currConfig.get("Key-2");
            String value3 = currConfig.get("Key-3");
            if(!value1.equals(value2)&& value2.equals(value3)){
                throw new IllegalStateException("Values are not equal.");
    
            }
            try{
                Thread.sleep(10);
            }catch  (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
    public static void readConfig(){
        Map<String,String>newConfig = new HashMap<String,String>();
        Date now = new Date();
        newConfig.put("key-1",sdf.format(now));
        newConfig.put("Key-2",sdf.format(now));
        newConfig.put("key-3",sdf.format(now));
        configuration = newConfig;
    }
    
    public static void main(String[]args)throws InterruptedException {
        readConfig();
        Thread configThread = new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < 1000; i++) {
                    readConfig();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        },"configuration-thread");
        configThread.start();
        Thread[] threads = new Thread[5];
        for (int i = 0; i < threads.length ; i++) {
            threads[i] = new Thread(new AtomicAssignment(), "thread-" + i);
            threads[i].start();
                }

                for (int i = 0; i < threads.length; i++) {
                    threads[i].join();
                }
                configThread.join();
                System.out.println("[" + Thread.currentThread().getName() + "] All threads have finished.");
            }
    }

