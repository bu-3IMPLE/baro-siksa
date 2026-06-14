package kr.ac.baekseok.java_project.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * OWNER가 등록한 "내 식당" id를 로컬에 저장/조회.
 *
 * 서버에 "내 식당 목록" API가 없어서, 식당을 등록할 때 반환받은
 * restaurantId를 여기에 저장해두고 관리 화면에서 사용한다.
 *
 * 한 계정이 식당 하나를 운영한다고 가정 (단일 식당).
 * 여러 식당을 운영하려면 서버에 목록 API가 필요하다.
 */
public class OwnerStore {

    private static final String PREFS = "owner_prefs";
    private static final String KEY_RESTAURANT_ID = "my_restaurant_id";

    public static void saveRestaurantId(Context context, long restaurantId) {
        prefs(context).edit().putLong(KEY_RESTAURANT_ID, restaurantId).apply();
    }

    /** 저장된 내 식당 id. 없으면 -1 */
    public static long getRestaurantId(Context context) {
        return prefs(context).getLong(KEY_RESTAURANT_ID, -1);
    }

    public static boolean hasRestaurant(Context context) {
        return getRestaurantId(context) >= 0;
    }

    public static void clear(Context context) {
        prefs(context).edit().remove(KEY_RESTAURANT_ID).apply();
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext()
                .getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }
}
