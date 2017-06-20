package fr.eove.RNRxBluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.util.Log;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import rx.Subscription;
import rx.functions.Action1;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.github.ivbaranov.rxbluetooth.Action;
import com.github.ivbaranov.rxbluetooth.BluetoothConnection;
import com.github.ivbaranov.rxbluetooth.RxBluetooth;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import static fr.eove.RNRxBluetooth.RNRxBluetoothPackage.TAG;


@SuppressWarnings("unused")
public class RNRxBluetoothModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private static final boolean D = true;

    private ReactApplicationContext reactContext;
    private RxBluetooth rxBluetooth;
    private Subscription discoveryStartSubscription;

    private static final String BT_DISCOVERY_STARTED = "discoveryStarted";

    public RNRxBluetoothModule(ReactApplicationContext reactContext) {
        super(reactContext);
        rxBluetooth = new RxBluetooth(reactContext);
        installSubscriptions();
    }

    private void installSubscriptions() {
        discoveryStartSubscription = rxBluetooth.observeDiscovery()
                .filter(Action.isEqualTo(BluetoothAdapter.ACTION_DISCOVERY_STARTED))
                .subscribe(new Action1<String>() {
                    @Override public void call(String action) {
                        if (D) Log.d(TAG, "Started discovery!");
                        sendEvent(BT_DISCOVERY_STARTED, null);
                    }
                });
    }

    @Override
    public String getName() {
        return "RNRxBluetooth";
    }


    @ReactMethod
    public void startDiscovery() {
        if (D) Log.d(TAG, "requested discovery start");
        rxBluetooth.startDiscovery();
    }

    @Override
    public void onHostResume() {

    }

    @Override
    public void onHostPause() {

    }

    @Override
    public void onHostDestroy() {
        if (rxBluetooth != null) {
            rxBluetooth.cancelDiscovery();
        }

        unsubscribe(discoveryStartSubscription);
    }

    private static void unsubscribe(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            subscription = null;
        }
    }

    /**
     * Send event to javascript
     * @param eventName Name of the event
     * @param params Additional params
     */
    private void sendEvent(String eventName, @Nullable WritableMap params) {
        if (reactContext.hasActiveCatalystInstance()) {
            if (D) Log.d(TAG, "Sending event: " + eventName);
            reactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(eventName, params);
        }
    }
}
