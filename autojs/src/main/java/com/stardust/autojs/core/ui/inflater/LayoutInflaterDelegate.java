package com.stardust.autojs.core.ui.inflater;

import android.view.View;
import android.view.ViewGroup;

import com.stardust.autojs.core.ui.inflater.inflaters.ViewGroupInflater;

import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stardust on 2018/3/29.
 */

public interface LayoutInflaterDelegate {

    LayoutInflaterDelegate NO_OP = new NoOp();

    View beforeInflation(String xml, ViewGroup parent);

    View afterInflation(View doInflation, String xml, ViewGroup parent);

    String beforeConvertXml(String xml);

    String afterConvertXml(String xml);

    View beforeInflateView(Node node, ViewGroup parent, boolean attachToParent);

    View afterInflateView(View view, Node node, ViewGroup parent, boolean attachToParent);

    View beforeCreateView(Node node, String viewName, HashMap<String, String> attrs);

    View afterCreateView(View view, Node node, String viewName, HashMap<String, String> attrs);

    boolean beforeApplyAttributes(View view, ViewInflater<View> inflater, HashMap<String, String> attrs, ViewGroup parent);

    void afterApplyAttributes(View view, ViewInflater<View> inflater, HashMap<String, String> attrs, ViewGroup parent);

    boolean beforeInflateChildren(ViewInflater<View> inflater, Node node, ViewGroup parent);

    void afterInflateChildren(ViewInflater<View> inflater, Node node, ViewGroup parent);

    void afterApplyPendingAttributesOfChildren(ViewGroupInflater inflater, ViewGroup view);

    boolean beforeApplyPendingAttributesOfChildren(ViewGroupInflater inflater, ViewGroup view);

    boolean beforeApplyAttribute(ViewInflater<View> inflater, View view, String ns, String attrName, String value, ViewGroup parent, Map<String, String> attrs);


    void afterApplyAttribute(ViewInflater<View> inflater, View view, String ns, String attrName, String value, ViewGroup parent, Map<String, String> attrs);


    class NoOp implements LayoutInflaterDelegate {
        @Override
        public String beforeConvertXml(String xml) {
            return null;
        }

        @Override
        public String afterConvertXml(String xml) {
            return xml;
        }

        @Override
        public View afterInflation(View result, String xml, ViewGroup parent) {
            return result;
        }

        @Override
        public View beforeInflation(String xml, ViewGroup parent) {
            return null;
        }

        @Override
        public View beforeInflateView(Node node, ViewGroup parent, boolean attachToParent) {
            return null;
        }

        @Override
        public View afterInflateView(View view, Node node, ViewGroup parent, boolean attachToParent) {
            return view;
        }

        @Override
        public View beforeCreateView(Node node, String viewName, HashMap<String, String> attrs) {
            return null;
        }

        @Override
        public View afterCreateView(View view, Node node, String viewName, HashMap<String, String> attrs) {
            return view;
        }

        @Override
        public boolean beforeApplyAttributes(View view, ViewInflater<View> inflater, HashMap<String, String> attrs, ViewGroup parent) {
            return false;
        }

        @Override
        public void afterApplyAttributes(View view, ViewInflater<View> inflater, HashMap<String, String> attrs, ViewGroup parent) {

        }

        @Override
        public boolean beforeInflateChildren(ViewInflater<View> inflater, Node node, ViewGroup parent) {
            return false;
        }

        @Override
        public void afterInflateChildren(ViewInflater<View> inflater, Node node, ViewGroup parent) {

        }

        @Override
        public void afterApplyPendingAttributesOfChildren(ViewGroupInflater inflater, ViewGroup view) {

        }

        @Override
        public boolean beforeApplyPendingAttributesOfChildren(ViewGroupInflater inflater, ViewGroup view) {
            return false;
        }

        @Override
        public boolean beforeApplyAttribute(ViewInflater<View> inflater, View view, String ns, String attrName, String value, ViewGroup parent, Map<String, String> attrs) {
            return false;
        }

        @Override
        public void afterApplyAttribute(ViewInflater<View> inflater, View view, String ns, String attrName, String value, ViewGroup parent, Map<String, String> attrs) {

        }
    }
}
