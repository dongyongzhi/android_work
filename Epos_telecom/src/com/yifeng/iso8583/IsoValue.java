
package com.yifeng.iso8583;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;


/**
 * iso 8583  域 内容
 * @author qin
 * 
 * 2012-11-12
 * @param <T>
 */
public class IsoValue<T> implements Cloneable {

	private IsoType type;
	private T value;
	private CustomField<T> encoder;
	private int length;
	private String encoding;

	public IsoValue(IsoType t, T value) {
		this(t, value, null);
	}

	/**
	 * 创建一个变长 iso 域  （LLVAR，LLLVAR ,N_LLVAR，N_LLLVAR，ALPHA_LLVAR,ALPHA_LLLVAR,DATE10, DATE4, AMOUNT）
	 * @param t      iso 类型
	 * @param value  值
	 * @param custom 自定义格式方式
	 */
	 
	public IsoValue(IsoType t, T value, CustomField<T> custom) {
		if (t.needsLength()) {
			throw new IllegalArgumentException("Fixed-value types must use constructor that specifies length");
		}
		encoder = custom;
		type = t;
		this.value = value;
		
		//长度计算
		if (type == IsoType.N_LLVAR || type == IsoType.ALPHA_LLVAR 
				|| type==IsoType.ALPHA_LLVAR || type== IsoType.ALPHA_LLLVAR) {  //变长型
			if (custom == null) {
				length = value.toString().length();
			} else {
				String enc = custom.encodeField(value);
				if (enc == null) {
					enc = value == null ? "" : value.toString();
				}
				length = enc.length();
			}
			
			//长度判断
			if ((t==IsoType.N_LLVAR || t==IsoType.ALPHA_LLVAR ) && length > 99 ) {
				throw new IllegalArgumentException("LLVAR can only hold values up to 99 chars");
			} else if ((t==IsoType.N_LLLVAR || t==IsoType.ALPHA_LLLVAR ) && length > 999) {
				throw new IllegalArgumentException("LLLVAR can only hold values up to 999 chars");
			}
			
		} else if (type == IsoType.LLBIN || type == IsoType.LLLBIN) {  //BINARY 型变长
			if (custom == null) {
				if (value instanceof byte[]) {
					length = ((byte[])value).length;
				} else {
					length = value.toString().length() / 2 + (value.toString().length() % 2);
				}
			} else {
				String enc = custom.encodeField(value);
				if (enc == null) {
					enc = value == null ? "" : value.toString();
				}
				length = enc.length();
			}
			if (t == IsoType.LLBIN && length > 99) {
				throw new IllegalArgumentException("LLBIN can only hold values up to 99 chars");
			} else if (t == IsoType.LLLBIN && length > 999) {
				throw new IllegalArgumentException("LLLBIN can only hold values up to 999 chars");
			}
		} else {
			length = type.getLength();
		}
	}

	public IsoValue(IsoType t, T val, int len) {
		this(t, val, len, null);
	}

	/**
	 * 创建一个变长 iso 域
	 * @param t      iso类型
	 * @param val    值
	 * @param len    长度
	 * @param custom 自定义格式方式
	 */
	public IsoValue(IsoType t, T val, int len, CustomField<T> custom) {
		type = t;
		value = val;
		length = len;
		encoder = custom;
		//长度验证
		if (length == 0 && t.needsLength()) {
			throw new IllegalArgumentException(String.format("Length must be greater than zero for type %s (value '%s')", t, val));
			
		} else if (t == IsoType.ALPHA_LLVAR || t == IsoType.ALPHA_LLLVAR || t==IsoType.N_LLVAR || t==IsoType.N_LLLVAR) {
			if(val instanceof byte[] && len==0){
				throw new IllegalArgumentException("LLVAR len =0");
			}else if(len==0){
				length = custom == null ? val.toString().length() : custom.encodeField(value).length();
			}
			//长度判断
			if ((t==IsoType.N_LLVAR || t==IsoType.ALPHA_LLVAR ) && length > 99 ) {
				throw new IllegalArgumentException("LLVAR can only hold values up to 99 chars");
			} else if ((t==IsoType.N_LLLVAR || t==IsoType.ALPHA_LLLVAR ) && length > 999) {
				throw new IllegalArgumentException("LLLVAR can only hold values up to 999 chars");
			}
	 
		} else if (t == IsoType.LLBIN || t == IsoType.LLLBIN) {
			if (len == 0) {
				length = custom == null ? ((byte[])val).length : custom.encodeField(value).length();
			}
			if (t == IsoType.LLBIN && length > 99) {
				throw new IllegalArgumentException("LLBIN can only hold values up to 99 chars");
			} else if (t == IsoType.LLLBIN && length > 999) {
				throw new IllegalArgumentException("LLLBIN can only hold values up to 999 chars");
			}
		}
	}

	public IsoType getType() {
		return type;
	}

 
	public int getLength() {
		return length;
	}

	public T getValue() {
		return value;
	}

	public void setCharacterEncoding(String value) {
		encoding = value;
	}
	public String getCharacterEncoding() {
		return encoding;
	}


	/** Returns a copy of the receiver that references the same value object. */
	@SuppressWarnings("unchecked")
	public IsoValue<T> clone() {
		try {
			return (IsoValue<T>)super.clone();
		} catch (CloneNotSupportedException ex) {
			return null;
		}
	}

	/** Returns true of the other object is also an IsoValue and has the same type and length,
	 * and if other.getValue().equals(getValue()) returns true. */
	public boolean equals(Object other) {
		if (other == null || !(other instanceof IsoValue<?>)) {
			return false;
		}
		IsoValue<?> comp = (IsoValue<?>)other;
		return (comp.getType() == getType() && comp.getValue().equals(getValue()) && comp.getLength() == getLength());
	}

	@Override
	public int hashCode() {
		return value == null ? 0 : toString().hashCode();
	}

	
	public CustomField<T> getEncoder() {
		return encoder;
	}

	/**
	 * 写入数据 
	 * @param outs 
	 * @throws IOException
	 */
	public void write(OutputStream outs) throws IOException {
		//变长数据类型
		if (type == IsoType.ALPHA_LLLVAR || type == IsoType.ALPHA_LLVAR || type== IsoType.N_LLLVAR || type==IsoType.N_LLVAR
				|| type == IsoType.LLBIN || type== IsoType.LLLBIN || type==IsoType.Z_LLLVAR || type==IsoType.Z_LLVAR) {
		 
			//写入变长数据长度，bcd 最多2个字节   右靠
			if (type == IsoType.ALPHA_LLLVAR || type== IsoType.N_LLLVAR ||  type== IsoType.LLLBIN || type==IsoType.Z_LLLVAR)  //LLLVAR型为2个bcd
				outs.write(length / 100);  
			outs.write((((length % 100) / 10) << 4) | (length % 10));
		} 
		
		//写入数据
		outs.write(encode(value,type));
	}
	
	/**
	 * 编码  
	 * @param value    数据
	 * @param type     数据类型
	 * @return   bytes数组
	 */
	private byte[] encode(T value,IsoType type){
		if(value==null)
			return null;
		
		if(value instanceof byte[]){  //byte 类型 直接返回   需要自己转换成 bcd
			return (byte[])value;  
		}
		
		if(type==IsoType.ALPHA || type==IsoType.ALPHA_LLLVAR || type==IsoType.ALPHA_LLVAR){  //字符串型
			return value.toString().getBytes();
			
		}else if(type==IsoType.N_LLLVAR || type==IsoType.N_LLVAR 
				|| type==IsoType.Z_LLLVAR || type==IsoType.Z_LLVAR){   //数字型变长，压缩成bcd 码 左靠
			if(value instanceof String){
				return StringUtils.strToBCD((String)value);
			}else
				return StringUtils.strToBCD(value.toString());
			
		}else if(type==IsoType.LLBIN || type==IsoType.LLLBIN || type==IsoType.B){  //byte 型
			return StringUtils.hexToBytes(value.toString());
			
		}else if (type == IsoType.DATE10 || type == IsoType.DATE4 || type == IsoType.DATE_EXP || type == IsoType.TIME) {//日期数据类型
			byte[] buf = new byte[length / 2];
		 
			if(value instanceof Date){
				toBcd(type.format((Date)value),buf);
			 }else
				toBcd(value.toString(),buf);
			return buf;
			 
		}else if (type == IsoType.N) { //数字定长数据类型
			//byte[] buf =  new byte[(length / 2) + (length % 2)];
		    if(((String)value).length()!= length)
		    	throw new IllegalArgumentException(String.format("The \"%s\" length != %d ", value,length));
			//toBcd(type.format(value.toString(), length), buf);
			return StringUtils.strToBCD((String)value); //压缩成bcd 码 左靠
		} else if (type == IsoType.AMOUNT) {
			byte[] buf = new byte[6];
			toBcd(type.format(value.toString(),12),buf);
			return buf;
		}else
			return null;
	}
	
	/**
	 * 右靠 bcd码  
	 * @param value
	 * @param buf
	 */
	private void toBcd(String value, byte[] buf) {
		int charpos = 0; //char where we start
		int bufpos = 0;
		if (value.length() % 2 == 1) {
			//for odd lengths we encode just the first digit in the first byte
			buf[0] = (byte)(value.charAt(0) - 48);
			charpos = 1;
			bufpos = 1;
		}
		//encode the rest of the string
		while (charpos < value.length()) {
			buf[bufpos] = (byte)(((value.charAt(charpos) - 48) << 4)
					| (value.charAt(charpos + 1) - 48));
			charpos += 2;
			bufpos++;
		}
	}

}
