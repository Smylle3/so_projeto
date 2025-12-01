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

        Scheduler scheduler = new Scheduler()
        MemoryManager memory = new MemoryManager()
        ResourceManager resources = new ResourceManager()
        FileSystemManager filesystem = new FileSystemManager()

        List<Process> processes = Process.parseInput(processFile)

        int clock = 0

        while (!scheduler.isDone(processes, clock)) {
            // adicionar processos que chegam neste instante
            processes
                .findAll { Process p -> p.arrivalTime == clock }
                .each { Process p ->
                    scheduler.addProcess(p)
                    println scheduler.formatProcessCreation(p)
                }

            Process next = scheduler.nextProcess()

            if (next != null) {
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
