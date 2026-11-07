public class ArvoreBinariaBusca implements Estrutura {
    private No raiz;

    public void inserir(int valor) {
        No novoNo = new No(valor);
        if (raiz == null) { raiz = novoNo; return; }
        No atual = raiz;
        while (true) {
            if (valor == atual.valor) return;
            if (valor < atual.valor) {
                if (atual.esq == null) { atual.esq = novoNo; return; }
                atual = atual.esq;
            } else {
                if (atual.dir == null) { atual.dir = novoNo; return; }
                atual = atual.dir;
            }
        }
    }

    public boolean buscar(int valor) {
        No atual = raiz;
        while (atual != null) {
            if (valor == atual.valor) return true;
            if (valor < atual.valor) atual = atual.esq;
            else atual = atual.dir;
        }
        return false;
    }

    public void remover(int valor) {
        // Iterativo para evitar estouro da pilha em arvores degeneradas.
        No pai = null;
        No atual = raiz;
        while (atual != null && atual.valor != valor) {
            pai = atual;
            if (valor < atual.valor) atual = atual.esq;
            else atual = atual.dir;
        }
        if (atual == null) return;
        if (atual.esq != null && atual.dir != null) {
            No paiSucessor = atual;
            No sucessor = atual.dir;
            while (sucessor.esq != null) {
                paiSucessor = sucessor;
                sucessor = sucessor.esq;
            }
            atual.valor = sucessor.valor;
            pai = paiSucessor;
            atual = sucessor;
        }
        No filho = atual.esq != null ? atual.esq : atual.dir;
        if (pai == null) raiz = filho;
        else if (pai.esq == atual) pai.esq = filho;
        else pai.dir = filho;
    }

    public int contarIntervalo(int minimo, int maximo) {
        if (minimo > maximo) return 0;
        // Percurso em ordem com pilha manual: nao usa recursao na arvore degenerada.
        No[] pilha = new No[64];
        int topo = 0;
        int quantidade = 0;
        No atual = raiz;
        while (atual != null || topo > 0) {
            while (atual != null) {
                if (atual.valor < minimo) {
                    atual = atual.dir; // Descarta a subarvore esquerda.
                } else {
                    if (topo == pilha.length) {
                        No[] novaPilha = new No[pilha.length * 2];
                        for (int i = 0; i < topo; i++) novaPilha[i] = pilha[i];
                        pilha = novaPilha;
                    }
                    pilha[topo++] = atual;
                    atual = atual.esq;
                }
            }
            if (topo == 0) break;
            atual = pilha[--topo];
            pilha[topo] = null;
            if (atual.valor > maximo) break;
            quantidade++;
            atual = atual.dir;
        }
        return quantidade;
    }
}
