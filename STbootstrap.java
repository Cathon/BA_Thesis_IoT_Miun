package stbootstrap;

import se.sensiblethings.disseminationlayer.communication.Communication;
import se.sensiblethings.disseminationlayer.communication.rudp.RUDPCommunication;
import se.sensiblethings.disseminationlayer.lookupservice.LookupService;
import se.sensiblethings.disseminationlayer.lookupservice.kelips.KelipsLookup;
import se.sensiblethings.interfacelayer.SensibleThingsListener;
import se.sensiblethings.interfacelayer.SensibleThingsNode;
import se.sensiblethings.interfacelayer.SensibleThingsPlatform;

public class LocalBootstrap implements SensibleThingsListener {

    SensibleThingsPlatform platform;
    boolean runBootstrap = true;
    String randomUci = "bootstrap@miun.se/random";

    public static void main(String[] args) {
        LocalBootstrap application = new LocalBootstrap();
        application.runBootstrap();
    }

    public LocalBootstrap() {
        RUDPCommunication.initCommunicationPort = 9009;
        KelipsLookup.BOOTSTRAP = true;
        platform = new SensibleThingsPlatform(LookupService.KELIPS, Communication.RUDP, this);
        RUDPCommunication.initCommunicationPort = 0;

        System.out.println("RUDP Bootstrap is now operational");
    }

    public void runBootstrap() {
        while (runBootstrap) {
            try {
                Thread.sleep(2000);
                platform.register(randomUci);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void getEvent(SensibleThingsNode source, String uci) {
        //Not Used
    }

    @Override
    public void getResponse(String uci, String value, SensibleThingsNode fromNode) {
        //Not Used
    }

    @Override
    public void resolveResponse(String uci, SensibleThingsNode node) {
        //Not Used
    }

    @Override
    public void setEvent(SensibleThingsNode source, String uci, String value) {
        //Not Used
    }
}
