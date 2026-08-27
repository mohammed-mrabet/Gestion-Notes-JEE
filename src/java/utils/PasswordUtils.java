package utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {
    
    private static final int BCRYPT_ROUNDS = 12;
    
    public static String hashPassword(String password) {
        System.out.println("🔐 [PasswordUtils] Hashage du mot de passe");
        String hash = BCrypt.hashpw(password, BCrypt.gensalt(BCRYPT_ROUNDS));
        System.out.println("   Hash généré: " + hash);
        return hash;
    }
    
    public static boolean verifyPassword(String password, String hashedPassword) {
        System.out.println("🔐 [PasswordUtils] Vérification du mot de passe");
        System.out.println("   Mot de passe fourni: " + password);
        System.out.println("   Hash en BD: " + hashedPassword);
        
        if (password == null || hashedPassword == null) {
            System.out.println("   ❌ Null detected");
            return false;
        }
        
        try {
            boolean result = BCrypt.checkpw(password, hashedPassword);
            System.out.println("   Résultat: " + (result ? "✅ OK" : "❌ ÉCHEC"));
            return result;
        } catch (Exception e) {
            System.err.println("   ❌ Exception: " + e.getMessage());
            return false;
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== GÉNÉRATION DES HASH ===");
        String[] logins = {"admin", "k.alami", "y.cherkaoui"};
        String[] passwords = {"admin123", "prof123", "etudiant123"};
        
        for (int i = 0; i < passwords.length; i++) {
            String hash = hashPassword(passwords[i]);
            System.out.println("\nCompte: " + logins[i]);
            System.out.println("Hash: " + hash);
            boolean verify = verifyPassword(passwords[i], hash);
            System.out.println("Vérification: " + (verify ? "✅ OK" : "❌ ÉCHEC"));
        }
    }
}