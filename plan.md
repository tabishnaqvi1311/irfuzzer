# 📡 IR Remote Rebuilder Plan — Exalta AC (No Remote)

## 🔍 Goal
Create a working Android app that can control an Exalta AC via IR, **without** access to the original remote. We'll try fuzzing IR signals and observing the AC's reaction.

---

## 🧩 Step 1: Understand Hardware Capability

- ✅ You have a Huawei LYA-L29 (Mate 20 Pro) — it has an **IR blaster**
- ❌ It **cannot receive** IR signals, only send
- ✅ So you can **transmit** IR codes using Android

---

## 🔬 Step 2: Attempt Brand Matching (Fallback)

> Only if Exalta matches a known brand like Midea, Gree, etc.

- ❌ Midea codes do not work (already tested)
- 🔁 Test other brands like:
  - Gree, AUX, Voltas, Haier, Whirlpool, Kelvinator
- ✅ Use an app like **ZaZa Remote**, **Mi Remote**, or **IRPlus**
- ❌ If nothing works → proceed to fuzzing

---

## 🔥 Step 3: Brute Force IR Pattern Testing (Fuzzing)

### 🧠 Idea:
Send lots of IR patterns from your phone. Observe if AC responds (beep, LED blink, fan turns on).

### 📱 Requirements:
- Android phone with IR (✅ Done)
- Java/Kotlin Android app
- Use `ConsumerIrManager` to transmit codes

### ⚙️ What You'll Code:
- A basic Android app
- Loop through a list of known IR patterns (start with NEC protocol)
- Use 38kHz as carrier frequency
- Add delay between signals
- Display index / hex being tried

---

## 🛠️ Step 4: Build the Fuzzer App

### Features:
- Button: `Start IR Scan`
- Optional: Button: `Test This Pattern`
- Auto-scroll log of IR patterns sent
- Delay between codes (e.g. 1500ms)

### Tech Stack:
- Java (or Kotlin) + Android Studio
- Target API 24+ (for IR support)

---

## 🧪 Step 5: Detect Positive Responses

### What to watch for:
- Beep from AC
- Fan or compressor click
- LED light blinking

### Optional (Hardcore):
- Use microphone to detect beep and auto-log positive patterns

---

## 🧰 Optional Hardware Plan (Fallback 2)

> If you ever get access to an actual remote:

### Tools:
- Arduino Nano or ESP8266
- TSOP IR receiver (38kHz)
- Use `IRrecvDumpV2` from `IRremote` library
- Capture and decode signals
- Reuse raw data in your Android app

---

## 🎯 Final Step: Custom Remote App

Once you find valid IR patterns:
- Create a clean UI: Power On/Off, Temp +/-, Fan Speed, etc.
- Map working patterns to each button
- Polish and optionally release to Play Store

---

## ✅ Stretch Goals
- Add voice control using Google Assistant
- Add scheduling with alarms
- Build a tiny ESP8266-based Wi-Fi → IR bridge
- Publish your patterns to GitHub

---

## 📚 References
- https://developer.android.com/reference/android/hardware/ConsumerIrManager
- https://github.com/z3t0/Arduino-IRremote
- https://irdb.globalcache.com/
- https://github.com/irplus-remote/irplus-codes.github.io

---

## 🧠 Notes
- Most ACs use NEC protocol (start with that)
- Don’t spam more than 1 signal/sec — ACs may ignore
- Create a break button to stop fuzzing in case of success

---


