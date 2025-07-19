package com.exalta.acremote;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.hardware.ConsumerIrManager;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Button btnStartScan;
    private Button btnTestPattern;
    private TextView logView;

    private ConsumerIrManager irManager;
    private Handler handler = new Handler();
    private int currentPatternIndex = 0;
    private boolean isScanning = false;

    // Placeholder for IR patterns (e.g., NEC protocol)
    // We'll need to generate or find these.
    // For now, just an example.
    private int[][] patterns = {
            {19000, 4500, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 40000}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStartScan = findViewById(R.id.btn_start_scan);
        btnTestPattern = findViewById(R.id.btn_test_pattern);
        logView = findViewById(R.id.log_view);

        irManager = (ConsumerIrManager) getSystemService(CONSUMER_IR_SERVICE);

        btnStartScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isScanning) {
                    stopScan();
                } else {
                    startScan();
                }
            }
        });

        btnTestPattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testPattern(currentPatternIndex);
            }
        });

        log("App started. Ready to scan.");
    }

    private void startScan() {
        if (!irManager.hasIrEmitter()) {
            log("Error: No IR Emitter found.");
            return;
        }
        isScanning = true;
        btnStartScan.setText("Stop IR Scan");
        log("Starting IR scan...");
        handler.post(scanRunnable);
    }

    private void stopScan() {
        isScanning = false;
        btnStartScan.setText("Start IR Scan");
        handler.removeCallbacks(scanRunnable);
        log("IR scan stopped.");
    }

    private void testPattern(int patternIndex) {
        if (!irManager.hasIrEmitter()) {
            log("Error: No IR Emitter found.");
            return;
        }
        if (patternIndex < patterns.length) {
            int[] pattern = patterns[patternIndex];
            // Carrier frequency is 38kHz, as mentioned in the plan
            irManager.transmit(38000, pattern);
            log("Sent pattern #" + patternIndex);
        } else {
            log("No more patterns to test.");
            stopScan();
        }
    }

    private Runnable scanRunnable = new Runnable() {
        @Override
        public void run() {
            if (isScanning) {
                testPattern(currentPatternIndex);
                currentPatternIndex++;
                if (currentPatternIndex < patterns.length) {
                    // Delay between signals, as per the plan (1500ms)
                    handler.postDelayed(this, 1500);
                } else {
                    stopScan();
                }
            }
        }
    };

    private void log(String message) {
        Log.d(TAG, message);
        logView.append(message + "\n");
    }
}