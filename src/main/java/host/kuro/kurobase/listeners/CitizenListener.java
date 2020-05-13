package host.kuro.kurobase.listeners;

import host.kuro.kurobase.npc.KuroTrait;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import net.citizensnpcs.api.event.NPCRemoveTraitEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class CitizenListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCRemoved(NPCRemoveEvent event) {
        NPC npc = event.getNPC();
        if (npc.hasTrait(KuroTrait.class)) {
            KuroTrait kurotrait = npc.getTrait(KuroTrait.class);
            //kurotrait.onTraitDeletion();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onTraitRemoved(NPCRemoveTraitEvent event) {
        if (event.getTrait() instanceof KuroTrait) {
            KuroTrait kurotrait = (KuroTrait) event.getTrait();
            //kurotrait.onTraitDeletion();
        }
    }
}
