package host.kuro.kurobase.database;

import java.util.*;

public class SkinData {

    UUID uuid;
    String base64;
    String signedBase64;

    public SkinData(UUID uuid, String base64, String signedBase64) {
        this.uuid = uuid;
        this.base64 = base64;
        this.signedBase64 = signedBase64;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getBase64() {
        return base64;
    }

    public String getSignedBase64() {
        return signedBase64;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof SkinData) {
            SkinData s2 = (SkinData) obj;
            return s2.getUUID().equals(uuid) && s2.getBase64().equals(base64) && s2.getSignedBase64().equals(signedBase64);
        }
        return false;
    }

    public static SkinData deserialize(Map<String, Object> args) {
        if(args.containsKey("uuid") && args.containsKey("base64") && args.containsKey("signedBase64")) {
            UUID uuid;
            try {
                uuid = UUID.fromString(args.get("uuid").toString());
            } catch(IllegalArgumentException ex) {
                return null;
            }
            return new SkinData(uuid, args.get("base64").toString(), args.get("signedBase64").toString());
        }
        return null;
    }
}
