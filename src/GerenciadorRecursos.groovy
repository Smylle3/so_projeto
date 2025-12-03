import java.util.concurrent.Semaphore

class GerenciadorRecursos {
    // Semáforos binários (1 = livre, 0 = ocupado)
    private Semaphore scanner = new Semaphore(1)
    private Semaphore[] impressoras = [new Semaphore(1), new Semaphore(1)]
    private Semaphore modem = new Semaphore(1)
    private Semaphore[] sata = [new Semaphore(1), new Semaphore(1), new Semaphore(1)]

  
    boolean tentandoAlocar(GerenciadorProcessos processo) {
        if (processo.scannerAlocado && !scanner.tryAcquire())
            return false
        
        if (processo.impressoraId > 0 && !impressoras[processo.impressoraId - 1].tryAcquire()) {
            if (processo.scannerAlocado)
                scanner.release()
            return false
        }
        
        if (processo.modemAlocado && !modem.tryAcquire()) {
            releaseAcquired(processo, true, true, false, false)
            return false
        }
        
        if (processo.sataId > 0 && !sata[processo.sataId - 1].tryAcquire()) {
            releaseAcquired(processo, true, true, true, false)
            return false
        }

        return true  
    }

    void liberarRecursos(GerenciadorProcessos processo) {
        if (processo.scannerAlocado) scanner.release()
        if (processo.impressoraId > 0) impressoras[processo.impressoraId - 1].release()
        if (processo.modemAlocado) modem.release()
        if (processo.sataId > 0) sata[processo.sataId - 1].release()
    }

    private void releaseAcquired(GerenciadorProcessos processo, boolean scanner_tag, boolean impressora_tag, boolean modem_tag, boolean sata_tag) {
        if (scanner_tag && processo.scannerAlocado) scanner.release()
        if (impressora_tag && processo.impressoraId > 0) impressoras[processo.impressoraId - 1].release()
        if (modem_tag && processo.modemAlocado) modem.release()
        if (sata_tag && processo.sataId > 0) sata[processo.sataId - 1].release()
    }
}