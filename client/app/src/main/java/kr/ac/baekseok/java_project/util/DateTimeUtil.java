package kr.ac.baekseok.java_project.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 서버 ISO-8601 날짜 문자열 ↔ 화면 표시/Calendar 변환 유틸.
 *
 * 서버 형식: "2025-12-01T18:30:00"
 */
public class DateTimeUtil {

    private static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String DISPLAY_FORMAT = "yyyy-MM-dd HH:mm";

    /** Calendar → 서버에 보낼 ISO 문자열 */
    public static String toIso(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT, Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    /** Date → ISO 문자열 */
    public static String toIso(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT, Locale.getDefault());
        return sdf.format(date);
    }

    /** 서버 ISO 문자열 → 화면 표시용 "2025-12-01 18:30" */
    public static String isoToDisplay(String iso) {
        if (iso == null) return "";
        try {
            SimpleDateFormat parser = new SimpleDateFormat(ISO_FORMAT, Locale.getDefault());
            Date date = parser.parse(iso);
            SimpleDateFormat formatter = new SimpleDateFormat(DISPLAY_FORMAT, Locale.getDefault());
            return date != null ? formatter.format(date) : iso;
        } catch (Exception e) {
            // 서버가 밀리초나 타임존을 붙여 보내는 경우 대비: 앞 19자만 잘라 재시도
            if (iso.length() >= 16) {
                return iso.substring(0, 16).replace('T', ' ');
            }
            return iso;
        }
    }

    /** 서버 ISO 문자열 → Calendar */
    public static Calendar isoToCalendar(String iso) {
        Calendar cal = Calendar.getInstance();
        try {
            SimpleDateFormat parser = new SimpleDateFormat(ISO_FORMAT, Locale.getDefault());
            Date date = parser.parse(iso);
            if (date != null) cal.setTime(date);
        } catch (Exception ignored) {
        }
        return cal;
    }
}
