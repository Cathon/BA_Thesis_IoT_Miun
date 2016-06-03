package stsource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.sensiblethings.disseminationlayer.communication.Communication;
import se.sensiblethings.disseminationlayer.lookupservice.LookupService;
import se.sensiblethings.disseminationlayer.lookupservice.kelips.KelipsLookup;

import se.sensiblethings.interfacelayer.SensibleThingsListener;
import se.sensiblethings.interfacelayer.SensibleThingsNode;
import se.sensiblethings.interfacelayer.SensibleThingsPlatform;

public class STsource implements SensibleThingsListener {
    SensibleThingsPlatform platform;
    static String bootstrapIP, uci;
    static int sendlog, interval;

    /**
     * @param args 
     * args[]: bootstrapIP, node id, sendlog, interval
     * 127.0.0.1 1 700 1000
     */
    public static void main(String[] args) {
        if (args.length == 4) {
            bootstrapIP = args[0];
            uci = "st" + args[1] + "@miun.se/lu";
            sendlog = Integer.valueOf(args[2]);
            interval = Integer.valueOf(args[3]);

            STsource application = new STsource();
            application.run();
        } else {
            System.out.println("please set correct arguments:\n"
                    + "1. IP of bootstrap node\n"
                    + "2. node id meaning the uci\n"
                    + "3. log amount to send\n"
                    + "4. interval(ms)");
        }
    }
    //////////////////////////////////////////	    

    public STsource() {
        KelipsLookup.bootstrapIp = bootstrapIP;
    	platform = new SensibleThingsPlatform(LookupService.KELIPS, Communication.RUDP, this);
    }

    public void run() {
        try {
            System.out.println("SensibleThings SourceExample is running");
            System.out.println("Press any key to shutdown");
            platform.register(uci);
            System.out.println("Registered uci: " +uci);
            
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String s = in.readLine();
            System.out.println("over" + s);
            platform.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getEvent(SensibleThingsNode source, String uci) {   // fired after platform.get(uci, node);
        System.out.println("getEvent");
        for (int i = 1; i <= sendlog; i++) {
            platform.notify(source, uci, i + "+" + String.valueOf(System.currentTimeMillis()));
            try {
                Thread.sleep(interval);
            } catch (InterruptedException ex) {
                Logger.getLogger(STsource.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void setEvent(SensibleThingsNode source, String uci, String value) {
        //Not used in this example
    }

    @Override
    public void getResponse(String uci, String value, SensibleThingsNode fromNode) {    //fired afterplatform.notify(fromNode, uci, value);
        String[] ss = value.split("\\+");
        System.out.println("[GetResponse] " + ss[0] + " " + (System.currentTimeMillis()-Long.valueOf(ss[1])));
//        platform.notify(fromNode, uci, value);  // send back the same value
    }

    @Override
    public void resolveResponse(String uci, SensibleThingsNode node) {
        //Not used in this example		
    }

}
 
