package com.vwo.mobile;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vwo.mobile.constants.ApiConstant;
import com.vwo.mobile.constants.AppConstants;
import com.vwo.mobile.data.VwoData;
import com.vwo.mobile.data.VwoLocalData;
import com.vwo.mobile.enums.VwoStartState;
import com.vwo.mobile.events.VwoStatusListener;
import com.vwo.mobile.listeners.VwoActivityLifeCycle;
import com.vwo.mobile.network.VwoDownloader;
import com.vwo.mobile.utils.VWOLogger;
import com.vwo.mobile.utils.VwoPreference;
import com.vwo.mobile.utils.VwoUrlBuilder;
import com.vwo.mobile.utils.VwoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Logger;

import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;

/**
 * Created by abhishek on 17/09/15 at 10:02 PM.
 */
public class Vwo {
    private static final Logger LOGGER = VWOLogger.getLogger(Vwo.class.getCanonicalName());

//    private static final String TAG = "VWO Mobile";

    @SuppressLint("StaticFieldLeak")
    private static Vwo sSharedInstance;

    private boolean mIsEditMode;
    @Nullable
    private final Context mContext;
    private VwoDownloader mVwoDownloader;
    private VwoUrlBuilder mVwoUrlBuilder;
    private VwoUtils mVwoUtils;
    private VwoPreference mVwoPreference;
    private VwoSocket mVwoSocket;

    private VwoData mVwoData;
    private VwoLocalData mVwoLocalData;
    private VwoConfig vwoConfig;

    private VwoStatusListener mStatusListener;
    private VwoStartState mVwoStartState;

    private Vwo(@NonNull Context context, @NonNull VwoConfig vwoConfig) {
        this.mContext = context;
        this.mIsEditMode = false;
        this.vwoConfig = vwoConfig;
        this.mVwoStartState = VwoStartState.NOT_STARTED;
    }

    private Vwo() {
        this.mContext = null;
        this.mIsEditMode = false;
        this.mVwoDownloader = new VwoDownloader(this);
        this.mVwoUrlBuilder = new VwoUrlBuilder(this);
        this.mVwoLocalData = new VwoLocalData(this);
    }

    /*private Vwo() {

    }*/

    public static Creator with(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context == null");
        }
        if (sSharedInstance == null) {
            synchronized (Vwo.class) {
                if (sSharedInstance == null) {
                    VwoConfig config = new VwoConfig
                            .Builder()
                            .build();
                    sSharedInstance = new Builder(context)
                            .setConfig(config)
                            .build();
                }
            }
        }
        return new Creator(sSharedInstance);
    }

    /*private static synchronized Vwo sharedInstance() {
        if (sSharedInstance != null) {
            return sSharedInstance;
        } else {
            sSharedInstance = new Vwo();
            return sSharedInstance;
        }
    }*/

    @SuppressWarnings("unused")
    public static Object getObjectForKey(String key) {

        if (sSharedInstance != null && sSharedInstance.mVwoStartState.getValue() >= VwoStartState.STARTED.getValue()) {
            // Only when the VWO has completely started or loaded from disk
            Object object;

            if (sSharedInstance.isEditMode()) {
                object = sSharedInstance.getVwoSocket().getObjectForKey(key);
            } else {
                object = sSharedInstance.getVwoData().getVariationForKey(key);
            }
            return object;

        }
        return null;
    }

    @SuppressWarnings("unused")
    public static Object getObjectForKey(String key, Object control) {

        Object data = getObjectForKey(key);
        if (data == null) {
            LOGGER.warning("No Key Value found. Serving Control");
            return control;
        } else {
            return data;
        }

    }

    @SuppressWarnings("unused")
    public static Object getAllObject() {

        if (sSharedInstance != null && sSharedInstance.mVwoStartState.getValue() >= VwoStartState.STARTED.getValue()) {
            // Only when the VWO has completely started or loaded from disk
            Object object;

            if (sSharedInstance.isEditMode()) {
                object = sSharedInstance.getVwoSocket().getVariation();
            } else {
                object = sSharedInstance.getVwoData().getAllVariations();
            }
            return object;
        }
        return new JSONObject();
    }

    public static void markConversionForGoal(String goalIdentifier) {

        if (sSharedInstance != null && sSharedInstance.mVwoStartState.getValue() >= VwoStartState.STARTED.getValue()) {

            Vwo vwo = new Builder().build();

            if (vwo.isEditMode()) {
                vwo.getVwoSocket().triggerGoal(goalIdentifier);
            } else {
                sSharedInstance.mVwoData.saveGoal(goalIdentifier);
            }
        }

    }

    public static void markConversionForGoal(String goalIdentifier, double value) {

        if (sSharedInstance != null && sSharedInstance.mVwoStartState.getValue() >= VwoStartState.STARTED.getValue()) {

            if (sSharedInstance.isEditMode()) {
                sSharedInstance.getVwoSocket().triggerGoal(goalIdentifier);
            } else {
                // Check if already present in persisting data
                sSharedInstance.mVwoData.saveGoal(goalIdentifier, value);
            }
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    boolean startVwoInstance() {
        LOGGER.entering(Vwo.class.getSimpleName(), "startVwoInstance(String, Application)", "**** Starting VWO ver " + VwoUtils.getVwoSdkVersion() + " ****");

        assert mContext != null;

        final AndroidSentryClientFactory factory = new AndroidSentryClientFactory(mContext);
//        factory.createSentryClient(new Dsn(ApiConstant.SENTRY));

        if (!VwoUtils.checkForInternetPermissions(mContext)) {
            return false;
        } else if (!VwoUtils.checkIfClassExists("io.socket.client.Socket") && !VwoUtils.checkIfClassExists("com.squareup.okhttp.OkHttpClient")) {
            String errMsg = "VWO is dependent on Socket.IO library.\n" +
                    "In application level build.gradle file add\t" +
                    "compile 'io.socket:socket.io-client:0.6.2'";
            LOGGER.finer(errMsg);
            return false;
        } else if (!isAndroidSDKSupported()) {
            Sentry.init(ApiConstant.SENTRY, factory);
            LOGGER.finer("Minimum SDK version should be 14");
            return false;
        } else if (!validateVwoAppKey(vwoConfig.getApiKey())) {
            Sentry.init(ApiConstant.SENTRY, factory);
            LOGGER.finer("Invalid App Key: " + vwoConfig.getAppKey());
            return false;
        } else if (this.mVwoStartState != VwoStartState.NOT_STARTED) {
            LOGGER.warning("VWO already started");
            return true;
        } else {
            // Everything is good so far
            this.mVwoStartState = VwoStartState.STARTING;
            ((Application) (mContext)).registerActivityLifecycleCallbacks(new VwoActivityLifeCycle());
            this.initializeComponents();

            int vwoSession = this.mVwoPreference.getInt(AppConstants.DEVICE_SESSION, 0) + 1;
            this.mVwoPreference.putInt(AppConstants.DEVICE_SESSION, vwoSession);

            this.mVwoDownloader.fetchFromServer(new VwoDownloader.DownloadResult() {
                @Override
                public void onDownloadSuccess(JSONArray data) {
                    Sentry.init(ApiConstant.SENTRY, factory);
                    if (data.length() == 0) {
                        LOGGER.warning("Empty data downloaded");
                        // FIXME: Handle this. Can crash here.
                    } else {
                        try {
                            LOGGER.info(data.toString(4));
                        } catch (JSONException exception) {
                            LOGGER.finer("Data not Downloaded: " + exception.getLocalizedMessage());
                            LOGGER.throwing(Vwo.class.getSimpleName(), "DownloadSuccess", exception);
                        }
                    }
                    mVwoData.parseData(data);
                    mVwoDownloader.startUpload();
                    mVwoSocket.connectToSocket();
                    mVwoLocalData.saveData(data);
                    mVwoStartState = VwoStartState.STARTED;
                    if (mStatusListener != null) {
                        Looper.prepare();
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                mStatusListener.onVwoLoaded();
                            }
                        });

                        Looper.loop();

                    }
                }

                @Override
                public void onDownloadError(Exception ex) {
                    Sentry.init(ApiConstant.SENTRY, factory);
                    mVwoDownloader.startUpload();
                    mVwoSocket.connectToSocket();
                    if (mVwoLocalData.isLocalDataPresent()) {
                        mVwoData.parseData(mVwoLocalData.getData());
                        mVwoStartState = VwoStartState.STARTED;
                        if (mStatusListener != null) {
                            Looper.prepare();
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    mStatusListener.onVwoLoaded();
                                }
                            });

                            Looper.loop();
                        }
                    } else {
                        mVwoStartState = VwoStartState.NO_INTERNET;
                        if (mStatusListener != null) {
                            Looper.prepare();
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    mStatusListener.onVwoLoadFailure();
                                }
                            });

                            Looper.loop();
                        }
                    }

                }
            });

            return true;
        }
    }

    private void initializeComponents() {
        this.mVwoLocalData = new VwoLocalData(this);
        this.mVwoUtils = new VwoUtils(this);
        this.mVwoDownloader = new VwoDownloader(this);
        this.mVwoUrlBuilder = new VwoUrlBuilder(this);
        // TODO: write a function to pass custom segment keys.
        this.mVwoData = new VwoData(this);
        this.mVwoSocket = new VwoSocket(this);
        this.mVwoPreference = new VwoPreference(this);

    }

    private boolean isAndroidSDKSupported() {
        try {
            int e = Build.VERSION.SDK_INT;
            if (e >= 14) {
                return true;
            }
        } catch (Exception var2) {
            // Not able to fetch Android Version. Ignoring and returning false.
        }

        return false;
    }

    private boolean validateVwoAppKey(String appKey) {

        return appKey.contains("-");
    }

    @SuppressWarnings("unused")
    public static void addVwoStatusListener(VwoStatusListener listener) {
        if (sSharedInstance != null) {
            sSharedInstance.mStatusListener = listener;
        }
    }

    public static class Builder {
        private final Context context;
        private VwoConfig vwoConfig;

        Builder(@NonNull Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null.");
            }
            this.context = context.getApplicationContext();
        }

        public Builder() {
            context = null;
        }

        public Vwo build() {
            return new Vwo(context, vwoConfig);
        }

        public Builder setConfig(VwoConfig vwoConfig) {
            if (vwoConfig == null) {
                throw new IllegalArgumentException("Config must not be null.");
            }
            if (this.vwoConfig != null) {
                throw new IllegalStateException("Config already set.");
            }

            this.vwoConfig = vwoConfig;
            return this;
        }

        public Context getContext() {
            return this.context;
        }

    }

    public VwoStatusListener getStatusListener() {
        return mStatusListener;
    }

    public void setStatusListener(VwoStatusListener mStatusListener) {
        this.mStatusListener = mStatusListener;
    }

    @Nullable
    public Context getCurrentContext() {
        return this.mContext;
    }

    public boolean isEditMode() {
        return mIsEditMode;
    }

    public void setIsEditMode(boolean isEditMode) {
        mIsEditMode = isEditMode;
    }

    public VwoSocket getVwoSocket() {
        return mVwoSocket;
    }

    public VwoData getVwoData() {
        return mVwoData;
    }

    public VwoUrlBuilder getVwoUrlBuilder() {
        return mVwoUrlBuilder;
    }

    public VwoConfig getConfig() {
        return this.vwoConfig;
    }

    void setConfig(VwoConfig config) {
        this.vwoConfig = config;
    }

    public VwoUtils getVwoUtils() {
        return mVwoUtils;
    }

    public VwoPreference getVwoPreference() {
        return mVwoPreference;
    }

}
