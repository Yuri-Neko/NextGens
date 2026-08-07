package com.muhammaddaffa.nextgens.utils;

import com.muhammaddaffa.nextgens.sellwand.models.SellwandData;
import com.muhammaddaffa.nextgens.users.models.User;

public class SellData {

    private final User user;
    private final double totalValue;
    private final int totalItems;
    private double multiplier;
    private final SellwandData sellwand;
    private final double totalExp;

    public SellData(User user, double totalValue, int totalItems, double multiplier, SellwandData sellwand) {
        this(user, totalValue, totalItems, multiplier, sellwand, 0);
    }

    public SellData(User user, double totalValue, int totalItems, double multiplier, SellwandData sellwand, double totalExp) {
        this.user = user;
        this.totalValue = totalValue;
        this.totalItems = totalItems;
        this.multiplier = multiplier;
        this.sellwand = sellwand;
        this.totalExp = totalExp;
    }

    // Getters and setters

    public User getUser() {
        return user;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public SellwandData getSellwand() {
        return sellwand;
    }

    /**
     * Total EXP (NextGensLevels' generators.yml "exp" field on each drop, summed across
     * every item in this sale) - 0 kalau item yang dijual tidak ada exp-nya sama sekali,
     * atau NextGensLevels tidak terpasang.
     */
    public double getTotalExp() {
        return totalExp;
    }

}
