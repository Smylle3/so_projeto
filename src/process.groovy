class Process {

    int pid
    int arrivalTime
    int priority
    int initialPriority
    int cpuTime
    int remainingTime
    int memoryBlocksNeeded
    int memoryOffset = -1

    boolean needsScanner
    int printerId        // 0 = nenhum | 1–2 = impressoras
    boolean needsModem
    int sataId           // 0 = nenhum | 1–3 = discos

    int waitingTime = 0  // usado para aging

    static Process fromLine(String line, int pid) {
        // Exemplo de linha:
        // <tempo>, <prioridade>, <cpu>, <mem>, <printer>, <scanner>, <modem>, <sata>

        def parts = line.split(',').collect { it.trim() }

        return new Process(
            pid: pid,
            arrivalTime: parts[0] as int,
            priority: parts[1] as int,
            initialPriority: parts[1] as int,
            cpuTime: parts[2] as int,
            remainingTime: parts[2] as int,
            memoryBlocksNeeded: parts[3] as int,
            printerId: parts[4] as int,
            needsScanner: (parts[5] as int) == 1,
            needsModem: (parts[6] as int) == 1,
            sataId: parts[7] as int
        )
    }

    // Chamado quando o processo começa a executar
    void printStart() {
        println "P${pid} STARTED"
    }

    // Simula uma instrução
    void printInstruction(int step) {
        println "P${pid} instruction ${step}"
    }

    // Quando finalizar
    void printEnd() {
        println "P${pid} return SIGINT"
    }

}
