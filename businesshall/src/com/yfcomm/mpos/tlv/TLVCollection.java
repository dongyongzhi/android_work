package com.yfcomm.mpos.tlv;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TLVCollection{
	
	private int parseCode = 0;
	
	private List<TLV<?>> elements = new ArrayList<TLV<?>>();
	
	
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
	
	
	public void encode(OutputStream os) {
		if(elements!=null  &&  elements.size() >0 ) {
			for(TLV<?> tlv : elements) {
				tlv.getTLVBytes(os);
			}
		} 
	}
	
	/**
	 * 解码 tlv
	 * @param src
	 * @param offset
	 * @param count
	 */
	public <T extends TLV<?>> void decode(Class<T> cls,byte[] src ,int offset,int count){
		this.elements.clear();
		try {
			parseCode = 0;
			this.parse(cls,src, offset, count);
		} catch (InstantiationException e) {
		
		} catch (IllegalAccessException e) {
		
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
	private <T extends TLV<?>> void parse(Class<T> cls,byte[] src,int offset,int count) throws InstantiationException, IllegalAccessException{
		if(offset - count >= src.length || count<2 || parseCode == count) {
			return;
		}
		
		TLV<?> tlv = cls.newInstance();
		//读取
		int readCount = tlv.read(src, offset);
		if(readCount > 0 ) {
			offset +=readCount;
			parseCode += readCount;
			this.elements.add(tlv);
			//递归解析直到结尾 
			parse(cls,src,offset,count);
		} 
	}
}
