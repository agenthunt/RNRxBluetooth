package fr.eove.RNRxBluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Base64;
import android.util.Log;
import com.facebook.react.bridge.*;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.github.ivbaranov.rxbluetooth.Action;
import com.github.ivbaranov.rxbluetooth.BluetoothConnection;
import com.github.ivbaranov.rxbluetooth.RxBluetooth;
import com.github.ivbaranov.rxbluetooth.events.AclEvent;
import com.google.common.primitives.UnsignedBytes;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static fr.eove.RNRxBluetooth.RNRxBluetoothPackage.TAG;


@SuppressWarnings("unused")
public class RNRxBluetoothModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private static final boolean D = true;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private ReactApplicationContext mReactContext;
    private RxBluetooth rxBluetooth;

    private List<Subscription> globalSubscriptions = new ArrayList<>();
    private List<Subscription> connectionSubscriptions = new ArrayList<>();

    private BluetoothConnection currentConnection;

    private static final String BT_DISCOVERY_STARTED = "discoveryStart";
    private static final String BT_DISCOVERY_FINISHED = "discoveryEnd";
    private static final String BT_DISCOVERED_DEVICE = "device";
    private static final String BT_RECEIVED_DATA = "data";
    private static final String BT_CONNECTED = "connected";
    private static final String BT_DISCONNECTED = "disconnected";

    public RNRxBluetoothModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
        rxBluetooth = new RxBluetooth(reactContext);
        installSubscriptions();
    }

    private void installSubscriptions() {
        globalSubscriptions.add(rxBluetooth.observeDiscovery()
                .observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.computation())
                .filter(Action.isEqualTo(BluetoothAdapter.ACTION_DISCOVERY_STARTED))
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String action) {
                        if (D) {
                            Log.d(TAG, "Started discovery!");
                        }
                        sendEventToJs(BT_DISCOVERY_STARTED, null);
                    }
                })
        );

        globalSubscriptions.add(rxBluetooth.observeDiscovery()
                .observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.computation())
                .filter(Action.isEqualTo(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String action) {
                        if (D) {
                            Log.d(TAG, "Finished discovery!");
                        }
                        sendEventToJs(BT_DISCOVERY_FINISHED, null);
                    }
                }));

        globalSubscriptions.add(rxBluetooth.observeDevices()
                .observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.computation())
                .subscribe(new Action1<BluetoothDevice>() {
                    @Override
                    public void call(BluetoothDevice bluetoothDevice) {
                        sendEventToJs(BT_DISCOVERED_DEVICE, RNRxBluetoothModule.createDevicePayload(bluetoothDevice));
                    }
                }));

        globalSubscriptions.add(rxBluetooth.observeAclEvent()
                .observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.computation())
                .subscribe(new Action1<AclEvent>() {
                    @Override
                    public void call(AclEvent aclEvent) {
                        BluetoothDevice device = aclEvent.getBluetoothDevice();
                        switch (aclEvent.getAction()) {
                            case BluetoothDevice.ACTION_ACL_CONNECTED:
                                sendEventToJs(BT_CONNECTED, RNRxBluetoothModule.createDevicePayload(device));
                                break;
                            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                                sendEventToJs(BT_DISCONNECTED, RNRxBluetoothModule.createDevicePayload(device));
                                break;
                        }
                    }
                }));
    }

    private void installConnectionHandlerFor(final String address) {
        unsubscribeConnection();
        final BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
        connectionSubscriptions.add(rxBluetooth.observeConnectDevice(device, MY_UUID)
                .observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.computation())
                .subscribe(new Subscriber<BluetoothSocket>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable connError) {
                        if (D) {
                            Log.e(TAG, "error when connecting to " + address, connError);
                        }
                    }

                    @Override
                    public void onNext(BluetoothSocket bluetoothSocket) {
                        try {
                            currentConnection = new BluetoothConnection(bluetoothSocket);
                            connectionSubscriptions.add(currentConnection
                                    .observeByteArraysStream(40)
                                    .observeOn(Schedulers.computation())
                                    .subscribeOn(Schedulers.computation())
                                    .subscribe(new Action1<byte[]>() {
                                        @Override
                                        public void call(byte[] bytes) {
                                            WritableMap params = Arguments.createMap();
                                            WritableArray data = new WritableNativeArray();
                                            for (byte b : bytes) {
                                                data.pushInt(UnsignedBytes.toInt(b));
                                            }
                                            params.putArray("payload", data);
                                            sendEventToJs(BT_RECEIVED_DATA, params);
                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable socketError) {
                                            if (D) {
                                                Log.e(TAG, "error when receiving bytes", socketError);
                                            }
                                        }
                                    }));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }));
    }

    private static WritableMap createDevicePayload(BluetoothDevice device) {
        WritableMap params = Arguments.createMap();
        params.putString("name", device.getName());
        params.putString("address", device.getAddress());
        return params;
    }

    @Override
    public String getName() {
        return "RNRxBluetooth";
    }


    @ReactMethod
    public void startDiscovery() {
        if (D) {
            Log.d(TAG, "request discovery: start...");
        }
        rxBluetooth.startDiscovery();
    }

    @ReactMethod
    public void cancelDiscovery() {
        if (D) {
            Log.d(TAG, "request discovery: cancel...");
        }
        rxBluetooth.cancelDiscovery();
    }

    @ReactMethod
    public void connect(String address) {
        if (D) {
            Log.d(TAG, "request connection to " + address);
        }
        installConnectionHandlerFor(address);
    }

    @ReactMethod
    public boolean sendBase64String(String message) {
        byte[] data = Base64.decode(message, Base64.DEFAULT);
        return currentConnection.send(data);
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

        unsubscribeGlobal();
        unsubscribeConnection();
    }

    private void unsubscribeGlobal() {
        unsubscribeAll(globalSubscriptions);
        globalSubscriptions = new ArrayList<>();
    }

    private void unsubscribeConnection() {
        unsubscribeAll(connectionSubscriptions);
        connectionSubscriptions = new ArrayList<>();
    }

    private static void unsubscribeAll(List<Subscription> subscriptions) {
        for (Subscription subscription : subscriptions) {
            unsubscribe(subscription);
        }
    }

    private static void unsubscribe(Subscription subscription) {
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    private void sendEventToJs(String eventName, @Nullable WritableMap params) {
        try {
            if (mReactContext.hasActiveCatalystInstance()) {
                if (D) {
                    Log.d(TAG, "Sending event: " + eventName);
                }
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
