public class Nodo {
    private int dado;
    private Nodo prox;
    private Nodo anterior;

    public Nodo(int dado) {
        this.dado = dado;
        this.prox = null;
        this.anterior = null;
    }
    public int getDado() { return dado; }
    public void setDado(int dado) { this.dado = dado; }
    public Nodo getProx() { return prox; }
    public void setProx(Nodo prox) { this.prox = prox; }
    public Nodo getAnterior() { return anterior; }
    public void setAnterior(Nodo anterior) { this.anterior = anterior; }
}
