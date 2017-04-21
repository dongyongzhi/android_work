 
package com.yifeng.iso8583;

/**
 * 自定义格式内容
 * @author qin
 * 
 * 2012-11-12
 * @param <T>
 */
public interface CustomField<T> {

	public T decodeField(String value);

	public String encodeField(T value);

}
