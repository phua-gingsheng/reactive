import com.flow.ReactiveFlowApp;
import org.junit.Test;

public class TestReactive {

    @Test
    public void testFlow() throws Exception {

        int NUMBER_OF_MAGAZINES = 20;

        final ReactiveFlowApp app = new ReactiveFlowApp();

        System.out.println("\n\n### CASE 1: Subscribers are fast, buffer size is not so " +
                "important in this case.");
        app.magazineDeliveryExample(100L, 100L, 8);

        System.out.println("\n\n### CASE 2: A slow subscriber, but a good enough buffer " +
                "size on the publisher's side to keep all items until they're picked up");
        app.magazineDeliveryExample(1000L, 3000L, NUMBER_OF_MAGAZINES);

        System.out.println("\n\n### CASE 3: A slow subscriber, and a very limited buffer " +
                "size on the publisher's side so it's important to keep the slow " +
                "subscriber under control");
        app.magazineDeliveryExample(1000L, 3000L, 8);

    }
}
