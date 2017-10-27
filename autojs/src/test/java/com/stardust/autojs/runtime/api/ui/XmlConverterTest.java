package com.stardust.autojs.runtime.api.ui;

import com.stardust.autojs.core.ui.xml.XmlConverter;

import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Stardust on 2017/5/14.
 */
public class XmlConverterTest {


    @Test
    public void testNodeNameMap() throws IOException, SAXException, ParserConfigurationException {
        System.out.println(XmlConverter.convertToAndroidLayout("<linear></linear>"));
    }

    @Test
    public void testAttrNameMap() throws IOException, SAXException, ParserConfigurationException {
        System.out.println(XmlConverter.convertToAndroidLayout("<linear w=\"*\" h=\"123\"></linear>"));
    }

    @Test
    public void testNonMappedNodeName() throws IOException, SAXException, ParserConfigurationException {
        System.out.println(XmlConverter.convertToAndroidLayout(("<others></others>")));
    }

    @Test
    public void testChildrenNode() throws ParserConfigurationException, SAXException, IOException {
        System.out.println(XmlConverter.convertToAndroidLayout("<linear w=\"*\" h=\"123\"><text id=\"aaa\"/><button bg=\"#ffffff\"/></linear>"));
    }

    @Test
    public void testTextContent() throws ParserConfigurationException, SAXException, IOException {
        System.out.println(XmlConverter.convertToAndroidLayout("<linear><text id=\"aaa\">some text</text></linear>"));
    }


}