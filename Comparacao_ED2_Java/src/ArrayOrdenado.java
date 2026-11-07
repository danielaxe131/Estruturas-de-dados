public class ArrayOrdenado implements Estrutura {
    private int[] array;
    private int tamanhoAtual;

    public ArrayOrdenado(int capacidade) {
        array = new int[Math.max(1, capacidade)];
    }

    public void inserir(int valor) {
        if (buscar(valor)) return; // Duplicatas sao ignoradas dentro da insercao.
        if (tamanhoAtual == array.length) {
            int[] novoArray = new int[array.length * 2];
            for (int i = 0; i < tamanhoAtual; i++) novoArray[i] = array[i];
            array = novoArray;
        }
        int i = tamanhoAtual - 1;
        while (i >= 0 && array[i] > valor) {
            array[i + 1] = array[i];
            i--;
        }
        array[i + 1] = valor;
        tamanhoAtual++;
    }

    private int buscarIndice(int valor) {
        int inicio = 0;
        int fim = tamanhoAtual - 1;
        while (inicio <= fim) {
            int meio = inicio + (fim - inicio) / 2;
            if (array[meio] == valor) return meio;
            if (valor < array[meio]) fim = meio - 1;
            else inicio = meio + 1;
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
        if (minimo > maximo) return 0;
        // Primeiro indice com valor >= minimo.
        int inicio = 0, fim = tamanhoAtual;
        while (inicio < fim) {
            int meio = inicio + (fim - inicio) / 2;
            if (array[meio] < minimo) inicio = meio + 1;
            else fim = meio;
        }
        int primeiro = inicio;
        // Primeiro indice com valor > maximo (sem somar 1 ao limite).
        inicio = 0;
        fim = tamanhoAtual;
        while (inicio < fim) {
            int meio = inicio + (fim - inicio) / 2;
            if (array[meio] <= maximo) inicio = meio + 1;
            else fim = meio;
        }
        return inicio - primeiro;
    }
}
