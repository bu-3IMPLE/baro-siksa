package kr.ac.baekseok.java_project.dto;

import java.io.Serializable;
import java.util.Locale;

/**
 * 서버의 LocalTime JSON 표현.
 * 서버는 시간을 {"hour":9,"minute":30,"second":0,"nano":0} 형태로 주고받는다.
 *
 * 화면에 표시할 때는 toDisplay()로 "09:30" 형식 문자열을 얻는다.
 */
public class ApiTime implements Serializable {
    public int hour;
    public int minute;
    public int second;
    public int nano;

    public ApiTime() {
    }

    public ApiTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    /** "09:30" 형식으로 변환 */
    public String toDisplay() {
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
    }
}
