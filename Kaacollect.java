package kaacollect;

import java.util.HashMap;
import java.util.Map;

import org.kaaproject.kaa.client.DesktopKaaPlatformContext;
import org.kaaproject.kaa.client.Kaa;
import org.kaaproject.kaa.client.KaaClient;
import org.kaaproject.kaa.client.SimpleKaaClientStateListener;
import org.kaaproject.kaa.client.logging.BucketInfo;
import org.kaaproject.kaa.client.logging.LogDeliveryListener;
import org.kaaproject.kaa.client.logging.RecordInfo;
import org.kaaproject.kaa.client.logging.future.RecordFuture;
import org.kaaproject.kaa.client.logging.strategies.RecordCountLogUploadStrategy;
import org.kaaproject.kaa.schema.sample.logging.LogData;

public class KAAcollect {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Args: NODE_ID, LOGS_TO_SEND_COUNT, interval");
            System.exit(0);
        }
        
        final String NODE_ID = args[0];
        final int LOGS_TO_SEND_COUNT = Integer.valueOf(args[1]);
        final int interval = Integer.valueOf(args[2]);
        
        System.out.println("Data collection demo started");
        KaaClient kaaClient = Kaa.newClient(new DesktopKaaPlatformContext(), new SimpleKaaClientStateListener() {
            @Override
            public void onStarted() {
                System.out.println("Kaa client started");
            }

            @Override
            public void onStopped() {
                System.out.println("Kaa client stopped");
            }
        });
        kaaClient.setLogUploadStrategy(new RecordCountLogUploadStrategy(1));
        kaaClient.setLogDeliveryListener(new LogDeliveryListener() {
            @Override
            public void onLogDeliverySuccess(BucketInfo bucketInfo) {
//                LOG.info("Bucket[{}] SUCCESS DELIVERY at [{}]", bucketInfo.getBucketId(), System.currentTimeMillis());
//                System.out.println("Bucket["+bucketInfo.getBucketId()+"] SUCCESS DELIVERY at ["+String.valueOf(System.currentTimeMillis())+"]");
            }

            @Override
            public void onLogDeliveryFailure(BucketInfo bucketInfo) {
                String t = String.valueOf(System.currentTimeMillis());
                System.out.println("Bucket[" + bucketInfo.getBucketId() + "] FAILEDDELIVERY at [" + t + "]");
            }

            @Override
            public void onLogDeliveryTimeout(BucketInfo bucketInfo) {
                String t = String.valueOf(System.currentTimeMillis());
                System.out.println("Bucket[" + bucketInfo.getBucketId() + "] TIMEOUTDELIVERY at [" + t + "]");
            }
            
        });
        kaaClient.start();

        Map<RecordFuture, Long> futuresMap = new HashMap<>();
        for (int i = 1; i <= LOGS_TO_SEND_COUNT; i++) {
            LogData log = new LogData(NODE_ID, i, System.currentTimeMillis());
            futuresMap.put(kaaClient.addLogRecord(log), log.getTimestamp());
            System.out.println("Log record "+log.toString()+" sent");
            try {
                Thread.sleep(interval);
            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(KAAcollect.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }

        for (RecordFuture future : futuresMap.keySet()) {
            try {
                RecordInfo recordInfo = future.get();
                BucketInfo bucketInfo = recordInfo.getBucketInfo();
                Long timeSpent = (recordInfo.getRecordAddedTimestampMs() - futuresMap.get(future))
                        + recordInfo.getRecordDeliveryTimeMs();
//                System.out.println(bucketInfo.getBucketId() + "," + recordInfo.getRecordAddedTimestampMs()
//                                              + "," + futuresMap.get(future) + "," + recordInfo.getRecordDeliveryTimeMs());
                System.out.println("Bucket Id ["+bucketInfo.getBucketId()+"]. Record delivery time ["+timeSpent+" ms]");
            } catch (Exception e) {
                System.err.println("Exception was caught while waiting for callback future");
            }
        }

        kaaClient.stop();
        System.out.println("Data collection demo stopped");
    }

}
