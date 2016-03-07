package csust.crawip.orm;

public class IpInfo {
	private String ipaddress;
	private String port;

	public IpInfo() {

	}

	public IpInfo(String ipaddress, String port) {
		super();
		this.ipaddress = ipaddress;
		this.port = port;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	@Override
	public String toString() {
		return "IpInfo [ipaddress=" + ipaddress + ", port=" + port + "]";
	}

}
