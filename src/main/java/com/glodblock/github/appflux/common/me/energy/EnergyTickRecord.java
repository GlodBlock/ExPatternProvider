package com.glodblock.github.appflux.common.me.energy;

public class EnergyTickRecord {

    public static final int MIN_RATE = 1;
    public static final int MAX_RATE = 30;
    public static final int THRESHOLD = 5;
    long lastSent = 0;
    int rate = 10;
    long nextTick = 0;

    public void sent(long sent) {
        if (sent == this.lastSent) {
            if (sent != 0) {
                if (this.rate > MIN_RATE) {
                    this.fast();
                }
            } else {
                if (this.rate < MAX_RATE) {
                    this.slow();
                }
            }
        } else if (sent > this.lastSent) {
            if (this.rate > MIN_RATE) {
                this.fast();
            }
        } else {
            if (this.rate < MAX_RATE) {
                this.slow();
            }
        }
        this.lastSent = sent;
    }

    private void fast() {
        if (this.rate > THRESHOLD) {
            this.rate /= 2;
        } else {
            this.rate --;
        }
    }

    private void slow() {
        if (this.rate < THRESHOLD * 2) {
            this.rate *= 2;
        } else {
            this.rate ++;
        }
    }

    public boolean needTick(long current) {
        if (current >= this.nextTick) {
            this.nextTick = current + this.rate;
            return true;
        }
        return false;
    }

}
