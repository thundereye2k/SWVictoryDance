package fun.brou.VictoryDance.Dance;

import fun.brou.VictoryDance.Main;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

public class DragonRider implements Listener {
    boolean dragon = false;

    @EventHandler
    public void onJoin(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Entity kesuyatu = player.getVehicle();
        if (kesuyatu instanceof EnderDragon) {
            kesuyatu.remove();
        }
    }

    @EventHandler
    public void KillEvent(PlayerDeathEvent event) {

        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }
        World w = killer.getWorld();

        if (killer instanceof Player) {
            if(Main.plugin.getConfig().getString(killer.getUniqueId().toString() + ".DANCE").equalsIgnoreCase("DRAGON_RIDER")){
                dragon = true;
                int dancestart = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {

                    public void run() {
                        Location unko = killer.getEyeLocation().toVector().add(killer.getEyeLocation().getDirection().multiply(5)).toLocation(killer.getWorld());
                        Fireball fireball2 = killer.getWorld().spawn(unko, Fireball.class);
                        fireball2.setVelocity(killer.getEyeLocation().getDirection().multiply(5));
                        fireball2.setIsIncendiary(false);
                        fireball2.setShooter(killer);
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {

                            @Override
                            public void run() {
                                fireball2.remove();
                            }
                        }, 11L);
                    }
                }, 20, 7);


                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {

                    @Override
                    public void run() {
                        EnderDragon e = (EnderDragon) killer.getWorld().spawn(killer.getLocation(), EnderDragon.class);
                        e.setPassenger(killer);
                        w.playSound(killer.getLocation(), Sound.ENDERDRAGON_GROWL, 10, 1);
                        Entity enti = killer.getVehicle();

                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {

                            @Override
                            public void run() {
                                enti.remove();
                                Bukkit.getScheduler().cancelTask(dancestart);
                                dragon = false;
                            }
                        }, 210L);
                    }
                }, 10L);
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        Player player = Bukkit.getPlayer("namem");
        if (dragon == true) {
            for (Block b : e.blockList()) {
                final BlockState state = b.getState();

                b.setType(Material.AIR); // Stop item drops from spawning.

                int delay = 220;

                if ((b.getType() == Material.SAND) || (b.getType() == Material.GRAVEL)) {
                    delay += 1;
                }

                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                    public void run() {
                        state.update(true, false);
                    }
                }, delay);
            }
        }else {

        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Entity ent = event.getEntity();

        if (ent instanceof EnderDragon) {
            if (dragon == true) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        World w = p.getWorld();
        if (p.getVehicle() == null) {
        } else {
            Entity ent = p.getVehicle();
            if (ent.getType().equals(EntityType.ENDER_DRAGON)) {
                Vector vec = p.getLocation().getDirection();
                ent.setVelocity(vec.multiply(0.5));
                ((CraftEntity) ent).getHandle().setPositionRotation(ent.getLocation().getX(), ent.getLocation().getY(), ent.getLocation().getZ(), p.getLocation().getYaw() - 180, p.getLocation().getPitch());
            }
        }
    }

    @EventHandler
    public void PlayerTeleportEvent(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Entity enti = player.getVehicle();
        if (player.isSneaking()) {
            if (enti instanceof EnderDragon) {
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                    @Override
                    public void run() {
                        enti.setPassenger(player);
                    }
                }, 1L);
            }
        }if(event.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND) {
            if (enti instanceof EnderDragon) {
                enti.remove();
            }
        }
    }
}
