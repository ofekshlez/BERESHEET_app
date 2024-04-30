package com.example.trying;

import java.math.BigInteger;

public class RSAEncryptor {
    private int n;
    private int e;

    public RSAEncryptor(int n, int e) {
        this.n = n;
        this.e = e;
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

}
