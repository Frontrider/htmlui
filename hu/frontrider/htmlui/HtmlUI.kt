/**
    Single file API to make it easy to use jfx webview as a powerfull ui framework.

    Copyright (C) 2017  András Gábor Kis

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/gpl-3.0.en.html/>.
*/
package hu.frontrider.htmlui

import javafx.concurrent.Worker
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Rectangle2D
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.scene.web.HTMLEditor
import javafx.scene.web.WebEngine
import javafx.scene.web.WebEvent
import javafx.scene.web.WebView
import javafx.stage.Stage
import javafx.stage.StageStyle
import netscape.javascript.JSObject
import org.w3c.dom.Document
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.EventTarget


/**
 * The binder class provides java side event binding for html elements.
 * The intended philosophy, is that you would use html-css-javascript on the ui side,
 * than bind the java code directly to the html elements, without any intermediary.(usually javascript.)
 * NO JAVASCRIPT SHOULD BE USED FOR THE LOGIC, IT SHOULD BE USED TO ENHANCE THE UI!
 *
 * The library was created to provide clean and easy access to the most powerful UI framework currently known.
 * JFX is already gives a non-native look, so its up to the task to decide weather or not we want to use something else,
 * depending on our team.
 */
private class EventHolder(val name:String){
    val events = ArrayList<HtmlEventHandler>()
}
class Binder(private val configuraion:Configuration) {
    //mouse events
    private val events = ArrayList<EventHolder>()
    private val jsObjects = HashMap<String,Any>()
    private var css = ""
    private lateinit var webEngine:WebEngine

    init{
        /**
         * all the html events what we can bind to.
         * Oh boy, thats a lot.
         * */
        //mouse events
        events.add(EventHolder("click"))
        events.add(EventHolder("mousedown"))
        events.add(EventHolder("mouseup"))
        events.add(EventHolder("wheel"))
        events.add(EventHolder("mouseover"))
        events.add(EventHolder("mouseout"))
        events.add(EventHolder("dbclick"))

        //form events
        events.add(EventHolder("blur"))
        events.add(EventHolder("change"))
        events.add(EventHolder("contextmenu"))
        events.add(EventHolder("focus"))
        events.add(EventHolder("input"))
        events.add(EventHolder("invalid"))
        events.add(EventHolder("reset"))
        events.add(EventHolder("search"))
        events.add(EventHolder("submit"))
        //keyboard
        events.add(EventHolder("keydown"))
        events.add(EventHolder("keypress"))
        events.add(EventHolder("keyup"))

        //drag events
        events.add(EventHolder("drag"))
        events.add(EventHolder("dragend"))
        events.add(EventHolder("dragenter"))
        events.add(EventHolder("dragleave"))
        events.add(EventHolder("dragover"))
        events.add(EventHolder("dragstart"))
        events.add(EventHolder("drop"))
        events.add(EventHolder("scroll"))
        //media
        events.add(EventHolder("abort"))
        events.add(EventHolder("canplay"))
        events.add(EventHolder("oncanplaythrough"))
        events.add(EventHolder("cuechange"))
        events.add(EventHolder("durationchange"))
        events.add(EventHolder("emptied"))
        events.add(EventHolder("ended"))
        events.add(EventHolder("error"))
        events.add(EventHolder("loadeddata"))
        events.add(EventHolder("loadedmetadata"))
        events.add(EventHolder("loadstart"))
        events.add(EventHolder("pause"))
        events.add(EventHolder("play"))
        events.add(EventHolder("playing"))
        events.add(EventHolder("progress"))
        events.add(EventHolder("ratechange"))
        events.add(EventHolder("seeked"))
        events.add(EventHolder("seeking"))
        events.add(EventHolder("stalled"))
        events.add(EventHolder("suspend"))
        events.add(EventHolder("timeupdate"))
        events.add(EventHolder("volumechange"))
        events.add(EventHolder("waiting"))
        //misc
        events.add(EventHolder("show"))
        events.add(EventHolder("toggle"))
        events.add(EventHolder("ratechange"))
        //window


    }

    fun getDoc(): Document {
        return webEngine.document
    }
    fun addHandler(type:String,handler: HtmlEventHandler):Boolean
    {

        for(eh in events)
        {
            //check if the event is actually valid.
            if(eh.name == type) {
                eh.events.add(handler)
                return true
            }
        }
        println("HTML binder, invalid event type")
        return false
    }

    fun addObject(name:String,obj:Any)
    {
        jsObjects.put(name,obj)
    }

    fun setStylesheet(css:String)
    {
        this.css = css
    }

    fun executeScript(javascript:String): Boolean {
        try {
            return webEngine.executeScript(javascript) == true
        }catch (e:Exception)
        {
            e.printStackTrace()
            return false
        }
    }

    fun init(stage: Stage)
    {
        val browser = WebView()
        webEngine = browser.engine

        webEngine.isJavaScriptEnabled = configuraion.enablejs
        webEngine.loadWorker.stateProperty().addListener { _, _, newValue ->
            if (newValue == Worker.State.SUCCEEDED) {
                val doc = webEngine.document
                for(eh in events) {
                    for (handler in eh.events) {

                        val id = handler.getID()
                        if (id != null) {
                            val el = doc.getElementById(id)
                            (el as EventTarget).addEventListener(eh.name, handler, false)
                        }
                        val tag = handler.getTag()
                        if (tag != null) {
                            val els = doc.getElementsByTagName(tag)
                            for (i in 0 until els.length) {
                                (els.item(i) as EventTarget).addEventListener(eh.name, handler, false)
                            }
                        }

                    }
                }

            }
        }

        val window = webEngine.executeScript("window") as JSObject
        for(obj in jsObjects)
        {
            window.setMember(obj.key,obj.value)
        }
        if(css !="")
        {
            webEngine.userStyleSheetLocation = css
        }

        val root = VBox()
        if(configuraion.html.contains("</html>"))
        {
            webEngine.loadContent(configuraion.html)
        }
        else
        {
            webEngine.load(configuraion.html)
        }

        root.padding = Insets(5.0)
        root.spacing = 5.0
        root.children.addAll(browser)
        val scene = Scene(root)
        if (configuraion.undecorated)
            stage.initStyle(StageStyle.UNDECORATED)
        stage.title = configuraion.title
        stage.scene = scene
        stage.width = configuraion.width.toDouble()
        stage.height = configuraion.height.toDouble()
        stage.show()
    }

}

class Configuration {
    var enablejs = true
    var html = "<!DOCTYPE html>\n" +
            "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/html\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>Title</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<button id=\"button\">click</button>\n" +
            "<p id=\"text\">This is the test page of the javafx html ui API. Supply your own to the \"HTML\" " +
            "parameter of the Configuratuion</p>\n" +
            "</body>\n" +
            "</html>"
    var undecorated = false
    var title = "javafx html ui window"
    var width = 450
    var height = 300
}

interface HtmlEventHandler : EventListener{
    override fun handleEvent(evt: Event?)
    fun getID():String?
    fun getTag(): String?
}
