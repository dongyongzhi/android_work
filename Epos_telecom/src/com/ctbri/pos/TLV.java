package com.ctbri.pos;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.ctbri.domain.TLVElement;
import com.ctbri.utils.ElecLog;

/**
 * TLV 格式解析
 * @author qin
 * 
 * 2012-12-22
 */
public class TLV {

	private final static String CHARSET = "gb2312"; //编码为国标gb2312
	
	private List<TLVElement>  elements;
	
	/**
	 * 请求获取 tlv 格式内容
	 * @param tag
	 * @return
	 */
	public static byte[] request(int ...tags){
		if(tags==null)
			return null;
		
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		for(int tag : tags){
			//tag
			buf.write((byte)(tag>> 8 ));
			buf.write((byte)tag);
		}
		return buf.toByteArray();
	}
	
	/**
	 * 编码 tlv 格式数据
	 * @param elements  
	 * @return
	 */
	public static byte[] encoder(List<TLVElement> elements){
		if(elements == null)
			return null;
		
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		
		byte[] data =null;
		for(TLVElement element : elements){
			//tag
			buf.write((byte)(element.getTag()>> 8 ));
			buf.write((byte)element.getTag());
			//len
			try {
				data = element.getValue().getBytes(CHARSET);
				buf.write((byte)data.length); //1个字节长度
				buf.write(data);  //数据内容
				
			} catch (UnsupportedEncodingException e) {
				ElecLog.w(TLV.class, e);
			} catch (IOException e) {
				ElecLog.w(TLV.class, e);
			}
		}
		return buf.toByteArray();
	}
	
    /**
     * 解析 TLV 格式数据
     * @param offset  偏移量
     * @param count   总数据长度
     * @param source  数据源
     * @return
     */
	public void decoder(int offset,int count,byte[] source){
		elements = new ArrayList<TLVElement>();
		parse(offset,count,source,elements);
	}
	
	/**
	 * 解析 TLV 格式数据
	 * @param source 原数据
	 * @return
	 */
	public void decoder(byte[] source){
		decoder(0,source.length,source);
	}
	
	public  List<TLVElement>  getElements(){
		return this.elements;
	}
	
	/**
	 * 获取 tlv 
	 * @param tag
	 * @return
	 */
	public TLVElement get(int tag){
		if(this.elements==null)
			return null;
		
		for(TLVElement e : elements){
			if(e.getTag() == tag)
				return e;
		}
		return null;
	}
	
	/**
	 * 获取 tlv 内容
	 * @param tag 
	 * @return
	 */
	public String getValue(int tag){
		TLVElement e = get(tag);
		if(e==null)
			return null;
		else
			return e.getValue();
	}
	
	/**
	 * 解析  
	 * @param offset 偏移量
	 * @param count  总数据长度
	 * @param data   数据源
	 * @param elements  输出的内容
	 */
	private static void parse(int offset,int count,byte[] data,List<TLVElement> elements){
		if(offset >=count-1)
			return;
		
		int tag = (data[offset] & 0xFF) <<8  | (data[++offset] & 0xFF); //获取  tag
		int len = data[++offset] & 0xFF;  //获取一个字节的长度
		//获取内容
		TLVElement  element = new TLVElement();
		element.setTag(tag);
		try {
			element.setValue(new String(data,++offset,len,CHARSET));
		} catch (UnsupportedEncodingException e) {
			ElecLog.w(TLV.class, e);
		}
		offset +=len;
		
		elements.add(element);
		parse(offset,count,data,elements); //递归解析直到结尾 
	}
}
