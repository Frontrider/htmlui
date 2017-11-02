package test;

import hu.frontrider.htmlui.Binder;
import hu.frontrider.htmlui.Configuration;
import hu.frontrider.htmlui.HtmlEventHandler;
import javafx.stage.Stage;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;

/**
 * Test application
 */
public class App extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws Exception {
        HtmlEventHandler button = new HtmlEventHandler() {

            /**After clicking the button, we will log the event type, and the document type.
             * */
            @Override
            public void handleEvent(@Nullable Event evt) {
                assert evt != null;
                System.out.println(evt.getType());
                System.out.println(((Element) evt.getTarget()).getOwnerDocument().getDoctype().getName());
            }

            @Override
            public String getID() {
                return "button";
            }

            @Override
            public String getTag() {
                return null;
            }
        };

        Configuration configuration =new Configuration();
        configuration.setTitle("Test HTML UI Window");
        Binder binder = new Binder(configuration);
        binder.addHandler("click",button);
        binder.init(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
