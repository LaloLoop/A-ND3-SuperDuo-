package it.jaschke.alexandria.camera;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.FocusingProcessor;
import com.google.android.gms.vision.Tracker;

/**
 * Returns the first code scanned
 * Created by lalo on 25/10/15.
 */
public class BarcodeProcessor extends FocusingProcessor {

    public BarcodeProcessor(Detector detector, Tracker tracker) {
        super(detector, tracker);
    }

    @Override
    public int selectFocus(Detector.Detections detections) {
        return 0;
    }
}
