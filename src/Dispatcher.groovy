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

        println "Conteúdo completo:\n$processFile.text"

        println "\n\nConteúdo completo:\n$fileOpsFile.text"

        Escalonador scheduler = new Escalonador()
        GerenciadorMemoria memory = new GerenciadorMemoria()
        GerenciadorRecursos resources = new GerenciadorRecursos()
        GerenciadorArquivos filesystem = new GerenciadorArquivos()

        List<GerenciadorProcessos> processes = GerenciadorProcessos.processarArquivo(processFile)

        int clock = 0

        while (!scheduler.isDone(processes, clock)) {
            // adicionar processos que chegam neste instante
            processes
                .findAll { GerenciadorProcessos p -> p.tempoChegada == clock }
                .each { GerenciadorProcessos p ->
                    scheduler.criarProcesso(p)
                    println scheduler.formatProcessCreation(p)
                }

            GerenciadorProcessos next = scheduler.carregarProcesso()

            if (next != null) {
                scheduler.runProcess(next, memory, resources, clock)
                clock = clock + next.tempoProcessamento
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
