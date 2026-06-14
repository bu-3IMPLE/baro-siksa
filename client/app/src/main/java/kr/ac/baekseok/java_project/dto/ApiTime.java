package kr.ac.baekseok.java_project.dto;

import java.io.Serializable;
import java.util.Locale;

/**
 * 서버의 LocalTime JSON 표현.
 *
 * [응답] 서버가 시간을 객체로 줄 때: {"hour":9,"minute":30,"second":0,"nano":0}
 *        → 이 클래스로 받아서 toDisplay()로 "09:30" 표시.
 *
 * [요청] 서버에 보낼 때는 객체가 아니라 "HH:mm:ss" 문자열로 보내야 한다.
 *        (서버 LocalTimeDeserializer가 문자열을 기대함)
 *        → toServerString()으로 "09:30:00" 생성.
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

    /** 화면 표시용 "09:30" */
    public String toDisplay() {
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
    }

    /** 서버 전송용 "09:30:00" (LocalTime 문자열 형식) */
    public String toServerString() {
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, minute, second);
    }
}
