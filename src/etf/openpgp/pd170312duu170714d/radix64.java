package etf.openpgp.pd170312duu170714d;

import java.util.Base64;

public class radix64 {

	public static String encode(byte[] input_) {
		return Base64.getEncoder().encodeToString(input_);
	}
}
