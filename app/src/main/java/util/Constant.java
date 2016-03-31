package util;

/**
 * Created by taozhiheng on 15-4-12.
 */
public class Constant {
    //LOCK
    public final static String LOCK_ACTION_TYPE = "lock_action_type";
    public final static int LOCK_ACTION_SET = 0;
    public final static int LOCK_ACTION_OPEN = 1;
    public final static String LOCK_IS_OPEN = "lock_is_open";
    public final static String LOCK_PASSWORD = "lock_password";


    public final static String ITEM_RECORD = "item_record";
    public final static String ITEM_SHAPE = "item_shape";
    public final static String RECORD_STATE = "record_state";


    public final static String INTENT_ACTION_MAIN = "intent.action.MAIN";
    public final static String INTENT_ACTION_DRAG = "intent.action.DRAG";
    public final static String INTENT_ACTION_CONTENT = "intent.action.CONTENT";

    public final static String APPLICATION_THEME_ID = "application_theme_id";
    public final static String[] APPLICATION_THEME = {"浅绿", "深绿", "浅橙", "深橙"};
    public final static int[] APPLICATION_THEME_COLOR = {
            android.support.v7.appcompat.R.color.accent_material_dark,
            android.support.v7.appcompat.R.color.accent_material_light,
            android.R.color.holo_orange_dark,
            android.R.color.holo_orange_light};

    public final static String SHARED_PREF = "shared_secret";


}
