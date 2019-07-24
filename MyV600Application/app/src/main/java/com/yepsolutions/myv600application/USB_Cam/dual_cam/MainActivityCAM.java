package com.yepsolutions.myv600application.USB_Cam.dual_cam;

import android.annotation.SuppressLint;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.serenegiant.usb.DeviceFilter;
import com.serenegiant.usb.IFrameCallback;
import com.serenegiant.usb.Size;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.USBMonitor.OnDeviceConnectListener;
import com.serenegiant.usb.USBMonitor.UsbControlBlock;
import com.serenegiant.usb.UVCCamera;
import com.shenyaocn.android.Encoder.CameraRecorder;
import com.yepsolutions.myv600application.R;
import com.yepsolutions.myv600application.USB_Cam.widget.UVCCameraTextureView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//import androidx.appcompat.app.AppCompatActivity;



public final class MainActivityCAM extends AppCompatActivity {
    private static final boolean DEBUG = true;	// 用于显示调试信息
    private static final String TAG = "MainActivity";

    private CameraRecorder mp4RecorderL=new CameraRecorder(1);
    private CameraRecorder mp4RecorderR=new CameraRecorder(2);

    private int bufferSize;

    private USBMonitor mUSBMonitor;					// 用于监视USB设备接入
    private UVCCamera mUVCCameraL;					// 表示左边摄像头设备
    private UVCCamera mUVCCameraR;					// 表示右边摄像头设备

    private OutputStream snapshotOutStreamL;		// 用于左边摄像头拍照
    private String snapshotFileNameL;

    private OutputStream snapshotOutStreamR;		// 用于右边摄像头拍照
    private String snapshotFileNameR;

    private static final float[] BANDWIDTH_FACTORS = { 0.5f, 0.5f };

    private int currentWidth = UVCCamera.DEFAULT_PREVIEW_WIDTH;
    private int currentHeight = UVCCamera.DEFAULT_PREVIEW_HEIGHT;

    private UVCCameraTextureView mUVCCameraViewR;	// 用于右边摄像头预览
    private Surface mRightPreviewSurface;

    private UVCCameraTextureView mUVCCameraViewL;	// 用于左边摄像头预览
    private Surface mLeftPreviewSurface;

    private ImageButton mRecordButton;
    private ImageButton mCaptureButton;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "*********onCreate");

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 避免屏幕关闭
        setContentView(R.layout.activity_usb_cam);
        com.rscja.deviceapi.OTG.getInstance().on(); //打开OTG
        mUVCCameraViewL = (UVCCameraTextureView)findViewById(R.id.camera_view_L);
        mUVCCameraViewL.setAspectRatio(UVCCamera.DEFAULT_PREVIEW_WIDTH / (float)UVCCamera.DEFAULT_PREVIEW_HEIGHT);

        mUVCCameraViewR = (UVCCameraTextureView)findViewById(R.id.camera_view_R);
        mUVCCameraViewR.setAspectRatio(UVCCamera.DEFAULT_PREVIEW_WIDTH / (float)UVCCamera.DEFAULT_PREVIEW_HEIGHT);

        mRecordButton = (ImageButton)findViewById(R.id.record_button);
        mRecordButton.setOnClickListener(mOnClickListener);

        mCaptureButton = (ImageButton)findViewById(R.id.capture_button);
        mCaptureButton.setOnClickListener(mOnClickListener);

        mUVCCameraViewL.setOnClickListener(mOnClickListener);
        mUVCCameraViewR.setOnClickListener(mOnClickListener);

        refreshControls();

        mUSBMonitor = new USBMonitor(this, mOnDeviceConnectListener);
        final List<DeviceFilter> filters = DeviceFilter.getDeviceFilters(this, R.xml.device_filter);
        mUSBMonitor.setDeviceFilter(filters);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mUSBMonitor.register();//start
        refreshControls();

    }

    @Override
    protected void onStop() {
        mUSBMonitor.unregister();
        super.onStop();
    }

    @Override
    protected void onPause() {

        // Activity暂停时要释放左右摄像头并停止录像
        super.onPause();
    }

    // 刷新UI控件状态
    @SuppressLint("RestrictedApi")
    private void refreshControls() {
        try {
            boolean enabled = (mUVCCameraL != null || mUVCCameraR != null);

            findViewById(R.id.record_button).setEnabled(enabled);
            findViewById(R.id.capture_button).setEnabled(enabled);

            ((ImageButton)findViewById(R.id.record_button)).setImageResource((mp4RecorderL.isOpened() || mp4RecorderR.isOpened()) ? R.drawable.ic_action_record_stop : R.drawable.ic_action_record);

            findViewById(R.id.textViewUVCPromptL).setVisibility(mUVCCameraL != null ? View.GONE : View.VISIBLE);
            findViewById(R.id.textViewUVCPromptR).setVisibility(mUVCCameraR != null ? View.GONE : View.VISIBLE);

            invalidateOptionsMenu();
        } catch (Exception e){}
    }

    private synchronized void releaseCameraL() {
        synchronized (this) {
            if (mp4RecorderL.isOpened()) {
                stopRecord(mp4RecorderL);
            }
            if (mUVCCameraL != null) {
                try {
                    mUVCCameraL.setStatusCallback(null);
                    mUVCCameraL.setButtonCallback(null);
                    mUVCCameraL.close();
                    mUVCCameraL.destroy();
                } catch (final Exception e) {
                    Log.e(TAG, e.getMessage());
                } finally {
                    //Log.d(TAG, "*******releaseCameraL mUVCCameraL=null");
                    mUVCCameraL = null;
                }
            }
            try {

                if (mLeftPreviewSurface != null) {
                    mLeftPreviewSurface.release();
                    mLeftPreviewSurface = null;
                }
            }
            catch (final Exception e) {
                Log.e(TAG, e.getMessage());
            }finally{
                mLeftPreviewSurface = null;
                //Log.d(TAG, "*******releaseCameraL mLeftPreviewSurface=null");
            }
        }
    }

    private synchronized void releaseCameraR() {
        synchronized (this) {
            if (mp4RecorderR.isOpened()) {
                stopRecord(mp4RecorderR);
            }
            if (mUVCCameraR != null) {
                try {
                    mUVCCameraR.setStatusCallback(null);
                    mUVCCameraR.setButtonCallback(null);
                    mUVCCameraR.close();
                    mUVCCameraR.destroy();
                } catch (final Exception e) {
                    Log.e(TAG, e.getMessage());
                } finally {
                    //Log.d(TAG, "*******mUVCCameraR mUVCCameraR=null");
                    mUVCCameraR = null;
                }
            }
            try {

                if (mRightPreviewSurface != null) {
                    mRightPreviewSurface.release();
                    mRightPreviewSurface = null;
                }
            }
            catch (final Exception e) {
                Log.e(TAG, e.getMessage());
            }finally{
                mRightPreviewSurface = null;
                //Log.d(TAG, "*******releaseCameraL mRightPreviewSurface=null");
            }
        }
    }

    @Override
    protected void onDestroy() {




        //mUSBMonitor.unregister();

        releaseCameraL();
        releaseCameraR();
        //stopAudio();

        if (mUSBMonitor != null) {
            mUSBMonitor.destroy();
            mUSBMonitor = null;
        }
        //com.rscja.deviceapi.OTG.getInstance().off(); //关闭OTG
        super.onDestroy();
    }

    // 实现快照抓取
    private synchronized void captureSnapshot() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd.HH.mm.ss.SSSS");
        Date currentTime = new Date();

        snapshotFileNameL = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DualCamera";
        snapshotFileNameL += "/IPC_";
        snapshotFileNameL += format.format(currentTime);
        snapshotFileNameL += ".L.jpg";
        File recordFile = new File(snapshotFileNameL);	// 左边摄像头快照的文件名
        if(recordFile.exists()) {
            recordFile.delete();
        }
        try {
            recordFile.createNewFile();
            snapshotOutStreamL = new FileOutputStream(recordFile);
        } catch (Exception e){}

        snapshotFileNameR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DualCamera";
        snapshotFileNameR += "/IPC_";
        snapshotFileNameR += format.format(currentTime);
        snapshotFileNameR += ".R.jpg";
        recordFile = new File(snapshotFileNameR);		// 右边摄像头快照的文件名
        if(recordFile.exists()) {
            recordFile.delete();
        }
        try {
            recordFile.createNewFile();
            snapshotOutStreamR = new FileOutputStream(recordFile);
        } catch (Exception e){}

    }

    private void startRecord(CameraRecorder mp4Recorder, UVCCamera uvcCamera) {
        if (mp4Recorder.isOpened()) {
            return;
        }

        if(uvcCamera == null) {
            return;
        }

        String extra = "L";

        if(uvcCamera == mUVCCameraR) // 通过判断是左边还是右边的摄像头来调整文件名
            extra = "R";

        String fileName = Helper.getVideoFileName(extra); // 可以参考函数实现

        try {
            mp4Recorder.open(fileName,currentWidth,currentHeight);
            mp4Recorder.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 录像计时器的实现部分
    public static String sec2time(int second){
        int h = 0;
        int d = 0;
        int s = 0;
        int temp = second%3600;
        if(second>3600){
            h= second/3600;
            if(temp!=0){
                if(temp>60){
                    d = temp/60;
                    if(temp%60!=0){
                        s = temp%60;
                    }
                }else{
                    s = temp;
                }
            }
        }else{
            d = second/60;
            if(second%60!=0){
                s = second%60;
            }
        }

        return String.format("%02d:%02d:%02d", h, d, s);
    }

    private int timer_count = 0;
    private Timer timer;

    public String getRecordTimeCount() {
        return sec2time(timer_count) + (timer_count % 2 == 0 ? " REC" : "");
    }

    private void startRecTimer() {
        stopRecTimer();
        timer_count = 0;

        timer = new Timer(true);
        timer.schedule(new TimerTask(){
            public void run() {
                timer_count++;
            }
        },1000, 1000);
    }

    private void stopRecTimer() {
        if(timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        timer_count = 0;
    }

    private void stopRecord(CameraRecorder mp4Recorder) {
        //stopRecTimer();
        if (!mp4Recorder.isOpened()) {
            return;
        }

        //final String fileName = mp4Recorder.getRecordFileName();
        mp4Recorder.stop();
    }


    private final OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
            switch (view.getId()) {
                case R.id.capture_button:
                    captureSnapshot();
                    break;
                case R.id.record_button:
                    if (mUVCCameraL != null) {
                        if (mp4RecorderL.isOpened()) {
                            stopRecord(mp4RecorderL);
                        } else {
                            startRecord(mp4RecorderL, mUVCCameraL);
                        }
                    }

                    if (mUVCCameraR != null) {
                        if (mp4RecorderR.isOpened()) {
                            stopRecord(mp4RecorderR);
                        } else {
                            startRecord(mp4RecorderR, mUVCCameraR);
                        }
                    }
                    // 实现录像功能，分别开始左右摄像头的录像

                    refreshControls();

                    break;
            }
        }
    };

    private final OnDeviceConnectListener mOnDeviceConnectListener = new OnDeviceConnectListener() {
        @Override
        public void onAttach(final UsbDevice device) {
            if (DEBUG) Log.i(TAG, "onAttach:" + device);
            final List<UsbDevice> list = mUSBMonitor.getDeviceList();
            mUSBMonitor.requestPermission(list.get(0));

            if(list.size() > 1)
                new Handler().postDelayed(new Runnable() {public void run() {mUSBMonitor.requestPermission(list.get(1));}}, 200);
        }

        @Override
        public void onConnect(final UsbDevice device, final UsbControlBlock ctrlBlock, final boolean createNew) {

            if (DEBUG) Log.i(TAG, "onConnect:"+ctrlBlock.getVenderId());
            synchronized (this) {
                if (mUVCCameraL != null && mUVCCameraR != null) { // 如果左右摄像头都打开了就不能再接入设备了
                    return;
                }
                if (ctrlBlock.getVenderId() == 2){
                    if (mUVCCameraL != null && mUVCCameraL.getDevice().equals(device)){
                        return;
                    }
                } else if (ctrlBlock.getVenderId() == 3) {
                    if ((mUVCCameraR != null && mUVCCameraR.getDevice().equals(device))) {
                        return;
                    }
                }else {
                    return;
                }
                final UVCCamera camera = new UVCCamera();
                final int open_camera_nums = (mUVCCameraL != null ? 1 : 0) + (mUVCCameraR != null ? 1 : 0);
                camera.open(ctrlBlock);

                try {
                    camera.setPreviewSize(currentWidth, currentHeight, UVCCamera.FRAME_FORMAT_MJPEG, 0.5f); // 0.5f是一个重要参数，表示带宽可以平均分配给两个摄像头，如果是一个摄像头则是1.0f，可以参考驱动实现
                } catch (final IllegalArgumentException e1) {
                    if (DEBUG) Log.i(TAG, "MJPEG Failed");
                    try {
                        camera.setPreviewSize(currentWidth, currentHeight, UVCCamera.DEFAULT_PREVIEW_MODE, 0.5f);
                    } catch (final IllegalArgumentException e2) {
                        try {
                            currentWidth = UVCCamera.DEFAULT_PREVIEW_WIDTH;
                            currentHeight = UVCCamera.DEFAULT_PREVIEW_HEIGHT;
                            camera.setPreviewSize(currentWidth, currentHeight, UVCCamera.DEFAULT_PREVIEW_MODE, 0.5f);
                        } catch (final IllegalArgumentException e3) {
                            camera.destroy();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //Toast.makeText(ShowCameraViewActivity.this, "UVC设备错误", Toast.LENGTH_LONG).show();
                                }
                            });

                            return;
                        }
                    }
                }

                // 将摄像头进行分配
                if(ctrlBlock.getVenderId() == 2 && mUVCCameraL == null) {
                    mUVCCameraL = camera;
                    try {
                        if (mLeftPreviewSurface != null) {
                            mLeftPreviewSurface.release();
                            mLeftPreviewSurface = null;
                        }

                        final SurfaceTexture st = mUVCCameraViewL.getSurfaceTexture();
                        if (st != null)
                            mLeftPreviewSurface = new Surface(st);
                        mUVCCameraL.setPreviewDisplay(mLeftPreviewSurface);

                        mUVCCameraL.setFrameCallback(mUVCFrameCallbackL, UVCCamera.PIXEL_FORMAT_YUV420SP);
                        mUVCCameraL.startPreview();
                    } catch (final Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                } else if(ctrlBlock.getVenderId() == 3 && mUVCCameraR == null) {
                    mUVCCameraR = camera;
                    if (mRightPreviewSurface != null) {
                        mRightPreviewSurface.release();
                        mRightPreviewSurface = null;
                    }

                    final SurfaceTexture st = mUVCCameraViewR.getSurfaceTexture();
                    if (st != null)
                        mRightPreviewSurface = new Surface(st);
                    mUVCCameraR.setPreviewDisplay(mRightPreviewSurface);

                    mUVCCameraR.setFrameCallback(mUVCFrameCallbackR, UVCCamera.PIXEL_FORMAT_YUV420SP);
                    mUVCCameraR.startPreview();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshControls();

                        //      if (mUVCCameraL != null || mUVCCameraR != null)
                        //      startAudio();
                    }
                });
            }
        }

        @Override
        public void onDisconnect(final UsbDevice device, final UsbControlBlock ctrlBlock) {
            if (DEBUG) Log.i(TAG, "onDisconnect:" + device);
		/*	runOnUiThread(new Runnable() {
				@Override
				public void run() {
					//refreshControls();
					if (mUVCCameraL == null && mUVCCameraR == null)
						stopAudio();
				}
			});*/
        }

        @Override
        public void onDettach(final UsbDevice device) {
            if (DEBUG) Log.i(TAG, "onDettach:" + device);
            if ((mUVCCameraL != null) && mUVCCameraL.getDevice().equals(device)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        releaseCameraL();
                    }
                });

            } else if ((mUVCCameraR != null) && mUVCCameraR.getDevice().equals(device)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        releaseCameraR();
                    }
                });
            }

        }

        @Override
        public void onCancel(final UsbDevice device) {
            if (DEBUG) Log.i(TAG, "onCancel:");
        }
    };

    // 左边摄像头的NV21视频帧回调
    private final IFrameCallback mUVCFrameCallbackL = new IFrameCallback() {
        @Override
        public void onFrame(final ByteBuffer frame) {

            if(mUVCCameraL == null)
                return;

            final Size size = mUVCCameraL.getPreviewSize();
            byte[] buffer = null;

            int FrameSize = frame.remaining();
            if (buffer == null) {
                buffer = new byte[FrameSize];
                frame.get(buffer);
            }
            if (mp4RecorderL.isVideoRecord()) { // 将视频帧发送到编码器
                mp4RecorderL.feedData(buffer);
            }

            if(snapshotOutStreamL != null) { // 将视频帧压缩成jpeg图片，实现快照捕获
                if (!(FrameSize < size.width * size.height * 3 / 2) && (buffer != null)) {
                    try {
                        new YuvImage(buffer, ImageFormat.NV21, size.width, size.height, null).compressToJpeg(new Rect(0, 0, size.width, size.height), 60, snapshotOutStreamL);
                        snapshotOutStreamL.flush();
                        snapshotOutStreamL.close();
                        Helper.fileSavedProcess(MainActivityCAM.this, snapshotFileNameL);
                    } catch (Exception ex) {
                    } finally {
                        snapshotOutStreamL = null;
                    }
                }
            }
            buffer = null;
        }
    };

    // 参考上面的注释
    private final IFrameCallback mUVCFrameCallbackR = new IFrameCallback() {
        @Override
        public void onFrame(final ByteBuffer frame) {

            if(mUVCCameraR == null)
                return;

            final Size size = mUVCCameraR.getPreviewSize();
            byte[] buffer = null;

            int FrameSize = frame.remaining();
            if (buffer == null) {
                buffer = new byte[FrameSize];
                frame.get(buffer);
            }

            if (mp4RecorderR.isVideoRecord()) {
                mp4RecorderR.feedData(buffer);
            }

            if(snapshotOutStreamR != null) {
                if (!(FrameSize < size.width * size.height * 3 / 2) && (buffer != null)) {
                    try {
                        new YuvImage(buffer, ImageFormat.NV21, size.width, size.height, null).compressToJpeg(new Rect(0, 0, size.width, size.height), 60, snapshotOutStreamR);
                        snapshotOutStreamR.flush();
                        snapshotOutStreamR.close();
                        Helper.fileSavedProcess(MainActivityCAM.this, snapshotFileNameR);
                    } catch (Exception ex) {
                    } finally {
                        snapshotOutStreamR = null;
                    }
                }
            }
            buffer = null;
        }
    };

}
