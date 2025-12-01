class MemoryManager {

    // 1024 blocos
    int[] memory = new int[1024].collect { -1 }

    // Região 0–63: tempo real
    final int RT_START = 0
    final int RT_END   = 63

    // Região 64–1023: usuário
    final int USR_START = 64
    final int USR_END   = 1023

    // Alocar bloco contíguo
    boolean allocate(Process p) {
        int start = (p.priority == 0) ? RT_START : USR_START
        int end   = (p.priority == 0) ? RT_END   : USR_END

        for (int i = start; i <= end - p.memoryBlocksNeeded + 1; i++) {
            if (isFreeRange(i, p.memoryBlocksNeeded)) {
                reserve(i, p)
                p.memoryOffset = i
                return true
            }
        }

        return false
    }

    boolean isFreeRange(int start, int size) {
        for (int i = 0; i < size; i++) {
            if (memory[start + i] != -1) return false
        }
        return true
    }

    void reserve(int start, Process p) {
        for (int i = 0; i < p.memoryBlocksNeeded; i++) {
            memory[start + i] = p.pid
        }
    }

    void free(Process p) {
        memory = memory.collect { it == p.pid ? -1 : it }
    }

}
