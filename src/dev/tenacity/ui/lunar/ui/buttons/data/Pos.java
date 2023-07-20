package dev.tenacity.ui.lunar.ui.buttons.data;

public class Pos {
    private float x, y, tx, ty, txc, tyc;

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getTx() {
        return tx;
    }

    public float getTy() {
        return ty;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setTxc(float tx) {
        this.txc = tx;
    }

    public void setTyc(float ty) {
        this.tyc = ty;
    }

    public void setTx(float tx) {
        this.tx = tx;
    }

    public void setTy(float ty) {
        this.ty = ty;
    }

    public float getTxc() {
        return txc;
    }

    public float getTyc() {
        return tyc;
    }

    public void update() {
        this.x += (tx - x) / 30;
        this.y += (ty - y) / 30;
    }
}
