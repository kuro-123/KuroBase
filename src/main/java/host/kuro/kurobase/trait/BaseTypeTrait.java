package host.kuro.kurobase.trait;

import host.kuro.kurobase.KuroBase;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.metadata.FixedMetadataValue;

public class BaseTypeTrait extends Trait {

    private final static String DATA_KEY = "NPCTYPE";

    @Persist
    private String type = "";

    public BaseTypeTrait() {
        super("BaseTypeTrait");
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    @Override
    public void onSpawn() {
        npc.getEntity().setCustomName(type);
        npc.getEntity().setCustomNameVisible(false);
        npc.getEntity().setMetadata(DATA_KEY, new FixedMetadataValue(KuroBase.GetInstance(), type));
    }
}