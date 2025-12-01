class Scheduler {

    // Fila de tempo real (FIFO)
    List<Process> realtimeQueue = []

    // Filas de usuário (prioridade 1..5)
    Map<Integer, List<Process>> userQueues = [
        1: [], 2: [], 3: [], 4: [], 5: []
    ]

    // Quantum por prioridade
    Map<Integer, Integer> quantumTable = [
        1: 6,
        2: 5,
        3: 4,
        4: 3,
        5: 2
    ]

    // Processos executados (para aging)
    List<Process> allProcesses = []

    // Adiciona processo à fila correta
    void addProcess(Process p) {
        allProcesses << p

        if (p.priority == 0) {
            realtimeQueue << p
        } else {
            userQueues[p.priority] << p
        }
    }

    // Retorna o próximo processo a ser executado
    Process nextProcess() {
        // Tempo real sempre vem antes
        if (!realtimeQueue.isEmpty()) {
            return realtimeQueue.remove(0)
        }

        // Filas de usuário em ordem de prioridade
        for (int pr = 1; pr <= 5; pr++) {
            if (!userQueues[pr].isEmpty()) {
                return userQueues[pr].remove(0)
            }
        }

        return null
    }

    // Aplica aging (subir prioridade)
    void applyAging() {
        allProcesses.each { p ->
            if (p.priority > 1 && p.waitingTime >= 5) {  // regra opcional
                p.priority--
                p.waitingTime = 0
            } else {
                p.waitingTime++
            }
        }
    }

    // Rebaixar prioridade depois de executar
    void demote(Process p) {
        if (p.priority < 5 && p.priority > 0) {
            p.priority++
        }
    }

    int getQuantum(Process p) {
        if (p.priority == 0) return Integer.MAX_VALUE // FIFO
        return quantumTable[p.priority]
    }

    boolean isDone(List<Process> all, int clock) {
        // Todos chegaram e todos terminaram?
        return all.every { it.remainingTime <= 0 }
    }

}
