package com.stardust.autojs.runtime.api.ui;

import com.stardust.util.MapEntries;

import static com.stardust.autojs.runtime.api.ui.AttributeHandler.*;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Stardust on 2017/5/14.
 */

public class XmlConverter {

    private static final Map<String, String> NODE_NAME_MAP = new MapEntries<String, String>()
            .entry("frame", "com.stardust.autojs.runtime.api.ui.widget.JsFrameLayout")
            .entry("linear", "com.stardust.autojs.runtime.api.ui.widget.JsLinearLayout")
            .entry("relative", "com.stardust.autojs.runtime.api.ui.widget.JsRelativeLayout")
            .entry("button", "Button")
            .entry("text", "TextView")
            .entry("input", "EditText")
            .entry("image", "ImageView")
            .map();


    private static final AttributeHandler ATTRIBUTE_HANDLER = new AttributeHandler.AttrNameRouter()
            .handler("w", new DimenHandler("width"))
            .handler("h", new DimenHandler("height"))
            .handler("size", new DimenHandler("textSize"))
            .handler("id", new IdHandler())
            .handler("vertical", new OrientationHandler())
            .handler("margin", new MarginPaddingHandler("layout_margin"))
            .handler("padding", new MarginPaddingHandler("padding"))
            .handler("marginLeft", new DimenHandler("layout_marginLeft"))
            .handler("marginRight", new DimenHandler("layout_marginRight"))
            .handler("marginTop", new DimenHandler("layout_marginTop"))
            .handler("marginBottom", new DimenHandler("layout_marginBottom"))
            .handler("paddingLeft", new DimenHandler("paddingLeft"))
            .handler("paddingRight", new DimenHandler("paddingRight"))
            .handler("paddingTop", new DimenHandler("paddingTop"))
            .handler("paddingBottom", new DimenHandler("paddingBottom"))
            .defaultHandler(new MappedAttributeHandler()
                    .mapName("align", "layout_gravity")
                    .mapName("bg", "background")
                    .mapName("color", "textColor")
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
        String mappedNodeName = mapNodeName(nodeName);
        layoutXml.append("<").append(mappedNodeName).append(" ").append(namespace).append("\n");
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

    private static String mapNodeName(String nodeName) {
        String str = NODE_NAME_MAP.get(nodeName);
        return str == null ? nodeName : str;
    }


}
