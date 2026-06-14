package kr.ac.baekseok.java_project.util;

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.TextView;

import kr.ac.baekseok.java_project.dto.ApiTime;

/**
 * 영업시간 입력용 TimePicker 헬퍼.
 *
 * TextView를 누르면 시간 선택 다이얼로그를 띄우고,
 * 선택한 시간을 ApiTime으로 보관 + TextView에 "09:30" 형식으로 표시.
 */
public class TimePickerHelper {

    private ApiTime selected;
    private final TextView target;

    public TimePickerHelper(TextView target) {
        this.target = target;
    }

    public void attach(Context context) {
        target.setOnClickListener(v -> showDialog(context));
    }

    private void showDialog(Context context) {
        int h = selected != null ? selected.hour : 9;
        int m = selected != null ? selected.minute : 0;

        TimePickerDialog dialog = new TimePickerDialog(context,
                (view, hourOfDay, minute) -> {
                    selected = new ApiTime(hourOfDay, minute);
                    target.setText(selected.toDisplay());
                }, h, m, true);
        dialog.show();
    }

    /** 선택된 시간 (없으면 null) */
    public ApiTime getValue() {
        return selected;
    }

    public void setValue(ApiTime time) {
        this.selected = time;
        if (time != null) target.setText(time.toDisplay());
    }

    public boolean hasValue() {
        return selected != null;
    }
}
