package kaasource;
import com.company.project.TestEvent;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kaaproject.kaa.client.DesktopKaaPlatformContext;
import org.kaaproject.kaa.client.Kaa;
import org.kaaproject.kaa.client.KaaClient;
import org.kaaproject.kaa.client.SimpleKaaClientStateListener;
import org.kaaproject.kaa.client.event.EventFamilyFactory;
import org.kaaproject.kaa.client.event.registration.UserAttachCallback;
import org.kaaproject.kaa.common.endpoint.gen.SyncResponseResultType;
import org.kaaproject.kaa.common.endpoint.gen.UserAttachResponse;
import org.kaaproject.kaa.demo.iot.IoTEventClassFamily;

public class KAAsource {

    private static final String USER_EXTERNAL_ID = "user@email.com";
    private static final String USER_ACCESS_TOKEN = "token";
    private static KaaClient kaaClient;

    private static int i = 0;
    private static int times;
    private static int interval;
    
    public static void main(String[] args) {
        if(args.length == 2) {
            times = Integer.valueOf(args[0]);
            interval = Integer.valueOf(args[1]);
        } else {
            System.out.println("args: time interval");
            System.exit(0);
        }
        
        System.out.println("Event demo started");
        System.out.println("--= Press any key to exit =--");

        kaaClient = Kaa.newClient(new DesktopKaaPlatformContext(), new SimpleKaaClientStateListener() {
            @Override
            public void onStarted() {
                System.out.println("Kaa client started");
            }

            @Override
            public void onStopped() {
                System.out.println("Kaa client stopped");
            }
        });
        kaaClient.start();
        kaaClient.attachUser(USER_EXTERNAL_ID, USER_ACCESS_TOKEN, new UserAttachCallback() {
            @Override
            public void onAttachResult(UserAttachResponse response) {
                System.out.println("Attach response " + response.getResult());

                if (response.getResult() == SyncResponseResultType.SUCCESS) {
                    onUserAttached();
                }
                else {
                    kaaClient.stop();
                    System.out.println("Event demo stopped");
                }
            }
        });

        try {
            System.in.read();
        } catch (IOException e) {
            System.out.println("IOException was caught");
        }
        
        kaaClient.stop();
        System.out.println("Event demo stopped");
    }

    public static void onUserAttached() {

        List<String> listenerFQNs = new LinkedList<>();
        listenerFQNs.add(IoTEventClassFamily.class.getName());
        
        final EventFamilyFactory eventFamilyFactory = kaaClient.getEventFamilyFactory();
        final IoTEventClassFamily tecf = eventFamilyFactory.getIoTEventClassFamily();
        
        tecf.addListener(new IoTEventClassFamily.Listener() {
            @Override
            public void onEvent(TestEvent event, String source) {
                Long now = System.currentTimeMillis();
                Long rtt = now - event.getTimestamp();
                System.out.println("Receive msg " + event + " at " + now + " rtt: " + rtt);
            }
        });
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (i++ < times) {
                    tecf.sendEventToAll(new TestEvent(System.currentTimeMillis()));
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(KAAsource.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }).start();
    }
}
