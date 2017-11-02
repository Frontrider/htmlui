package test;

import hu.frontrider.htmlui.Binder;
import hu.frontrider.htmlui.Configuration;
import hu.frontrider.htmlui.HtmlEventHandler;
import javafx.stage.Stage;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.events.Event;

/**
 * Test application
 */
public class App extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws Exception {
        HtmlEventHandler button = new HtmlEventHandler() {

            @Override
            public void handleEvent(@Nullable Event evt) {
                assert evt != null;
                System.out.println(evt.getType());
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
