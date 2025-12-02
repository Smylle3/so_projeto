class GerenciadorRecursos {
    Integer scanner 
    Integer[] printers    
    Integer modem
    Integer[] sata
    GerenciadorRecursos() {
        this.scanner = null
        this.printers= [null, null]         // IDs 1 e 2
        this.modem = null
        this.sata  = [null, null, null]     // IDs 1 a 3
    }
    /**
     * Aloca TODOS os recursos necessários para o processo.
     * Se qualquer recurso estiver indisponível, nada é alocado.
     *
     * Retorna true = sucesso, false = falha
     */
    boolean allocate(GerenciadorProcessos processo) {
        // ------------ VERIFICAÇÃO PRÉVIA (sem modificar nada) ------------
        // Scanner
        if (processo.scannerAlocado && scanner != null) {
            return false
        }

        // Printer
        if (processo.impressoraId > 0) {
            int idx = processo.impressoraId - 1
            if (printers[idx] != null) {
                return false
            }
        }

        // Modem
        if (processo.modemAlocado && modem != null) {
            return false
        }

        // SATA
        if (processo.sataId > 0) {
            int idx = processo.sataId - 1
            if (sata[idx] != null) {
                return false
            }
        }

        // ------------ TODAS AS VERIFICAÇÕES PASSARAM ------------
        // Agora sim podemos alocar efetivamente

        if (processo.scannerAlocado) {
            scanner = processo.processoId
        }

        if (processo.impressoraId > 0) {
            int idx = processo.impressoraId - 1
            printers[idx] = processo.processoId
        }

        if (processo.modemAlocado) {
            modem = processo.processoId
        }

        if (processo.sataId > 0) {
            int idx = processo.sataId - 1
            sata[idx] = processo.processoId
        }

        return true
    }

    /**
     * Libera todos os recursos usados pelo processo.
     */
    void free(GerenciadorProcessos processo) {
        if (scanner == processo.processoId) {
            scanner = null
        }

        if (modem == processo.processoId) {
            modem = null
        }

        // printers
        for (int i = 0; i < printers.size(); i++) {
            if (printers[i] == processo.processoId) {
                printers[i] = null
            }
        }

        // sata
        for (int i = 0; i < sata.size(); i++) {
            if (sata[i] == processo.processoId) {
                sata[i] = null
            }
        }
    }

}
