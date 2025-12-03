import java.util.concurrent.Semaphore

class GerenciadorRecursos {
    // Semáforos binários (1 = livre, 0 = ocupado)
    private Semaphore scanner = new Semaphore(1)
    private Semaphore[] impressoras = [new Semaphore(1), new Semaphore(1)]
    private Semaphore modem = new Semaphore(1)
    private Semaphore[] sata = [new Semaphore(1), new Semaphore(1), new Semaphore(1)]

    Integer scannerIO = null
    Integer[] impressoraIO = [null, null]
    Integer modemIO = null
    Integer[] sataIO = [null, null, null]

  
    boolean tentandoAlocar(GerenciadorProcessos processo) {
        if (processo.scannerAlocado && !scanner.tryAcquire())
            return false
        else if (processo.scannerAlocado)
            scannerIO = processo.processoId
        
        if (processo.impressoraId > 0 && !impressoras[processo.impressoraId - 1].tryAcquire()) {
            if (processo.scannerAlocado)
                scanner.release()
            return false
        }else if (processo.impressoraId > 0){
            impressoraIO[processo.impressoraId - 1] = processo.processoId
        }
        
        if (processo.modemAlocado && !modem.tryAcquire()) {
            releaseAcquired(processo, true, true, false, false)
            return false
        }else if(processo.modemAlocado ){
            modemIO = processo.processoId
        }
        
        if (processo.sataId > 0 && !sata[processo.sataId - 1].tryAcquire()) {
            releaseAcquired(processo, true, true, true, false)
            return false
        }else if(processo.sataId > 0){
            sataIO[processo.sataId - 1] = processo.processoId
        }

        return true  
    }

    void liberarRecursos(GerenciadorProcessos processo) {
        if (processo.scannerAlocado) {
            scanner.release()
            scannerIO = null
        }


        if (processo.impressoraId > 0) {
            impressoras[processo.impressoraId - 1].release()
            impressoraIO[processo.impressoraId - 1] = null
        }

        if (processo.modemAlocado) {
            modem.release()
            modemIO = null
        }
        if (processo.sataId > 0) {
            sata[processo.sataId - 1].release()
            sataIO[processo.sataId - 1] = null
        }
    }

    private void releaseAcquired(GerenciadorProcessos processo, boolean scanner_tag, boolean impressora_tag, boolean modem_tag, boolean sata_tag) {
        if (scanner_tag && processo.scannerAlocado) {
            scanner.release()
            scannerIO = null
        }
        if (impressora_tag && processo.impressoraId > 0) {
            impressoras[processo.impressoraId - 1].release()
            impressoraIO[processo.impressoraId - 1] = null
        }
        if (modem_tag && processo.modemAlocado) {
            modem.release()
            modemIO = null
        }
        if (sata_tag && processo.sataId > 0) {
            sata[processo.sataId - 1].release()
            sataIO[processo.sataId - 1] = null
        }
    }
}