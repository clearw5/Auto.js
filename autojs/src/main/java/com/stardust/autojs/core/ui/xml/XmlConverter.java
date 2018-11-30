package com.stardust.autojs.core.ui.xml;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TimePicker;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.stardust.autojs.core.graphics.ScriptCanvasView;
import com.stardust.autojs.core.ui.widget.JsButton;
import com.stardust.autojs.core.ui.widget.JsEditText;
import com.stardust.autojs.core.ui.widget.JsFrameLayout;
import com.stardust.autojs.core.ui.widget.JsGridView;
import com.stardust.autojs.core.ui.widget.JsImageView;
import com.stardust.autojs.core.ui.widget.JsLinearLayout;
import com.stardust.autojs.core.ui.widget.JsListView;
import com.stardust.autojs.core.ui.widget.JsRelativeLayout;
import com.stardust.autojs.core.ui.widget.JsSpinner;
import com.stardust.autojs.core.ui.widget.JsTabLayout;
import com.stardust.autojs.core.ui.widget.JsTextView;
import com.stardust.autojs.core.ui.widget.JsToolbar;
import com.stardust.autojs.core.ui.widget.JsViewPager;
import com.stardust.autojs.core.ui.widget.JsWebView;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Stardust on 2017/5/14.
 */

public class XmlConverter {

    private static final NodeHandler NODE_HANDLER = new NodeHandler.NameRouter()
            .handler("vertical", new NodeHandler.VerticalHandler(JsLinearLayout.class.getName()))
            .defaultHandler(new NodeHandler.MapNameHandler()
                    .map("frame", JsFrameLayout.class.getName())
                    .map("linear", JsLinearLayout.class.getName())
                    .map("horizontal", JsLinearLayout.class.getName())
                    .map("relative", JsRelativeLayout.class.getName())
                    .map("button", JsButton.class.getName())
                    .map("text", JsTextView.class.getName())
                    .map("input", JsEditText.class.getName())
                    .map("img", JsImageView.class.getName())
                    .map("datepicker", DatePicker.class.getName())
                    .map("timepicker", TimePicker.class.getName())
                    .map("webview", JsWebView.class.getName())
                    .map("progressbar", ProgressBar.class.getName())
                    .map("seekbar", SeekBar.class.getName())
                    .map("spinner", JsSpinner.class.getName())
                    .map("radio", RadioButton.class.getName())
                    .map("radiogroup", RadioGroup.class.getName())
                    .map("checkbox", CheckBox.class.getName())
                    .map("scroll", ScrollView.class.getName())
                    .map("toolbar", JsToolbar.class.getName())
                    .map("canvas", ScriptCanvasView.class.getName())
                    .map("list", JsListView.class.getName())
                    .map("grid", JsGridView.class.getName())
                    .map("drawer", DrawerLayout.class.getName())
                    .map("appbar", AppBarLayout.class.getName())
                    .map("tabs", JsTabLayout.class.getName())
                    .map("viewpager", JsViewPager.class.getName())
                    .map("card", CardView.class.getName())
                    .map("fab", FloatingActionButton.class.getName())
            );

    private static final AttributeHandler ATTRIBUTE_HANDLER = new AttributeHandler.AttrNameRouter()
            .handler("w", new AttributeHandler.DimenHandler("width"))
            .handler("h", new AttributeHandler.DimenHandler("height"))
            .handler("size", new AttributeHandler.DimenHandler("textSize"))
            .handler("id", new AttributeHandler.IdHandler())
            .handler("vertical", new AttributeHandler.OrientationHandler())
            .handler("margin", new AttributeHandler.DimenHandler("layout_margin"))
            .handler("padding", new AttributeHandler.DimenHandler("padding"))
            .handler("marginLeft", new AttributeHandler.DimenHandler("layout_marginLeft"))
            .handler("marginRight", new AttributeHandler.DimenHandler("layout_marginRight"))
            .handler("marginTop", new AttributeHandler.DimenHandler("layout_marginTop"))
            .handler("marginBottom", new AttributeHandler.DimenHandler("layout_marginBottom"))
            .handler("paddingLeft", new AttributeHandler.DimenHandler("paddingLeft"))
            .handler("paddingRight", new AttributeHandler.DimenHandler("paddingRight"))
            .handler("paddingTop", new AttributeHandler.DimenHandler("paddingTop"))
            .handler("paddingBottom", new AttributeHandler.DimenHandler("paddingBottom"))
            .defaultHandler(new AttributeHandler.MappedAttributeHandler()
                    .mapName("align", "layout_gravity")
            );

    public static String convertToAndroidLayout(String xml) throws IOException, SAXException, ParserConfigurationException {
        return convertToAndroidLayout(new InputSource(new StringReader(xml)));
    }

    public static String convertToAndroidLayout(InputSource source) throws ParserConfigurationException, IOException, SAXException {
        StringBuilder layoutXml = new StringBuilder();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(source);
        handleNode(document.getFirstChild(), "xmlns:android=\"http://schemas.android.com/apk/res/android\"", layoutXml);
        return layoutXml.toString();
    }

    private static void handleNode(Node node, String namespace, StringBuilder layoutXml) {
        String nodeName = node.getNodeName();
        String mappedNodeName = NODE_HANDLER.handleNode(node, namespace, layoutXml);
        handleText(nodeName, node.getTextContent(), layoutXml);
        handleAttributes(nodeName, node.getAttributes(), layoutXml);
        layoutXml.append(">\n");
        handleChildren(node.getChildNodes(), layoutXml);
        layoutXml.append("</").append(mappedNodeName).append(">\n");
    }

    private static void handleText(String nodeName, String textContent, StringBuilder layoutXml) {
        if (textContent == null || textContent.isEmpty()) {
            return;
        }
        if (nodeName.equals("text") || nodeName.equals("button") || nodeName.equals("input"))
            layoutXml.append("android:text=\"").append(textContent).append("\"\n");
    }

    private static void handleChildren(NodeList nodes, StringBuilder layoutXml) {
        if (nodes == null)
            return;
        int len = nodes.getLength();
        for (int i = 0; i < len; i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE)
                continue;
            handleNode(node, "", layoutXml);
        }
    }


    private static void handleAttributes(String nodeName, NamedNodeMap attributes, StringBuilder layoutXml) {
        if (attributes == null)
            return;
        int len = attributes.getLength();
        for (int i = 0; i < len; i++) {
            Node attr = attributes.item(i);
            handleAttribute(nodeName, attr, layoutXml);
        }
    }

    private static void handleAttribute(String nodeName, Node attr, StringBuilder layoutXml) {
        ATTRIBUTE_HANDLER.handle(nodeName, attr, layoutXml);
    }


}
