package fr.eove.RNRxBluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import rx.Subscription;
import rx.functions.Action1;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.github.ivbaranov.rxbluetooth.Action;
import com.github.ivbaranov.rxbluetooth.RxBluetooth;

import javax.annotation.Nullable;

import static fr.eove.RNRxBluetooth.RNRxBluetoothPackage.TAG;


@SuppressWarnings("unused")
public class RNRxBluetoothModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private static final boolean D = false;

    private ReactApplicationContext mReactContext;
    private RxBluetooth rxBluetooth;
    private Subscription deviceSubscription;
    private Subscription discoveryStartSubscription;
    private Subscription discoveryFinishSubscription;

    private static final String BT_DISCOVERY_STARTED = "discoveryStart";
    private static final String BT_DISCOVERY_FINISHED = "discoveryEnd";
    private static final String BT_DISCOVERED_DEVICE = "device";

    public RNRxBluetoothModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
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

        discoveryFinishSubscription = rxBluetooth.observeDiscovery()
                .filter(Action.isEqualTo(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
                .subscribe(new Action1<String>() {
                    @Override public void call(String action) {
                        if (D) Log.d(TAG, "Finished discovery!");
                        sendEvent(BT_DISCOVERY_FINISHED, null);
                    }
                });

        deviceSubscription = rxBluetooth.observeDevices()
                .subscribe(new Action1<BluetoothDevice>() {
                    @Override
                    public void call(BluetoothDevice bluetoothDevice) {
                        WritableMap params = Arguments.createMap();
                        params.putString("name", bluetoothDevice.getName());
                        params.putString("address", bluetoothDevice.getAddress());
                        sendEvent(BT_DISCOVERED_DEVICE, params);
                    }
                });
    }

    @Override
    public String getName() {
        return "RNRxBluetooth";
    }


    @ReactMethod
    public void startDiscovery() {
        rxBluetooth.startDiscovery();
    }

    @ReactMethod
    public void cancelDiscovery() {
        rxBluetooth.cancelDiscovery();
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
        unsubscribe(deviceSubscription);
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
        try {
            if (mReactContext.hasActiveCatalystInstance()) {
                if (D) Log.d(TAG, "Sending event: " + eventName);
                mReactContext
                        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                        .emit(eventName, params);
            }
        } catch (Exception error) {
            error.printStackTrace();
            Log.e(TAG, error.toString());
        }

    }
}
