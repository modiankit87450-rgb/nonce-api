package com.example.nonceapi;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class DecryptTest {

    public static void main(String[] args) throws Exception {

        //STEP 1: POSTMAN RESPONSE

        String encryptedNonce = "oaIvli/YMJe5q+99TidzrxDEp8SuOlcoxcdM+XIyDDBH5crhzC0dvZMrK5Ntz1Pfkz2YkoqcC6cC9x7bwTO0/8n3wYtNP6C4L/Ud2XakfKo=";

        String encryptedKey = "fj1QO2jLh8scYVNG6fWuzX6sOXXl/9BwViuRGsUacST50YdzW8m9mxiUEVVcHd3ioNgeuqlsrdB5ftzFzC74qjEN8GIg2sQqmeY0yWCtK18Uh3Eo36FBW9R9bAIYzFAYc65mj9rW02ms1tJDfdik83NoEbLfHE1E5H65MEggxgVNpbFheg1tUj0IqaOD0WR+tvgipV9PWy4vke8kpeL+bQ+anDIi5Z9EcSMbhUmZ43DG/8SWoZ+5IkhkrwWBYNrwt6aUhWNoMT5rFG/72dm+bEPhOBe9GvVpkNQviZput9CuLQAnYk9z6fhPNZRzkD+NlOuuD2X1R8rXhX3l1K+U3Q==";

        // STEP 2: PASTE YOUR PRIVATE KEY

        String privateKeyStr = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCisEOXU7DDSU9oB8YIuu0kYHdXMLo2Poez6ihYmbRlLy7TmswVSd9oH0+kRL1qSh3Ab8CiCUu2hw/2MUh+Ezg8fDm5h9cewwYn1wk1Ao55jr6U8MuLf42CoY+OML58BrF7I0JeYNoVnS5bk2ru8pugIRFtnBWZBeOLnVq+ue6L4BCt2fOKnaAPpAwJccuOuN6VhpkH5i+qT3b+hlDw1zpDYPd/aHLbg2GeN2ono9gNMwPiJCzL58LOP8q3SJRzXkgzt6wbUFarq+yhbQHUGFxbMuDpp0m3SaHY6EBfM65g8EbG3P67Kmzf7AeRi0v/GkYZsssA7dUpFCuxruKy4gcNAgMBAAECggEAJUsAg3zkxS9V64P0qmWnHLKcSLlbxjl5RA3GJ+E8EAHL2yfd5ndjpeufhwlW3jmVLlqvrrUwzOuTBh1v/66KMptLZDM2Zc/Q5msNqkAmxsio+V1lW1nm523/iVm1IJssLgz8FsRe2ZuJV+aoGZQ1hnRuGVExRj2qVoPAnH3kp91cfRkNPeRgL11Z2vnNvXm1uEyqH8NjW8lhxHR3wSygBXcvqiDLFfRdfEwaco7Z7F9T81N+le86qYU3c2d9pZDyuU9B6nXC5FkWi5ef44z9ZW+IUjRa4kx3ATXVNeHfERrh7Q4rxzoHqGDkTm5ueXi7VpIeGXTeXA5RXmx2r2VUJQKBgQDG02mCSamibjk41KFEce9mW8kvbXHVajutWl0PdyDXR4exchc4872YmZDxn2D5gwBUpZGcL3jLTDqie2myeMYPx4ajKIBcYgeX1+BAitqpFZf8aZSurP43P8A2xlKFY2XXpfK79UIzIRPjJ1D+Gy+/YgeIAC2PzeBvoUEqkjFvMwKBgQDReJmsueV0L5yHTVvWygwpuDmqNrl74qYAyYbsjS7tlwiKuFQNIItCsX/cf3I3fNV7/AypbovkOOa5Jy82ATrz3TjEfALjiWQSugGGj7kGB9E40kPEHSKvMSTK6O9A5g9Mc79bCDK2M96OCzJ3m3BvHHS81T9CmNN/3UaXoj+wvwKBgH4jniqEZVShrTf63h04U3OTA75NJw0IirfePnATWgh6XwIaqdT9/ekUdDp2rOVfURRivlBiZbZEtGZk+Ze6u51DLo3Qoi76GmshECqibTsccgI3UZbINbgDgATAKZZqizAXHjpUzRz2/VD9noq34MEAhlqoEj/Lk3n+ygMiIK61AoGBAMeuWYBIba1GPcbVPbkRPmr+zSmvpT2tn2WcttwE8jlXArOKZ8Vjwp0/K438h9rRS4k3irJxSTWz8G2MJvsqTRJvTlAvj2lsbVNUsWOe2lgfV5j9B9CJnai4BkTPrFOHfAn7ROz4ca8y3vM1RVSzfojM7ZOdDQLlQKjhPQwLhP9tAoGBAIdg6fK5hpKuLL22LJOwvmQ4b3A1ussauhx3bR8oGmquw9XzvoqKb1BmxrEPGXSZBCGAMoxpO6PbpgJYVRuN9ZSZDRhsitcxL4oFD4FpVSy/HLebr8weg0jr3/hE1zfKKlJoAEl0TnfyMa0m2aQz6IM0aMFXDgeWJSqgzDfPcrvJ";

        // STEP 3: RSA se AES key decrypt karo

        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        Cipher rsaCipher = Cipher.getInstance("RSA");
        rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);

        // STEP 1: Decrypt with RSA
        byte[] decryptedKey = rsaCipher.doFinal(
                Base64.getDecoder().decode(encryptedKey)
        );

        // STEP 2: Base64 string ko actual AES key me convert
        byte[] aesKeyBytes = Base64.getDecoder().decode(new String(decryptedKey));

        // STEP 3: AES se NONCE decrypt

        SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");

        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, aesKey);

        byte[] decrypted = aesCipher.doFinal(
                Base64.getDecoder().decode(encryptedNonce)
        );

        String nonce = new String(decrypted);

        // FINAL OUTPUT

        System.out.println("DECRYPTED NONCE:");
        System.out.println(nonce);
    }
}