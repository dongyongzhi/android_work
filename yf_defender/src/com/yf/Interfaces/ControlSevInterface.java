package com.yf.Interfaces;


public interface ControlSevInterface {
	/**
	 * @name: uninstallPacket
	 * @Description: 卸载指定的包名
	 * @param：包名 
	 * @return: 执行成功 返回:yf_success
	 */
	public int uninstallPacket(String packetname);
	/**
	 * @name: CheckPacketMD5
	 * @Description: 判断某一个包的签名是否正确
	 * @Packetname:包名
	 * @certmd5: 该包名的证书MD5值
	 * @return:执行成功 返回:true
	 */
	public boolean CheckPacketSignature(String packetname ,String certmd5);
	/**
	 * @name:Downloadxml
	 * @Description: 下载初始的XML
	 * @SevXmlInterface 回调地址，
	 * @Isextend:是否外部地址引用。
	 * @return:执行成功 返回:yf_success 
	 */
	public int Downloadxml(SevXmlInterface sevxml,int Isextend);
	
	/**
	 * @name:CheckAllUserApplication
	 * @Description: 检查所有的USER应用是否合法
	 */
	public void CheckAllUserApplication();
	
	/**
	 * @name:loadxmltoGlobalParms
	 * @Description:将文件写入到全局USER变量
	 * @file: 路径+文件名
	 * @return:执行成功 返回:true 
	*/
	public boolean loadxmltoGlobalParms(String file);
	
	/**
	 * @name:UpdateXmlFile
	 * @Description:将内容写入到文件中
	 * @file: 路径+文件名
	 * @return:执行成功 返回:yf_success 
	*/
	public int UpdateXmlFile(String file);
}