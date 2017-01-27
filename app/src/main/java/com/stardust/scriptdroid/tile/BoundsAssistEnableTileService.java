package com.stardust.scriptdroid.tile;

import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.stardust.scriptdroid.droid.runtime.action.ActionPerformService;

/**
 * Created by Stardust on 2017/1/26.
 */

public class BoundsAssistEnableTileService extends TileService {

    public void onClick() {
        ActionPerformService.setAssistModeEnable(!ActionPerformService.isAssistModeEnable());
        updateTile();
    }

    private void updateTile() {
        getQsTile().setState(ActionPerformService.isAssistModeEnable() ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        getQsTile().updateTile();
    }


    @Override
    public void onStartListening() {
        updateTile();
    }
}
