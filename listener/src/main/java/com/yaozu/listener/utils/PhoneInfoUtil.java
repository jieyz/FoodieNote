package com.yaozu.listener.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import android.content.Context;
import android.telephony.TelephonyManager;
/**
 * 
 * 类描述：电话相关的工具类：电话号码、设备编号、IP地址    
 * 创建时间：	2014-1-14
 */
public class PhoneInfoUtil {
	private TelephonyManager telephonyManager;
	private Context cxt; 
	public PhoneInfoUtil(Context context){
		this.cxt = context;
		telephonyManager = (TelephonyManager) cxt  
				.getSystemService(Context.TELEPHONY_SERVICE);  
	}
	
	/** 
	* 获取电话号码 
	* 不一定都能获取到
	*/  
	public String getNativePhoneNumber() {  
	     String NativePhoneNumber=null;  
	     NativePhoneNumber=telephonyManager.getLine1Number();  
	     return NativePhoneNumber;  
	}  

	/**
	 * 获取设备编号
	 */
	public String getDeviceId(){
		return telephonyManager.getDeviceId();
	}
	/**
	 * 获取手机的IP地址
	 */
	public String getPhoneIp() {
        try {  
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {  
                NetworkInterface intf = en.nextElement();  
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {  
                    InetAddress inetAddress = enumIpAddr.nextElement();  
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {  
                    //if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet6Address) {  
                        return inetAddress.getHostAddress().toString();  
                    }  
                }  
            }  
        } catch (Exception e) {  
        }  
        return ""; 
    }
	
}
