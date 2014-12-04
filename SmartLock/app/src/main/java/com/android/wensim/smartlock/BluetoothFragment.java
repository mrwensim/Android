package com.android.wensim.smartlock;



import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.support.v4.app.Fragment;

import com.material.widget.PaperButton;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link android.app.Fragment} subclass.
 *
 */
public class BluetoothFragment extends Fragment {
    private final static int REQUEST_DEVICE_LIST = 1;

    private final static String TAG = "BluetoothFragment";

    //    private final static String UUID_KEY_DATA = "0000fee1-0000-1000-8000-00805f9b34fb";
    private final static String UUID_KEY_DATA = "0000fee1-0000-1000-8000-00805f9b34fb";

    /**搜索BLE终端*/
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeClass mBLE;

    private ArrayList<BluetoothDevice> bluetoothDevicesList;
    private boolean mScanning;
    private Handler mHandler;

    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    public static BluetoothFragment newInstance() {
        BluetoothFragment bluetoothFragment = new BluetoothFragment();
        return bluetoothFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bluetooth, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //检查设备是否支持BLE
        if(!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(getActivity(), R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
        //通过 BluetoothManager 初始化一个 BluetoothAdapter
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        //检查设备是否支持蓝牙
        if (mBluetoothAdapter == null) {
            Toast.makeText(getActivity(), R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            getActivity().finish();
            return;
        }

        mBLE = new BluetoothLeClass(getActivity());
        if (!mBLE.initialize()) {
            Log.e(TAG, "Unable to initialize Bluetooth");
            getActivity().finish();
        }

        final PaperButton mBtnScan = (PaperButton)getActivity().findViewById(R.id.btn_scan);
        mBtnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothAdapter.enable();
                if (mBluetoothAdapter.isEnabled()) {
                    Intent intent = new Intent(getActivity(), DeviceScanActivity.class);
                    startActivityForResult(intent, REQUEST_DEVICE_LIST);
                }
            }
        });
    }
    /**
     * 搜索到BLE终端服务的事件
     */
    private BluetoothLeClass.OnServiceDiscoverListener mOnServiceDiscover = new BluetoothLeClass.OnServiceDiscoverListener() {
        @Override
        public void onServiceDiscover(BluetoothGatt gatt) {
            displayGattServices(mBLE.getSupportedGattServices());
        }
    };
    /**
     * 收到BLE终端数据交互的事件
     */
    private BluetoothLeClass.OnDataAvailableListener mOnDataAvailable = new BluetoothLeClass.OnDataAvailableListener(){

        /**
         * BLE终端数据被读的事件
         */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS)
                Log.e(TAG,"onCharRead "+gatt.getDevice().getName()
                        +" read "
                        +characteristic.getUuid().toString()
                        +" -> "
                        +Utils.bytesToHexString(characteristic.getValue()));
        }
        /**
         * 收到BLE终端写入数据回调
         */
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic) {
            Log.e(TAG,"onCharWrite "+gatt.getDevice().getName()
                    +" write "
                    +characteristic.getUuid().toString()
                    +" -> "
                    +new String(characteristic.getValue()));
        }
    };
    /**
     * 读取并打印搜索到的GattService以及Characteristic
     * @param gattServices
     */
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;

        for (BluetoothGattService gattService : gattServices) {
            //-----Service的字段信息-----//
            int type = gattService.getType();
            Log.e(TAG,"-->service type:"+Utils.getServiceType(type));
            Log.e(TAG,"-->includedServices size:"+gattService.getIncludedServices().size());
            Log.e(TAG,"-->service uuid:"+gattService.getUuid());

            //-----Characteristics的字段信息-----//
            List<BluetoothGattCharacteristic> gattCharacteristics =gattService.getCharacteristics();
            for (final BluetoothGattCharacteristic  gattCharacteristic: gattCharacteristics) {
                Log.e(TAG,"---->char uuid:"+gattCharacteristic.getUuid());

                int permission = gattCharacteristic.getPermissions();
                Log.e(TAG,"---->char permission:"+Utils.getCharPermission(permission));

                int property = gattCharacteristic.getProperties();
                Log.e(TAG,"---->char property:"+Utils.getCharPropertie(property));

                byte[] data = gattCharacteristic.getValue();
                if (data != null && data.length > 0) {
                    Log.e(TAG,"---->char value:"+new String(data));
                }

                //UUID_KEY_DATA是可以跟蓝牙模块串口通信的Characteristic
                if(gattCharacteristic.getUuid().toString().equals(UUID_KEY_DATA)){
                    //测试读取当前Characteristic数据，会触发mOnDataAvailable.onCharacteristicRead()
//                    mHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            mBLE.readCharacteristic(gattCharacteristic);
//                        }
//                    }, 500);
//
//                    //接受Characteristic被写的通知,收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
//                    mBLE.setCharacteristicNotification(gattCharacteristic, true);
//                    //设置数据内容
//                    gattCharacteristic.setValue("send data->I am from China. Nice to meet you!");
//                    //往蓝牙模块写入数据
//                    mBLE.writeCharacteristic(gattCharacteristic);
                }

                //-----Descriptors的字段信息-----//
                List<BluetoothGattDescriptor> gattDescriptors = gattCharacteristic.getDescriptors();
                for (BluetoothGattDescriptor gattDescriptor : gattDescriptors) {
                    Log.e(TAG, "-------->desc uuid:" + gattDescriptor.getUuid());
                    int descPermission = gattDescriptor.getPermissions();
                    Log.e(TAG,"-------->desc permission:"+ Utils.getDescPermission(descPermission));

                    byte[] desData = gattDescriptor.getValue();
                    if (desData != null && desData.length > 0) {
                        Log.e(TAG, "-------->desc value:"+ new String(desData));
                    }
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mBLE.disconnect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mBLE.close();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
                switch (requestCode) {
            case REQUEST_DEVICE_LIST:
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras().getString(DeviceScanActivity.EXTRA_DEVICE_ADDRESS);
                    if (mBLE.connect(address)) {
//                        mBtnSend = (Button)findViewById(R.id.btn_send);
//                        mBtnSend.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                String data = mEditText.getText().toString();
//                                if (data.length() > 0) {
//                                    mGattCharacteristic = mBLE.getGattCharByUuid(UUID_KEY_DATA);
////                    mBLE.getGattCharByUuid(UUID_KEY_DATA).setValue(data);
//                                    mGattCharacteristic.setValue(data);
//                                    mBLE.writeCharacteristic(mGattCharacteristic);
//                                }
//                                else
//                                    Toast.makeText(MyActivity.this, R.string.empty_text, Toast.LENGTH_SHORT).show();
//                            }
//                        });
                        Toast.makeText(getActivity(), R.string.succeed_connect, Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getActivity(), R.string.failed_connect, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Toast.makeText(getActivity(), "erro", Toast.LENGTH_SHORT).show();
        }
    }

//    private void scanLeDevice(boolean enable) {
//        mHandler = new Handler();
//
//        if (enable) {
//            // Stops scanning after a pre-defined scan period.
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mScanning = false;
//                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
////                    invalidateOptionsMenu();
//                }
//            }, SCAN_PERIOD);
//
//            mScanning = true;
//            mBluetoothAdapter.startLeScan(mLeScanCallback);
//        } else {
//            mScanning = false;
//            mBluetoothAdapter.stopLeScan(mLeScanCallback);
//        }
//        //                    invalidateOptionsMenu();
//    }
//    // Device scan callback.
//    private BluetoothAdapter.LeScanCallback mLeScanCallback =
//            new BluetoothAdapter.LeScanCallback() {
////                @Override
////                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
////                    runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            bluetoothDevicesList.add(device);
//////                            mLeDeviceListAdapter.notifyDataSetChanged();
////                        }
////                    });
////                }
//
//                @Override
//                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
//
//                }
//            };
    //    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case REQUEST_DEVICE_LIST:
//                if (resultCode == Activity.RESULT_OK) {
//                    String address = data.getExtras().getString(DeviceScanActivity.EXTRA_DEVICE_ADDRESS);
//                    if (mBLE.connect(address)) {
////                        mBtnSend = (Button)findViewById(R.id.btn_send);
////                        mBtnSend.setOnClickListener(new View.OnClickListener() {
////                            @Override
////                            public void onClick(View view) {
////                                String data = mEditText.getText().toString();
////                                if (data.length() > 0) {
////                                    mGattCharacteristic = mBLE.getGattCharByUuid(UUID_KEY_DATA);
//////                    mBLE.getGattCharByUuid(UUID_KEY_DATA).setValue(data);
////                                    mGattCharacteristic.setValue(data);
////                                    mBLE.writeCharacteristic(mGattCharacteristic);
////                                }
////                                else
////                                    Toast.makeText(MyActivity.this, R.string.empty_text, Toast.LENGTH_SHORT).show();
////                            }
////                        });
//                        Toast.makeText(getActivity(), R.string.succeed_connect, Toast.LENGTH_SHORT).show();
//                    }
//                    else
//                        Toast.makeText(getActivity(), R.string.failed_connect, Toast.LENGTH_SHORT).show();
//                }
//                break;
//            default:
//                Toast.makeText(getActivity(), "erro", Toast.LENGTH_SHORT).show();
//        }
//    }
}
