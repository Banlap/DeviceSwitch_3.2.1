package com.aliyun.iot.aep.sdk;

import android.app.Application;
import android.content.SharedPreferences;

import com.aliyun.iot.aep.sdk.framework.config.AConfigure;
import com.aliyun.iot.aep.sdk.threadpool.ThreadPool;

import java.util.HashSet;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by wuwang on 2017/11/7.
 */

public class EnvConfigure {
    static final public String KEY_IS_DEBUG = "KEY_IS_DEBUG";
    static final public String KEY_DEVICE_ID = "KEY_DEVICE_ID";
    static final public String KEY_PRODUCT_ID = "KEY_PRODUCT_ID"; // 阿里云移动平台分配的产品ID
    static final public String KEY_APPKEY = "KEY_APPKEY"; // 阿里云移动平台分配的AppKey
    static final public String KEY_TRACE_ID = "KEY_TRACE_ID";
    static final public String KEY_LANGUAGE = "language";

    static final private String SHARED_PREFERENCES_NAME = "ENV_CONFIGURE";
    static private Application app = null;
    static final private HashSet<Listener> listeners = new HashSet<>();

    static public void init(Application app, HashSet<String> configuresWithinDB) {
        EnvConfigure.app = app;
        EnvConfigure.initConfiguresByConfigureDB(configuresWithinDB);
    }

    static public boolean hasEnvArg(String key) {
        return AConfigure.getInstance().getConfig().containsKey(key);
    }

    static public String getEnvArg(String key) {
        return (String) AConfigure.getInstance().getConfig().get(key);
    }

    static public String getEnvArg(String key, boolean fromDB) {
        String ret = null;

        do {
            if (false == fromDB) {
                ret = (String) AConfigure.getInstance().getConfig().get(key);
                break;
            }

            SharedPreferences preferences = app.getApplicationContext().getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
            if (null == preferences)
                break;

            if (preferences.contains(key))
                ret = preferences.getString(key, null);

            AConfigure.getInstance().getConfig().put(key, ret);
        } while (false);

        return ret;
    }

    static public void putEnvArg(String key, String value) {
        putEnvArg(key, value, false);
    }

    static public void putEnvArg(String key, String value, boolean saveToDB) {
        if (null == key || 0 >= key.length())
            return;

        String oldValue = null;
        synchronized (EnvConfigure.class) {
            oldValue = (String) AConfigure.getInstance().getConfig().get(key);
            AConfigure.getInstance().getConfig().put(key, value);
        }

        /* is there changed ? */
        boolean changed = false;
        if (null != oldValue && null != value) {
            changed = !oldValue.equals(value);
        } else if (null != oldValue && null == value) {
            changed = true;
        } else if (null == oldValue && null != value) {
            changed = true;
        } else if (null == oldValue && null == value) {
            changed = false;
        }

        if (false == changed) {
            return;
        }

        /* OK, let's save it into DB */
        if (saveToDB) {
            saveToDB(key, value);
        }

        /* OK, let's invoke the listeners */
        if (null == EnvConfigure.listeners || EnvConfigure.listeners.isEmpty())
            return;
        ThreadPool.DefaultThreadPool.getInstance().submit(new InvokeListenerTask(key, oldValue, value));
    }

    static public void registerListener(Listener listener) {
        synchronized (EnvConfigure.class) {
            if (false == listeners.contains(listener)) {
                listeners.add(listener);
            }
        }
    }

    static public void unRegisterListener(Listener listener) {
        synchronized (EnvConfigure.class) {
            if (listeners.contains(listener)) {
                listeners.remove(listener);
            }
        }
    }

    /* helper */

    static private void initConfiguresByConfigureDB(HashSet<String> configuresWithinDB) {
        if (null == configuresWithinDB || configuresWithinDB.isEmpty())
            return;

        SharedPreferences preferences = app.getApplicationContext().getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        if (null == preferences)
            return;

        for (String key : configuresWithinDB) {
            if (preferences.contains(key))
                AConfigure.getInstance().getConfig().put(key, preferences.getString(key, ""));
        }
    }

    static private void saveToDB(String key, String value) {
        if (null == key || key.isEmpty())
            return;

        SharedPreferences preferences = app.getApplicationContext().getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        if (null == preferences)
            return;

        SharedPreferences.Editor editor = preferences.edit();
        if (null == editor)
            return;

        editor.putString(key, value);
        editor.commit();
    }

    /* inner type */

    public interface Listener {
        boolean needUIThread();

        boolean needInvoked(String key);

        void onConfigureChanged(String key, String oldValue, String newValue);
    }

    static private class InvokeListenerTask implements Runnable {

        final private String key;
        final private String oldValue;
        final private String newValue;

        public InvokeListenerTask(String key, String oldValue, String newValue) {
            this.key = key;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        @Override
        public void run() {
            synchronized (EnvConfigure.class) {

                /* check the parameters */
                if (null == this.key || this.key.isEmpty())
                    return;

                /* is there listener ? */
                if (null == EnvConfigure.listeners || EnvConfigure.listeners.isEmpty())
                    return;

                for (Listener l : EnvConfigure.listeners) {
                    invoke(l);
                }
            }

        }

        private void invoke(final Listener listener) {
            if (null == listener)
                return;

            try {
                if (listener.needInvoked(this.key)) {
                    if (listener.needUIThread())
                        listener.onConfigureChanged(this.key, this.oldValue, this.newValue);
                    else
                        ThreadPool.MainThreadHandler.getInstance().post(new Runnable() {

                            @Override
                            public void run() {
                                listener.onConfigureChanged(InvokeListenerTask.this.key, InvokeListenerTask.this.oldValue, InvokeListenerTask.this.newValue);
                            }

                        });
                }
            } catch (Exception ex) {
            }

        }

    }

}
