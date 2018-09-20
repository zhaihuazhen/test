package com.zhihu.commercial.biz.brand.service;

import com.zhihu.commercial.plutus.wallet.proto.PlutusWalletService;
import com.zhihu.commercial.plutus.wallet.proto.TransactionRecord;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.*;


/**
 * java类简单作用描述
 *
 */

public class BiddingBillingService {

    private static ExecutorService executorService = null;

    public static void start() {

        int count = 1000;
        int threadCount = 10;

        executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        System.out.println("执行开始");

        for(int i=0; i < count; i++){
            executorService.submit(new WalletConsume(countDownLatch));
        }
        try{
            countDownLatch.await();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            executorService.shutdown();
            System.out.println("执行结束");
            System.exit(0);
        }
    }
}

class WalletConsume implements Callable<String> {
    int userId = 1;
    double money = 0.01D;
    String sign = "";
    boolean flag = true;
    @Autowired
    private PlutusWalletService.Iface plutusWalletService;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-hh hh:mm:ss");

    private CountDownLatch countDownLatch;

    public WalletConsume(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public String call() {
        System.out.println("==============");
        String identify_code = UUID.randomUUID().toString();
        for (int i = 0; i < 10; i++) {
            try {
                System.out.println("11111111111");

                Date date = new Date();
                TransactionRecord record = plutusWalletService.walletConsume(userId, money, identify_code, sign, flag);
                if (record != null && record.getAmount() > 0) {
                    System.out.println(sdf.format(date) + "\t" + identify_code + "\t" + i);
                    break;
                }
                System.out.println("22222222");
            } catch (TException e) {
                e.printStackTrace();
            }
        }
        countDownLatch.countDown();

        return "";
    }
}