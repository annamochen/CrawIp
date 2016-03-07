package csust.crawip.craw;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import csust.crawip.Connection.ConnectionFactory;
import csust.crawip.orm.IpInfo;

public class Crawler {
	
	private java.sql.Connection sqlconn;
	private PreparedStatement pstam;
	private ResultSet rs;
	
	/**
	 * 得到10个解析后的网页，以string的形式
	 * 
	 * @return
	 */
	public List<Document> getDocument() {
		List<Document> list = new ArrayList<Document>();
		StringBuilder url1 = new StringBuilder();
		url1.append("http://www.kuaidaili.com/proxylist/");
		
		for (int i = 1; i < 11; i++) {
			StringBuilder url = new StringBuilder();
			url.append(url1);
			url.append(i + "");
			url.append("/");
			System.out.println("url拼接成功！"+url);
			Connection connJsoup = Jsoup.connect(url.toString());
			
			Document dochtml = new Document(url.toString());
			try {
				dochtml = connJsoup.timeout(20000).get();
				System.out.println("获取document成功！");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("#connJsoup.timeout(20000).get();出错！！！#");
			}

			list.add(dochtml);
		}
		return list;
	}

	/**
	 * 通过解析后的网页得到里面的ip和端口
	 * 
	 * @param strs
	 * @return
	 */
	public List<IpInfo> getIpAndPort(List<Document> documents) {
		List<IpInfo> list = new ArrayList<IpInfo>();
		for (int i = 0; i < documents.size(); i++) {
			Element body = documents.get(i).body();
			Elements trs = body.getElementsByTag("tr");
			for (int j = 1; j < trs.size(); j++) {
				Elements tds = trs.get(j).children();
				if (tds.get(2).text().trim().equals("高匿名")) {
					IpInfo ii = new IpInfo();
					
					ii.setIpaddress(tds.get(0).text().trim());
					ii.setPort(tds.get(1).text().trim());
					list.add(ii);
					System.out.println("获取高匿名ip成功！"+ii.toString());
				}

			}
		}
		return list;
	}



	/**
	 * 把测试可以用的ip插入到数据库
	 * 
	 * @param ipInfo
	 * @throws Exception 
	 */
	public void insert(IpInfo ipInfo) throws Exception {
		String sql = "insert into ipinfo(ipaddress,port) values(?,?)";
		
		if(getHtmlResultString(ipInfo)){
			//可用！
			sqlconn = ConnectionFactory.getConnection();
			try {
				pstam = sqlconn.prepareStatement(sql);
				pstam.setString(1, ipInfo.getIpaddress().toString());
				pstam.setString(2, ipInfo.getPort().toString());
				pstam.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("sql语句执行失败！");
				return;
			} finally{
				ConnectionFactory.close(sqlconn, rs, pstam);
			}
			System.out.println("sql语句成功！！！！！！！！");
		}
	}
	
	/**
	 * 得到每个ip检验后的结果
	 * @return
	 * @throws Exception 
	 */
	public boolean getHtmlResultString(IpInfo ipInfo) throws Exception{
		
		String myIp = new String();
		try {
			InetAddress ia = InetAddress.getLocalHost();
			myIp = ia.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.out.println("####获取本机ip地址失败####");
		}

		
//        System.getProperties().setProperty("http.proxyHost", ipInfo.getIpaddress());
//        System.getProperties().setProperty("http.proxyPort", ipInfo.getPort());
//        
//        String nextip = getHtmlIp(getHtml());
//        if("222.240.152.229".equals(nextip)){
//        	return false;
//        }else{
//        	return true;
//        }
		if(getHtml1(ipInfo) == null ){
			return false;
		}else{
        	return true;
        }
	}
	
	/**
	 * 来获得某个网站的string
	 * @param address
	 * @return
	 */
    public String getHtml(){
    	String address = "http://www.ip138.com/ip2city.asp";
        StringBuffer html = new StringBuffer();
        String result = null;
        try{
            URL url = new URL(address);
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; GTB5; .NET CLR 2.0.50727; CIBA)");
            BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
            try {
                String inputLine;
                byte[] buf = new byte[4096];
                int bytesRead = 0;
                while (bytesRead >= 0) {
                    inputLine = new String(buf, 0, bytesRead, "ISO-8859-1");
                    html.append(inputLine);
                    bytesRead = in.read(buf);
                    inputLine = null;
                }
                buf = null;
            } finally {
                in.close();
                conn = null;
                url = null;
            }
        result = new String(html.toString().trim().getBytes("ISO-8859-1"), "gb2312").toLowerCase();
        }catch(Exception  e){
            e.printStackTrace();
            return null;
        }
        html = null;
        return  result;
    }
    
    
    public String getHtml1(IpInfo ipInfo){
    	@SuppressWarnings("resource")
		WebClient wc = new WebClient(BrowserVersion.CHROME);
    	wc.getOptions().setCssEnabled(false);
    	wc.getOptions().setJavaScriptEnabled(false);
    	ProxyConfig pc = new ProxyConfig(ipInfo.getIpaddress(), Integer.parseInt(ipInfo.getPort()));
    	wc.getOptions().setProxyConfig(pc);
    	HtmlPage html;
		try {
			html = wc.getPage("http://www.ip138.com/ip2city.asp");
		} catch (FailingHttpStatusCodeException | IOException e) {
			System.out.println("这个ip不行！");
			return null;
		}
		//String strxml = html.asXml();
    	return html.asXml();
//    	try {
//			return new String(strxml.getBytes("ISO-8859-1"), "gb2312");
//		} catch (Exception e) {
//			System.out.println("字符串转化失败！！");
//			e.printStackTrace();
//			return null;
//		}
    	
    }
    
    public String getHtmlIp(String html){
    	String[] strs1 = html.split("[");
    	String[] strs2 = strs1[1].split("]");
    	return strs2[0];
    }
    
    /**
     * 具体执行方法
     */
    public void action(){
    	List<IpInfo> list = getIpAndPort(getDocument());
    	for(int i = 0;i < list.size();i++){
    		try {
				if(getHtmlResultString(list.get(i))){
					insert(list.get(i));
				}
			} catch (Exception e) {
				System.out.println("action 方法执行失败！");
				e.printStackTrace();
			}
    		System.out.println("第"+i+"个ip已分析完");
    	}
    }
    
    
    
    public static void main(String[] args) {
		Crawler crawler = new Crawler();
		crawler.action();
		
	}
}
