package com.yfcomm.businesshall;

import java.util.Timer;
import java.util.TimerTask;

import com.yfcomm.public_define.CustomInfo;
import com.yfcomm.public_define.public_define;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Recharge extends Fragment {

	private static final String ARG_SECTION_NUMBER = "section_number";
	private int num;
	private TextView phone_fifty, phone_onehundred, phone_twohundred, phonefee_showacount, payment_show,
			telcommadd_showamount;
	private EditText phone_customamount, phone_num, payment_phonenum, telcommadd_1, telcommadd_2;
	private Context context;
	private String phonenum, amount;
	private View rootView;
	private Button btnRandphoneNum, btnpay, payment_btnnum, payment_zf, gh_random, telcommadd_zf;
	private CustomInfo user;
	private TextView[] tecsel = new TextView[3];
	private int[] telID = { R.id.tel_sel1, R.id.tel_sel2, R.id.tel_sel3 };
	
	public static Recharge newInstance(int sectionNumber, Context context) {
		Recharge fragment = new Recharge(sectionNumber, context);
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);

		return fragment;
	}

	public Recharge(int sectionNumber, Context context) {
		this.num = sectionNumber;
		this.context = context;

	}

	public CustomInfo getRandUser() {
		int size = MainActivity.customifo.size();
		if (size > 0) {
			int i = (int) (Math.random() * size);
			return MainActivity.customifo.get(i);
		}
		return null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if (null != rootView) {
			ViewGroup parent = (ViewGroup) rootView.getParent();
			if (null != parent) {
				parent.removeView(rootView);
			}
		} else {

			if (num == 1) {
				rootView = inflater.inflate(R.layout.phonefee, container, false);
				btnRandphoneNum = (Button) rootView.findViewById(R.id.b_phonefee1);
				btnRandphoneNum.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						phone_num.setText(getRandUser().phonenum);
					}
				});

				btnpay = (Button) rootView.findViewById(R.id.phonefee_zf);
				btnpay.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						phonenum = phone_num.getText().toString();
						if (phonenum == null || phonenum.length() != 11) {
							Toast.makeText(context.getApplicationContext(), "请输入充值号码", Toast.LENGTH_LONG).show();
						} else {
							if (amount == null || amount.length() == 0) {
								Toast.makeText(context.getApplicationContext(), "请输入金额", Toast.LENGTH_LONG).show();
							} else {
								Intent intent = new Intent();
								intent.setAction("android.intent.action.com.yfcomm.businesshall.paymentActivety");
								Bundle arguments = new Bundle();
								arguments.putString(public_define.acount, amount);
								arguments.putString(public_define.phonenum, phonenum);
								arguments.putInt(public_define.Tradetype, 1);
								intent.putExtra(public_define.SelphonenumInfo, arguments);
								startActivity(intent);
							}
						}
					}
				});

				phone_fifty = (TextView) rootView.findViewById(R.id.phonefee_fifty);
				phone_onehundred = (TextView) rootView.findViewById(R.id.phonefee_onehundred);
				phone_twohundred = (TextView) rootView.findViewById(R.id.phonefee_twohundred);
				phone_customamount = (EditText) rootView.findViewById(R.id.phonefee_customamount);
				phonefee_showacount = (TextView) rootView.findViewById(R.id.phonefee_showamount);
				phone_num = (EditText) rootView.findViewById(R.id.phonefee_phonenum);

				phone_num.addTextChangedListener(new EditChangedListener(phone_num, context, 11));
				phone_customamount.addTextChangedListener(new TextWatcher() {
					// private String temp;// 监听前的文本
					private int editStart;// 光标开始位置
					private int editEnd;// 光标结束位置
					private final int charMaxNum = 3;
					// private EditText mEditTextMsg;
					private Timer time = null;

					@Override
					public void afterTextChanged(Editable s) {
						editStart = phone_customamount.getSelectionStart();
						editEnd = phone_customamount.getSelectionEnd();
						amount = s.toString();
						if (amount.length() > charMaxNum) {
							Toast.makeText(context.getApplicationContext(), "你的输入已经超过了金额的限制！", Toast.LENGTH_LONG)
									.show();
							s.delete(editStart - 1, editEnd);
							int tempSelection = editStart;
							phone_customamount.setText(s);
							phone_customamount.setSelection(tempSelection);
							if (MainActivity.myHandler != null) {
								MainActivity.myHandler.obtainMessage(MainActivity.MSG_CLOSEKEYBOARD).sendToTarget();
							}
						} else {
							phonefee_showacount.setText("充值 " + amount + ".00元");
						}
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {

					}

					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						if (time != null) {
							time.cancel();
							time = null;
						}
						time = new Timer();
						time.schedule(new TimerTask() {
							@Override
							public void run() {
								if (MainActivity.myHandler != null) {
									MainActivity.myHandler.obtainMessage(MainActivity.MSG_CLOSEKEYBOARD).sendToTarget();
								}
							}
						}, 2000);

					}
				});

				phone_fifty.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						phone_fifty.setBackground(getResources().getDrawable(R.drawable.settext_bg));
						phone_onehundred.setBackground(getResources().getDrawable(R.drawable.setbar_bg));
						phone_twohundred.setBackground(getResources().getDrawable(R.drawable.setbar_bg));
						amount = "50";
						phonenum = phone_num.getText().toString();
						phonefee_showacount.setText("充值 50.00元");
						if (MainActivity.myHandler != null) {
							MainActivity.myHandler.obtainMessage(MainActivity.MSG_CLOSEKEYBOARD).sendToTarget();
						}

					}
				});

				phone_onehundred.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						phone_fifty.setBackground(getResources().getDrawable(R.drawable.setbar_bg));
						phone_onehundred.setBackground(getResources().getDrawable(R.drawable.settext_bg));
						phone_twohundred.setBackground(getResources().getDrawable(R.drawable.setbar_bg));
						phone_customamount.setText("");
						amount = "100";
						phonenum = phone_num.getText().toString();
						phonefee_showacount.setText("充值 100.00元");
						if (MainActivity.myHandler != null) {
							MainActivity.myHandler.obtainMessage(MainActivity.MSG_CLOSEKEYBOARD).sendToTarget();
						}
					}
				});
				phone_twohundred.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						phone_fifty.setBackground(getResources().getDrawable(R.drawable.setbar_bg));
						phone_onehundred.setBackground(getResources().getDrawable(R.drawable.setbar_bg));
						phone_twohundred.setBackground(getResources().getDrawable(R.drawable.settext_bg));
						phone_customamount.setText("");
						amount = "200";
						phonenum = phone_num.getText().toString();
						phonefee_showacount.setText("充值 200.00元");
						if (MainActivity.myHandler != null) {
							MainActivity.myHandler.obtainMessage(MainActivity.MSG_CLOSEKEYBOARD).sendToTarget();
						}
					}
				});
				phone_customamount.setOnTouchListener(new View.OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						phone_fifty.setBackground(getResources().getDrawable(R.drawable.setbar_bg));
						phone_onehundred.setBackground(getResources().getDrawable(R.drawable.setbar_bg));
						phone_twohundred.setBackground(getResources().getDrawable(R.drawable.setbar_bg));
						return false;
					}
				});

			} else if (num == 2) {
				rootView = inflater.inflate(R.layout.payment, container, false);
				payment_phonenum = (EditText) rootView.findViewById(R.id.payment_phonenum);
				payment_btnnum = (Button) rootView.findViewById(R.id.payment_btnnum);
				payment_btnnum.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						user = getRandUser();
						payment_phonenum.setText(user.phonenum);
					}
				});
				payment_zf = (Button) rootView.findViewById(R.id.payment_zf);
				payment_zf.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (payment_zf.getText().toString().equals("查询缴费账单")) {
							phonenum = payment_phonenum.getText().toString();
							if (phonenum == null || phonenum.length() != 11) {
								Toast.makeText(context.getApplicationContext(), "请输入缴费号码", Toast.LENGTH_LONG).show();
							} else {
								payment_show.setText(
										"账户名：" + user.name + "\r\n" + "用户号码：" + user.phonenum + "\r\n欠费金额：80.0元");
								payment_zf.setText("立即缴费");
							}
						} else {// 支付

							phonenum = payment_phonenum.getText().toString();
							if (phonenum == null || phonenum.length() != 11) {
								Toast.makeText(context.getApplicationContext(), "请输入缴费号码", Toast.LENGTH_LONG).show();
							} else {

								Intent intent = new Intent();
								intent.setAction("android.intent.action.com.yfcomm.businesshall.paymentActivety");
								Bundle arguments = new Bundle();
								arguments.putString(public_define.acount, "80");
								arguments.putString(public_define.phonenum, phonenum);
								arguments.putInt(public_define.Tradetype, 2);
								intent.putExtra(public_define.SelphonenumInfo, arguments);
								startActivity(intent);
								payment_zf.setText("查询缴费账单");
								payment_show.setText("");
								payment_phonenum.setText("");
								
							}

						}
					}
				});

				payment_show = (TextView) rootView.findViewById(R.id.payment_show);

			} else {
				rootView = inflater.inflate(R.layout.telcommadd, container, false);
				telcommadd_1 = (EditText) rootView.findViewById(R.id.telcommadd_1);
				telcommadd_2 = (EditText) rootView.findViewById(R.id.telcommadd_2);
				gh_random = (Button) rootView.findViewById(R.id.gh_random);
				telcommadd_showamount = (TextView) rootView.findViewById(R.id.telcommadd_showamount);
				telcommadd_zf = (Button) rootView.findViewById(R.id.telcommadd_zf);
				telcommadd_zf.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (telcommadd_1.getText().toString().length() != 4
								|| telcommadd_2.getText().toString().length() != 8) {

							Toast.makeText(context.getApplicationContext(), "请输入正确的区号和固话号码", Toast.LENGTH_LONG).show();

						} else {
							if (amount == null || amount.length() == 0)
								Toast.makeText(context.getApplicationContext(), "请选择固话充值金额", Toast.LENGTH_LONG).show();
							else {

								Intent intent = new Intent();
								intent.setAction("android.intent.action.com.yfcomm.businesshall.paymentActivety");
								Bundle arguments = new Bundle();
								arguments.putString(public_define.acount, amount);
								arguments.putString(public_define.phonenum,
										telcommadd_1.getText().toString() +"-"+ telcommadd_2.getText().toString());
								arguments.putInt(public_define.Tradetype, 3);
								intent.putExtra(public_define.SelphonenumInfo, arguments);
								startActivity(intent);
							}
						}

					}
				});

				gh_random.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						telcommadd_1.setText("0514");
						telcommadd_2.setText("84243536");
					}
				});

				for (int i = 0; i < 3; i++) {
					tecsel[i] = (TextView) rootView.findViewById(telID[i]);
					tecsel[i].setId(i);
					tecsel[i].setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {

							switch (v.getId()) {
							case 0:
								amount = "50";
								break;
							case 1:
								amount = "100";
								break;
							case 2:
								amount = "200";
								break;
							default:
								break;
							}
							setteclom(v.getId());
						}

					});
				}

			}
		}
		return rootView;

	}

	public void setteclom(int index) {
		for (int i = 0; i < 3; i++) {
			if (i == index)
				tecsel[i].setBackground(getResources().getDrawable(R.drawable.settext_bg));
			else {
				tecsel[i].setBackground(getResources().getDrawable(R.drawable.setbar_bg));
			}
		}
		telcommadd_showamount.setText("充值 " + amount + ".0 元");
	}

}
