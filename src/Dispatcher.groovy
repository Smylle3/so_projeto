#!/usr/bin/env groovy

@GrabConfig(systemClassLoader = true)

class Dispatcher {

    static void main(String[] args) {
        if (args.length != 2) {
            println 'Uso: groovy Dispatcher.groovy processes.txt files.txt'
            return
        }

        File processFile = new File(args[0])
        File fileOpsFile = new File(args[1])

//        println "Conteúdo completo:\n$processFile.text"
//
//        println "\n\nConteúdo completo:\n$fileOpsFile.text"

        Escalonador scheduler = new Escalonador()
        GerenciadorMemoria memory = new GerenciadorMemoria()
        GerenciadorRecursosAvancado resources = new GerenciadorRecursosAvancado()
        GerenciadorArquivos filesystem = new GerenciadorArquivos()

        List<GerenciadorProcessos> processos = GerenciadorProcessos.processarArquivo(processFile)
        List<GerenciadorProcessos> processosEmEspera = []
        int clock = 0

        while (!scheduler.isDone(processos, clock)) {
            // adicionar processos que chegam neste instante

            if (scheduler.listaProcessosEmAtrasado.size() > 0){
                scheduler.listaProcessosEmAtrasado.each { GerenciadorProcessos processo ->
                    if (scheduler.listaProcessos.size() <= 99){
                        scheduler.criarProcesso(processo, memory, resources)
                    }
                }
            }

            processos
                .findAll { GerenciadorProcessos processo -> processo.tempoChegada == clock }
                .each { GerenciadorProcessos processo ->
                    if (scheduler.listaProcessos.size() <= 99){
                        scheduler.criarProcesso(processo, memory, resources)
                    }
                    else{
                        scheduler.listaProcessosEmAtrasado.add(processo)
                    }

                }



            GerenciadorProcessos next = scheduler.carregarProcesso()

            if (next != null) {
                scheduler.runProcess(next, memory, resources, clock)
            }
            println "Clock: ${clock}"

            clock++
        }

        // Carregar estado inicial do disco
        filesystem.carregaEstadoInicial(fileOpsFile)

        // Executar operações
        filesystem.executarOperacoes(fileOpsFile, scheduler.listaProcessos)

        // Imprimir mapa final do disco
        filesystem.imprimirMapaDoDisco()
    }

}
