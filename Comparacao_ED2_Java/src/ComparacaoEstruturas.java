import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public class ComparacaoEstruturas {
    private static final long SEMENTE = 42;
    private static final int REPETICOES = 5;
    private static final String[] NOMES = {
        "Array linear", "Array ordenado", "Lista dupla", "Tabela hash",
        "Arvore de busca", "Arvore AVL", "Arvore rubro-negra"
    };
    private static final String[] PASTAS = {
        "ArrayLinear", "ArrayOrdenado", "ListaDuplamenteEncadeada", "HashTable2",
        "ArvoreBinariaBusca", "ArvoreAVL", "ArvoreRubroNegra"
    };
    private static final String[] CENARIOS = {"Crescente", "Decrescente", "Aleatorio"};
    private static final String[] TESTES = {
        "Construcao", "Busca presente", "Busca ausente", "Insercao", "Remocao", "Intervalo"
    };

  
    private static class DadosTeste {
        int[] iniciais;
        int[] buscasPresentes;
        int[] buscasAusentes;
        int[] insercoes;
        int[] remocoes;
        int[] minimos;
        int[] maximos;
    }

    public static Estrutura criarEstrutura(int tipo, int capacidade) {
        switch (tipo) {
            case 0: return new ArrayLinear(capacidade + capacidade / 10);
            case 1: return new ArrayOrdenado(capacidade + capacidade / 10);
            case 2: return new ListaDuplamenteEncadeada();
            case 3: return new HashTable2(16);
            case 4: return new ArvoreBinariaBusca();
            case 5: return new ArvoreAVL();
            case 6: return new ArvoreRubroNegra();
            default: throw new IllegalArgumentException("Estrutura invalida");
        }
    }

    private static void embaralhar(int[] valores, Random random) {
        for (int i = valores.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int aux = valores[i];
            valores[i] = valores[j];
            valores[j] = aux;
        }
    }

    private static DadosTeste prepararDados(int n, int cenario) {
        DadosTeste dados = new DadosTeste();
        dados.iniciais = new int[n];
        for (int i = 0; i < n; i++) dados.iniciais[i] = 2 * (i + 1);
        if (cenario == 1) {
            for (int i = 0; i < n / 2; i++) {
                int aux = dados.iniciais[i];
                dados.iniciais[i] = dados.iniciais[n - 1 - i];
                dados.iniciais[n - 1 - i] = aux;
            }
        } else if (cenario == 2) {
            embaralhar(dados.iniciais, new Random(SEMENTE));
        }

        // Sorteios feitos uma unica vez antes de aquecer ou medir.
        // Reiniciar com 42 tambem permite repetir os mesmos lotes nos Mains separados.
        Random random = new Random(SEMENTE);
        dados.buscasPresentes = new int[2000];
        dados.buscasAusentes = new int[2000];
        for (int i = 0; i < 2000; i++) dados.buscasPresentes[i] = 2 * (1 + random.nextInt(n));
        for (int i = 0; i < 2000; i++) dados.buscasAusentes[i] = 2 * random.nextInt(n) + 1;

        // Embaralhar um conjunto sem repeticoes permite sortear valores distintos.
        int[] impares = new int[n];
        int[] pares = new int[n];
        for (int i = 0; i < n; i++) {
            impares[i] = 2 * i + 1;
            pares[i] = 2 * (i + 1);
        }
        embaralhar(impares, random);
        embaralhar(pares, random);
        dados.insercoes = Arrays.copyOf(impares, n / 10);
        dados.remocoes = Arrays.copyOf(pares, n / 10);

        dados.minimos = new int[200];
        dados.maximos = new int[200];
        for (int i = 0; i < 200; i++) {
            dados.minimos[i] = 1 + random.nextInt(2 * n - 100);
            dados.maximos[i] = dados.minimos[i] + 100;
        }
        return dados;
    }

    private static void preencher(Estrutura estrutura, int[] iniciais) {
        for (int valor : iniciais) estrutura.inserir(valor);
    }

    private static long executarTeste(int tipo, int teste, DadosTeste dados, boolean medir) {
        int n = dados.iniciais.length;
        Estrutura estrutura = null;
        // Cada chamada usa estado novo. Nas cinco operacoes, a reconstrucao e externa ao tempo.
        if (teste != 0) {
            estrutura = criarEstrutura(tipo, n);
            preencher(estrutura, dados.iniciais);
            conferir(estrutura.contarIntervalo(Integer.MIN_VALUE, Integer.MAX_VALUE) == n,
                     "Estado inicial deve conter exatamente n valores distintos");
        }
        int encontrados = 0;
        int[] contagens = teste == 5 ? new int[200] : null;

        long inicio = medir ? System.nanoTime() : 0;
        switch (teste) {
            case 0:
                // Construcao inclui a criacao da estrutura e todas as insercoes.
                estrutura = criarEstrutura(tipo, n);
                preencher(estrutura, dados.iniciais);
                break;
            case 1:
                for (int valor : dados.buscasPresentes) if (estrutura.buscar(valor)) encontrados++;
                break;
            case 2:
                for (int valor : dados.buscasAusentes) if (estrutura.buscar(valor)) encontrados++;
                break;
            case 3:
                for (int valor : dados.insercoes) estrutura.inserir(valor);
                break;
            case 4:
                // remover ja inclui localizar o valor; nao recebe um no pronto.
                for (int valor : dados.remocoes) estrutura.remover(valor);
                break;
            case 5:
                for (int i = 0; i < 200; i++) {
                    contagens[i] = estrutura.contarIntervalo(dados.minimos[i], dados.maximos[i]);
                }
                break;
            default:
                throw new IllegalArgumentException("Teste invalido");
        }
        long tempo = medir ? System.nanoTime() - inicio : 0;

        // Conferencias ocorrem somente depois do fim do lote.
        int tamanhoEsperado = n;
        if (teste == 3) tamanhoEsperado += dados.insercoes.length;
        if (teste == 4) tamanhoEsperado -= dados.remocoes.length;
        int tamanhoFinal = estrutura.contarIntervalo(Integer.MIN_VALUE, Integer.MAX_VALUE);
        conferir(tamanhoFinal == tamanhoEsperado, "Quantidade de elementos incorreta");
        int somaIntervalos = 0;
        if (teste == 0) {
            conferir(estrutura.buscar(2) && estrutura.buscar(2 * n), "Construcao incorreta");
        } else if (teste == 1) {
            conferir(encontrados == 2000, "Busca presente incorreta");
        } else if (teste == 2) {
            conferir(encontrados == 0, "Busca ausente incorreta");
        } else if (teste == 3) {
            for (int valor : dados.insercoes) conferir(estrutura.buscar(valor), "Insercao incorreta");
        } else if (teste == 4) {
            for (int valor : dados.remocoes) conferir(!estrutura.buscar(valor), "Remocao incorreta");
        } else if (teste == 5) {
            for (int i = 0; i < 200; i++) {
                // Quantidade de pares em [minimo, maximo], com limites inclusivos.
                int esperado = dados.maximos[i] / 2 - (dados.minimos[i] - 1) / 2;
                conferir(contagens[i] == esperado, "Consulta por intervalo incorreta");
                somaIntervalos += contagens[i];
            }
        }
        System.out.printf("  Conferencia OK: %d elementos (esperado %d)", tamanhoFinal, tamanhoEsperado);
        if (teste == 1 || teste == 2) {
            System.out.printf("; buscas bem-sucedidas = %d/2000", encontrados);
        }
        if (teste == 5) {
            System.out.printf("; 200 intervalos conferidos; soma das contagens = %d", somaIntervalos);
        }
        if (teste == 3) System.out.printf("; %d novos valores conferidos", dados.insercoes.length);
        if (teste == 4) System.out.printf("; %d remocoes conferidas", dados.remocoes.length);
        System.out.println(); // Todas as conferencias e impressoes sao posteriores ao cronometro.
        return tempo;
    }

    private static void conferir(boolean condicao, String mensagem) {
        if (!condicao) throw new IllegalStateException(mensagem);
    }

    private static int quantidadeOperacoes(int teste, int n) {
        if (teste == 0) return n;
        if (teste == 1 || teste == 2) return 2000;
        if (teste == 3 || teste == 4) return n / 10;
        return 200;
    }

    private static long medianaNs(long[] tempos) {
        long[] copia = tempos.clone();
        Arrays.sort(copia);
        return copia[2]; // Terceiro dos cinco tempos ordenados.
    }

    private static double medianaMs(long[] tempos) {
        return medianaNs(tempos) / 1_000_000.0;
    }

    private static double mediaMs(long[] tempos) {
        double soma = 0;
        for (long tempo : tempos) soma += tempo;
        return soma / tempos.length / 1_000_000.0;
    }

    public static void main(String[] args) throws IOException {
        executarComparacao(args, -1);
    }

    public static void executarComparacao(String[] args, int estruturaEscolhida) throws IOException {
        if (estruturaEscolhida < -1 || estruturaEscolhida >= NOMES.length) {
            throw new IllegalArgumentException("Estrutura invalida");
        }
        int[] tipos = estruturaEscolhida == -1 ? new int[] {0, 1, 2, 3, 4, 5, 6}
                                               : new int[] {estruturaEscolhida};
        int[] tamanhos = {5000, 10000, 100000};
        try {
            // Opcional: tamanhos menores para conferencia; mantenha o padrao na coleta final.
            if (args.length > 1) throw new IllegalArgumentException();
            if (args.length == 1) {
                String[] partes = args[0].split(",", -1);
                tamanhos = new int[partes.length];
                for (int i = 0; i < partes.length; i++) tamanhos[i] = Integer.parseInt(partes[i]);
            }
            for (int n : tamanhos) if (n < 51 || n > 100000) throw new IllegalArgumentException();
        } catch (IllegalArgumentException e) {
            System.out.println("Use sem argumentos para n=5000,10000,100000 e cinco repeticoes.");
            System.out.println("Opcional: informe apenas os tamanhos separados por virgula (51..100000).");
            System.out.println("Exemplo de conferencia: 100,300. As cinco repeticoes sao fixas.");
            return;
        }
        String pastaNome = estruturaEscolhida == -1 ? "TodasEstruturas" : PASTAS[estruturaEscolhida];
        File pasta = new File("resultados", pastaNome);
        if (!pasta.isDirectory() && !pasta.mkdirs()) throw new IOException("Nao foi possivel criar resultados");
        try (PrintWriter resumo = new PrintWriter(new File(pasta, "resumo.csv"), "UTF-8");
             PrintWriter bruto = new PrintWriter(new File(pasta, "resultados_brutos.csv"), "UTF-8");
             PrintWriter ambiente = new PrintWriter(new File(pasta, "ambiente.txt"), "UTF-8")) {
            ambiente.println("Java: " + System.getProperty("java.version"));
            ambiente.println("VM: " + System.getProperty("java.vm.name"));
            ambiente.println("SO: " + System.getProperty("os.name") + " " + System.getProperty("os.arch"));
            ambiente.println("Processadores logicos: " + Runtime.getRuntime().availableProcessors());
            ambiente.println("Heap maximo (bytes): " + Runtime.getRuntime().maxMemory());
            ambiente.println("Semente: " + SEMENTE + "; tamanhos: " + Arrays.toString(tamanhos));
            ambiente.println("Estruturas: " + pastaNome);
            ambiente.println("Um aquecimento e cinco medicoes por estrutura/tamanho/ordem/teste.");
            ambiente.println("Cada execucao independente; construcao inclui criacao; demais reconstrucoes fora do tempo.");
            ambiente.println("Arrays: capacidade inicial n + n/10.");
            ambiente.println("Hash: capacidade inicial 16; sondagem linear; fator 0.7; dobra e reinsere ao ampliar.");
            ambiente.println("Para o relatorio: anote tambem modelo do processador e memoria RAM fisica instalada.");
            resumo.println("tamanho;ordem;estrutura;teste;operacoes;mediana_tempo_total_ms;mediana_tempo_operacao_ns");
            bruto.println("tamanho;ordem;estrutura;teste;repeticao;operacoes;tempo_total_ns");
            for (int n : tamanhos) {
                for (int cenario = 0; cenario < CENARIOS.length; cenario++) {
                    DadosTeste dados = prepararDados(n, cenario);
                    double[][] medianas = new double[NOMES.length][TESTES.length];
                    for (int tipo : tipos) {
                        for (int teste = 0; teste < TESTES.length; teste++) {
                            System.out.printf("%n%s | n=%d | %s | %s | aquecimento%n",
                                              NOMES[tipo], n, CENARIOS[cenario], TESTES[teste]);
                            executarTeste(tipo, teste, dados, false);
                            long[] tempos = new long[REPETICOES];
                            int operacoes = quantidadeOperacoes(teste, n);
                            for (int repeticao = 0; repeticao < REPETICOES; repeticao++) {
                                System.out.printf("  Execucao %d/%d (inclui preparo fora da medicao)%n", repeticao + 1, REPETICOES);
                                tempos[repeticao] = executarTeste(tipo, teste, dados, true);
                                bruto.printf(Locale.US, "%d;%s;%s;%s;%d;%d;%d%n", n, CENARIOS[cenario],
                                             NOMES[tipo], TESTES[teste], repeticao + 1, operacoes, tempos[repeticao]);
                                bruto.flush();
                            }
                            double media = mediaMs(tempos), mediana = medianaMs(tempos);
                            double medianaOperacao = medianaNs(tempos) / (double) operacoes;
                            medianas[tipo][teste] = mediana;
                            resumo.printf(Locale.US, "%d;%s;%s;%s;%d;%.6f;%.6f%n",
                                          n, CENARIOS[cenario], NOMES[tipo], TESTES[teste], operacoes,
                                          mediana, medianaOperacao);
                            resumo.flush();
                            System.out.printf(Locale.US, "  %s | %s | mediana: %.6f ms | media: %.6f ms%n",
                                              NOMES[tipo], TESTES[teste], mediana, media);
                        }
                    }
                    System.out.printf("%nComparacao | n=%d | %s | medianas dos lotes em ms%n", n, CENARIOS[cenario]);
                    System.out.printf("%-20s", "Estrutura");
                    for (String nome : TESTES) System.out.printf("%16s", nome);
                    System.out.println();
                    for (int tipo : tipos) {
                        System.out.printf("%-20s", NOMES[tipo]);
                        for (int teste = 0; teste < TESTES.length; teste++) {
                            System.out.printf(Locale.US, "%16.6f", medianas[tipo][teste]);
                        }
                        System.out.println();
                    }
                }
            }
        }
        System.out.println("\nColeta concluida. Arquivos em resultados/" + pastaNome + "/");
    }
}
