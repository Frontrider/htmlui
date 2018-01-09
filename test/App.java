package test;

import hu.frontrider.htmlui.Binder;
import hu.frontrider.htmlui.Configuration;
import hu.frontrider.htmlui.HtmlEventHandler;
import javafx.stage.Stage;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;

/**
 * Test application
 */
public class App extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws Exception {
        Configuration configuration =new Configuration();
        Binder binder = new Binder(configuration);
        HtmlEventHandler button = new HtmlEventHandler() {
            /**After clicking the button, we will log the event type, and the document type.
             * */
            String id = "button";
            @Override
            public void handleEvent(@Nullable Event evt) {
                assert evt != null;
                System.out.println(evt.getType());
                Document doc = ((Element) evt.getTarget()).getOwnerDocument();
                System.out.println(doc.getDoctype().getName());
                System.out.println("button text is "+doc.getElementById(id).getTextContent());
                System.out.println("main text is "+doc.getElementById("text").getTextContent());

            }

            @Override
            public String getID() {
                return id;
            }

            @Override
            public String getTag() {
                return null;
            }
        };

        configuration.setTitle("Test HTML UI Window");

        binder.addHandler("click",button);
        //add the logger object to javascript
        binder.addObject("logger",new JsLogger());
        binder.init(stage);

        //print a string using the javascript logger object
        binder.executeScript("var s = \"String to be logged\";" +
                "logger.log(s)");

    }

    public static void main(String[] args) {
        launch(args);
    }
}
