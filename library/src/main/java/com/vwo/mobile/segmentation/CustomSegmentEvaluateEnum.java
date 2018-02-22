package com.vwo.mobile.segmentation;

import android.text.TextUtils;
import android.util.Log;

import com.vwo.mobile.VWO;
import com.vwo.mobile.constants.AppConstants;
import com.vwo.mobile.data.VWOPersistData;
import com.vwo.mobile.utils.VWOLog;
import com.vwo.mobile.utils.VWOUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum CustomSegmentEvaluateEnum {

    ANDROID_VERSION_EQUAL_TO(AppConstants.ANDROID_VERSION, AppConstants.EQUAL_TO, new EvaluateSegment() {
        @Override
        public boolean evaluate(VWO vwo, JSONArray data) {
            for (int i = 0; i < data.length(); i++) {
                try {
                    if (Integer.parseInt(VWOUtils.androidVersion()) == Integer.parseInt(data.getString(i))) {
                        return true;
                    }
                } catch (NumberFormatException | JSONException exception) {
                    VWOLog.e(VWOLog.SEGMENTATION_LOGS, "Unable to parse Android Version",
                            exception, false, true);
                }
            }
            return false;
        }
    }),

    ANDROID_VERSION_NOT_EQUAL_TO(AppConstants.ANDROID_VERSION, AppConstants.NOT_EQUAL_TO, new EvaluateSegment() {
        @Override
        public boolean evaluate(VWO vwo, JSONArray data) {
            for (int i = 0; i < data.length(); i++) {
                try {
                    if (Integer.parseInt(VWOUtils.androidVersion()) == Integer.parseInt(data.getString(i))) {
                        return false;
                    }
                } catch (NumberFormatException | JSONException ex) {
                    VWOLog.e(VWOLog.SEGMENTATION_LOGS, "Unable to parse Android Version", ex,
                            false, true);
                }
            }
            return true;
        }
    }),

    ANDROID_VERSION_LESS_THAN(AppConstants.ANDROID_VERSION, AppConstants.LESS_THAN, new EvaluateSegment() {
        @Override
        public boolean evaluate(VWO vwo, JSONArray data) {
            for (int i = 0; i < data.length(); i++) {
                try {
                    if (Integer.parseInt(VWOUtils.androidVersion()) < Integer.parseInt(data.getString(i))) {
                        return true;
                    }
                } catch (NumberFormatException | JSONException ex) {
                    VWOLog.e(VWOLog.SEGMENTATION_LOGS, "Unable to parse Android Version", ex,
                            false, true);
                }
            }
            return false;
        }
    }),

    ANDROID_VERSION_GREATER_THAN(AppConstants.ANDROID_VERSION, AppConstants.GREATER_THAN, new EvaluateSegment() {
        @Override
        public boolean evaluate(VWO vwo, JSONArray data) {
            for (int i = 0; i < data.length(); i++) {
                try {
                    if (Integer.parseInt(VWOUtils.androidVersion()) > Integer.parseInt(data.getString(i))) {
                        return true;
                    }
                } catch (NumberFormatException | JSONException ex) {
                    VWOLog.e(VWOLog.SEGMENTATION_LOGS, "Unable to parse Android Version", ex,
                            false, true);
                }
            }
            return false;
        }
    }),

    DAY_OF_WEEK_EQUAL_TO(AppConstants.DAY_OF_WEEK, AppConstants.EQUAL_TO, new EvaluateSegment() {
        @Override
        public boolean evaluate(VWO vwo, JSONArray data) {
            Calendar c = VWOUtils.getCalendar();
            int dayOfWeek = c.get(GregorianCalendar.DAY_OF_WEEK) - 1;
            for (int i = 0; i < data.length(); i++) {
                try {
                    if (dayOfWeek == Integer.parseInt(data.getString(i))) {
                        return true;
                    }
                } catch (NumberFormatException | JSONException ex) {
                    VWOLog.e(VWOLog.SEGMENTATION_LOGS, "Unable to parse day of week", ex,
                            false, true);
                }
            }
            return false;
        }
    }),

    DAY_OF_WEEK_NOT_EQUAL_TO(AppConstants.DAY_OF_WEEK, AppConstants.NOT_EQUAL_TO, new EvaluateSegment() {
        @Override
        public boolean evaluate(VWO vwo, JSONArray data) {

            Calendar c = VWOUtils.getCalendar();
            int dayOfWeek = c.get(GregorianCalendar.DAY_OF_WEEK) - 1;
            for (int i = 0; i < data.length(); i++) {
                try {
                    if (dayOfWeek == Integer.parseInt(data.getString(i))) {
                        return false;
                    }
                } catch (NumberFormatException | JSONException ex) {
                    VWOLog.e(VWOLog.SEGMENTATION_LOGS, "Unable to parse day of week", ex,
                            false, true);
                }
            }
            return true;
        }
    }),

    HOUR_OF_DAY_EQUAL_TO(AppConstants.HOUR_OF_DAY, AppConstants.EQUAL_TO, new EvaluateSegment() {
        @Override
        public boolean evaluate(VWO vwo, JSONArray data) {
            Calendar c = VWOUtils.getCalendar();
            int hourOfTheDay = c.get(GregorianCalendar.HOUR_OF_DAY);
            for (int i = 0; i < data.length(); i++) {
                try {
                    if (hourOfTheDay == Integer.parseInt(data.getString(i))) {
                        return true;
                    }
                } catch (NumberFormatException | JSONException ex) {
                    VWOLog.e(VWOLog.SEGMENTATION_LOGS, "Unable to parse hour of the day", ex,
                            false, true);
                }
            }
            return false;
        }
    }),

    HOUR_OF_DAY_NOT_EQUAL_TO(AppConstants.HOUR_OF_DAY, AppConstants.NOT_EQUAL_TO, new EvaluateSegment() {
        @Override
        public boolean evaluate(VWO vwo, JSONArray data) {
            Calendar c = VWOUtils.getCalendar();
            int hourOfTheDay = c.get(GregorianCalendar.HOUR_OF_DAY);
            for (int i = 0; i < data.length(); i++) {
                try {
                    if (hourOfTheDay == Integer.parseInt(data.getString(i))) {
                        return false;
                    }
                } catch (NumberFormatException | JSONException ex) {
                    VWOLog.e(VWOLog.SEGMENTATION_LOGS, "Unable to parse hour of the day", ex,
                            false, true);
                }
            }
            return true;
        }
    }),

    LOCATION_EQUAL_TO(AppConstants.LOCATION, AppConstants.EQUAL_TO, new EvaluateSegment() {
        @Override
        public boolean evaluate(VWO vwo, JSONArray data) {
            String locale = VWOUtils.getDeviceCountryCode(vwo.getCurrentContext());

            for (int i = 0; i < data.length(); i++) {
                try {
                    if (data.getString(i).equalsIgnoreCase(locale)) {
                        return true;
                    }
                } catch (Exception exception) {
                    VWOLog.e(VWOLog.SEGMENTATION_LOGS, "Unable to parse location", exception,
                            false, true);
                }
            }
            return false;
        }
    }),

    LOCATION_NOT_EQUAL_TO(AppConstants.LOCATION, AppConstants.NOT_EQUAL_TO, new EvaluateSegment() {
        @Override
        public boolean evaluate(VWO vwo, JSONArray data) {
            String locale = VWOUtils.getDeviceCountryCode(vwo.getCurrentContext());

            for (int i = 0; i < data.length(); i++) {
                try {
                    if (data.getString(i).equalsIgnoreCase(locale)) {
                        return false;
                    }
                } catch (Exception exception) {
                    VWOLog.e(VWOLog.SEGMENTATION_LOGS, "Unable to parse location", exception,
                            false, true);
                }
            }
            return true;
        }
    }),

    APP_VERSION_EQUAL_TO(AppConstants.APP_VERSION, AppConstants.EQUAL_TO, new EvaluateSegment() {
        @Override
        public boolean evaluate(VWO vwo, JSONArray data) {
            int appVersion = VWOUtils.applicationVersion(vwo.getCurrentContext());
            for (int i = 0; i < data.length(); i++) {
                try {
                    int version = Integer.parseInt(data.getString(i));
                    if (version == appVersion) {
                        return true;
                    }
                } catch (JSONException | NumberFormatException exception) {
                    VWOLog.e(VWOLog.SEGMENTATION_LOGS, "Unable to parse app version", exception,
                            false, false);
                }
            }
            return false;
        }
    }),

    APP_VERSION_NOT_EQUAL_TO(AppConstants.APP_VERSION, AppConstants.NOT_EQUAL_TO, new EvaluateSegment() {
        @Override
        public boolean evaluate(VWO vwo, JSONArray data) {
            int appVersion = VWOUtils.applicationVersion(vwo.getCurrentContext());
            for (int i = 0; i < data.length(); i++) {
                try {
                    int version = Integer.parseInt(data.getString(i));
                    if (version != appVersion) {
                        return true;
                    }
                } catch (JSONException | NumberFormatException exception) {
                    VWOLog.e(VWOLog.SEGMENTATION_LOGS, "Unable to parse app version", exception,
                            false, false);
                }
            }
            return false;
        }
    }),

    APP_VERSION_LESS_THAN(AppConstants.APP_VERSION, AppConstants.LESS_THAN, new EvaluateSegment() {
        @Override
        public boolean evaluate(VWO vwo, JSONArray data) {
            int appVersion = VWOUtils.applicationVersion(vwo.getCurrentContext());
            for (int i = 0; i < data.length(); i++) {
                try {
                    if (appVersion < data.getInt(i)) {
                        return true;
                    }
                } catch (Exception exception) {
                    VWOLog.e(VWOLog.SEGMENTATION_LOGS, "Unable to parse app version", exception,
                            false, false);
                }
            }
            return false;
        }
    }),

    APP_VERSION_GREATER_THAN(AppConstants.APP_VERSION, AppConstants.GREATER_THAN, new EvaluateSegment() {
        @Override
        public boolean evaluate(VWO vwo, JSONArray data) {
            int appVersion = VWOUtils.applicationVersion(vwo.getCurrentContext());
            for (int i = 0; i < data.length(); i++) {
                try {
                    int version = data.getInt(i);
                    if (appVersion > version) {
                        return true;
                    }
                } catch (JSONException exception) {
                    VWOLog.e(VWOLog.SEGMENTATION_LOGS, "Unable to parse app version", exception,
                            false, false);
                }
            }
            return false;
        }
    }),

    CUSTOM_SEGMENT_EQUAL_TO(AppConstants.CUSTOM_SEGMENT, AppConstants.EQUAL_TO, new EvaluateSegment() {
        @Override
        public boolean evaluate(VWO vwo, JSONArray data, String key) {
            String customVariable = vwo.getConfig().getValueForCustomSegment(key);
            // Check if custom keys are set by developer or not if not return false otherwise evaluate.
            if (TextUtils.isEmpty(customVariable)) {
                Log.w(CustomSegmentEvaluateEnum.class.getSimpleName(), "Custom variable value is not set for key: " + key);
                return false;
            }
            for (int i = 0; i < data.length(); i++) {
                try {
                    String customVar = data.getString(i);
                    if (customVar.equals(customVariable)) {
                        return true;
                    }
                } catch (JSONException exception) {
                    VWOLog.e(VWOLog.SEGMENTATION_LOGS, "Unable to parse custom segment", exception,
                            false, false);
                }
            }
            return false;
        }
    }),

    CUSTOM_SEGMENT_NOT_EQUAL_TO(AppConstants.CUSTOM_SEGMENT, AppConstants.NOT_EQUAL_TO, new EvaluateSegment() {
        @Override
        public boolean evaluate(VWO vwo, JSONArray data, String key) {
            String customVariable = vwo.getConfig().getValueForCustomSegment(key);
            // Check if custom keys are set by developer or not if not return false otherwise evaluate.
            if (TextUtils.isEmpty(customVariable)) {
                VWOLog.w(VWOLog.SEGMENTATION_LOGS, "Custom variable value is not set for key: " + key, false);
                return false;
            }
            for (int i = 0; i < data.length(); i++) {
                try {
                    String customVar = data.getString(i);
                    if (customVar.equals(customVariable)) {
                        return false;
                    }
                } catch (JSONException exception) {
                    VWOLog.e(VWOLog.SEGMENTATION_LOGS, "Unable to parse custom segment", exception,
                            false, false);
                }
            }
            return true;
        }
    }),

    CUSTOM_SEGMENT_MATCHES_REGEX(AppConstants.CUSTOM_SEGMENT, AppConstants.MATCHES_REGEX, new EvaluateSegment() {
        @Override
        public boolean evaluate(VWO vwo, JSONArray data, String key) {
            String customVariable = vwo.getConfig().getValueForCustomSegment(key);
            // Check if custom keys are set by developer or not if not return false otherwise evaluate.
            if (TextUtils.isEmpty(customVariable)) {
                VWOLog.e(VWOLog.SEGMENTATION_LOGS, "Custom variable value is not set for key: " + key,
                        false, false);
                return false;
            }
            for (int i = 0; i < data.length(); i++) {
                try {
                    Pattern pattern = Pattern.compile(data.getString(i));
                    Matcher matcher = pattern.matcher(customVariable);
                    if (matcher.matches()) {
                        return true;
                    }
                } catch (JSONException exception) {
                    VWOLog.e(VWOLog.SEGMENTATION_LOGS, "Unable to parse custom segment", exception,
                            false, false);
                }
            }
            return false;
        }
    }),

    CUSTOM_SEGMENT_CONTAINS(AppConstants.CUSTOM_SEGMENT, AppConstants.CONTAINS, new EvaluateSegment() {
        @Override
        public boolean evaluate(VWO vwo, JSONArray data, String key) {
            String customVariable = vwo.getConfig().getValueForCustomSegment(key);
            // Check if custom keys are set by developer or not if not return false otherwise evaluate.
            if (TextUtils.isEmpty(customVariable)) {
                VWOLog.e(VWOLog.SEGMENTATION_LOGS, "Custom variable value is not set for key: " + key,
                        false, false);
                return false;
            }
            for (int i = 0; i < data.length(); i++) {
                try {
                    String customVar = data.getString(i);
                    if (customVariable.contains(customVar)) {
                        return true;
                    }
                } catch (JSONException exception) {
                    VWOLog.e(VWOLog.SEGMENTATION_LOGS, "Unable to parse custom segment", exception,
                            false, false);
                }
            }
            return false;
        }
    }),

    CUSTOM_SEGMENT_STARTS_WITH(AppConstants.CUSTOM_SEGMENT, AppConstants.STARTS_WITH, new EvaluateSegment() {
        @Override
        public boolean evaluate(VWO vwo, JSONArray data, String key) {
            String customVariable = vwo.getConfig().getValueForCustomSegment(key);
            // Check if custom keys are set by developer or not if not return false otherwise evaluate.
            if (TextUtils.isEmpty(customVariable)) {
                VWOLog.e(VWOLog.SEGMENTATION_LOGS, "Custom variable value is not set for key: " + key,
                        false, false);
                return false;
            }
            for (int i = 0; i < data.length(); i++) {
                try {
                    String customVar = data.getString(i);
                    if (customVariable.startsWith(customVar)) {
                        return true;
                    }
                } catch (JSONException exception) {
                    VWOLog.e(VWOLog.SEGMENTATION_LOGS, "Unable to parse custom segment", exception,
                            false, false);
                }
            }
            return false;
        }
    }),

    CUSTOM_SEGMENT_USER_TYPE_EQUALS(AppConstants.USER_TYPE, AppConstants.EQUAL_TO, new EvaluateSegment() {
        @Override
        public boolean evaluate(VWO vwo, JSONArray data) {
            boolean isReturning = VWOPersistData.isReturningUser(vwo);
            for (int i = 0; i < data.length(); i++) {
                try {
                    String type = data.getString(i);
                    return isReturning && type.equalsIgnoreCase(AppConstants.USER_TYPE_RETURNING) ||
                            !isReturning && type.equalsIgnoreCase(AppConstants.USER_TYPE_NEW);
                } catch (JSONException exception) {
                    VWOLog.e(VWOLog.SEGMENTATION_LOGS, "Unable to parse custom segment", exception,
                            false, false);
                }
            }
            return false;
        }
    }),
    CUSTOM_SEGMENT_USER_TYPE_NOT_EQUALS(AppConstants.USER_TYPE, AppConstants.NOT_EQUAL_TO, new EvaluateSegment() {
        @Override
        public boolean evaluate(VWO vwo, JSONArray data) {
            boolean isReturning = VWOPersistData.isReturningUser(vwo);
            for (int i = 0; i < data.length(); i++) {
                try {
                    String type = data.getString(i);
                    return !isReturning && type.equalsIgnoreCase(AppConstants.USER_TYPE_RETURNING) ||
                            isReturning && type.equalsIgnoreCase(AppConstants.USER_TYPE_NEW);
                } catch (JSONException exception) {
                    VWOLog.e(VWOLog.SEGMENTATION_LOGS, "Unable to parse custom segment", exception,
                            false, false);
                }
            }
            return false;
        }
    }),
    CUSTOM_SEGMENT_DEVICE_TYPE_EQUALS(AppConstants.DEVICE_TYPE, AppConstants.EQUAL_TO, new EvaluateSegment() {
        @Override
        public boolean evaluate(VWO vwo, JSONArray data) {
            boolean isTablet = VWOUtils.isTablet(vwo.getCurrentContext());
            for (int i = 0; i < data.length(); i++) {
                try {
                    String type = data.getString(i);
                    return isTablet && type.equalsIgnoreCase(AppConstants.DEVICE_TYPE_TABLET) ||
                            !isTablet && type.equalsIgnoreCase(AppConstants.DEVICE_TYPE_PHONE);
                } catch (JSONException exception) {
                    VWOLog.e(VWOLog.SEGMENTATION_LOGS, "Unable to parse custom segment", exception,
                            false, false);
                }
            }
            return false;
        }
    }),

    CUSTOM_SEGMENT_DEVICE_TYPE_NOT_EQUALS(AppConstants.DEVICE_TYPE, AppConstants.NOT_EQUAL_TO, new EvaluateSegment() {
        @Override
        public boolean evaluate(VWO vwo, JSONArray data) {
            boolean isTablet = VWOUtils.isTablet(vwo.getCurrentContext());
            for (int i = 0; i < data.length(); i++) {
                try {
                    String type = data.getString(i);
                    return !isTablet && type.equalsIgnoreCase(AppConstants.DEVICE_TYPE_TABLET) ||
                            isTablet && type.equalsIgnoreCase(AppConstants.DEVICE_TYPE_PHONE);
                } catch (JSONException exception) {
                    VWOLog.e(VWOLog.SEGMENTATION_LOGS, "Unable to parse custom segment", exception,
                            false, false);
                }
            }
            return false;
        }
    }),

    DEFAULT("", -11, new EvaluateSegment() {
    });


    private int mOperator;
    private String mType;
    private EvaluateSegment mSegmentFunction;

    CustomSegmentEvaluateEnum(String type, int operator, EvaluateSegment segmentFunction) {
        mOperator = operator;
        mType = type;
        mSegmentFunction = segmentFunction;
    }

    public static EvaluateSegment getEvaluator(String type, int operator) {
        CustomSegmentEvaluateEnum[] evaluators = CustomSegmentEvaluateEnum.values();
        for (CustomSegmentEvaluateEnum evaluator : evaluators) {
            if (type.equals(evaluator.getType()) && (operator == evaluator.getOperator())) {
                return evaluator.getSegmentFunction();
            }
        }
        return DEFAULT.getSegmentFunction();
    }

    public int getOperator() {
        return mOperator;
    }

    public String getType() {
        return mType;
    }

    public EvaluateSegment getSegmentFunction() {
        return mSegmentFunction;
    }

    public interface EvaluateSegment {
        default boolean evaluate(VWO vwo, JSONArray data) {
            return false;
        }

        default boolean evaluate(VWO vwo, JSONArray data, String key) {
            return false;
        }
    }
}
