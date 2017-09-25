package com.secureChatWebApp.configs;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SecurityConfigsTest {

	/**
	 * Test that the keys parameters are generated, stored and retrieved
	 * correctly
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	public void testSecuirtyConfigsOperations() throws NoSuchAlgorithmException {
		// Generate and store keys for the first time
		SecurityConfigs securityConfigs1 = new SecurityConfigs();
		KeyPair encryptionKeyPair1 = securityConfigs1.getEncryptionKeyPair();
		KeyPair signatureKeyPair1 = securityConfigs1.getSignatureKeyPair();

		// Initialize a new SecurityConfigs to test that it will only read
		// previously generated keys
		SecurityConfigs securityConfigs2 = new SecurityConfigs();
		KeyPair encryptionKeyPair2 = securityConfigs2.getEncryptionKeyPair();
		KeyPair signatureKeyPair2 = securityConfigs2.getSignatureKeyPair();

		assertEquals("Wrong loaded encryption public key modulas parameter",
				((RSAPublicKey) encryptionKeyPair1.getPublic()).getModulus(),
				((RSAPublicKey) encryptionKeyPair2.getPublic()).getModulus());

		assertEquals("Wrong loaded encryption public key exponent parameter",
				((RSAPublicKey) encryptionKeyPair1.getPublic()).getPublicExponent(),
				((RSAPublicKey) encryptionKeyPair2.getPublic()).getPublicExponent());

		assertEquals("Wrong loaded encryption private key modulas parameter",
				((RSAPrivateKey) encryptionKeyPair1.getPrivate()).getModulus(),
				((RSAPrivateKey) encryptionKeyPair2.getPrivate()).getModulus());

		assertEquals("Wrong loaded encryption private key exponent parameter",
				((RSAPrivateKey) encryptionKeyPair1.getPrivate()).getPrivateExponent(),
				((RSAPrivateKey) encryptionKeyPair2.getPrivate()).getPrivateExponent());

		assertEquals("Wrong loaded signature public key modulas parameter",
				((RSAPublicKey) signatureKeyPair1.getPublic()).getModulus(),
				((RSAPublicKey) signatureKeyPair2.getPublic()).getModulus());

		assertEquals("Wrong loaded signature public key exponent parameter",
				((RSAPublicKey) signatureKeyPair1.getPublic()).getPublicExponent(),
				((RSAPublicKey) signatureKeyPair2.getPublic()).getPublicExponent());

		assertEquals("Wrong loaded signature private key modulas parameter",
				((RSAPrivateKey) signatureKeyPair1.getPrivate()).getModulus(),
				((RSAPrivateKey) signatureKeyPair2.getPrivate()).getModulus());

		assertEquals("Wrong loaded signature private key exponent parameter",
				((RSAPrivateKey) signatureKeyPair1.getPrivate()).getPrivateExponent(),
				((RSAPrivateKey) signatureKeyPair2.getPrivate()).getPrivateExponent());
	}

	@After
	public void finish() throws IOException {
		String userHomeDir = System.getProperty("user.home");
		Path path = Paths.get(userHomeDir, ".serverKeys.properties");
		Files.delete(path);
	}

}
