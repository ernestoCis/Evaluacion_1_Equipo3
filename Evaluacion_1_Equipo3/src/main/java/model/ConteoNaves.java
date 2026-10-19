package model;

public final class ConteoNaves {
    private final int sinDanos;
    private final int danadas;
    private final int hundidas;

    public ConteoNaves(int sinDanos, int danadas, int hundidas) {
        this.sinDanos = sinDanos;
        this.danadas = danadas;
        this.hundidas = hundidas;
    }

    public int getSinDanos() { return sinDanos; }
    public int getDanadas()  { return danadas; }
    public int getHundidas() { return hundidas; }
}
