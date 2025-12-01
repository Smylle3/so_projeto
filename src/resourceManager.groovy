class ResourceManager {

    Integer scanner = null
    Integer[] printers = [null, null]         // IDs 1 e 2
    Integer modem = null
    Integer[] sata = [null, null, null]       // IDs 1 a 3

    boolean request(Process p) {
        if (p.needsScanner) {
            if (scanner != null) return false
            scanner = p.pid
        }

        if (p.printerId > 0) {
            int idx = p.printerId - 1
            if (printers[idx] != null) return false
            printers[idx] = p.pid
        }

        if (p.needsModem) {
            if (modem != null) return false
            modem = p.pid
        }

        if (p.sataId > 0) {
            int idx = p.sataId - 1
            if (sata[idx] != null) return false
            sata[idx] = p.pid
        }

        return true
    }

    void release(Process p) {
        if (scanner == p.pid) scanner = null

        printers = printers.collect { it == p.pid ? null : it }
        sata = sata.collect { it == p.pid ? null : it }

        if (modem == p.pid) modem = null
    }

}
