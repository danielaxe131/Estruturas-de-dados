// Criada no estilo dos exemplos; nao foi enviado um arquivo AVL de referencia.
public class ArvoreAVL implements Estrutura {
    private class No {
        int valor;
        int altura = 1;
        No esq, dir;
        No(int valor) { this.valor = valor; }
    }
    private No raiz;

    private int altura(No no) { return no == null ? 0 : no.altura; }
    private int fator(No no) { return no == null ? 0 : altura(no.esq) - altura(no.dir); }
    private void atualizarAltura(No no) {
        no.altura = 1 + Math.max(altura(no.esq), altura(no.dir));
    }

    private No rotacaoDireita(No no) {
        No novaRaiz = no.esq;
        no.esq = novaRaiz.dir;
        novaRaiz.dir = no;
        atualizarAltura(no);
        atualizarAltura(novaRaiz);
        return novaRaiz;
    }

    private No rotacaoEsquerda(No no) {
        No novaRaiz = no.dir;
        no.dir = novaRaiz.esq;
        novaRaiz.esq = no;
        atualizarAltura(no);
        atualizarAltura(novaRaiz);
        return novaRaiz;
    }

    private No balancear(No no) {
        if (no == null) return null;
        atualizarAltura(no);
        if (fator(no) > 1) {
            if (fator(no.esq) < 0) no.esq = rotacaoEsquerda(no.esq);
            return rotacaoDireita(no);
        }
        if (fator(no) < -1) {
            if (fator(no.dir) > 0) no.dir = rotacaoDireita(no.dir);
            return rotacaoEsquerda(no);
        }
        return no;
    }

    public void inserir(int valor) { raiz = inserir(raiz, valor); }
    private No inserir(No no, int valor) {
        if (no == null) return new No(valor);
        if (valor < no.valor) no.esq = inserir(no.esq, valor);
        else if (valor > no.valor) no.dir = inserir(no.dir, valor);
        else return no;
        return balancear(no);
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

    public void remover(int valor) { raiz = remover(raiz, valor); }
    private No remover(No no, int valor) {
        if (no == null) return null;
        if (valor < no.valor) no.esq = remover(no.esq, valor);
        else if (valor > no.valor) no.dir = remover(no.dir, valor);
        else {
            if (no.esq == null) return no.dir;
            if (no.dir == null) return no.esq;
            No sucessor = no.dir;
            while (sucessor.esq != null) sucessor = sucessor.esq;
            no.valor = sucessor.valor;
            no.dir = remover(no.dir, sucessor.valor);
        }
        return balancear(no);
    }

    public int contarIntervalo(int minimo, int maximo) {
        if (minimo > maximo) return 0;
        return contarIntervalo(raiz, minimo, maximo);
    }

    private int contarIntervalo(No no, int minimo, int maximo) {
        if (no == null) return 0;
        if (no.valor < minimo) return contarIntervalo(no.dir, minimo, maximo);
        if (no.valor > maximo) return contarIntervalo(no.esq, minimo, maximo);
        return 1 + contarIntervalo(no.esq, minimo, maximo)
                 + contarIntervalo(no.dir, minimo, maximo);
    }
}
