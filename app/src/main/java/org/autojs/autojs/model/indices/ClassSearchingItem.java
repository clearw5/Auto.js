package org.autojs.autojs.model.indices;

import androidx.annotation.NonNull;
import android.util.Log;

public abstract class ClassSearchingItem implements Comparable<ClassSearchingItem> {

    static final String BASE_URL = "http://www.android-doc.com/reference/";

    protected int rank;

    public abstract boolean matches(String keywords);

    public abstract String getLabel();

    public abstract String getUrl();

    @Override
    public int compareTo(@NonNull ClassSearchingItem o) {
        return Integer.compare(o.rank, rank);
    }

    protected int rank(String words, String keywords) {
        int length = words.length();
        int i = words.indexOf(keywords);
        if (i < 0) {
            return 0;
        }
        //full matches
        if (i == 0 && keywords.length() == length) {
            return 10;
        }
        //words ends with keywords
        if (i + keywords.length() == length) {
            if (i > 0 && words.charAt(i - 1) == '.') {
                return 9;
            }
            return 8;
        }
        //package starts with keywords
        if (i > 0 && words.charAt(i - 1) == '.') {
            //package equals keywords
            if (i < length - 1 && words.charAt(i + 1) == '.') {
                return 7;
            }
            return 6;
        }
        //package ends with keywords
        if (i < length - 1 && words.charAt(i + 1) == '.') {
            return 6;
        }
        if (i == 0) {
            return 5;
        }
        return 1;
    }

    @Override
    public String toString() {
        return "ClassSearchingItem{" + getLabel() + "}";
    }

    public abstract String getImportText();

    public static class ClassItem extends ClassSearchingItem {

        private final AndroidClass mAndroidClass;

        public ClassItem(AndroidClass androidClass) {
            mAndroidClass = androidClass;
        }

        @Override
        public boolean matches(String keywords) {
            rank = rank(mAndroidClass.getFullName(), keywords);
            Log.d("ClassSearching", "rank = " + rank + ", word = " + mAndroidClass.getFullName());
            return rank > 0;
        }


        public String getLabel() {
            return String.format("%s (%s)", mAndroidClass.getClassName(), mAndroidClass.getPackageName());
        }

        @Override
        public String getUrl() {
            return BASE_URL + mAndroidClass.getPackageName().replace('.', '/')
                     + "/" + mAndroidClass.getClassName() + ".html";
        }

        @Override
        public String getImportText() {
            return String.format("importClass(%s)", mAndroidClass.getFullName());
        }

        public AndroidClass getAndroidClass() {
            return mAndroidClass;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ClassItem classItem = (ClassItem) o;
            return mAndroidClass.equals(classItem.mAndroidClass);
        }

        @Override
        public int hashCode() {
            return mAndroidClass.hashCode();
        }
    }

    public static class PackageItem extends ClassSearchingItem {

        private final String mPackageName;

        public PackageItem(String packageName) {
            mPackageName = packageName;
        }

        @Override
        public boolean matches(String keywords) {
            rank = rank(mPackageName, keywords);
            return rank > 0;
        }

        @Override
        public String getLabel() {
            return mPackageName;
        }

        @Override
        public String getUrl() {
            return BASE_URL + mPackageName.replace('.', '/') + "/package-summary.html";
        }

        @Override
        public String getImportText() {
            return String.format("importPackage(%s)", mPackageName);
        }

        public String getPackageName() {
            return mPackageName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PackageItem that = (PackageItem) o;
            return mPackageName.equals(that.mPackageName);
        }

        @Override
        public int hashCode() {
            return mPackageName.hashCode();
        }
    }
}
