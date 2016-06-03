package stsink;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import se.sensiblethings.disseminationlayer.communication.Communication;
import se.sensiblethings.disseminationlayer.lookupservice.LookupService;
import se.sensiblethings.disseminationlayer.lookupservice.kelips.KelipsLookup;
import se.sensiblethings.interfacelayer.SensibleThingsListener;
import se.sensiblethings.interfacelayer.SensibleThingsNode;
import se.sensiblethings.interfacelayer.SensibleThingsPlatform;

public class STsink implements SensibleThingsListener {
    SensibleThingsPlatform platform;
    
    static String bootstrapIP, uci;
    static int interval;
    int received = 0, i = 0;
    Long resolveStartTime;
    
    ScheduledExecutorService executor;
    ScheduledFuture result;

    /**
     * @param args 
     * args[]: bootstrapIP, UCI to resolve, message delivery interval(ms), duraion to send messages(s)
     * 127.0.0.1 xxxxx@miun.se/sensor 50 10
     */
    public static void main(String[] args) {
        if (args.length == 3) {
            bootstrapIP = args[0];
            uci = args[1];
            interval = Integer.valueOf(args[2]);
            
            STsink application = new STsink();
            application.run();
        } else {
            System.out.println("please set correct arguments:\n"
                    + "1. IP of bootstrap node (127.0.0.1)\n"
                    + "2. source's UCI to resolve\n"
                    + "3. message delivery interval(ms) (1000 500 100 50 10 5 1)");
        }
    }

    public STsink() {
        KelipsLookup.bootstrapIp = bootstrapIP;
    	platform = new SensibleThingsPlatform(LookupService.KELIPS, Communication.RUDP, this);
    }

    public void run() {
        try {
            System.out.println("SensibleThings Sink program is running");
            System.out.println("message delivery interval is " + interval + " ms, "
                    + "duration is " + 10*interval + " s, need to send 10000 messages.");
            
            resolveStartTime = System.currentTimeMillis();
            platform.resolve(uci);  
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            in.readLine();
            System.out.println("Expect received 10000 messages;\n" + 
                "Total received " + received + " messages.\n");
            executor.shutdown();
            platform.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resolveResponse(String uci, SensibleThingsNode node) {  // fired after platform.resolve("xxx@miun.se/sensor");
        Long now = System.currentTimeMillis();
        System.out.println("Resolve spent time: " + (now - resolveStartTime) + " ms.");

        executor = Executors.newScheduledThreadPool(1);
        result = executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                platform.notify(node, uci, String.valueOf(System.currentTimeMillis()));
                if (++i >= 10000) result.cancel(true);
            }
        }, 1000, interval, TimeUnit.MILLISECONDS);
    }

    @Override
    public void getResponse(String uci, String value, SensibleThingsNode fromNode) {    //fired after platform.notify(source, uci, ts);
        Long now = System.currentTimeMillis();  // get timestamp when receiving a message
        received++;
        System.out.println("Message received, send time: " + value + ", reveive time: " + now 
                + ", RTT: " + (now - Long.valueOf(value)) + " ms");     // print out the RTT of a message
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
 
