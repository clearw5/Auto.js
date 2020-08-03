package org.autojs.autojs.tool;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.math.BigDecimal;
import java.math.BigInteger;

public class SafeJsonElement extends JsonElement {
    private final JsonElement mJsonElement;

    public SafeJsonElement(JsonElement jsonElement) {
        mJsonElement = jsonElement;
    }


    @Override
    public JsonElement deepCopy() {
        return null;
    }

    @Override
    public boolean isJsonArray() {
        return mJsonElement.isJsonArray();
    }

    @Override
    public boolean isJsonObject() {
        return mJsonElement.isJsonObject();
    }

    @Override
    public boolean isJsonPrimitive() {
        return mJsonElement.isJsonPrimitive();
    }

    @Override
    public boolean isJsonNull() {
        return mJsonElement.isJsonNull();
    }

    public JsonObject getAsJsonObject() {
        return mJsonElement.getAsJsonObject();
    }

    public SafeJsonObject getAsSafeJsonObject() {
        try {
            return new SafeJsonObject(mJsonElement.getAsJsonObject());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public JsonArray getAsJsonArray() {
        return mJsonElement.getAsJsonArray();
    }

    @Override
    public JsonPrimitive getAsJsonPrimitive() {
        return mJsonElement.getAsJsonPrimitive();
    }

    @Override
    public JsonNull getAsJsonNull() {
        return mJsonElement.getAsJsonNull();
    }

    @Override
    public boolean getAsBoolean() {
        return mJsonElement.getAsBoolean();
    }

    @Override
    public Number getAsNumber() {
        return mJsonElement.getAsNumber();
    }

    @Override
    public String getAsString() {
        return mJsonElement.getAsString();
    }

    @Override
    public double getAsDouble() {
        return mJsonElement.getAsDouble();
    }

    @Override
    public float getAsFloat() {
        return mJsonElement.getAsFloat();
    }

    @Override
    public long getAsLong() {
        return mJsonElement.getAsLong();
    }

    @Override
    public int getAsInt() {
        return mJsonElement.getAsInt();
    }

    @Override
    public byte getAsByte() {
        return mJsonElement.getAsByte();
    }

    @Override
    public char getAsCharacter() {
        return mJsonElement.getAsCharacter();
    }

    @Override
    public BigDecimal getAsBigDecimal() {
        return mJsonElement.getAsBigDecimal();
    }

    @Override
    public BigInteger getAsBigInteger() {
        return mJsonElement.getAsBigInteger();
    }

    @Override
    public short getAsShort() {
        return mJsonElement.getAsShort();
    }

    @Override
    public String toString() {
        return mJsonElement.toString();
    }
}
