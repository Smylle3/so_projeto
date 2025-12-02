class ProcessManager {

    int pid
    int arrivalTime
    int priority
    int initialPriority
    int cpuTime
    int remainingTime
    int memoryBlocksNeeded
    int memoryOffset = -1

    boolean needsScanner
    int printerId
    boolean needsModem
    int sataId

    int waitingTime = 0

    static ProcessManager fromLine(String line, int pid) {
        def parts = line.split(',').collect { it.trim() }

        return new ProcessManager(
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

    static List<ProcessManager> parseInput(File file) {
        List<ProcessManager> list = []
        int pidCounter = 0

        file.eachLine { line ->
            if (line.trim()) {
                list << ProcessManager.fromLine(line, pidCounter++)
            }
        }

        return list
    }

    void printStart() {
        println "P${pid} STARTED"
    }

    void printInstruction(int step) {
        println "P${pid} instruction ${step}"
    }

    void printEnd() {
        println "P${pid} return SIGINT"
    }

}
