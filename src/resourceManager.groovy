class ResourceManager {

    Integer scanner = null
    Integer[] printers = [null, null]   // IDs 1 e 2
    Integer modem = null
    Integer[] sata = [null, null, null] // IDs 1 a 3

    /**
     * Aloca TODOS os recursos necessários para o processo.
     * Se qualquer recurso estiver indisponível, nada é alocado.
     *
     * Retorna true = sucesso, false = falha
     */
    boolean allocate(ProcessManager p) {
        // ------------ VERIFICAÇÃO PRÉVIA (sem modificar nada) ------------
        // Scanner
        if (p.needsScanner && scanner != null) {
            return false
        }

        // Printer
        if (p.printerId > 0) {
            int idx = p.printerId - 1
            if (printers[idx] != null) {
                return false
            }
        }

        // Modem
        if (p.needsModem && modem != null) {
            return false
        }

        // SATA
        if (p.sataId > 0) {
            int idx = p.sataId - 1
            if (sata[idx] != null) {
                return false
            }
        }

        // ------------ TODAS AS VERIFICAÇÕES PASSARAM ------------
        // Agora sim podemos alocar efetivamente

        if (p.needsScanner) {
            scanner = p.pid
        }

        if (p.printerId > 0) {
            int idx = p.printerId - 1
            printers[idx] = p.pid
        }

        if (p.needsModem) {
            modem = p.pid
        }

        if (p.sataId > 0) {
            int idx = p.sataId - 1
            sata[idx] = p.pid
        }

        return true
    }

    /**
     * Libera todos os recursos usados pelo processo.
     */
    void free(ProcessManager p) {
        if (scanner == p.pid) {
            scanner = null
        }

        if (modem == p.pid) {
            modem = null
        }

        // printers
        for (int i = 0; i < printers.size(); i++) {
            if (printers[i] == p.pid) {
                printers[i] = null
            }
        }

        // sata
        for (int i = 0; i < sata.size(); i++) {
            if (sata[i] == p.pid) {
                sata[i] = null
            }
        }
    }

}
