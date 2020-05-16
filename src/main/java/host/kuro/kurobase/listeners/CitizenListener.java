package host.kuro.kurobase.listeners;

import host.kuro.kurobase.npc.KuroTrait;
import net.citizensnpcs.api.ai.speech.event.NPCSpeechEvent;
import net.citizensnpcs.api.event.*;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class CitizenListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCRemoved(NPCRemoveEvent event) {
        NPC npc = event.getNPC();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onTraitRemoved(NPCRemoveTraitEvent event) {
        NPC npc = event.getNPC();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCClick(NPCRightClickEvent event) {
        NPC npc = event.getNPC();
        Player player = event.getClicker();
        npc.getNavigator().setTarget(player, false);
        player.sendMessage("[opnopai3] あーたに、ついてくわよ！");
    }

/*
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onEntityTargetNPC(EntityTargetNPCEvent event) {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCClick(NPCClickEvent event) {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCClick(NPCCollisionEvent event) {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCClick(NPCCombustByBlockEvent event) {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCClick(NPCCombustByEntityEvent event) {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCClick(NPCCreateEvent event) {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCClick(NPCDamageByBlockEvent event) {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCClick(NPCDamageByEntityEvent event) {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCClick(NPCDamageEntityEvent event) {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCClick(NPCDeathEvent event) {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCClick(NPCDespawnEvent event) {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCClick(NPCOpenDoorEvent event) {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCClick(NPCPushEvent event) {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCClick(NPCRightClickEvent event) {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCClick(NPCSpeechEvent event) {
    }
*/
}
