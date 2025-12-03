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
            if (espacoEstaDisponivel(i, processo.blocosDeMemoriaAlocados)) {
                reservarEspaco(i, processo)
                processo.offsetMemoria = i
                return true
            }
        }

        return false
    }

    boolean espacoEstaDisponivel(int start, int size) {
        for (int i = 0; i < size; i++) {
            if (memoria[start + i] != -1) return false
        }
        return true
    }

    void reservarEspaco(int start, GerenciadorProcessos processo) {
        for (int i = 0; i < processo.blocosDeMemoriaAlocados; i++) {
            memoria[start + i] = processo.processoId
        }
        if (processo.prioridade == 0){
            ponteiroRT += processo.blocosDeMemoriaAlocados
        }else{
            ponteiroUSR += processo.blocosDeMemoriaAlocados
        }
    }

    void liberarBlocos(GerenciadorProcessos processo) {
        if (processo.prioridade == 0){
            ponteiroRT = memoria.findIndexOf { it == processo.processoId }
        }else{
            ponteiroUSR = memoria.findIndexOf { it == processo.processoId }
        }
        memoria = memoria.collect { it == processo.processoId ? -1 : it }


    }
}
