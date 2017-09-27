package com.secureChatWebApp.utilites;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;

public class SignaturesUtility {

	public static String performSigning(String s, PrivateKey privateKey) throws Exception {

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
		Signature sign = Signature.getInstance("SHA256withRSA");

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
		return new String(Base64.getEncoder().encode(signature));
	}

	public static boolean performVerification(String s,String signature, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {

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
		Signature sign = Signature.getInstance("SHA256withRSA");

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
		return sign.verify(Base64.getDecoder().decode(signature));
	}
}
