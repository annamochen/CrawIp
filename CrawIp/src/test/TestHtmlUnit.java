package test;

import java.io.IOException;
import java.net.MalformedURLException;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestHtmlUnit {
	public static void main(String[] args) throws Exception {
		WebClient wc = new WebClient(BrowserVersion.CHROME);
    	wc.getOptions().setCssEnabled(false);
    	wc.getOptions().setJavaScriptEnabled(false);
    	ProxyConfig pc = new ProxyConfig("120.52.73.10", 80);
    	wc.getOptions().setProxyConfig(pc);
    	HtmlPage html = wc.getPage("http://www.ip138.com/ip2city.asp");
    	System.out.println(html.asXml());
	}
}
