package org.autojs.autojs.tool;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

public class SafeJsonObject extends JsonElement {

    private final JsonObject mJsonObject;

    public SafeJsonObject(JsonObject jsonObject) {
        mJsonObject = jsonObject;
    }

    public JsonObject deepCopy() {
        return mJsonObject.deepCopy();
    }

    public void add(String property, JsonElement value) {
        mJsonObject.add(property, value);
    }

    public JsonElement remove(String property) {
        return mJsonObject.remove(property);
    }

    public void addProperty(String property, String value) {
        mJsonObject.addProperty(property, value);
    }

    public void addProperty(String property, Number value) {
        mJsonObject.addProperty(property, value);
    }

    public void addProperty(String property, Boolean value) {
        mJsonObject.addProperty(property, value);
    }

    public void addProperty(String property, Character value) {
        mJsonObject.addProperty(property, value);
    }

    public Set<Map.Entry<String, JsonElement>> entrySet() {
        return mJsonObject.entrySet();
    }

    public Set<String> keySet() {
        return mJsonObject.keySet();
    }

    public int size() {
        return mJsonObject.size();
    }

    public boolean has(String memberName) {
        return mJsonObject.has(memberName);
    }

    public JsonElement get(String memberName) {
        return mJsonObject.get(memberName);
    }

    public JsonPrimitive getAsJsonPrimitive(String memberName) {
        return mJsonObject.getAsJsonPrimitive(memberName);
    }

    public JsonArray getAsJsonArray(String memberName) {
        return mJsonObject.getAsJsonArray(memberName);
    }

    public JsonObject getAsJsonObject(String memberName) {
        return mJsonObject.getAsJsonObject(memberName);
    }

    public boolean isJsonArray() {
        return mJsonObject.isJsonArray();
    }

    public boolean isJsonObject() {
        return mJsonObject.isJsonObject();
    }

    public boolean isJsonPrimitive() {
        return mJsonObject.isJsonPrimitive();
    }

    public boolean isJsonNull() {
        return mJsonObject.isJsonNull();
    }

    public JsonObject getAsJsonObject() {
        return mJsonObject.getAsJsonObject();
    }

    public JsonArray getAsJsonArray() {
        return mJsonObject.getAsJsonArray();
    }

    public JsonPrimitive getAsJsonPrimitive() {
        return mJsonObject.getAsJsonPrimitive();
    }

    public JsonNull getAsJsonNull() {
        return mJsonObject.getAsJsonNull();
    }

    public boolean getAsBoolean() {
        return mJsonObject.getAsBoolean();
    }

    public Number getAsNumber() {
        return mJsonObject.getAsNumber();
    }

    public String getAsString() {
        return mJsonObject.getAsString();
    }

    public double getAsDouble() {
        return mJsonObject.getAsDouble();
    }

    public float getAsFloat() {
        return mJsonObject.getAsFloat();
    }

    public long getAsLong() {
        return mJsonObject.getAsLong();
    }

    public int getAsInt() {
        return mJsonObject.getAsInt();
    }

    public byte getAsByte() {
        return mJsonObject.getAsByte();
    }

    public char getAsCharacter() {
        return mJsonObject.getAsCharacter();
    }

    public BigDecimal getAsBigDecimal() {
        return mJsonObject.getAsBigDecimal();
    }

    public BigInteger getAsBigInteger() {
        return mJsonObject.getAsBigInteger();
    }

    public short getAsShort() {
        return mJsonObject.getAsShort();
    }
}
