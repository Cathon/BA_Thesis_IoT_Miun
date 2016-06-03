package stsink;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import se.sensiblethings.disseminationlayer.communication.Communication;
import se.sensiblethings.disseminationlayer.lookupservice.LookupService;
import se.sensiblethings.disseminationlayer.lookupservice.kelips.KelipsLookup;
import se.sensiblethings.interfacelayer.SensibleThingsListener;
import se.sensiblethings.interfacelayer.SensibleThingsNode;
import se.sensiblethings.interfacelayer.SensibleThingsPlatform;

public class STsink implements SensibleThingsListener {
    SensibleThingsPlatform platform;
    
    static String bootstrapIP;
    static int nodes;
    int received = 0, i = 0;
    Long resolveStartTime;

    /**
     * @param args 
     * args[]: bootstrapIP nodes amount to link
     * 127.0.0.1 4
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            bootstrapIP = args[0];
            nodes = Integer.valueOf(args[1]);
            
            STsink application = new STsink();
            application.run();
        } else {
            System.out.println("please set correct arguments:\n"
                    + "1. IP of bootstrap node (10.14.14.11)\n"
                    + "3. nodes amount to link (4 6 8 10 12 14 16 18 20)");
        }
    }

    public STsink() {
        KelipsLookup.bootstrapIp = bootstrapIP;
    	platform = new SensibleThingsPlatform(LookupService.KELIPS, Communication.RUDP, this);
    }

    public void run() {
        try {
            System.out.println("SensibleThings Sink program is running");
//            System.out.println("message delivery interval is " + interval + " ms, "
//                    + "duration is " + 10*interval + " s, need to send 10000 messages.");
            
//            resolveStartTime = System.currentTimeMillis();
            for (int i = 1; i <= nodes; i++) {
                System.out.println("Resolving node " + "st" + i + "@miun.se/lu");
                platform.resolve("st" + i + "@miun.se/lu");
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            in.readLine();
//            System.out.println("Expect received 10000 messages;\n" + 
//                "Total received " + received + " messages.\n");
//            executor.shutdown();
            platform.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resolveResponse(String uci, SensibleThingsNode node) {  // fired after platform.resolve("xxx@miun.se/sensor");
//        Long now = System.currentTimeMillis();
//        System.out.println("Resolve spent time: " + (now - resolveStartTime) + " ms.");
        System.out.println("Resolved node: " + uci);
        platform.get(uci, node);
        System.out.println("send get event to " + uci);
        
//        executor = Executors.newScheduledThreadPool(1);
//        result = executor.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                platform.notify(node, uci, String.valueOf(System.currentTimeMillis()));
//                if (++i >= 10000) result.cancel(true);
//            }
//        }, 1000, interval, TimeUnit.MILLISECONDS);
    }

    @Override
    public void getResponse(String uci, String value, SensibleThingsNode fromNode) {    //fired after platform.notify(source, uci, ts);
        System.out.println(value + " from " + uci);
        platform.notify(fromNode, uci, value);
//        Long now = System.currentTimeMillis();  // get timestamp when receiving a message
//        received++;
//        System.out.println("Message received, send time: " + value + ", reveive time: " + now 
//                + ", RTT: " + (now - Long.valueOf(value)) + " ms");     // print out the RTT of a message
    }

    @Override
    public void getEvent(SensibleThingsNode source, String uci) {
        //Not used in this example
    }

    @Override
    public void setEvent(SensibleThingsNode source, String uci, String value) {
        //Not used in this example		
    }

}
 
