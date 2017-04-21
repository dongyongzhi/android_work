package com.ctbri.widget;


import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

/**
 * 金额 输入
 * 
 * @author qin
 * 
 *         2012-11-27
 */
public class MoneyEditText extends EditText implements OnClickListener {
	
	private final static String INITVALUE="0.00";

	public MoneyEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setText(INITVALUE);
		this.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
		
		//this.setInputType(InputType.TYPE_CLASS_PHONE);
		this.setLongClickable(false);
		this.setKeyListener( new DigitsKeyListener(false,true));
		/*
		this.setKeyListener(new NumberKeyListener(){

			// 0无键盘 1英文键盘 2模拟键盘 3数字键盘
			@Override
			public int getInputType() {
				return 3;
			}

			@Override
			protected char[] getAcceptedChars() {
				char[] c = {'.','0','1','2','3','4','5','6','7','8','9'};
				return c;
			}
		});
		*/
		resetSelection();//光标位置
		/*
		this.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				//if(event.getAction() == MotionEvent.ACTION_DOWN)
				resetSelection();
				return false;
			}
		});
		*/
		
	    this.setOnClickListener(this);
		
		this.addTextChangedListener(new TextWatcher(){
			private boolean client = true;  //是否是客户端发生的事件
			private char current ='0';
			private int offset=0;
			private int insertNumber = 1;
			
			@Override
			public void afterTextChanged(Editable edit) {
				if(client){
					client = false;
					StringBuilder sb = new StringBuilder(edit.toString());
					
					//如果新加的不是数字则直接删除
					if(insertNumber>1){
						sb.delete(offset, offset+insertNumber);
						
					}else if(current<48 || current>57){
						sb.delete(offset, offset+1);
					}
					
					if(sb.length()<INITVALUE.length()){ //小于4位
						sb.insert(0, "0");
					}else if(sb.length()>INITVALUE.length()){   //大于4位
						if(sb.charAt(0)=='0'){  //去掉第一位0
							sb.delete(0, 1);
						}
					}
					
					//移动小数点
					for(int i=0;i<sb.length();i++){
					 
						if(sb.charAt(i)=='.'){
							if(i==sb.length()-3){
								edit.replace(0, edit.length(), sb.toString());
								resetSelection();//光标位置
								return;
							}
							sb.delete(i, i+1);	
								break;
						} 
					}
					sb.insert(sb.length()-2, ".");
					edit.replace(0, edit.length(), sb.toString());
					resetSelection();//光标位置 
				}else
					client = true;
			}

			@Override
			public void beforeTextChanged(CharSequence s,  int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int count, int after) {
				//获取增加字符 
				if(client){
					if(start<s.length()){
						current = s.charAt(start);
					 	offset = start;
					 	insertNumber = after;
					 }
				}
			}
		});
	}
	
	public void resetSelection(){
		this.setSelection(this.getText().length());
	}
	
	/**
	 * 获取输入的金额
	 * @return
	 */
	public long getMoney(){
		String price = this.getText().toString();
		if("".equals(price))
			return 0L;
		price = price.replace(".", "");
		return Long.parseLong(price);
	}

	@Override
	public void onClick(View arg0) {
		resetSelection();
	}
}
