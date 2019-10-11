package org.autojs.autojs.model.autocomplete;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Stardust on 2018/2/3.
 */

public class DictionaryTree<T> {

    private static class Node<T> {

        char ch;
        Map<Character, Node<T>> children = new TreeMap<>();
        String wordEndHere;
        T tag;

        Node(char ch) {
            this.ch = ch;
        }
    }

    public static class Entry<T> {
        public String word;
        public T tag;

        public Entry(String word, T tag) {
            this.word = word;
            this.tag = tag;
        }
    }

    private Node<T> mRoot = new Node<>('@');

    public void putWord(String word, T tag) {
        Node<T> node = mRoot;
        for (int i = 0; i < word.length(); i++) {
            node = getOrCreateNode(node, word.charAt(i));
        }
        node.tag = tag;
        node.wordEndHere = word;
    }

    @NonNull
    public List<Entry<T>> searchByPrefill(String prefill) {
        Node<T> node = mRoot;
        for (int i = 0; i < prefill.length(); i++) {
            node = node.children.get(prefill.charAt(i));
            if (node == null) {
                return Collections.emptyList();
            }
        }
        List<Entry<T>> entries = new ArrayList<>();
        collectChildren(node, entries);
        return entries;
    }

    private void collectChildren(Node<T> node, List<Entry<T>> entries) {
        for (Map.Entry<Character, Node<T>> entry : node.children.entrySet()) {
            Node<T> child = entry.getValue();
            if (child.wordEndHere != null) {
                entries.add(new Entry<>(child.wordEndHere, child.tag));
            }
            collectChildren(child, entries);
        }
    }

    private Node<T> getOrCreateNode(Node<T> parent, char ch) {
        Node<T> child = parent.children.get(ch);
        if (child == null) {
            child = new Node<>(ch);
            parent.children.put(ch, child);
        }
        return child;
    }
}
