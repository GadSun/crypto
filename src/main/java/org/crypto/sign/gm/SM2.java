/**
 * Copyright Dingxuan. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.crypto.sign.gm;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;
import org.crypto.common.exception.SignException;
import org.crypto.common.log.CryptoLog;
import org.crypto.common.log.CryptoLogFactory;
import org.crypto.intfs.ISign;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @Author: zhangmingyang
 * @Date: 2019/10/25
 * @Company Dingxuan
 */
public class SM2 implements ISign {
    private static CryptoLog log = CryptoLogFactory.getLog(SM2.class);
    private static final String KEY_ALGORITHM = "EC";
    private static final String SIGNATURE_ALGORITHM = "SM3WithSM2";
    private static final String PROVIDER = "BC";
    private static final String KEY_GEN_PARAMTER = "sm2p256v1";

    @Override
    public KeyPair genKeyPair(int keySize) throws SignException {
        KeyPairGenerator generator = null;
        try {
            Security.addProvider(new BouncyCastleProvider());
            generator = KeyPairGenerator.getInstance(KEY_ALGORITHM, PROVIDER);
            generator.initialize(new ECNamedCurveGenParameterSpec(KEY_GEN_PARAMTER));
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            log.error(e.getMessage());
            throw new SignException(e.getMessage(), e);
        }
        return generator.genKeyPair();
    }

    @Override
    public byte[] sign(byte[] data, PrivateKey privateKey) throws SignException {
        Signature signature;
        byte[] signValue;
        try {
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey priKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(priKey);
            signature.update(data);
            signValue = signature.sign();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            log.error(e.getMessage());
            throw new SignException(e.getMessage(), e);
        }
        return signValue;
    }

    @Override
    public boolean verify(byte[] data, PublicKey publicKey, byte[] sign) throws SignException {
        boolean verify;
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey.getEncoded());
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            PublicKey pubKey = keyFactory.generatePublic(keySpec);
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(pubKey);
            signature.update(data);
            verify = signature.verify(sign);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            log.error(e.getMessage());
            throw new SignException(e.getMessage(), e);
        }
        return verify;
    }
}
