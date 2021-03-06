/*
 * 官网地站:http://www.ShareSDK.cn
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2013年 ShareSDK.cn. All rights reserved.
 */
package cn.smssdk.gui;

import static cn.smssdk.framework.utils.R.getBitmapRes;
import static cn.smssdk.framework.utils.R.getIdRes;
import static cn.smssdk.framework.utils.R.getLayoutRes;
import static cn.smssdk.framework.utils.R.getStringRes;
import static cn.smssdk.framework.utils.R.getStyleRes;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.analysis.MobclickAgent;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.framework.FakeActivity;

/** 短信注册页面 */
public class RegisterPage extends FakeActivity implements OnClickListener, TextWatcher {
	private static final String DEFAULT_COUNTRY_ID = "40"; // 默认使用中国区号
	private boolean flag = false;
	private EventHandler callback;

	private TextView tvCountry;// 国家
	private EditText etPhoneNum;// 手机号码
	private TextView tvCountryNum;// 国家编号
	private ImageView ivClear; // clear 号码
	private Button btnNext;// 下一步按钮

	private String currentId;
	private String currentCode;
	private EventHandler handler;
	private HashMap<String, String> countryRules; // 国家号码规则
	private Dialog pd;

	public void setRegisterCallback(EventHandler callback) {
		this.callback = callback;
	}

	public void show(Context context) {
		super.show(context, null);
	}

	public void onCreate() {
		int resId = getLayoutRes(activity, "smssdk_regist_page");
		if (resId > 0) {
			activity.setContentView(resId);
			currentId = DEFAULT_COUNTRY_ID;

			resId = getIdRes(activity, "ll_back");
			View llBack = activity.findViewById(resId);
			resId = getIdRes(activity, "tv_title");
			TextView tv = (TextView) activity.findViewById(resId);
			resId = getStringRes(activity, "smssdk_regist");
			if (resId > 0) {
				tv.setText(resId);
			}
			resId = getIdRes(activity, "rl_country");
			View viewCountry = activity.findViewById(resId);
			resId = getIdRes(activity, "btn_next");
			btnNext = (Button) activity.findViewById(resId);

			resId = getIdRes(activity, "tv_country");
			tvCountry = (TextView) activity.findViewById(resId);
			String[] country = SMSSDK.getCountry(currentId);
			if (country != null) {
				currentCode = country[1];
				tvCountry.setText(country[0]);
			}
			resId = getIdRes(activity, "tv_country_num");
			tvCountryNum = (TextView) activity.findViewById(resId);
			tvCountryNum.setText("+" + currentCode);

			resId = getIdRes(activity, "et_write_phone");
			etPhoneNum = (EditText) activity.findViewById(resId);
			etPhoneNum.setText(getLine1Number(currentCode));
			etPhoneNum.addTextChangedListener(this);
			etPhoneNum.requestFocus();
//			if (etPhoneNum.getText().length() > 0) {
//				btnNext.setEnabled(true);
//				resId = getIdRes(activity, "iv_clear");
//				ivClear = (ImageView) activity.findViewById(resId);
//				ivClear.setVisibility(View.VISIBLE);
//				resId = getBitmapRes(activity, "smssdk_btn_enable");
//				if (resId > 0) {
//					btnNext.setBackgroundResource(resId);
//				}
//			}

			resId = getIdRes(activity, "iv_clear");
			ivClear = (ImageView) activity.findViewById(resId);

			llBack.setOnClickListener(this);
			btnNext.setOnClickListener(this);
			ivClear.setOnClickListener(this);
			viewCountry.setOnClickListener(this);

			handler = new EventHandler() {
				@SuppressWarnings("unchecked")
				public void afterEvent(final int event, final int result, final Object data) {
					runOnUIThread(new Runnable() {
						public void run() {
							if (pd != null && pd.isShowing()) {
								pd.dismiss();
							}
							if (result == SMSSDK.RESULT_COMPLETE) {
								if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
									// 请求支持国家列表
									onCountryListGot((ArrayList<HashMap<String, Object>>) data);
								} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
									// 请求验证码后，跳转到验证码填写页面
									afterVerificationCodeRequested();
								}
							} else {
								((Throwable) data).printStackTrace();
								Throwable throwable = (Throwable) data;
								// 根据服务器返回的网络错误，给toast提示
								try {
									JSONObject object = new JSONObject(throwable.getMessage());
									String des = object.optString("detail");
									if (!TextUtils.isEmpty(des)) {
										Toast.makeText(activity, des, Toast.LENGTH_SHORT).show();
										return;
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
								// 如果木有找到资源，默认提示
								int resId = getStringRes(activity, "smssdk_network_error");
								if (resId > 0) {
									Toast.makeText(activity, resId, Toast.LENGTH_SHORT).show();
								}
							}
						}
					});
				}
			};
		}

	}

	private String getLine1Number(String country) {
		try {
			TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
			String num = tm.getLine1Number();
			if (TextUtils.isEmpty(num)) {
				return null;
			}

			num = num.replace("-", "").replace(" ", "");
			String countryCode = "+" + country;
			if (num.startsWith(countryCode)) {
				num = num.substring(countryCode.length());
			}

			return num;
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return null;
	}

	public void onResume() {
		SMSSDK.registerEventHandler(handler);
		MobclickAgent.onPageStart("RegisterPage");
	}

	public void onPause() {
		SMSSDK.unregisterEventHandler(handler);
		MobclickAgent.onPageEnd("RegisterPage");
	}

	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	// 电话号码输入框
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		
	}

	protected static byte[] readInputStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();// 网页的二进制数据
		outStream.close();
		inStream.close();
		return data;
	}

	public boolean checkUser(String phone) throws Exception {
		String path = "elfsoft?commond=checkphone&phone=" + phone;
		InputStream in = request(path);
		String result = new String(readInputStream(in), "utf-8");
		if (result.equals("true")) {
			return true;
		}
		return false;
	}

	 protected String httpUrl = "http://westudio.duapp.com/NewJw/servlet/";
//	protected String httpUrl = "http://192.168.191.1:8080/newjw/servlet/";

	public void afterTextChanged(Editable s) {

	}

	protected InputStream request2(String path) throws Exception {
		URL url = new URL(httpUrl + path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(2 * 1000);
		conn.setReadTimeout(2 * 1000);
		InputStream in = conn.getInputStream();
		if (in == null)
			throw new Exception("连接失败");
		return in;
	}

	protected InputStream request(String path) throws Exception {
		for (int i = 0; i < 5; i++) {
			try {
				return request2(path);
			} catch (Exception e) {
				continue;
			}
		}
		return null;
	}

	public void onClick(View v) {
		int id = v.getId();
		int id_ll_back = getIdRes(activity, "ll_back");
		int id_rl_country = getIdRes(activity, "rl_country");
		int id_btn_next = getIdRes(activity, "btn_next");
		int id_iv_clear = getIdRes(activity, "iv_clear");

		if (id == id_ll_back) {
			finish();
		} else if (id == id_rl_country) {
			// 国家列表
			CountryPage countryPage = new CountryPage();
			countryPage.setCountryId(currentId);
			countryPage.setCountryRuls(countryRules);
			countryPage.showForResult(activity, null, this);
		} else if (id == id_btn_next) {
			// 请求发送短信验证码
			if (countryRules == null || countryRules.size() <= 0) {
				if (pd != null && pd.isShowing()) {
					pd.dismiss();
				}
				pd = CommonDialog.ProgressDialog(activity);
				if (pd != null) {
					pd.show();
				}

				SMSSDK.getSupportedCountries();
			} else {

				String phone = etPhoneNum.getText().toString().trim().replaceAll("\\s*", "");
				String code = tvCountryNum.getText().toString().trim();

				checkPhoneNum(phone, code);
			}
		} else if (id == id_iv_clear) {
			// 清除电话号码输入框
			etPhoneNum.getText().clear();
		}
	}

	private Handler handlers = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				Toast.makeText(getContext(), "网络连接错误,请重试~", Toast.LENGTH_SHORT).show();
			} else if (msg.what == 1) {
				Toast.makeText(getContext(), "该手机号已注册，请直接登录~", Toast.LENGTH_SHORT).show();
				finish();
			} else if (msg.what == 2) {
				showDialog(etPhoneNum.getText().toString().trim().replaceAll("\\s*", ""), codes);
			}
		};
	};

	@SuppressWarnings("unchecked")
	public void onResult(HashMap<String, Object> data) {
		if (data != null) {
			int page = (Integer) data.get("page");
			if (page == 1) { // 国家列表返回
				currentId = (String) data.get("id");
				countryRules = (HashMap<String, String>) data.get("rules");
				String[] country = SMSSDK.getCountry(currentId);
				if (country != null) {
					currentCode = country[1];
					tvCountryNum.setText("+" + currentCode);
					tvCountry.setText(country[0]);
				}
			} else if (page == 2) { // 验证码校验返回
				Object res = data.get("res");
				HashMap<String, Object> phoneMap = (HashMap<String, Object>) data.get("phone");
				if (res != null && phoneMap != null) {
					int resId = getStringRes(activity, "smssdk_your_ccount_is_verified");
					if (resId > 0) {
						Toast.makeText(activity, resId, Toast.LENGTH_SHORT).show();
					}

					if (callback != null) {
						callback.afterEvent(SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE, SMSSDK.RESULT_COMPLETE, phoneMap);
					}
					finish();
				}
			}
		}
	}

	private void onCountryListGot(ArrayList<HashMap<String, Object>> countries) {
		// 解析国家列表
		for (HashMap<String, Object> country : countries) {
			String code = (String) country.get("zone");
			String rule = (String) country.get("rule");
			if (TextUtils.isEmpty(code) || TextUtils.isEmpty(rule)) {
				continue;
			}

			if (countryRules == null) {
				countryRules = new HashMap<String, String>();
			}
			countryRules.put(code, rule);
		}

		// 回归检查手机号码
		String phone = etPhoneNum.getText().toString().trim().replaceAll("\\s*", "");
		String code = tvCountryNum.getText().toString().trim();
		checkPhoneNum(phone, code);
	}

	// 分割电话号码
	private String splitPhoneNum(String phone) {
		StringBuilder builder = new StringBuilder(phone);
		builder.reverse();
		for (int i = 4, len = builder.length(); i < len; i += 5) {
			builder.insert(i, ' ');
		}
		builder.reverse();
		return builder.toString();
	}

	// 检查电话号码
	private void checkPhoneNum(String phone, String code) {

		if (code.startsWith("+")) {
			code = code.substring(1);
		}

		if (TextUtils.isEmpty(phone)) {
			int resId = getStringRes(activity, "smssdk_write_mobile_phone");
			if (resId > 0) {
				Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
			}
			return;
		}

		String rule = countryRules.get(code);
		Pattern p = Pattern.compile(rule);
		Matcher m = p.matcher(phone);
		int resId = 0;
		if (!m.matches()) {
			resId = getStringRes(activity, "smssdk_write_right_mobile_phone");
			if (resId > 0) {
				Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
			}
			return;
		}
		codes = code;
		new Thread() {
			public void run() {
				try {
					flag = checkUser(etPhoneNum.getText().toString().trim().replaceAll("\\s*", ""));
					if (flag) {
						handlers.sendEmptyMessage(1);
					} else {
						handlers.sendEmptyMessage(2);

					}
				} catch (Exception e) {
					handlers.sendEmptyMessage(0);
					e.printStackTrace();
				}
			};
		}.start();

		// 弹出对话框，发送验证码

	}

	private String codes = "";

	// 是否请求发送验证码，对话框
	public void showDialog(final String phone, final String code) {
		int resId = getStyleRes(activity, "CommonDialog");
		if (resId > 0) {
			final String phoneNum = "+" + code + " " + splitPhoneNum(phone);
			final Dialog dialog = new Dialog(getContext(), resId);
			resId = getLayoutRes(activity, "smssdk_send_msg_dialog");
			if (resId > 0) {
				dialog.setContentView(resId);
				resId = getIdRes(activity, "tv_phone");
				((TextView) dialog.findViewById(resId)).setText(phoneNum);
				resId = getIdRes(activity, "tv_dialog_hint");
				TextView tv = (TextView) dialog.findViewById(resId);
				resId = getStringRes(activity, "smssdk_make_sure_mobile_detail");
				if (resId > 0) {
					String text = getContext().getString(resId);
					tv.setText(Html.fromHtml(text));
				}
				resId = getIdRes(activity, "btn_dialog_ok");
				if (resId > 0) {
					((Button) dialog.findViewById(resId)).setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							dialog.dismiss();
							if (pd != null && pd.isShowing()) {
								pd.dismiss();
							}
							pd = CommonDialog.ProgressDialog(activity);
							if (pd != null) {
								pd.show();
							}
							Log.e("verification phone ==>>", phone);
							SMSSDK.getVerificationCode(code, phone.trim());
							// 跳转到验证码页面

						}
					});
				}
				resId = getIdRes(activity, "btn_dialog_cancel");
				if (resId > 0) {
					((Button) dialog.findViewById(resId)).setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
				}
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();
			}
		}
	}

	/** 请求验证码后，跳转到验证码填写页面 */
	private void afterVerificationCodeRequested() {
		String phone = etPhoneNum.getText().toString().trim().replaceAll("\\s*", "");
		String code = tvCountryNum.getText().toString().trim();
		if (code.startsWith("+")) {
			code = code.substring(1);
		}
		String formatedPhone = "+" + code + " " + splitPhoneNum(phone);
		// 验证码页面
		IdentifyNumPage page = new IdentifyNumPage();
		page.setPhone(phone, code, formatedPhone);
		page.showForResult(activity, null, this);
	}

}
