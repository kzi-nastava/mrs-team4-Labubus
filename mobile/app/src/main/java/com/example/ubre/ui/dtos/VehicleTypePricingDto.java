package com.example.ubre.ui.dtos;

public class VehicleTypePricingDto {
    private double pricePerKm;
    private double standardBasePrice;
    private double luxuryBasePrice;
    private double vanBasePrice;

    public double getPricePerKm() { return pricePerKm; }
    public void setPricePerKm(double pricePerKm) { this.pricePerKm = pricePerKm; }

    public double getStandardBasePrice() { return standardBasePrice; }
    public void setStandardBasePrice(double standardBasePrice) { this.standardBasePrice = standardBasePrice; }

    public double getLuxuryBasePrice() { return luxuryBasePrice; }
    public void setLuxuryBasePrice(double luxuryBasePrice) { this.luxuryBasePrice = luxuryBasePrice; }

    public double getVanBasePrice() { return vanBasePrice; }
    public void setVanBasePrice(double vanBasePrice) { this.vanBasePrice = vanBasePrice; }
}
