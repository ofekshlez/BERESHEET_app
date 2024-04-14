package com.example.trying;

import java.math.BigInteger;

public class RSAEncryptor {
    private int p;
    private int q;
    private int n;
    private int e;
    private int d;

    public RSAEncryptor(int p, int q, int e) {
        this.p = p;
        this.q = q;
        this.n = p * q;
        this.e = e;
        this.d = this.calculatePrivateKey();
    }

    private int calculatePrivateKey() {
        int phi = (p - 1) * (q - 1);
        BigInteger eBigInteger = BigInteger.valueOf(e);
        BigInteger phiBigInteger = BigInteger.valueOf(phi);
        BigInteger dBigInteger = eBigInteger.modInverse(phiBigInteger);
        return dBigInteger.intValue();
    }

    public String encrypt(String message) {
        StringBuilder ciphertextBuilder = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            BigInteger m = BigInteger.valueOf((int) c);
            BigInteger encrypted = m.modPow(BigInteger.valueOf(e), BigInteger.valueOf(n));
            ciphertextBuilder.append(encrypted).append(" ");
        }
        return ciphertextBuilder.toString();
    }

    public String decrypt(String ciphertext) {
        StringBuilder plaintextBuilder = new StringBuilder();
        String[] encryptedChars = ciphertext.split(" ");
        for (String encryptedChar : encryptedChars) {
            BigInteger encrypted = new BigInteger(encryptedChar);
            BigInteger decrypted = encrypted.modPow(BigInteger.valueOf(d), BigInteger.valueOf(n));
            char plaintextChar = (char) decrypted.intValue();
            plaintextBuilder.append(plaintextChar);
        }
        return plaintextBuilder.toString();
    }

    public void setP(int p) {
        this.p = p;
    }

    public void setQ(int q) {
        this.q = q;
    }
}
