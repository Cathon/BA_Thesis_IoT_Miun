package kaasink;
import com.company.project.TestEvent;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.kaaproject.kaa.client.DesktopKaaPlatformContext;
import org.kaaproject.kaa.client.Kaa;
import org.kaaproject.kaa.client.KaaClient;
import org.kaaproject.kaa.client.SimpleKaaClientStateListener;
import org.kaaproject.kaa.client.event.EventFamilyFactory;
import org.kaaproject.kaa.client.event.registration.UserAttachCallback;
import org.kaaproject.kaa.common.endpoint.gen.SyncResponseResultType;
import org.kaaproject.kaa.common.endpoint.gen.UserAttachResponse;
import org.kaaproject.kaa.demo.iot.IoTEventClassFamily;

public class KAAsink {

    //Credentials for attaching an endpoint to the user.
    private static final String USER_EXTERNAL_ID = "user@email.com";
    private static final String USER_ACCESS_TOKEN = "token";
    // A Kaa client.
    private static KaaClient kaaClient;

    public static void main(String[] args) {
        System.out.println("Event demo started");
        System.out.println("--= Press any key to exit =--");

        // Create a Kaa client and add a listener which creates a log record
        // as soon as the Kaa client is started.  
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

        //Start the Kaa client and connect it to the Kaa server.
        kaaClient.start();

        // Attach the endpoint running the Kaa client to the user by verifying 
        // credentials sent by the endpoint against the user credentials
        // stored on the Kaa server.
        // This demo application uses a trustful verifier, therefore
        // any credentials sent by the endpoint are accepted as valid. 
        kaaClient.attachUser(USER_EXTERNAL_ID, USER_ACCESS_TOKEN, new UserAttachCallback() {
            @Override
            public void onAttachResult(UserAttachResponse response) {
                System.out.println("Attach response " + response.getResult());

                // Call onUserAttached if the endpoint was successfully attached.
                if (response.getResult() == SyncResponseResultType.SUCCESS) {
                    onUserAttached();
                } // Shut down all the Kaa client tasks and release 
                // all network connections and application resources 
                // if the endpoint was not attached.
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

        // Shut down all the Kaa client tasks and release
        // all network connections and application resources.
        kaaClient.stop();
        System.out.println("Event demo stopped");
    }

    public static void onUserAttached() {

        List<String> listenerFQNs = new LinkedList<>();
        listenerFQNs.add(IoTEventClassFamily.class.getName());
        
        //Obtain the event family factory.
        final EventFamilyFactory eventFamilyFactory = kaaClient.getEventFamilyFactory();
        //Obtain the concrete event family.
        final IoTEventClassFamily tecf = eventFamilyFactory.getIoTEventClassFamily();

        // Add event listeners to the family factory.
        tecf.addListener(new IoTEventClassFamily.Listener() {
            @Override
            public void onEvent(TestEvent event, String source) {
                tecf.sendEvent(new TestEvent(event.getTimestamp()), source);
                System.out.println("Received: " + event.toString() + " sent.");
            }
        });
    }
}
