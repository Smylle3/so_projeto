import java.util.concurrent.Semaphore

class GerenciadorRecursosAvancado {
    // Semáforos binários (1 = livre, 0 = ocupado)
    private Semaphore scanner = new Semaphore(1)
    private Semaphore[] printers = [new Semaphore(1), new Semaphore(1)]
    private Semaphore modem = new Semaphore(1)
    private Semaphore[] sata = [new Semaphore(1), new Semaphore(1), new Semaphore(1)]

    /**
     * Tenta alocar todos os recursos (não bloqueante)
     * Retorna true se conseguiu, false se não
     */
    boolean tryAllocate(GerenciadorProcessos p) {
        // Tenta pegar scanner
        if (p.scannerAlocado && !scanner.tryAcquire())
            return false

        // Tenta pegar impressora
        if (p.impressoraId > 0 && !printers[p.impressoraId - 1].tryAcquire()) {
            if (p.scannerAlocado)
                scanner.release()
            return false
        }

        // Tenta pegar modem
        if (p.modemAlocado && !modem.tryAcquire()) {
            releaseAcquired(p, true, true, false, false)
            return false
        }

        // Tenta pegar disco
        if (p.sataId > 0 && !sata[p.sataId - 1].tryAcquire()) {
            releaseAcquired(p, true, true, true, false)
            return false
        }

        return true  // Todos alocados!
    }

    /**
     * Libera recursos
     */
    void free(GerenciadorProcessos p) {
        if (p.scannerAlocado) scanner.release()
        if (p.impressoraId > 0) printers[p.impressoraId - 1].release()
        if (p.modemAlocado) modem.release()
        if (p.sataId > 0) sata[p.sataId - 1].release()
    }

    private void releaseAcquired(GerenciadorProcessos p, boolean s, boolean pr, boolean m, boolean sa) {
        if (s && p.scannerAlocado) scanner.release()
        if (pr && p.impressoraId > 0) printers[p.impressoraId - 1].release()
        if (m && p.modemAlocado) modem.release()
        if (sa && p.sataId > 0) sata[p.sataId - 1].release()
    }
}