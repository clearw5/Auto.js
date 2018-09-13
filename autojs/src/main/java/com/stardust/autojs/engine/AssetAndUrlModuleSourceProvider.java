package com.stardust.autojs.engine;

import android.content.res.AssetManager;
import android.net.Uri;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.commonjs.module.provider.ModuleSource;
import org.mozilla.javascript.commonjs.module.provider.UrlModuleSourceProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Stardust on 2017/5/9.
 */

public class AssetAndUrlModuleSourceProvider extends UrlModuleSourceProvider {

    private android.content.Context mContext;
    private final URI mBaseURI;
    private final String mAssetDirPath;
    private final AssetManager mAssetManager;

    public AssetAndUrlModuleSourceProvider(android.content.Context context, String assetDirPath, List<URI> list) {
        super(list, null);
        mContext = context;
        mAssetDirPath = assetDirPath;
        mBaseURI = URI.create("file:///android_asset/" + assetDirPath);
        mAssetManager = mContext.getAssets();
    }

    @Override
    protected ModuleSource loadFromPrivilegedLocations(String moduleId, Object validator) throws IOException, URISyntaxException {
        String moduleIdWithExtension = moduleId;
        if (!moduleIdWithExtension.endsWith(".js")) {
            moduleIdWithExtension += ".js";
        }
        try {
            return new ModuleSource(new InputStreamReader(mAssetManager.open(mAssetDirPath + "/" + moduleIdWithExtension)), null,
                    new URI(mBaseURI.toString() + "/" + moduleIdWithExtension), mBaseURI, validator);
        } catch (FileNotFoundException e) {
            return super.loadFromPrivilegedLocations(moduleId, validator);
        }
    }
}