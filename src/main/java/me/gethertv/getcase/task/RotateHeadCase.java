package me.gethertv.getcase.task;

import me.gethertv.getcase.GetCase;
import me.gethertv.getcase.data.CaseChestData;
import me.gethertv.getcase.data.MoveHead;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class RotateHeadCase extends BukkitRunnable {

    float yaw = -180.0f;

    private HashMap<String, MoveHead> moveHead = new HashMap<>();
    @Override
    public void run() {

        GetCase.getInstance().getDataChest().forEach(caseChestData -> {

            if(caseChestData.getLocation().getChunk().isLoaded())
            {
                moveY(caseChestData);

                ArmorStand armorStand = caseChestData.getArmorStand();
                float currentYaw = armorStand.getLocation().getYaw();
                Location location = armorStand.getLocation().clone();
                if(location.getYaw()>=180)
                {
                    location.setYaw(yaw);
                    armorStand.teleport(location);
                } else {
                    location.setYaw(currentYaw+2.0f);
                    armorStand.teleport(location);
                }
            }

        });
    }

    private void moveY(CaseChestData caseChestData) {
        MoveHead head = moveHead.get(caseChestData.getId());
        if(head==null)
        {
            MoveHead temp = new MoveHead(caseChestData.getArmorStand().getLocation());
            moveHead.put(caseChestData.getId(), temp);
            head=temp;
        }

        Location location = caseChestData.getArmorStand().getLocation().clone();
        if(head.isUp())
        {
            if (location.getY() >= head.getMaxHeight()) {
                head.setUp(false);
                return;
            }
            location.setY(location.getY()+0.01);
            caseChestData.getArmorStand().teleport(location);
            return;
        }
        if(location.getY() <= head.getMinHeight()) {
            head.setUp(true);
            return;
        }
        location.setY(location.getY()-0.01);
        caseChestData.getArmorStand().teleport(location);
    }
}
