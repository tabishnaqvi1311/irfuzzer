package com.exalta.acremote;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.hardware.ConsumerIrManager;
import android.os.Handler;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Button btnStartScan;
    private Button btnTestPattern;
    private TextView logView;

    private ConsumerIrManager irManager;
    private Handler handler = new Handler();
    private int currentPatternIndex = 0;
    private boolean isScanning = false;

    private List<int[]> patterns = new ArrayList<>();

    // NEC protocol constants
    private static final int NEC_HDR_MARK = 9000;
    private static final int NEC_HDR_SPACE = 4500;
    private static final int NEC_BIT_MARK = 560;
    private static final int NEC_ONE_SPACE = 1690;
    private static final int NEC_ZERO_SPACE = 560;
    private static final int NEC_RPT_SPACE = 2250;

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

        log("App started. Generating IR patterns...");
        generateNecCodes();
        log(patterns.size() + " patterns generated. Ready to scan.");
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
        if (patternIndex < patterns.size()) {
            int[] pattern = patterns.get(patternIndex);
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
                if (currentPatternIndex < patterns.size()) {
                    // Delay between signals, as per the plan (1500ms)
                    handler.postDelayed(this, 2000); // Increased delay
                } else {
                    stopScan();
                }
            }
        }
    };

    private void generateNecCodes() {
        // Generate codes for a range of addresses and commands
        for (int address = 0; address < 256; address++) {
            for (int command = 0; command < 256; command++) {
                patterns.add(buildNecPattern(address, command));
            }
        }
    }

    private int[] buildNecPattern(int address, int command) {
        List<Integer> pattern = new ArrayList<>();
        pattern.add(NEC_HDR_MARK);
        pattern.add(NEC_HDR_SPACE);

        addByte(pattern, address);
        addByte(pattern, ~address & 0xFF);
        addByte(pattern, command);
        addByte(pattern, ~command & 0xFF);

        pattern.add(NEC_BIT_MARK);

        return pattern.stream().mapToInt(i -> i).toArray();
    }

    private void addByte(List<Integer> pattern, int data) {
        for (int i = 0; i < 8; i++) {
            pattern.add(NEC_BIT_MARK);
            if ((data & 1) == 1) {
                pattern.add(NEC_ONE_SPACE);
            } else {
                pattern.add(NEC_ZERO_SPACE);
            }
            data >>= 1;
        }
    }

    private void log(String message) {
        Log.d(TAG, message);
        logView.append(message + "\n");
    }
}