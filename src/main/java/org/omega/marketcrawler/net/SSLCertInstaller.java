package org.omega.marketcrawler.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.omega.marketcrawler.exchange.Poloniex;

public final class SSLCertInstaller {
	public static void main(String[] args) throws Exception {
		String host = "poloniex.com";
		int port = 443;

		new SSLCertInstaller(host, port).install();
		
		System.out.println(NetUtils.accessDirectly(Poloniex.instance().getMarketSummaryAPI()));
	}
	
	private String host; 
	private int port; 
	private char[] passphrase;
	
	
	public SSLCertInstaller(String host, int port) {
		this(host, port, "changeit".toCharArray());
	}
	
	public SSLCertInstaller(String host, int port, char[] passphrase) {
		this.host = host;
		this.port = port;
		this.passphrase = passphrase;
	}

	public static final String SECURITY_PATH = System.getProperty("java.home") + "/" + "lib" + "/" + "security";
	
	private File getCertsInFile() {
		File dir = new File(SECURITY_PATH);
		File file = new File(dir, "jssecacerts");
		if (file.isFile() == false) {
			file = new File(dir, "cacerts");
		}
		return file;
	}
	private File getCertsOutFile() {
		File file = new File("cacerts");
		return file;
	}
	
	public void install() throws Exception {
		StringBuilder info = new StringBuilder();
		
		File file = getCertsInFile();
		
		info.append("Loading KeyStore " + file.getAbsolutePath() + "...").append("\n");
		InputStream in = new FileInputStream(file);
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(in, passphrase);
		in.close();

		SSLContext context = SSLContext.getInstance("TLS");
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(ks);
		X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
		SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
		context.init(null, new TrustManager[] { tm }, null);
		SSLSocketFactory factory = context.getSocketFactory();

		info.append("Opening connection to " + host + ":" + port + "...").append("\n");
		SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
		socket.setSoTimeout(10000);
		try {
			info.append("Starting SSL handshake...");
			socket.startHandshake();
			socket.close();
			info.append("\n");
			info.append("No errors, certificate is already trusted").append("\n");
		} catch (SSLException e) {
//			System.out.println();
//			e.printStackTrace(System.out);
			info.append(e.getMessage()).append("\n");
		}

		X509Certificate[] chain = tm.getChain();
		if (chain == null) {
			info.append("Could not obtain server certificate chain").append("\n");
			return;
		}

		info.append("\n");
		info.append("Server sent " + chain.length + " certificate(s):").append("\n");
		info.append("\n");
		MessageDigest sha1 = MessageDigest.getInstance("SHA1");
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		for (int i = 0; i < chain.length; i++) {
			X509Certificate cert = chain[i];
			info.append(" " + (i + 1) + " Subject " + cert.getSubjectDN()).append("\n");
			info.append("   Issuer  " + cert.getIssuerDN()).append("\n");
			sha1.update(cert.getEncoded());
			info.append("   sha1    " + toHexString(sha1.digest())).append("\n");
			md5.update(cert.getEncoded());
			info.append("   md5     " + toHexString(md5.digest())).append("\n");
			info.append("\n");
		}

		int k = 0;// first 
		X509Certificate cert = chain[k];
		String alias = host + "-" + (k + 1);
		ks.setCertificateEntry(alias, cert);

		File certsOutFile = getCertsOutFile();
		info.append("certsOutFile path is " + certsOutFile.getAbsolutePath()).append("\n");
		OutputStream out = new FileOutputStream(certsOutFile);
		ks.store(out, passphrase);
		out.close();

		System.out.println("1 javax.net.ssl.trustStore is " + System.getProperty("javax.net.ssl.trustStore"));
		System.setProperty("javax.net.ssl.trustStore", certsOutFile.getAbsolutePath());
		System.out.println("2 javax.net.ssl.trustStore is " + System.getProperty("javax.net.ssl.trustStore"));
		
		info.append("------------------------------- print cert --------------------------------------").append("\n");
		info.append(cert).append("\n");
		info.append("\n");
		info.append("Added certificate to keystore 'jssecacerts' using alias '" + alias + "'").append("\n");
		
		System.out.println(info.toString());
	}

	private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

	private static String toHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 3);
		for (int b : bytes) {
			b &= 0xff;
			sb.append(HEXDIGITS[b >> 4]);
			sb.append(HEXDIGITS[b & 15]);
			sb.append(' ');
		}
		return sb.toString();
	}
}
