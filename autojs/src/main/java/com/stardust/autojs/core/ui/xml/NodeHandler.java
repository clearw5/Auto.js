package com.stardust.autojs.core.ui.xml;

import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stardust on 2017/5/15.
 */

public interface NodeHandler {

    String handleNode(Node node, StringBuilder layoutXml);

    String handleNode(Node node, String namespace, StringBuilder layoutXml);

    abstract class Adapter implements NodeHandler {

        @Override
        public String handleNode(Node node, String namespace, StringBuilder layoutXml) {
            String name = handleNode(node, layoutXml);
            layoutXml.append(namespace);
            return name;
        }
    }


    class NameRouter extends Adapter {

        private Map<String, NodeHandler> mNodeHandlerMap = new HashMap<>();
        private NodeHandler mDefaultHandler;

        @Override
        public String handleNode(Node node, StringBuilder layoutXml) {
            NodeHandler handler = mNodeHandlerMap.get(node.getNodeName());
            if (handler != null) {
                return handler.handleNode(node, layoutXml);
            }
            if (mDefaultHandler == null)
                return null;
            return mDefaultHandler.handleNode(node, layoutXml);
        }

        public NameRouter defaultHandler(NodeHandler defaultHandler) {
            mDefaultHandler = defaultHandler;
            return this;
        }

        public NameRouter handler(String name, NodeHandler handler) {
            mNodeHandlerMap.put(name, handler);
            return this;
        }
    }

    class MapNameHandler extends Adapter {

        private Map<String, String> mNameMap = new HashMap<>();

        @Override
        public String handleNode(Node node, StringBuilder layoutXml) {
            String name = mNameMap.get(node.getNodeName());
            if (name == null) {
                name = node.getNodeName();
            }
            layoutXml.append("<").append(name).append("\n");
            return name;
        }

        public MapNameHandler map(String oldName, String newName) {
            mNameMap.put(oldName, newName);
            return this;
        }
    }

    class VerticalHandler extends Adapter {

        private String mLinearLayoutClassName;

        public VerticalHandler(String linearLayoutClassName) {
            mLinearLayoutClassName = linearLayoutClassName;
        }

        @Override
        public String handleNode(Node node, StringBuilder layoutXml) {
            layoutXml.append("<").append(mLinearLayoutClassName)
                    .append("\nandroid:orientation=\"vertical\"\n");
            return mLinearLayoutClassName;
        }
    }
}
