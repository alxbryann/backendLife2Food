package life2food.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class PaymentService {

    @Value("${epayco.cust-id}")
    private String custId;

    @Value("${epayco.public-key}")
    private String publicKey;

    @Value("${epayco.private-key}")
    private String privateKey;

    public String getPublicKey() {
        return publicKey;
    }

    public String getCustId() {
        return custId;
    }

    public String generateSignature(String invoiceId, String amount, String currency) {
        // Signature structure: p_cust_id_cliente + '^' + p_key + '^' + p_id_invoice +
        // '^' + p_amount + '^' + p_currency
        String data = custId + "^" + privateKey + "^" + invoiceId + "^" + amount + "^" + currency;
        return sha256(data);
    }

    private String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
