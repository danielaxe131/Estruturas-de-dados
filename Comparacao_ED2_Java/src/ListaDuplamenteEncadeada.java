public class ListaDuplamenteEncadeada implements Estrutura {
    private Nodo inicio;
    private Nodo fim;

    public boolean vazia() { return inicio == null; }

    public void inserir(int valor) { inserirFinal(valor); }

    public void inserirFinal(int valor) {
        if (buscar(valor)) return; // A verificacao tambem faz parte do tempo medido.
        Nodo novoNodo = new Nodo(valor);
        novoNodo.setAnterior(fim);
        if (vazia()) inicio = novoNodo;
        else fim.setProx(novoNodo);
        fim = novoNodo;
    }

    public boolean buscar(int valor) {
        Nodo aux = inicio;
        while (aux != null) {
            if (aux.getDado() == valor) return true;
            aux = aux.getProx();
        }
        return false;
    }

    public void remover(int valor) { removerValor(valor); }

    public void removerValor(int valor) {
        Nodo aux = inicio;
        while (aux != null && aux.getDado() != valor) aux = aux.getProx();
        if (aux == null) return;
        if (aux.getAnterior() == null) inicio = aux.getProx();
        else aux.getAnterior().setProx(aux.getProx());
        if (aux.getProx() == null) fim = aux.getAnterior();
        else aux.getProx().setAnterior(aux.getAnterior());
    }

    public int contarIntervalo(int minimo, int maximo) {
        int quantidade = 0;
        Nodo aux = inicio;
        while (aux != null) {
            if (aux.getDado() >= minimo && aux.getDado() <= maximo) quantidade++;
            aux = aux.getProx();
        }
        return quantidade;
    }
}
