package com.example.midterm.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.midterm.R;
import com.example.midterm.viewModel.TicketViewModel;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QRScannerActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST = 100;
    private DecoratedBarcodeView barcodeView;
    private TicketViewModel ticketViewModel;
    private boolean isScanning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        barcodeView = findViewById(R.id.barcode_scanner);
        ticketViewModel = new ViewModelProvider(this).get(TicketViewModel.class);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Quét mã QR Check-in");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Kiểm tra quyền camera
        if (checkCameraPermission()) {
            startScanning();
        } else {
            requestCameraPermission();
        }
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanning();
            } else {
                Toast.makeText(this, "Cần quyền truy cập camera để quét QR code",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void startScanning() {
        barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result != null && isScanning) {
                    isScanning = false;
                    handleQRCodeScanned(result.getText());
                }
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
            }
        });
    }

    private void handleQRCodeScanned(String qrCode) {
        // Pause scanning
        barcodeView.pause();

        // Check-in ticket
        new Thread(() -> {
            try {
                String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                        Locale.getDefault()).format(new Date());

                boolean success = ticketViewModel.checkInTicketByQrCode(qrCode, currentDateTime);

                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(QRScannerActivity.this,
                                "Check-in thành công! ✓",
                                Toast.LENGTH_SHORT).show();

                        // Resume scanning after 2 seconds
                        barcodeView.postDelayed(() -> {
                            isScanning = true;
                            barcodeView.resume();
                        }, 2000);
                    } else {
                        Toast.makeText(QRScannerActivity.this,
                                "Vé không hợp lệ hoặc đã được check-in",
                                Toast.LENGTH_LONG).show();

                        // Resume scanning immediately
                        isScanning = true;
                        barcodeView.resume();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(QRScannerActivity.this,
                            "Lỗi khi check-in: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    isScanning = true;
                    barcodeView.resume();
                });
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkCameraPermission()) {
            barcodeView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
