
public class HashTable2 implements Estrutura {
    class Entrada {
        int chave;
        boolean removida;
        Entrada(int chave) { this.chave = chave; }
    }

    private final double FATOR_DE_CARGA = 0.7;
    private Entrada[] array;
    private int tamanhoAtual;
    private int ocupados; 

    public HashTable2(int capacidadeInicial) {
        array = new Entrada[Math.max(2, capacidadeInicial)];
    }

    private int hash(int chave) {
       
        return Math.floorMod(chave, array.length);
    }

    public void inserir(int chave) {
        if ((double) ocupados / array.length >= FATOR_DE_CARGA) redimensionar();
        int indiceBase = hash(chave);
        int primeiraRemovida = -1;
        for (int colisoes = 0; colisoes < array.length; colisoes++) {
            int indice = (int) (((long) indiceBase + colisoes) % array.length);
            if (array[indice] == null) {
                if (primeiraRemovida != -1) indice = primeiraRemovida;
                else ocupados++;
                array[indice] = new Entrada(chave);
                tamanhoAtual++;
                return;
            }
            if (array[indice].removida) {
                if (primeiraRemovida == -1) primeiraRemovida = indice;
            } else if (array[indice].chave == chave) return;
        }
        if (primeiraRemovida != -1) {
            array[primeiraRemovida] = new Entrada(chave);
            tamanhoAtual++;
        }
    }

    private int buscarIndice(int chave) {
        int indiceBase = hash(chave);
        for (int colisoes = 0; colisoes < array.length; colisoes++) {
            int indice = (int) (((long) indiceBase + colisoes) % array.length);
            if (array[indice] == null) return -1;
            if (!array[indice].removida && array[indice].chave == chave) return indice;
        }
        return -1;
    }

    public boolean buscar(int valor) { return buscarIndice(valor) != -1; }

    public void remover(int valor) {
        int indice = buscarIndice(valor);
        if (indice == -1) return;
        
        array[indice].removida = true;
        tamanhoAtual--;
    }

    private void redimensionar() {
        Entrada[] antigoArray = array;
        array = new Entrada[antigoArray.length * 2];
        tamanhoAtual = 0;
        ocupados = 0;
        for (Entrada entrada : antigoArray) {
            if (entrada != null && !entrada.removida) inserir(entrada.chave);
        }
    }

    public int contarIntervalo(int minimo, int maximo) {
        int quantidade = 0;
        for (int i = 0; i < array.length; i++) {
            Entrada entrada = array[i];
            if (entrada != null && !entrada.removida &&
                entrada.chave >= minimo && entrada.chave <= maximo) quantidade++;
        }
        return quantidade;
    }
}
