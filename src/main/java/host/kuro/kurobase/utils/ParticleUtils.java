package host.kuro.kurobase.utils;

import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ParticleUtils {

    public static final void CenterParticle(Player player, Particle particle, int count, int range) {
        if (player == null) return;
        World world = player.getWorld();

        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();
        Vector center = new Vector(x, y, z);
        double yaw = 0;
        y = center.getY();
        for(;y < (center.getY() + range);){
            x = (-Math.sin(yaw) + center.getX());
            z = (Math.cos(yaw) + center.getZ());
            world.spawnParticle(particle, x, y, z, count, 0, 0, 0);
            yaw += ((Math.PI * 2) / 20);
            y += 0.05;
        }
    }

    public static final void CrownParticle(Player player, Particle particle, int count) {
        if (player == null) return;
        World world = player.getWorld();
        double x1 = player.getLocation().getX();
        double y1 = player.getLocation().getY();
        double z1 = player.getLocation().getZ();
        Vector center = new Vector(x1, y1, z1);
        double time = 1;
        double pi = 3.14159;
        time=time+0.1/pi;
        for(double i=0; i<=(2*pi); i+=(pi/8)) {
            double x = time*Math.cos(i);
            double y = Math.exp(-0.1*time)*Math.sin(time)+1.5;
            double z = time*Math.sin(i);
            world.spawnParticle(particle, center.getX()+x, center.getY()+y, center.getZ()+z, count, 0, 0, 0);
        }
    }

    public static final void PartyParticle(Player player, Particle particle, int count) {
        if (player == null) return;
        World world = player.getWorld();
        double x1 = player.getLocation().getX();
        double y1 = player.getLocation().getY();
        double z1 = player.getLocation().getZ();
        Vector center = new Vector(x1, y1, z1);
        double pi = 3.14159;
        for(double i=0; i<=16; i+=pi/22){
            double radio = Math.sin(i);
            double y = Math.cos(i);
            for(double a=0; a<pi*4; a+=pi/8){
                double x = Math.cos(a)*radio;
                double z = Math.sin(a)*radio;
                world.spawnParticle(particle, center.getX()+x, center.getY()+y, center.getZ()+z, count, 0, 0, 0);
            }
        }

    }
}
/*
switch (seq) {
    case 0: ParticleUtils.CenterParticle(player, Particle.LAVA); break;
    case 1: ParticleUtils.CenterParticle(player, Particle.LANDING_LAVA); break;
    case 2: ParticleUtils.CenterParticle(player, Particle.FALLING_LAVA); break;
    case 3: ParticleUtils.CenterParticle(player, Particle.DRIP_LAVA); break;
    case 4: ParticleUtils.CenterParticle(player, Particle.BARRIER); break;
    case 5: ParticleUtils.CenterParticle(player, Particle.BLOCK_CRACK); break;
    case 6: ParticleUtils.CenterParticle(player, Particle.BLOCK_DUST); break;
    case 7: ParticleUtils.CenterParticle(player, Particle.BUBBLE_COLUMN_UP); break;
    case 8: ParticleUtils.CenterParticle(player, Particle.BUBBLE_POP); break;
    case 9: ParticleUtils.CenterParticle(player, Particle.CAMPFIRE_COSY_SMOKE); break;
    case 10: ParticleUtils.CenterParticle(player, Particle.CAMPFIRE_SIGNAL_SMOKE); break;
    case 11: ParticleUtils.CenterParticle(player, Particle.CLOUD); break;
    case 12: ParticleUtils.CenterParticle(player, Particle.COMPOSTER); break;
    case 13: ParticleUtils.CenterParticle(player, Particle.CRIT); break;
    case 14: ParticleUtils.CenterParticle(player, Particle.CRIT_MAGIC); break;
    case 15: ParticleUtils.CenterParticle(player, Particle.CURRENT_DOWN); break;
    case 16: ParticleUtils.CenterParticle(player, Particle.DAMAGE_INDICATOR); break;
    case 17: ParticleUtils.CenterParticle(player, Particle.DOLPHIN); break;
    case 18: ParticleUtils.CenterParticle(player, Particle.DRAGON_BREATH); break;
    case 19: ParticleUtils.CenterParticle(player, Particle.DRIP_WATER); break;
    case 20: ParticleUtils.CenterParticle(player, Particle.DRIPPING_HONEY); break;
    case 21: ParticleUtils.CenterParticle(player, Particle.ENCHANTMENT_TABLE); break;
    case 22: ParticleUtils.CenterParticle(player, Particle.END_ROD); break;
    case 23: ParticleUtils.CenterParticle(player, Particle.EXPLOSION_HUGE); break;
    case 24: ParticleUtils.CenterParticle(player, Particle.EXPLOSION_LARGE); break;
    case 25: ParticleUtils.CenterParticle(player, Particle.EXPLOSION_NORMAL); break;
    case 26: ParticleUtils.CenterParticle(player, Particle.FALLING_DUST); break;
    case 27: ParticleUtils.CenterParticle(player, Particle.FALLING_HONEY); break;
    case 28: ParticleUtils.CenterParticle(player, Particle.FALLING_NECTAR); break;
    case 29: ParticleUtils.CenterParticle(player, Particle.FALLING_WATER); break;
    case 30: ParticleUtils.CenterParticle(player, Particle.HEART); break;
    case 31: ParticleUtils.CenterParticle(player, Particle.ITEM_CRACK); break;
    case 32: ParticleUtils.CenterParticle(player, Particle.LANDING_HONEY); break;
    case 33: ParticleUtils.CenterParticle(player, Particle.LEGACY_BLOCK_CRACK); break;
    case 34: ParticleUtils.CenterParticle(player, Particle.LEGACY_BLOCK_DUST); break;
    case 35: ParticleUtils.CenterParticle(player, Particle.LEGACY_FALLING_DUST); break;
    case 36: ParticleUtils.CenterParticle(player, Particle.MOB_APPEARANCE); break;
    case 37: ParticleUtils.CenterParticle(player, Particle.NAUTILUS); break;
    case 38: ParticleUtils.CenterParticle(player, Particle.NOTE); break;
    case 39: ParticleUtils.CenterParticle(player, Particle.PORTAL); break;
    case 40: ParticleUtils.CenterParticle(player, Particle.REDSTONE); break;
    case 41: ParticleUtils.CenterParticle(player, Particle.SLIME); break;
    case 42: ParticleUtils.CenterParticle(player, Particle.SMOKE_LARGE); break;
    case 43: ParticleUtils.CenterParticle(player, Particle.SMOKE_NORMAL); break;
    case 44: ParticleUtils.CenterParticle(player, Particle.SNEEZE); break;
    case 45: ParticleUtils.CenterParticle(player, Particle.SNOW_SHOVEL); break;
    case 46: ParticleUtils.CenterParticle(player, Particle.SNOWBALL); break;
    case 47: ParticleUtils.CenterParticle(player, Particle.SPELL); break;
    case 48: ParticleUtils.CenterParticle(player, Particle.SPELL_INSTANT); break;
    case 49: ParticleUtils.CenterParticle(player, Particle.SPELL_MOB); break;
    case 50: ParticleUtils.CenterParticle(player, Particle.SPELL_MOB_AMBIENT); break;
    case 51: ParticleUtils.CenterParticle(player, Particle.SPELL_WITCH); break;
    case 52: ParticleUtils.CenterParticle(player, Particle.SUSPENDED); break;
    case 53: ParticleUtils.CenterParticle(player, Particle.SUSPENDED_DEPTH); break;
    case 54: ParticleUtils.CenterParticle(player, Particle.SWEEP_ATTACK); break;
    case 55: ParticleUtils.CenterParticle(player, Particle.TOTEM); break;
    case 56: ParticleUtils.CenterParticle(player, Particle.TOWN_AURA); break;
    case 57: ParticleUtils.CenterParticle(player, Particle.VILLAGER_ANGRY); break;
    case 58: ParticleUtils.CenterParticle(player, Particle.VILLAGER_HAPPY); break;
    case 59: ParticleUtils.CenterParticle(player, Particle.WATER_BUBBLE); break;
    case 60: ParticleUtils.CenterParticle(player, Particle.WATER_DROP); break;
    case 61: ParticleUtils.CenterParticle(player, Particle.WATER_SPLASH); break;
    case 62: ParticleUtils.CenterParticle(player, Particle.WATER_WAKE); break;
}
 */