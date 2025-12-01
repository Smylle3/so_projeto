#!/usr/bin/env groovy

@GrabConfig(systemClassLoader=true)

class Dispatcher {

    static void main(String[] args) {
        if (args.length != 2) {
            println 'Uso: groovy dispatcher.groovy processes.txt files.txt'
            System.exit(1)
        }

        def processFile = new File(args[0])
        def fileOpsFile = new File(args[1])

        def scheduler = new Scheduler()
        def memory = new MemoryManager()
        def resources = new ResourceManager()
        def filesystem = new FileSystemManager()

        def processes = Process.parseInput(processFile)

        int clock = 0
        int pidCounter = 0

        while (!scheduler.isDone(processes, clock)) {
            // adicionar processos que chegam agora
            processes.findAll { it.arrivalTime == clock }.each { p ->
                scheduler.addProcess(p)
                println scheduler.formatProcessCreation(p)
        }

            def next = scheduler.getNextProcess()

            if (next) {
                scheduler.runProcess(next, memory, resources, clock)
            }

            clock++
    }

        // Executar operações de arquivos
        filesystem.executeOperations(fileOpsFile, scheduler.allProcesses)

        // Imprimir mapa final do disco
        filesystem.printDiskMap()
}

}
