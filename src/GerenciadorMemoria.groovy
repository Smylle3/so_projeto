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
        if (ponteiroRT >= 63 ){
            ponteiroRT = 0
        }
        if (ponteiroUSR >= 1023 ){
            ponteiroUSR = 0
        }

        int start = (processo.prioridade == 0) ? ponteiroRT : ponteiroUSR
        int end   = (processo.prioridade == 0) ? RT_END   : USR_END


        for (int i = start; i <= end - processo.blocosDeMemoriaAlocados + 1; i++) {
            if (espacoEstaDisponivel(i, processo.blocosDeMemoriaAlocados)) {
                reservarEspaco(i, processo)
                processo.offsetMemoria = i
                return true
            }
        }

        int[] memoriaParcial
        int[] memoriaParcial2

        if (processo.prioridade == 0) {
            memoriaParcial = Arrays.copyOfRange(memoria, 0, start)
            memoriaParcial2 = Arrays.copyOfRange(memoria, start-1, 64)
        } else {
            memoriaParcial = Arrays.copyOfRange(memoria, 64, start)
            memoriaParcial2 = Arrays.copyOfRange(memoria, start, 1024)
        }
        int memoriaDisponivel1 = memoriaParcial2.findAll {   it == -1}.size()
        int memoriaDisponivel =  memoriaParcial.findAll {   it == -1}.size()
        int memoriaTotal = memoriaDisponivel1+memoriaDisponivel
        if((processo.blocosDeMemoriaAlocados > (end - start) && memoriaDisponivel>= processo.blocosDeMemoriaAlocados)){
             start = (processo.prioridade == 0) ? 0 : 64
             end   = (processo.prioridade == 0) ? RT_END   : USR_END

            for (int i = start; i <= end - processo.blocosDeMemoriaAlocados + 1; i++) {
                if (espacoEstaDisponivel(i, processo.blocosDeMemoriaAlocados)) {
                    reservarEspaco(i, processo)
                    processo.offsetMemoria = i
                    return true
                }
            }
        }
        if ((processo.prioridade != 0 && memoriaTotal >=960) || (processo.prioridade == 0 && memoriaTotal >=62)){
            start = (processo.prioridade == 0) ? 0 : 64
            end   = (processo.prioridade == 0) ? RT_END   : USR_END

            for (int i = start; i <= end - processo.blocosDeMemoriaAlocados + 1; i++) {
                if (espacoEstaDisponivel(i, processo.blocosDeMemoriaAlocados)) {
                    reservarEspaco(i, processo)
                    processo.offsetMemoria = i
                    return true
                }
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
        memoria = memoria.collect { it == processo.processoId ? -1 : it }
    }
}
