package com.wf.ew.common.utils.excelUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
public class DateConvertHelper {
	/**
	 * string转常用日期Date工具类 "yyyy-MM-dd"
	 * @param dateS
	 * @return
	 */
	public static Date StringToDate(String dateS){
		Date date=null;
		try  
		{  
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
		    date = sdf.parse(dateS);  
		}  
		catch ( Exception e)  
		{  
		    e.printStackTrace();  
		    date=null;
		}  		
		return date;
	}

	/**
	 * string 转yyyy-MM-dd HH:mm:ss类型时间
	 * @param dateS
	 * @return
	 */
	public static Date str2LongDate(String dateS){
		Date date;
		if(dateS !=null && dateS.length()>=8){
            try
            {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                date = sdf.parse(dateS);
            }
            catch ( Exception e)
            {
                e.printStackTrace();
                date=null;
            }
            return date;
        }else{
		    return  null;
        }

	}
	
	public static java.sql.Date StringToSqlDate(String dateS){	 
		java.sql.Date  sqlDate=null;
		try  
		{  
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
		    Date date = sdf.parse(dateS);  
		    
		    sqlDate = new java.sql.Date(date.getTime());
		}  
		catch ( Exception e)  
		{  
		    e.printStackTrace(); 
		}  		
		return sqlDate;
	}
	
	public static String getNowDateStr(){
		String str=null;
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		str = sdf.format(new Date());  
		
		return str;
	}
	
	public static String getTodayStr(){
		String str=null;
		DateFormat sdf = new SimpleDateFormat("yyyyMMdd");  
		str = sdf.format(new Date());  
		
		return str;
		
	}
	
	/**
	 * 日期Date转String工具类 "yyyy-MM-dd"
	 * @param dateD
	 * @return
	 */
	public static String DateToString(Date dateD){
		
		return  (new SimpleDateFormat("yyyy-MM-dd")).format(dateD);
	}
	
	/**
	 * 获取当前时间的数字格式
	 * @return
	 */
	public static String getNowDateNumberStr(){
		return  (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date());
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String st="2017-08-30";
		String st2="2016-08-30";
		Date dd=StringToDate(st);
		Date dd2=StringToDate(st2);
		
		if(dd2.getTime()>dd.getTime()){
			System.out.println("dd2大，但dd2早");
		}else{
			System.out.println("dd大，但dd2早");
		}
		System.out.println(DateToString(dd));
		System.out.println(getTodayStr());
	}
}
