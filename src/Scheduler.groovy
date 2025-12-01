class Scheduler {

    List<ProcessManager> realtimeQueue = []
    Map<Integer, List<ProcessManager>> userQueues = [
        1: [], 2: [], 3: [], 4: [], 5: []
    ]

    Map<Integer, Integer> quantumTable = [
        1: 6, 2: 5, 3: 4, 4: 3, 5: 2
    ]

    List<ProcessManager> allProcesses = []

    void addProcess(ProcessManager p) {
        allProcesses << p

        if (p.priority == 0) {
            realtimeQueue << p
        }
        else {
            userQueues[p.priority] << p
        }
    }

    ProcessManager nextProcess() {
        if (!realtimeQueue.isEmpty()) {
            return realtimeQueue.remove(0)
        }

        for (int pr = 1; pr <= 5; pr++) {
            if (!userQueues[pr].isEmpty()) {
                return userQueues[pr].remove(0)
            }
        }
        return null
    }

    void applyAging() {
        allProcesses.each { p ->
            if (p.remainingTime > 0) {
                if (p.priority > 1 && p.waitingTime >= 5) {
                    p.priority--
                    p.waitingTime = 0
                } else {
                    p.waitingTime++
                }
            }
        }
    }

    void demote(ProcessManager p) {
        if (p.priority > 0 && p.priority < 5) {
            p.priority++
        }
    }

    int getQuantum(ProcessManager p) {
        if (p.priority == 0) return Integer.MAX_VALUE
        return quantumTable[p.priority]
    }

    boolean isDone(List<ProcessManager> all, int clock) {
        return all.every { it.remainingTime <= 0 }
    }

    String formatProcessCreation(ProcessManager p) {
        return String.format(
            'Process P%-3d created | arrival=%-3d prio=%-2d cpu=%-3d mem=%-3d printer=%-2d scanner=%-2d modem=%-2d sata=%-2d',
            p.pid,
            p.arrivalTime,
            p.priority,
            p.cpuTime,
            p.memoryBlocksNeeded,
            p.printerId,
            p.needsScanner ? 1 : 0,
            p.needsModem ? 1 : 0,
            p.sataId
        )
    }

    // ---------------------------------------------------------
    // 🔥 AQUI — método completo runProcess()
    // ---------------------------------------------------------
    void runProcess(
        ProcessManager p,
        MemoryManager memory,
        ResourceManager resources,
        int clock
    ) {
        // -----------------------------
        // ALOCAÇÃO DE MEMÓRIA
        // -----------------------------
        if (p.memoryOffset == -1) {
            boolean ok = memory.allocate(p)
            if (!ok) {
                // sem memória → processo volta pra fila
                requeue(p)
                return
            }
        }

        // -----------------------------
        // ALOCAÇÃO DE RECURSOS
        // -----------------------------
        if (!resources.allocate(p)) {
            // não conseguiu I/O → devolve para fila
            requeue(p)
            return
        }

        // Marca início
        p.printStart()

        int quantum = getQuantum(p)
        int used = 0

        // Executa até quantum expirar ou terminar
        while (used < quantum && p.remainingTime > 0) {
            p.printInstruction(used + 1)
            p.remainingTime--
            used++
        }

        // TERMINOU?
        if (p.remainingTime <= 0) {
            p.printEnd()
            memory.free(p)
            resources.free(p)
            return
        }

        // NÃO TERMINOU → rebaixa prioridade (se user)
        demote(p)

        // reaplica aging global
        applyAging()

        // devolve processo à fila
        requeue(p)
    }

    // devolve processo para a fila correta
    void requeue(ProcessManager p) {
        if (p.priority == 0) {
            realtimeQueue << p
        }
        else {
            userQueues[p.priority] << p
        }
    }

}
