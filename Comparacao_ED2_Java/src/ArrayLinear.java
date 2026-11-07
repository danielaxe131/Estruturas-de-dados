public class ArrayLinear implements Estrutura {
    private int[] array;
    private int tamanhoAtual;

    public ArrayLinear(int capacidade) {
        array = new int[Math.max(1, capacidade)];
    }

    public void inserir(int valor) {
        if (buscar(valor)) return; // Duplicatas sao ignoradas dentro da insercao.
        if (tamanhoAtual == array.length) {
            int[] novoArray = new int[array.length * 2];
            for (int i = 0; i < tamanhoAtual; i++) novoArray[i] = array[i];
            array = novoArray;
        }
        array[tamanhoAtual] = valor;
        tamanhoAtual++;
    }

    private int buscarIndice(int valor) {
        for (int i = 0; i < tamanhoAtual; i++) {
            if (array[i] == valor) return i;
        }
        return -1;
    }

    public boolean buscar(int valor) {
        return buscarIndice(valor) != -1;
    }

    public void remover(int valor) {
        int indice = buscarIndice(valor);
        if (indice == -1) return;
        for (int i = indice; i < tamanhoAtual - 1; i++) array[i] = array[i + 1];
        tamanhoAtual--;
    }

    public int contarIntervalo(int minimo, int maximo) {
        int quantidade = 0;
        for (int i = 0; i < tamanhoAtual; i++) {
            if (array[i] >= minimo && array[i] <= maximo) quantidade++;
        }
        return quantidade;
    }
}
