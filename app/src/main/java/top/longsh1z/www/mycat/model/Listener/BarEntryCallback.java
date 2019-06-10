package top.longsh1z.www.mycat.model.Listener;

import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public interface BarEntryCallback {
    void OnSuccess(ArrayList<BarEntry> barEntries);
    void OnFailed();
}
