package etf.openpgp.pd170312duu170714d;

import java.security.Security;
import java.util.ArrayList;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class main {
	public static void main(String[] args) {
		Security.addProvider(new BouncyCastleProvider());
		Window win = new Window();
	}
}
