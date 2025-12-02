class GerenciadorMemoria {

    // 1024 blocos
    int[] memoria = new int[1024].collect { -1 }

    // Região 0–63: tempo real
    final int RT_START = 0
    final int RT_END   = 63
    int ponteiroRT = 0

    // Região 64–1023: usuário
    final int USR_START = 64
    final int USR_END   = 1023
    int ponteiroUSR = 0

    // Alocar bloco contíguo
    boolean alocarBlocos(GerenciadorProcessos processo) {
        int start = (processo.prioridade == 0) ? ponteiroRT : ponteiroUSR
        int end   = (processo.prioridade == 0) ? RT_END   : USR_END

        for (int i = start; i <= end - processo.blocosDeMemoriaAlocados + 1; i++) {
            if (isFreeRange(i, processo.blocosDeMemoriaAlocados)) {
                reserve(i, processo)
                processo.offsetMemoria = i
                return true
            }
        }

        return false
    }

    boolean isFreeRange(int start, int size) {
        for (int i = 0; i < size; i++) {
            if (memoria[start + i] != -1) return false
        }
        return true
    }

    void reserve(int start, GerenciadorProcessos p) {
        for (int i = 0; i < p.blocosDeMemoriaAlocados; i++) {
            memoria[start + i] = p.processoId
        }
        if (p.prioridade == 0){
            ponteiroRT += p.blocosDeMemoriaAlocados
        }else{
            ponteiroUSR += p.blocosDeMemoriaAlocados
        }
    }

    void free(GerenciadorProcessos p) {
        if (p.prioridade == 0){
            ponteiroRT = memoria.findIndexOf { it == p.processoId }
        }else{
            ponteiroUSR = memoria.findIndexOf { it == p.processoId }
        }
        memoria = memoria.collect { it == p.processoId ? -1 : it }


    }
}
