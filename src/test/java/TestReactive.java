import com.flow.basic.EndSubscriber;
import com.flow.sample.ReactiveFlowApp;
import com.flow.sample.TransformProcessor;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;

public class TestReactive {

    @Test
    public void testFlowA() throws Exception {
        final ReactiveFlowApp app = new ReactiveFlowApp();
        System.out.println("\n\n### CASE 1: Subscribers are fast, buffer size is not " +
                "so important in this case.");
        app.magazineDeliveryExample(100L, 100L, 8);
    }

    @Test
    public void testFlowB() throws Exception {
        int NUMBER_OF_MAGAZINES = 20;
        final ReactiveFlowApp app = new ReactiveFlowApp();
        System.out.println("\n\n### CASE 2: A slow subscriber, but a good enough buffer " +
                "size on the publisher's side to keep all items until they're picked up");
        app.magazineDeliveryExample(1000L, 3000L, NUMBER_OF_MAGAZINES);
    }

    @Test
    public void testFlowC() throws Exception {
        final ReactiveFlowApp app = new ReactiveFlowApp();
        System.out.println("\n\n### CASE 3: A slow subscriber, and a very limited buffer " +
                "size on the publisher's side so it's important to keep the slow " +
                "subscriber under control");
        app.magazineDeliveryExample(1000L, 3000L, 8);
    }

    @Test
    public void testWhenSubscribeToItThenShouldConsumeAll() {

        // given
        SubmissionPublisher<String> publisher = new SubmissionPublisher<>();
        TransformProcessor<String, Integer> transformProcessor = new TransformProcessor<>(Integer::parseInt);
        EndSubscriber<Integer> subscriber = new EndSubscriber<>(10);

        publisher.subscribe(transformProcessor);
        transformProcessor.subscribe(subscriber);

        List<String> items = List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");

        items.forEach(s -> {
            try {
                TimeUnit.MILLISECONDS.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publisher.submit(s);
        });

        publisher.close();


    }
}
