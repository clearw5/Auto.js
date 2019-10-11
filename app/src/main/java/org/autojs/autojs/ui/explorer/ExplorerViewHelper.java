package org.autojs.autojs.ui.explorer;

import android.graphics.Color;

import com.stardust.app.GlobalAppContext;
import com.stardust.pio.PFiles;

import org.autojs.autojs.R;
import org.autojs.autojs.model.explorer.ExplorerFileItem;
import org.autojs.autojs.model.explorer.ExplorerItem;
import org.autojs.autojs.model.explorer.ExplorerPage;
import org.autojs.autojs.model.explorer.ExplorerProjectPage;
import org.autojs.autojs.model.explorer.ExplorerSamplePage;

import static androidx.core.content.ContextCompat.getColor;
import static org.autojs.autojs.model.explorer.ExplorerItem.TYPE_AUTO_FILE;
import static org.autojs.autojs.model.explorer.ExplorerItem.TYPE_JAVASCRIPT;
import static org.autojs.autojs.model.explorer.ExplorerItem.TYPE_UNKNOWN;

public class ExplorerViewHelper {

    public static String getDisplayName(ExplorerItem item) {
        if (item instanceof ExplorerSamplePage && ((ExplorerSamplePage) item).isRoot()) {
            return GlobalAppContext.getString(R.string.text_sample);
        }
        if (item instanceof ExplorerPage) {
            return item.getName();
        }
        String type = item.getType();
        if (type.equals(TYPE_JAVASCRIPT) || type.equals(TYPE_AUTO_FILE)) {
            if (item instanceof ExplorerFileItem) {
                return ((ExplorerFileItem) item).getFile().getSimplifiedName();
            }
            return PFiles.getNameWithoutExtension(item.getName());
        }
        return item.getName();
    }

    public static String getIconText(ExplorerItem item) {
        String type = item.getType();
        if (type.isEmpty()) {
            return TYPE_UNKNOWN;
        }
        if (type.equals(TYPE_AUTO_FILE)) {
            return "R";
        }

        return type.substring(0, 1).toUpperCase();
    }

    public static int getIconColor(ExplorerItem item) {
        switch (item.getType()) {
            case TYPE_JAVASCRIPT:
                return getColor(GlobalAppContext.get(), R.color.color_j);
            case TYPE_AUTO_FILE:
                return getColor(GlobalAppContext.get(), R.color.color_r);
            default:
                return Color.GRAY;
        }
    }

    public static int getIcon(ExplorerPage page) {
        if (page instanceof ExplorerSamplePage) {
            return R.drawable.ic_sample_dir;
        }
        if(page instanceof ExplorerProjectPage){
            return R.drawable.ic_project;
        }
        return R.drawable.ic_folder_yellow_100px;
    }
}
