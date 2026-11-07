public class ArvoreRubroNegra implements Estrutura {

    private enum Cor {
        VERMELHO,
        PRETO
    }

    private class No {
        private int valor;
        private Cor cor;
        private No esq, dir, pai;

        public No(int valor) {
            this.valor = valor;
            this.cor = Cor.VERMELHO;
            this.esq = this.dir = this.pai = null;
        }
    }

    private No raiz;

    public boolean buscar(int valor) {
        No atual = raiz;

        while (atual != null) {
            if (valor == atual.valor) {
                return true;
            } else if (valor < atual.valor) {
                atual = atual.esq;
            } else {
                atual = atual.dir;
            }
        }

        return false;
    }

    public void inserir(int valor) {
        if (buscar(valor)) {
            return;
        }

        No novoNo = new No(valor);
        raiz = inserirNo(raiz, novoNo);
        corrigirInsercao(novoNo);
    }

    private No inserirNo(No atual, No novoNo) {
        if (atual == null) {
            return novoNo;
        }

        if (novoNo.valor < atual.valor) {
            atual.esq = inserirNo(atual.esq, novoNo);
            atual.esq.pai = atual;
        } else if (novoNo.valor > atual.valor) {
            atual.dir = inserirNo(atual.dir, novoNo);
            atual.dir.pai = atual;
        }

        return atual;
    }

    private void corrigirInsercao(No no) {
        // A raiz deve ser preta ao final.
        // Corrige enquanto o pai for vermelho.
        while (no != raiz && cor(no.pai) == Cor.VERMELHO) {

            No pai = no.pai;
            No avo = pai.pai;

            if (pai == avo.esq) {
                No tio = avo.dir;

                // Caso 1: tio vermelho.
                // Recolore pai, tio e avo.
                if (cor(tio) == Cor.VERMELHO) {
                    pai.cor = Cor.PRETO;
                    tio.cor = Cor.PRETO;
                    avo.cor = Cor.VERMELHO;
                    no = avo;
                } else {
                	// caso 2: triangulo : transforma em uma linha
                	if (no == pai.dir) {
                		no = pai;
                		rotacaoEsquerda(no);
                		
                		// rotacao mudou as relacoes entre os nos
                		pai = no.pai;
                		avo = pai.pai;
                		
                	}
                	// caso 3: linha recolore e gira sobre avo
                	pai.cor = Cor.PRETO;
                	avo.cor = Cor.VERMELHO;
                	rotacaoDireita(avo);
                }
            } else {
            	// Caso Simétricos: Pai é filho direito do avô
            	No tio = avo.esq;
            	//CASO 1: tio vermelho
            	if (cor(tio)== Cor.VERMELHO) {
            		pai.cor = Cor.PRETO;
            		tio.cor = Cor.PRETO;
            		avo.cor = Cor.VERMELHO;
            		no=avo;
            	} else {
            		//Caso 2: triangulo. transforma em uma linha.
            		if(no == pai.esq) {
            			no = pai;
            			rotacaoDireita(no);
            			
            			pai = no.pai;
            			avo = pai.pai;
            			
            		}
            		//Caso 3 : linha
            		pai.cor = Cor.PRETO;
            		avo.cor = Cor.VERMELHO;
            		rotacaoEsquerda(avo);
            	}
            }
        }
        
        raiz.cor = Cor.PRETO;
    }
    
    private void rotacaoEsquerda(No no) {
    	No novaRaiz = no.dir;
    	
    	no.dir = novaRaiz.esq;
    	
    	if(novaRaiz.esq != null) {
    		
    		novaRaiz.esq.pai = no;
    		
    	} 
    	novaRaiz.pai = no.pai;
    	if(no.pai == null) {
    		raiz = novaRaiz;
    	} else if(no == no.pai.esq) {
    		no.pai.esq = novaRaiz ;
    		
    	} else {
    		no.pai.dir = novaRaiz;
    	}
    	novaRaiz.esq = no;
    	no.pai = novaRaiz;
    	
    }
    
    private void rotacaoDireita(No no) {
    	No novaRaiz = no.esq;
    	
    	no.esq = novaRaiz.dir;
    	
    	if (novaRaiz.dir != null) {
    		novaRaiz.dir.pai = no;
    	}
    	
    	novaRaiz.pai = no.pai;
    	if (no.pai == null) {
    		raiz = novaRaiz;
    	} else if(no == no.pai.dir){
    		no.pai.dir = novaRaiz;
    	} else {
    		no.pai.esq = novaRaiz;
    	}
    	
    	novaRaiz.dir = no;
    	no.pai = novaRaiz;
    }
    

    public boolean buscarNo(int valor) {
        return buscarNo(raiz, valor) != null;
    }

    private No buscarNo(No atual, int valor) {
        if (atual == null || atual.valor == valor) {
            return atual;
        }

        if (valor < atual.valor) {
            return buscarNo(atual.esq, valor);
        } else {
            return buscarNo(atual.dir, valor);
        }
    }


    public void mostrar() {
    	mostrar(raiz);
    }
    
    private void mostrar(No no) {
    	if (no != null) {
    		mostrar(no.esq);
    		System.out.println(no.valor + "(" + no.cor + ")");
    		mostrar(no.dir);
    	}
    }

    
    private Cor cor(No no) {
        return no == null ? Cor.PRETO : no.cor;
    }

    public void remover(int valor) {
        No alvo = raiz;
        while (alvo != null && alvo.valor != valor) {
            if (valor < alvo.valor) alvo = alvo.esq;
            else alvo = alvo.dir;
        }
        if (alvo == null) return;

        // Com dois filhos, copia o sucessor e remove fisicamente esse sucessor.
        if (alvo.esq != null && alvo.dir != null) {
            No sucessor = alvo.dir;
            while (sucessor.esq != null) sucessor = sucessor.esq;
            alvo.valor = sucessor.valor;
            alvo = sucessor;
        }

        No filho = alvo.esq != null ? alvo.esq : alvo.dir;
        No pai = alvo.pai;
        boolean eraEsquerdo = pai != null && alvo == pai.esq;
        Cor corRemovida = alvo.cor;
        if (filho != null) filho.pai = pai;
        if (pai == null) raiz = filho;
        else if (eraEsquerdo) pai.esq = filho;
        else pai.dir = filho;

        if (corRemovida == Cor.PRETO) corrigirRemocao(filho, pai, eraEsquerdo);
    }

    private void pintar(No no, Cor novaCor) {
        if (no != null) no.cor = novaCor;
    }

    private void corrigirRemocao(No no, No pai, boolean esquerdo) {
        // Quando no e null, pai e esquerdo identificam a posicao removida.
        while (no != raiz && cor(no) == Cor.PRETO) {
            if (pai == null) break;
            if (esquerdo) {
                No irmao = pai.dir;
                if (cor(irmao) == Cor.VERMELHO) {
                    pintar(irmao, Cor.PRETO);
                    pintar(pai, Cor.VERMELHO);
                    rotacaoEsquerda(pai);
                    irmao = pai.dir;
                }
                if (irmao == null || (cor(irmao.esq) == Cor.PRETO && cor(irmao.dir) == Cor.PRETO)) {
                    pintar(irmao, Cor.VERMELHO);
                    no = pai;
                    pai = no.pai;
                    esquerdo = pai != null && no == pai.esq;
                } else {
                    if (cor(irmao.dir) == Cor.PRETO) {
                        pintar(irmao.esq, Cor.PRETO);
                        pintar(irmao, Cor.VERMELHO);
                        rotacaoDireita(irmao);
                        irmao = pai.dir;
                    }
                    pintar(irmao, cor(pai));
                    pintar(pai, Cor.PRETO);
                    pintar(irmao.dir, Cor.PRETO);
                    rotacaoEsquerda(pai);
                    no = raiz;
                }
            } else {
                No irmao = pai.esq;
                if (cor(irmao) == Cor.VERMELHO) {
                    pintar(irmao, Cor.PRETO);
                    pintar(pai, Cor.VERMELHO);
                    rotacaoDireita(pai);
                    irmao = pai.esq;
                }
                if (irmao == null || (cor(irmao.esq) == Cor.PRETO && cor(irmao.dir) == Cor.PRETO)) {
                    pintar(irmao, Cor.VERMELHO);
                    no = pai;
                    pai = no.pai;
                    esquerdo = pai != null && no == pai.esq;
                } else {
                    if (cor(irmao.esq) == Cor.PRETO) {
                        pintar(irmao.dir, Cor.PRETO);
                        pintar(irmao, Cor.VERMELHO);
                        rotacaoEsquerda(irmao);
                        irmao = pai.esq;
                    }
                    pintar(irmao, cor(pai));
                    pintar(pai, Cor.PRETO);
                    pintar(irmao.esq, Cor.PRETO);
                    rotacaoDireita(pai);
                    no = raiz;
                }
            }
        }
        pintar(no, Cor.PRETO);
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
