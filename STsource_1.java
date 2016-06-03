 package stsource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
     * args[]: bootstrapIP, uci
     * 127.0.0.1 1 xxxxx@miun.se/sensor
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            bootstrapIP = args[0];
            uci = args[1];

            STsource application = new STsource();
            application.run();
        } else {
            System.out.println("please set correct arguments:\n"
                    + "1. IP of bootstrap node\n"
                    + "2. node id meaning the uci\n");
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

    }

    @Override
    public void setEvent(SensibleThingsNode source, String uci, String value) {
        //Not used in this example
    }

    @Override
    public void getResponse(String uci, String value, SensibleThingsNode fromNode) {    //fired afterplatform.notify(fromNode, uci, value);
       platform.notify(fromNode, uci, value);  // send back the same value
    }

    @Override
    public void resolveResponse(String uci, SensibleThingsNode node) {
        //Not used in this example		
    }

}
