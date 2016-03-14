package bl4ckscor3.bot.bl4ckb0t.irc;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.LinkedList;
import java.util.List;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Utility SSL factory to create a working SSL connection
 * @author bl4ckscor3, initially created by PircBotX' author TheLQ
 */
public class UtilSSLSocketFactory extends SSLSocketFactory
{
	private SSLSocketFactory wrappedFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
	private boolean trustingAllCertificates = false;

	public UtilSSLSocketFactory trustAllCertificates()
	{
		if(trustingAllCertificates)
			return this;

		trustingAllCertificates = true;

		try
		{
			TrustManager[] tm = new TrustManager[]{new TrustingX509TrustManager()};
			SSLContext context = SSLContext.getInstance("SSL");

			context.init(new KeyManager[0], tm, new SecureRandom());
			wrappedFactory = context.getSocketFactory();
		}
		catch(Exception e)
		{
			throw new RuntimeException("Can't recreate socket factory that trusts all certificates", e);
		}
		
		return this;
	}

	private SSLSocket prepare(Socket socket)
	{
		SSLSocket sslSocket = (SSLSocket) socket;
		List<String> limited = new LinkedList<String>();

		for(String suite : sslSocket.getEnabledCipherSuites())
		{
			if(!suite.contains("_DHE_"))
				limited.add(suite);
		}

		sslSocket.setEnabledCipherSuites(limited.toArray(new String[limited.size()]));
		return sslSocket;
	}

	@Override
	public SSLSocket createSocket(String host, int port) throws IOException, UnknownHostException
	{
		return prepare(wrappedFactory.createSocket(host, port));
	}

	@Override
	public SSLSocket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException
	{
		return prepare(wrappedFactory.createSocket(host, port, localHost, localPort));
	}

	@Override
	public SSLSocket createSocket(InetAddress address, int port) throws IOException
	{
		return prepare(wrappedFactory.createSocket(address, port));
	}

	@Override
	public SSLSocket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException
	{
		return prepare(wrappedFactory.createSocket(address, port, localAddress, localPort));
	}

	@Override
	public SSLSocket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException
	{
		return prepare(wrappedFactory.createSocket(s, host, port, autoClose));
	}

	//@Override
	public SSLSocket createSocket(Socket socket, InputStream in, boolean bln) throws IOException
	{
		try
		{
			return prepare((Socket) getClass().getMethod("createSocket", Socket.class, InputStream.class, Boolean.class).invoke(this, socket, in, bln));
		}
		catch(Exception e)
		{
			throw new RuntimeException("Failed to create socket", e);
		}
	}

	@Override
	public SSLSocket createSocket() throws IOException
	{
		return prepare(wrappedFactory.createSocket());
	}

	@Override
	public String[] getDefaultCipherSuites()
	{
		return wrappedFactory.getDefaultCipherSuites();
	}

	@Override
	public String[] getSupportedCipherSuites()
	{
		return wrappedFactory.getSupportedCipherSuites();
	}

	public static class TrustingX509TrustManager implements X509TrustManager
	{
		public void checkClientTrusted(X509Certificate[] cert, String authType) throws CertificateException {}

		public void checkServerTrusted(X509Certificate[] cert, String authType) throws CertificateException {}

		public X509Certificate[] getAcceptedIssuers()
		{
			return new X509Certificate[0];
		}
	}
}
