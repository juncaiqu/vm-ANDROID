package com.kdx.brower;


import android.content.Intent;

import com.kdx.brower.config.VmBrowerConfig;
import com.kdx.brower.logger.LoggerSetting;
import com.kdx.brower.utils.UIUtil;

import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.xwalk.core.JavascriptInterface;



public class NativeMethod {
	private static Logger logger = LoggerSetting.getLogger();
	@JavascriptInterface
	public void log(String msg){
		logger.info("webapp   --> "+msg);
	}
	@JavascriptInterface
	public void sendBroadcast(String str){
		logger.info("sendBroadcast call str = "+str);
		try {
			final Intent intent = getIntent(str);
			UIUtil.post(new Runnable() {
				@Override
				public void run() {
					logger.info("send broadcast");
					intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
					BaseApplication.getApplication().sendBroadcast(intent);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@JavascriptInterface
	public void startActivity(String str){
		/**
		 * {"action":"com.ubox.launcher","data":{"id":"1234","name":"qjc","age":"123"}}
		 */
		try {
			final Intent intent = getIntent(str);
			UIUtil.post(new Runnable() {
				@Override
				public void run() {
					logger.info("start activity");
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					BaseApplication.getApplication().startActivity(intent);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Intent getIntent(String str) throws JSONException {
		final Intent intent = new Intent();
		JSONObject intentJsonObj = new JSONObject(str);
		String action = intentJsonObj.getString("action");
		intent.setAction(action);
		JSONObject data = intentJsonObj.getJSONObject("data");
		if(data == null){
			return intent;
		}
		while (data.keys().hasNext()){
            String keyName = (String) data.keys().next();
            Object value = data.get(keyName);
            if(toBoolean(value)!=null){
                intent.putExtra(keyName,data.optBoolean(keyName));
            }else if(toDouble(value)!=null){
                intent.putExtra(keyName,data.optDouble(keyName));
            }else if(toInteger(value)!=null){
                intent.putExtra(keyName,data.optInt(keyName));
            }else if(toLong(value)!=null){
                intent.putExtra(keyName,data.optLong(keyName));
            }else if(toString(value)!=null){
                intent.putExtra(keyName,data.optDouble(keyName));
            }
        }
		return intent;
	}

	@JavascriptInterface
	public void reloadDefault(){
		try {
			UIUtil.post(new Runnable() {
				@Override
				public void run() {
                    logger.info("launcher --> reloadDefault" + VmBrowerActivity.xWalkView);
					if (VmBrowerActivity.xWalkView != null) {
						VmBrowerActivity.xWalkView.load(VmBrowerConfig.getMainUrl(), null);
					}
				}
			});
		}catch (Exception e){
			e.printStackTrace();
		}

	}

	Boolean toBoolean(Object value) {
		if (value instanceof Boolean) {
			return (Boolean) value;
		} else if (value instanceof String) {
			String stringValue = (String) value;
			if ("true".equalsIgnoreCase(stringValue)) {
				return true;
			} else if ("false".equalsIgnoreCase(stringValue)) {
				return false;
			}
		}
		return null;
	}

	Double toDouble(Object value) {
		if (value instanceof Double) {
			return (Double) value;
		} else if (value instanceof Number) {
			return ((Number) value).doubleValue();
		} else if (value instanceof String) {
			try {
				return Double.valueOf((String) value);
			} catch (NumberFormatException ignored) {
			}
		}
		return null;
	}

	Integer toInteger(Object value) {
		if (value instanceof Integer) {
			return (Integer) value;
		} else if (value instanceof Number) {
			return ((Number) value).intValue();
		} else if (value instanceof String) {
			try {
				return (int) Double.parseDouble((String) value);
			} catch (NumberFormatException ignored) {
			}
		}
		return null;
	}

	Long toLong(Object value) {
		if (value instanceof Long) {
			return (Long) value;
		} else if (value instanceof Number) {
			return ((Number) value).longValue();
		} else if (value instanceof String) {
			try {
				return (long) Double.parseDouble((String) value);
			} catch (NumberFormatException ignored) {
			}
		}
		return null;
	}

	String toString(Object value) {
		if (value instanceof String) {
			return (String) value;
		} else if (value != null) {
			return String.valueOf(value);
		}
		return null;
	}

}
