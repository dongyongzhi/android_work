/*
 * Copyright (c) 2014, The Linux Foundation. All rights reserved.
 * Not a Contribution.
 *
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yfcomm.launcher;

import android.app.Application;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.LauncherApps;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.os.Handler;

import java.lang.ref.WeakReference;

public class LauncherApplication extends Application {
	
    static final String TAG = "LauncherApplication";
    private LauncherModel mModel;
    private IconCache mIconCache;
    private WidgetPreviewLoader.CacheDb mWidgetPreviewCacheDb;
    private static boolean sIsScreenLarge;
    private static float sScreenDensity;
    private static int sLongPressTimeout = 300;
    private static final String sSharedPreferencesKey = "com.yfcomm.launcher.prefs";
    WeakReference<LauncherProvider> mLauncherProvider;
    public static boolean LAUNCHER_SHOW_UNREAD_NUMBER;

    public static boolean LUNCHER_SORT_ENABLED = false;
    public static boolean SHOW_CTAPP_FEATRUE = false;
    public static boolean LAUNCHER_SHORTCUT_ENABLED = false;
    public static boolean DISPLAY_WALLPAPER_IN_ALLAPP;
    public static float WALLPAPER_DIM_IN_ALLAPP;
    private String mStkAppName = new String();
    private final String STK_PACKAGE_INTENT_ACTION_NAME =
            "org.codeaurora.carrier.ACTION_TELEPHONY_SEND_STK_TITLE";
    private final String STK_APP_NAME = "StkTitle";

    @Override
    public void onCreate() {
        super.onCreate();

        // set sIsScreenXLarge and sScreenDensity *before* creating icon cache
        sIsScreenLarge = getResources().getBoolean(R.bool.is_large_screen);
        sScreenDensity = getResources().getDisplayMetrics().density;

        recreateWidgetPreviewDb();
        mIconCache = new IconCache(this);
        mModel = new LauncherModel(this, mIconCache);
        
        LauncherApps launcherApps = (LauncherApps)
                getSystemService(Context.LAUNCHER_APPS_SERVICE);
        launcherApps.registerCallback(mModel.getLauncherAppsCallback());

        
        LUNCHER_SORT_ENABLED = getResources().getBoolean(R.bool.config_launcher_sort);
        SHOW_CTAPP_FEATRUE = getResources().getBoolean(R.bool.config_launcher_page);
        LAUNCHER_SHORTCUT_ENABLED = getResources().getBoolean(R.bool.config_launcher_shortcut);
        LAUNCHER_SHOW_UNREAD_NUMBER = getResources().getBoolean(
                R.bool.config_launcher_show_unread_number);
        DISPLAY_WALLPAPER_IN_ALLAPP = getResources()
                .getBoolean(R.bool.config_launcher_wallpaper_enabled);
        WALLPAPER_DIM_IN_ALLAPP = getResources()
                .getInteger(R.integer.def_launcher_wallpaper_dim) / 10f;

        
        
        
        
        // Register intent receivers
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);//监视区域的变化
        filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);//监视屏幕的变化
        
        registerReceiver(mModel, filter);
        filter = new IntentFilter();
        filter.addAction(SearchManager.INTENT_GLOBAL_SEARCH_ACTIVITY_CHANGED);
        registerReceiver(mModel, filter);
        filter = new IntentFilter();
        filter.addAction(SearchManager.INTENT_ACTION_SEARCHABLES_CHANGED);
        registerReceiver(mModel, filter);
        
        filter = new IntentFilter();
        if (LAUNCHER_SHOW_UNREAD_NUMBER) {
            filter.addAction(LauncherModel.ACTION_UNREAD_CHANGED);
            registerReceiver(mModel, filter);
        }
        // Register for changes to the favorites
        ContentResolver resolver = getContentResolver();
        resolver.registerContentObserver(LauncherSettings.Favorites.CONTENT_URI, true,
                mFavoritesObserver);
        if (getResources().getBoolean(R.bool.config_telcel_homescreen_enabled)
                || getResources().getBoolean(R.bool.config_claro_homescreen_enabled)) {
            registerAppNameChangeReceiver();
        }
    }

    private void registerAppNameChangeReceiver() {
        IntentFilter intentFilter = new IntentFilter(STK_PACKAGE_INTENT_ACTION_NAME);
        registerReceiver(appNameChangeReceiver, intentFilter);
    }

    /**
     * Receiver for STK Name change broadcast
     */
    private BroadcastReceiver appNameChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mStkAppName = intent.getStringExtra(STK_APP_NAME);
        }
    };

    public String getStkAppName(){
        return mStkAppName;
    }

    public void recreateWidgetPreviewDb() {
        mWidgetPreviewCacheDb = new WidgetPreviewLoader.CacheDb(this);
    }

    /**
     * There's no guarantee that this function is ever called.
     */
    @Override
    public void onTerminate() {
        super.onTerminate();

        unregisterReceiver(mModel);

        ContentResolver resolver = getContentResolver();
        resolver.unregisterContentObserver(mFavoritesObserver);
    }

    /**
     * Receives notifications whenever the user favorites have changed.
     */
    private final ContentObserver mFavoritesObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            // If the database has ever changed, then we really need to force a reload of the
            // workspace on the next load
            mModel.resetLoadedState(false, true);
            mModel.startLoaderFromBackground();
        }
    };

    LauncherModel setLauncher(Launcher launcher) {
        mModel.initialize(launcher);
        return mModel;
    }

    IconCache getIconCache() {
        return mIconCache;
    }

    LauncherModel getModel() {
        return mModel;
    }

    WidgetPreviewLoader.CacheDb getWidgetPreviewCacheDb() {
        return mWidgetPreviewCacheDb;
    }

    void setLauncherProvider(LauncherProvider provider) {
        mLauncherProvider = new WeakReference<LauncherProvider>(provider);
    }

    LauncherProvider getLauncherProvider() {
        return mLauncherProvider.get();
    }

    public static String getSharedPreferencesKey() {
        return sSharedPreferencesKey;
    }

    public static boolean isScreenLarge() {
        return sIsScreenLarge;
    }

    public static boolean isScreenLandscape(Context context) {
        return context.getResources().getConfiguration().orientation ==
            Configuration.ORIENTATION_LANDSCAPE;
    }

    public static float getScreenDensity() {
        return sScreenDensity;
    }

    public static int getLongPressTimeout() {
        return sLongPressTimeout;
    }
}
