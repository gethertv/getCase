package me.gethertv.getcase.data;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

public class CaseChestData {

    private CaseData caseData;
    private Location location;
    private Location hologram;
    private ArmorStand armorStand;
    private String id;


    public CaseChestData(String id, CaseData caseData, Location location, Location hologram) {
        this.id = id;
        this.caseData = caseData;
        this.location = location;
        this.hologram = hologram;

    }

    public CaseData getCaseData() {
        return caseData;
    }

    public String getId() {
        return id;
    }

    public ArmorStand getArmorStand() {
        return armorStand;
    }


    public void setArmorStand(ArmorStand armorStand) {
        this.armorStand = armorStand;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getHologram() {
        return hologram;
    }

    public void setHologram(Location hologram) {
        this.hologram = hologram;
    }

}
