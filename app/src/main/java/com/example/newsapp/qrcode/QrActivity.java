package com.example.newsapp.qrcode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.util.Size;
import android.widget.Toast;

import com.example.newsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QrActivity extends AppCompatActivity {
private ListenableFuture cameraProviderFuture;
private ExecutorService executor;
private PreviewView previewView;
private MyImageAnalyzer analyzer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);


        previewView=findViewById(R.id.previewView);
        this.getWindow().setFlags(1024,1024);
        //backGround Jobs
        executor= Executors.newSingleThreadExecutor();
        cameraProviderFuture= ProcessCameraProvider.getInstance(this);
        analyzer=new MyImageAnalyzer(getSupportFragmentManager());
        //Camera Provider future
        cameraProviderFuture.addListener(() -> {
            try {
                if (ActivityCompat.checkSelfPermission(QrActivity.this, Manifest.permission.CAMERA)!=
                        ( PackageManager.PERMISSION_GRANTED)){
                    ActivityCompat.requestPermissions(QrActivity.this,
                            new String[]{Manifest.permission.CAMERA},101);

                }
                else {
                    ProcessCameraProvider processCameraProvider= (ProcessCameraProvider) cameraProviderFuture.get();
                    bindPreview(processCameraProvider);
                }
            } catch (ExecutionException e) {
               e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==101 && grantResults.length>0){
            ProcessCameraProvider processCameraProvider=null;
            try {
                processCameraProvider= (ProcessCameraProvider) cameraProviderFuture.get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }bindPreview(processCameraProvider);


        }
    }

    private void bindPreview(ProcessCameraProvider processCameraProvider) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        ImageCapture imageCapture;
        imageCapture = new ImageCapture.Builder().build();
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(1280,720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();
        imageAnalysis.setAnalyzer(executor,analyzer);
        processCameraProvider.unbindAll();
        processCameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, imageAnalysis);
    }

    //Image AnalyzerClass
    public class MyImageAnalyzer implements ImageAnalysis.Analyzer {
            private final FragmentManager fragmentManager;
            private final BottomDialog bottomDialog;
        public MyImageAnalyzer(FragmentManager supportFragmentManager) {
            this.fragmentManager=supportFragmentManager;
            bottomDialog=new BottomDialog();

        }

        @Override
        public void analyze(@NonNull ImageProxy image) {
           scanBarCode(image);

        }


    private void scanBarCode(ImageProxy image) {
        @SuppressLint("UnsafeOptInUsageError")Image image1=image.getImage();
        assert image1!=null;
        InputImage inputImage=InputImage.fromMediaImage(image1,image.getImageInfo().getRotationDegrees());
        BarcodeScannerOptions scannerOptions=new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                        Barcode.FORMAT_QR_CODE,
                        Barcode.FORMAT_AZTEC)
                .build();
         BarcodeScanner scanner= BarcodeScanning.getClient(scannerOptions);
        Task<List<Barcode>>result=scanner.process(inputImage)
                .addOnSuccessListener(barcodes -> readerBarcodeData(barcodes))
                .addOnFailureListener(e -> Toast.makeText(QrActivity.this, "Failed To Read BarCode", Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(task -> image.close());
    }

    private void readerBarcodeData(List<Barcode> barcodes) {
        for (Barcode barcode: barcodes) {
            Rect bounds = barcode.getBoundingBox();
            Point[] corners = barcode.getCornerPoints();

            String rawValue = barcode.getRawValue();

            int valueType = barcode.getValueType();
            // See API reference for complete list of supported types
            switch (valueType) {
                case Barcode.TYPE_WIFI:
                    String ssid = barcode.getWifi().getSsid();
                    String password = barcode.getWifi().getPassword();
                    int type = barcode.getWifi().getEncryptionType();
                    break;
                case Barcode.TYPE_URL:
                    String title = barcode.getUrl().getTitle();
                    String url = barcode.getUrl().getUrl();
                    break;
            }
        }


    }
    }
    }
