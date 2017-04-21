package com.yfcomm.pos.tlv;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.util.ByteArrayBuffer;

import com.yfcomm.pos.YFLog;
import com.yfcomm.pos.tlv.support.TLVByteArray;
import com.yfcomm.pos.tlv.support.TLVString;

public class TLVCollection{
	private final static YFLog logger = YFLog.getLog(TLVCollection.class);
	
	//参数设置
	private final static int capacity = 1024;
	
	private int parseCode = 0;
	
	private List<TLV<?>> elements = new ArrayList<TLV<?>>();
	
	private final static Map<Integer,Class<? extends TLV<?>>> tlvMapping = new HashMap<Integer,Class<? extends TLV<?>>>();
	static{
		Class<? extends TLV<?>> stringTLV = TLVString.class;
		
		tlvMapping.put(0x1A03, stringTLV);
		tlvMapping.put(0x1A04, stringTLV);
		tlvMapping.put(0x1A05, stringTLV);
		tlvMapping.put(0x1A06, stringTLV);
		tlvMapping.put(0x9F01, stringTLV);
		tlvMapping.put(0x9F02, stringTLV);
		tlvMapping.put(0x9F03, stringTLV);
		tlvMapping.put(0x9F04, stringTLV);
		tlvMapping.put(0x9F05, stringTLV);
		tlvMapping.put(0x9F06, stringTLV);
		tlvMapping.put(0x9F07, stringTLV);
		tlvMapping.put(0x9F08, stringTLV);
		tlvMapping.put(0x9F09, stringTLV);
		tlvMapping.put(0x9F0A, stringTLV);
		tlvMapping.put(0x9F0B, stringTLV);
		tlvMapping.put(0x9F0C, stringTLV);
		tlvMapping.put(0x9F0D, stringTLV);
		tlvMapping.put(0x9F0E, stringTLV);
		tlvMapping.put(0x9F0F, stringTLV);
		tlvMapping.put(0x9F10, stringTLV);
		tlvMapping.put(0x9F11, stringTLV);
		tlvMapping.put(0x9F12, stringTLV);
		tlvMapping.put(0x9F13, stringTLV);
		tlvMapping.put(0x9F14, stringTLV);
		tlvMapping.put(0x9F15, stringTLV);
		tlvMapping.put(0x9F16, stringTLV);
		tlvMapping.put(0x9F17, stringTLV);
		tlvMapping.put(0x9F18, stringTLV);
		tlvMapping.put(0x9F19, stringTLV);
		tlvMapping.put(0x9F1A, stringTLV);
		tlvMapping.put(0x9F1B, stringTLV);
		tlvMapping.put(0x9F1C, stringTLV);
		tlvMapping.put(0x9F1D, stringTLV);
		tlvMapping.put(0x9F1E, stringTLV);
		tlvMapping.put(0x9F1F, stringTLV);
		tlvMapping.put(0x9F20, stringTLV);
		tlvMapping.put(0x9F21, stringTLV);
		tlvMapping.put(0x9F22, stringTLV);
		tlvMapping.put(0x9F23, stringTLV);
		tlvMapping.put(0x9F24, stringTLV);
		tlvMapping.put(0x9F25, stringTLV);
		tlvMapping.put(0x9F26, stringTLV);
		tlvMapping.put(0x9F27, stringTLV);
		tlvMapping.put(0x9F28, stringTLV);
		tlvMapping.put(0x9F29, stringTLV);
		tlvMapping.put(0x9F2A, stringTLV);
		tlvMapping.put(0x9F2B, stringTLV);
		tlvMapping.put(0x9F2C, stringTLV);
		tlvMapping.put(0x9F2D, stringTLV);
		tlvMapping.put(0x9F2E, stringTLV);
		tlvMapping.put(0x9F2F, stringTLV);
		tlvMapping.put(0x9F31, TLVByteArray.class);
		tlvMapping.put(0x9F32, TLVByteArray.class);
		tlvMapping.put(0x9F33, TLVByteArray.class);
		tlvMapping.put(0x9F34, TLVByteArray.class);
		tlvMapping.put(0x9F35, TLVByteArray.class);
		tlvMapping.put(0x9F36, TLVByteArray.class);
	}
	
	public void add(TLV<?> tlv) {
		this.elements.add(tlv);
	}
	
	/**
	 * 获取  tlv 
	 * @param tag
	 * @return
	 */
	public TLV<?> get(int tag) {
		for(TLV<?> tlv : this.elements) {
			if(tlv.getTag() == tag)
				return tlv;
		}
		return null;
	}
	
	/**
	 * 获取 tag 下数据
	 * @param tag
	 * @return
	 */
	public Object getValue(int tag) {
		TLV<?> tlv = this.get(tag);
		if(tlv==null) {
			return null;
		} else {
			return tlv.getValue();
		}
	}
	
	/**
	 * 获取 tag 下字符串数据
	 * @param tag
	 * @return
	 */
	public String getValueString(int tag) {
		TLV<?> tlv = this.get(tag);
		if(tlv!=null && tlv.getValue()!=null) {
			return tlv.getValue().toString();
		} else {
			return null;
		}
	}
	
	public int size() {
		return this.elements == null ?  0 :  this.elements.size();
	}
	
	public Map<Integer,TLV<?>> toMap(){
		Map<Integer,TLV<?>> map = new HashMap<Integer,TLV<?>>(this.elements.size());
		for(TLV<?> tlv : this.elements) {
			map.put(tlv.getTag(), tlv);
		}
		return Collections.unmodifiableMap(map);
	}
	
	/**
	 * 编码 
	 * @param elements
	 * @return
	 */
	public byte[] encode(){
		if(elements!=null  &&  elements.size() >0 ) {
			ByteArrayBuffer buf = new ByteArrayBuffer(capacity);
			
			byte[] data;
			for(TLV<?> tlv : elements) {
				data = tlv.getTLVBytes();
			    //数据
			    buf.append(data,0,data.length);
			}
			return buf.toByteArray();
		} else {
			return new byte[0];
		}
	}
	
	public void encode(OutputStream os) {
		if(elements!=null  &&  elements.size() >0 ) {
			byte[] data;
			try {
				for(TLV<?> tlv : elements) {
					data = tlv.getTLVBytes();
				    //数据
					os.write(data, 0, data.length);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
	}
	
	/**
	 * 解码 tlv
	 * @param src
	 * @param offset
	 * @param count
	 */
	public void decode(byte[] src ,int offset,int count){
		this.elements.clear();
		try {
			parseCode = 0;
			this.parse(src, offset, count);
		} catch (InstantiationException e) {
			logger.e(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			logger.e(e.getMessage(), e);
		}
	}
	
	/**
	 * 解析  
	 * @param src   数据源
	 * @param offset 偏移量
	 * @param count  总数据长度
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	private void parse(byte[] src,int offset,int count) throws InstantiationException, IllegalAccessException{
		if(offset - count >= src.length || count<2 || parseCode == count) {
			return;
		}
		
		//获取  tag
		int tmp = offset;
		Integer tag = (src[offset] & 0xFF) <<8  | (src[++tmp] & 0xFF); 
		if(tag == 0) {
			return;
		}
		Class<? extends TLV<?>> tlvCls = tlvMapping.get(tag);
		TLV<?> tlv = tlvCls!=null ? tlvCls.newInstance() : new TLVByteArray();
		
		//读取
		int readCount = tlv.read(src, offset);
		if(readCount > 0 ) {
			offset +=readCount;
			parseCode += readCount;
			this.elements.add(tlv);
			//递归解析直到结尾 
			parse(src,offset,count);
		} 
	}
}
