package com.example.demo.model;

import java.util.List;

public class ActionData {
    public int damageToPlayer;
    public int healPlayer;
    public int xpGained;
    public List<String> itemsGained;
    public List<String> itemsLost;

    public int getDamageToPlayer() {
        return damageToPlayer;
    }

    public void setDamageToPlayer(int damageToPlayer) {
        this.damageToPlayer = damageToPlayer;
    }

    public int getHealPlayer() {
        return healPlayer;
    }

    public void setHealPlayer(int healPlayer) {
        this.healPlayer = healPlayer;
    }

    public int getXpGained() {
        return xpGained;
    }

    public void setXpGained(int xpGained) {
        this.xpGained = xpGained;
    }

    public List<String> getItemsGained() {
        return itemsGained;
    }

    public void setItemsGained(List<String> itemsGained) {
        this.itemsGained = itemsGained;
    }

    public List<String> getItemsLost() {
        return itemsLost;
    }

    public void setItemsLost(List<String> itemsLost) {
        this.itemsLost = itemsLost;
    }
}
