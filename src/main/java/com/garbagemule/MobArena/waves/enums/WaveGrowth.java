package com.garbagemule.MobArena.waves.enums;

public enum WaveGrowth
{
    OLD(0), SLOW(0.5), MEDIUM(0.65), FAST(0.8), PSYCHO(1.2);
    private final double exp;

    WaveGrowth(double exp) {
        this.exp = exp;
    }

    public int getAmount(int wave, int playerCount) {
        if (this == OLD) return wave + playerCount;

        double base = Math.min(Math.ceil((double) playerCount/2L) + 1, 13);
        return (int) ( base * Math.pow(wave, exp) );
    }
}
