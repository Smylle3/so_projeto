#!/usr/bin/env groovy

@GrabConfig(systemClassLoader = true)

class Dispatcher {

    static void main(String[] args) {
        if (args.length != 2) {
            println 'Uso: groovy Dispatcher.groovy processes.txt files.txt'
            return
        }

        File arquivoProcessos = new File(args[0])
        File arquivoOperacoesArquivos = new File(args[1])


        Escalonador escalonadorInstancia = new Escalonador()
        GerenciadorMemoria memoriaInstancia = new GerenciadorMemoria()
        GerenciadorRecursos recursosInstancia = new GerenciadorRecursos()
        GerenciadorArquivos sistemaArquivosInstancia = new GerenciadorArquivos()

        escalonadorInstancia.listaArquivoProcessos = GerenciadorProcessos.processarArquivo(arquivoProcessos)

        int clock = 0

        while (!escalonadorInstancia.jaAcabou(escalonadorInstancia.listaArquivoProcessos )) {
            // adicionar processos que chegam neste instante

            if (escalonadorInstancia.listaProcessosEmAtrasado.size() > 0){
                escalonadorInstancia.listaProcessosEmAtrasado.each { GerenciadorProcessos processo ->
                    if (escalonadorInstancia.listaProcessos.size() <= 99){
                        escalonadorInstancia.criarProcesso(processo, memoriaInstancia, recursosInstancia,escalonadorInstancia.listaArquivoProcessos )
                    }
                }
            }

            escalonadorInstancia.listaArquivoProcessos
                    .findAll { GerenciadorProcessos processo -> processo.tempoChegada == clock }
                .each { GerenciadorProcessos processo ->
                    if (escalonadorInstancia.listaProcessos.size() <= 99){
                        escalonadorInstancia.criarProcesso(processo, memoriaInstancia, recursosInstancia,escalonadorInstancia.listaArquivoProcessos )
                    }
                    else{
                        escalonadorInstancia.listaProcessosEmAtrasado.add(processo)
                    }

                }
            
            GerenciadorProcessos processoQueSeraExecutado = escalonadorInstancia.carregarProcesso()
            if(clock == 9){
                println("teste")
            }
            if (processoQueSeraExecutado != null) {
                escalonadorInstancia.executandoProcesso(processoQueSeraExecutado, memoriaInstancia, recursosInstancia)
            }
            println "Clock: ${clock}"

            clock++
        }
        
        sistemaArquivosInstancia.carregaEstadoInicial(arquivoOperacoesArquivos)
        sistemaArquivosInstancia.executarOperacoes(arquivoOperacoesArquivos, escalonadorInstancia.listaProcessoEncerrados)
        sistemaArquivosInstancia.imprimirMapaDoDisco()
    }

}
