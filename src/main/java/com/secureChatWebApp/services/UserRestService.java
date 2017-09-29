package com.secureChatWebApp.services;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.secureChatWebApp.models.User;

@RestController
public class UserRestService {

	@RequestMapping(value = "test", method = RequestMethod.GET, produces = "application/json")
    public String getDefaultUserInJSON() {
		System.out.println("in userController");
    	return "Hello World";
    }
    
	@RequestMapping(value = "test2", method = RequestMethod.POST, produces = "application/json")
    public String testPost() {
		System.out.println("in post request");
    	return "Hello World";
    }
	
	 @RequestMapping(value = "/", method = RequestMethod.GET)
	    public ResponseEntity<User> getDisplayDefault()
	    {
	        return new ResponseEntity<User>(new User(), HttpStatus.OK);
	    }
	 
	    @ExceptionHandler
	    public ResponseEntity<ErrorHolder> handle() {
	        return new ResponseEntity<ErrorHolder>(new ErrorHolder("Uh oh"), HttpStatus.NOT_FOUND);
	    }
	    class ErrorHolder {
	        public String errorMessage;
	        @JsonCreator
	        public ErrorHolder(@JsonProperty("errorMessage") String errorMessage)
	        {
	            this.errorMessage = errorMessage;
	        }

	    }
	
    public static void main(String[] args) throws Exception {
    	BigInteger modulus = new BigInteger("9579395940609983593307985278396088956051575943622517115758142329514659049231799125540545342549638974014020899333615940728093556477765550635369547517163601");
    	BigInteger exponent = new BigInteger("3");
    	RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
    	KeyFactory factory = KeyFactory.getInstance("RSA");
    	PublicKey pub = factory.generatePublic(spec);
    	
    	String plainText = "Hatem";
    	String cipher = new String(Base64.getEncoder().encode(encrypt(plainText,pub)));
    	System.out.println(cipher);
    	
    	
    	KeyPair keyPair = generatetKeyPair();
    	RSAPublicKey key = (RSAPublicKey) keyPair.getPublic();
    	RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
    	System.out.println(privateKey.getPrivateExponent());
    	System.out.println(privateKey.getModulus());
    	
    	System.out.println("---->"+key.getPublicExponent());
    	System.out.println("---->"+key.getModulus());
    	System.out.println("n = "+  String.format("%040x",key.getModulus()));
    	System.out.println("e = "+  String.format("%040x",key.getPublicExponent()));
    	System.out.println(new BigInteger((String.format("%040x",key.getModulus())),16));
    	
    	BigInteger privateExponent = new BigInteger("6969969314326684788757048218075709801652660258043315402751582315321228427561845787301752744834957930045024395213531245448943986747638235659735422026661369");
    	BigInteger privateModulas = new BigInteger("9030878767778967220908457109745315301613459507154559252488690421288771425181993925836332492165958967519196312333408580963015703994605694235068391369987151");
    	RSAPrivateKeySpec spec2 = new RSAPrivateKeySpec(privateModulas, privateExponent);
    	KeyFactory factory2 = KeyFactory.getInstance("RSA");
    	PrivateKey prvKey = factory2.generatePrivate(spec2);
    	
    	String cipherText = "UVW/lSfZzkB4myYA/1L1QwmrwgB9junnosqfJUanuEuf4bm72YBIxpdetaeDAzo9r2qUdVhKXs++hGgZ0hV9FA==";
    	String plian = decrypt(Base64.getDecoder().decode(cipherText.getBytes()),prvKey);
    	System.out.println(plian);
//    	String[] cipherText = new String("MyWo3E7QsZvZaqCKwlb6jqce2aelshhnPeBq3u1yBqnOduJblH+XemRMo2r7UP3gUq8L+/Cl0KyEqOAkM5b74w==:zMjzWoxS6ZnsiOS0QlFmklFirsMKqPl0sB8XWQbGn84=").split(":");
//    	String aesKey = decrypt(Base64.getDecoder().decode(cipherText[0].getBytes()),prvKey);
//    	// decode the base64 encoded string
//    	byte[] decodedKey = Base64.getDecoder().decode(aesKey);
//    	// rebuild key using SecretKeySpec
//    	SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 
//    	    	String plain = aesDecrypt(cipherText[1].getBytes(),originalKey);
//    	    	System.out.println(plain);
    	System.out.println("------------------------------------------------------------------");
    	
    	// Signatures
    	
    	BigInteger signModulus = new BigInteger("5842315304732174395910317252871816783654489460059964118242354313185333377425654783456914407579194031220048901123342403391992648585492887635752802718862863");
    	BigInteger signExponent = new BigInteger("3");
    	RSAPublicKeySpec signSpec = new RSAPublicKeySpec(signModulus, signExponent);
    	KeyFactory signFactory = KeyFactory.getInstance("RSA");
    	PublicKey signPub = signFactory.generatePublic(signSpec);
    	String signature = "bTKQBO1RuvM4qO8bqy+jvnsddSuWSA7sILD30xdogDyjJGErViUIxD7anBg5CjyzkoQil8cOZjunse0sjHxfxA==";
    	performVerification("Hatem1995", "SHA256withRSA", Base64.getDecoder().decode(signature.getBytes()), signPub);
    
    	
    	KeyPair signKeyPair = generatetKeyPair();
    	RSAPublicKey signPublicKey = (RSAPublicKey) signKeyPair.getPublic();
    	RSAPrivateKey signPrivateKey = (RSAPrivateKey) signKeyPair.getPrivate();
    	System.out.println(signPrivateKey.getPrivateExponent());
    	System.out.println(signPrivateKey.getModulus());
    	
    	System.out.println("---->"+signPublicKey.getPublicExponent());
    	System.out.println("---->"+signPublicKey.getModulus());
    	System.out.println("n = "+  String.format("%040x",signPublicKey.getModulus()));
    	System.out.println("e = "+  String.format("%040x",signPublicKey.getPublicExponent()));
    	
    	
    	BigInteger signPrivateExponent = new BigInteger("6546126846981795436790447750647187516394117370692755052371433187731062392852715784297063668374613302716035533133840655182550788832601709883970977305158313");
    	BigInteger signPrivateModulas = new BigInteger("7099699061191949418983824684895241005873554373427293889606742297720069439826546086505195964788853064491748962384772612077285895966053821553463572415737447");
    	RSAPrivateKeySpec signSpec2 = new RSAPrivateKeySpec(signPrivateModulas, signPrivateExponent);
    	KeyFactory signFactory2 = KeyFactory.getInstance("RSA");
    	PrivateKey signPrvKey = signFactory2.generatePrivate(signSpec2);
    	
    	String signature2 = new String(Base64.getEncoder().encode(performSigning("Hatem", "SHA256withRSA", signPrvKey)));
    	System.out.println("Signature = "+signature2);
    	
    }    	

    /**
	 * default block size is 16 byte so the generated IV is also 16 bytes
	 */
	public static byte[] aesEncrypt(String plaintext, SecretKey key)
			throws InvalidAlgorithmParameterException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, IOException {

		Cipher cipher = Cipher.getInstance("AES/CBC");

		System.out.println(cipher.getProvider() + "  " + cipher.getAlgorithm()
				+ "  " + cipher.getBlockSize() + "  " + cipher.getParameters());

		cipher.init(Cipher.ENCRYPT_MODE, key);

		byte[] iv = cipher.getIV();
		byte[] byteCipherText = cipher.doFinal(plaintext.getBytes());

		// concatenating IV to byteCipherText to form one cipher text
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(iv);
		outputStream.write(byteCipherText);

		byte[] finalData = outputStream.toByteArray();

		return finalData;
	}
	
	public static String aesDecrypt(byte[] byteCipherText, SecretKey key)
			throws InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, UnsupportedEncodingException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidAlgorithmParameterException {

		Cipher cipher = Cipher.getInstance("AES/CBC");

		// get IV and cipher text from byteCipherText resulted after
		// concatenation of encrypted plain text and IV
		byte[] iv = Arrays.copyOfRange(byteCipherText, 0, 16);
		byte[] cipherText = Arrays.copyOfRange(byteCipherText, 16,
				byteCipherText.length);

		IvParameterSpec iv_specs = new IvParameterSpec(iv);
		cipher.init(Cipher.DECRYPT_MODE, key, iv_specs);

		byte[] plainTextBytes = cipher.doFinal(cipherText);
		String plainText = new String(plainTextBytes);
		return plainText;
	}

    
    public static SecretKey generatetSecretEncryptionKey() throws Exception {
		KeyGenerator generator = KeyGenerator.getInstance("AES");
		generator.init(192); // The AES key size in number of bits
		SecretKey secKey = generator.generateKey();
		return secKey;
	}

	/**
	 * default block size is 16 byte so the generated IV is also 16 bytes
	 */
	public static byte[] encrypt(String plaintext, SecretKey key)
			throws InvalidAlgorithmParameterException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, IOException {

		Cipher cipher = Cipher.getInstance("AES/CBC/");

		System.out.println(cipher.getProvider() + "  " + cipher.getAlgorithm()
				+ "  " + cipher.getBlockSize() + "  " + cipher.getParameters());

		cipher.init(Cipher.ENCRYPT_MODE, key);

		byte[] iv = cipher.getIV();
		byte[] byteCipherText = cipher.doFinal(plaintext.getBytes());

		// concatenating IV to byteCipherText to form one cipher text
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(iv);
		outputStream.write(byteCipherText);

		byte[] finalData = outputStream.toByteArray();

		return finalData;
	}

	public static String decrypt(byte[] byteCipherText, SecretKey key)
			throws InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, UnsupportedEncodingException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidAlgorithmParameterException {

		Cipher cipher = Cipher.getInstance("AES/CBC/"	);

		// get IV and cipher text from byteCipherText resulted after
		// concatenation of encrypted plain text and IV
		byte[] iv = Arrays.copyOfRange(byteCipherText, 0, 16);
		byte[] cipherText = Arrays.copyOfRange(byteCipherText, 16,
				byteCipherText.length);

		IvParameterSpec iv_specs = new IvParameterSpec(iv);
		cipher.init(Cipher.DECRYPT_MODE, key, iv_specs);

		byte[] plainTextBytes = cipher.doFinal(cipherText);
		String plainText = new String(plainTextBytes);
		return plainText;
	}
    

	/**
	 * KeyPairGenerator class can be used to generate pairs of private and
	 * public keys specific to a certain public-key algorithm.
	 * 
	 * RSA keys must be at least 512 bits long and It can reach a key of 8192
	 * bits but it will take long time up to 3 minutes
	 * 
	 * @return
	 * @throws Exception
	 */
	public static KeyPair generatetKeyPair() throws Exception {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(512);
		KeyPair keyPair = kpg.generateKeyPair();
		return keyPair;
	}

	public static byte[] encrypt(String plainText, PublicKey pubKey)
			throws InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException,
			NoSuchPaddingException {
		Cipher cipher = Cipher.getInstance("RSA");

		System.out.println(cipher.getProvider() + "  " + cipher.getAlgorithm()
				+ "  " + cipher.getBlockSize() + "  " + cipher.getParameters());

		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		byte[] cipherText = cipher.doFinal(plainText.getBytes());
		return cipherText;
	}

	public static String decrypt(byte[] byteCipherText, PrivateKey privKey)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

		Cipher cipher = Cipher.getInstance("RSA");

		System.out.println(cipher.getProvider() + "  " + cipher.getAlgorithm()
				+ "  " + cipher.getBlockSize() + "  " + cipher.getParameters());

		cipher.init(Cipher.DECRYPT_MODE, privKey);
		byte[] decryptedPlainText = cipher.doFinal(byteCipherText);
		String plainText = new String(decryptedPlainText);
		return plainText;

	}

	
	
	 static byte[] performSigning(String s, String alg, PrivateKey privateKey)
			    throws Exception {

			/*
			 * Signature objects are modal objects. This means that a Signature
			 * object is always in a given state, where it may only do one type of
			 * operation. States are represented as final integer constants defined
			 * in their respective classes.
			 * 
			 * The three states a Signature object may have are: 1-UNINITIALIZED
			 * 2-SIGN 3-VERIFY
			 * 
			 * When it is first created, a Signature object is in the UNINITIALIZED
			 * state. The Signature class defines two initialization methods,
			 * initSign and initVerify, which change the state to SIGN and VERIFY,
			 * respectively.
			 */
			Signature sign = Signature.getInstance(alg);

			System.out
				.println("Signature Provider and Algorithm used to generate a signature: "
					+ sign.getProvider() + "  " + sign.getAlgorithm());


			/*
			 * A Signature object must be initialized before it is used. The
			 * initialization method depends on whether the object is going to be
			 * used for signing or for verification.
			 * 
			 * If it is going to be used for signing, the object must first be
			 * initialized with the private key of the entity whose signature is
			 * going to be generated. This initialization is done by calling the
			 * method:
			 */
			sign.initSign(privateKey);

			/*
			 * If the Signature object has been initialized for signing (if it is in
			 * the SIGN state), the data to be signed can then be supplied to the
			 * object. This is done by making one or more calls to one of the update
			 * methods:
			 * 
			 * 1- final void update(byte b) 2-final void update(byte[] data) 3-
			 * final void update(byte[] data, int off, int len)
			 * 
			 * Calls to the update method(s) should be made until all the data to be
			 * signed has been supplied to the Signature object.
			 */
			sign.update(s.getBytes());

			/*
			 * To generate the signature, simply call one of the sign methods:
			 * 
			 * 1-final byte[] sign() 2-final int sign(byte[] outbuf, int offset, int
			 * len)
			 * 
			 * The first method returns the signature result in a byte array. The
			 * second stores the signature result in the provided buffer outbuf,
			 * starting at offset. len is the number of bytes in outbuf allotted for
			 * the signature. The method returns the number of bytes actually
			 * stored.
			 */
			byte[] signature = sign.sign();
			System.out.println("signature generated: " + bytesToHex(signature));
			System.out.println("Signature Length in bytes = " + signature.length);
			return signature;
		    }

		    static void performVerification(String s, String alg, byte[] signature,
			    PublicKey publicKey) throws Exception {

			/*
			 * Signature objects are modal objects. This means that a Signature
			 * object is always in a given state, where it may only do one type of
			 * operation. States are represented as final integer constants defined
			 * in their respective classes.
			 * 
			 * The three states a Signature object may have are: 1-UNINITIALIZED
			 * 2-SIGN 3-VERIFY
			 * 
			 * When it is first created, a Signature object is in the UNINITIALIZED
			 * state. The Signature class defines two initialization methods,
			 * initSign and initVerify, which change the state to SIGN and VERIFY,
			 * respectively.
			 */
			Signature sign = Signature.getInstance(alg);

			System.out
				.println("Signature Provider and Algorithm used to verify a signature: "
					+ sign.getProvider() + "  " + sign.getAlgorithm());

			/*
			 * If instead the Signature object is going to be used for verification,
			 * it must first be initialized with the public key of the entity whose
			 * signature is going to be verified. This initialization is done by
			 * calling either of these methods:
			 * 
			 * 1-final void initVerify(PublicKey publicKey)
			 * 
			 * 2-final void initVerify(Certificate certificate)
			 * 
			 * This method puts the Signature object in the VERIFY state.
			 */
			sign.initVerify(publicKey);

			/*
			 * If the Signature object has been initialized for verification (if it
			 * is in the VERIFY state), it can then verify if an alleged signature
			 * is in fact the authentic signature of the data associated with it. To
			 * start the process, the data to be verified (as opposed to the
			 * signature itself) is supplied to the object. The data is passed to
			 * the object by calling one of the update methods:
			 * 
			 * final void update(byte b)
			 * 
			 * final void update(byte[] data)
			 * 
			 * final void update(byte[] data, int off, int len)
			 * 
			 * Calls to the update method(s) should be made until all the data to be
			 * verified has been supplied to the Signature object.
			 */
			sign.update(s.getBytes());

			/*
			 * The signature can now be verified by calling one of the verify
			 * methods:
			 * 
			 * final boolean verify(byte[] signature)
			 * 
			 * final boolean verify(byte[] signature, int offset, int length)
			 * 
			 * The argument must be a byte array containing the signature. This byte
			 * array would hold the signature bytes which were returned by a
			 * previous call to one of the sign methods.
			 * 
			 * The verify method returns a boolean indicating whether or not the
			 * encoded signature is the authentic signature of the data supplied to
			 * the update method(s)
			 * 
			 * A call to the verify method resets the signature object to its state
			 * when it was initialized for verification via a call to initVerify.
			 * That is, the object is reset and available to verify another
			 * signature from the identity whose public key was specified in the
			 * call to initVerify.
			 * 
			 * Alternatively, a new call can be made to initVerify specifying a
			 * different public key (to initialize the Signature object for
			 * verifying a signature from a different entity), or to initSign (to
			 * initialize the Signature object for generating a signature).
			 */
			System.out.println("The Signature Verfication  result : "
				+ sign.verify(signature));
		    }
	
	/**
	 * Convert a binary byte array into readable hex form
	 * 
	 * @param hash
	 * @return
	 */
	private static String bytesToHex(byte[] hash) {
		return DatatypeConverter.printHexBinary(hash);
	}
}
