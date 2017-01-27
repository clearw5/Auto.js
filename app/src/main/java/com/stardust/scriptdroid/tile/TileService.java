package com.stardust.scriptdroid.tile;

import com.stardust.scriptdroid.action.ActionPerformService;

/**
 * Created by Stardust on 2017/1/26.
 */

public class TileService extends android.service.quicksettings.TileService {

    public void onClick(){
        ActionPerformService.assistModeEnable = !ActionPerformService.assistModeEnable;
    }
}
