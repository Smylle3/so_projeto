class Escalonador {
    List<GerenciadorProcessos> fileTempoReal
    Map<Integer, List<GerenciadorProcessos>> fileUsuario
    Map<Integer, Integer> quantumTable

    List<GerenciadorProcessos> listaProcessos,listaProcessosEmAtrasado

    Escalonador() {
        this.fileTempoReal  = []
        this.fileUsuario = [
                1: [], 2: [], 3: [], 4: [], 5: []
        ]

        this.quantumTable  = [
                1: 6, 2: 5, 3: 4, 4: 3, 5: 2
        ]
        this.listaProcessos  = []
        this.listaProcessosEmAtrasado = []
    }
    void criarProcesso(GerenciadorProcessos processo, GerenciadorMemoria memory, GerenciadorRecursosAvancado resources) {

        // -----------------------------
        // ALOCAÇÃO DE MEMÓRIA
        // -----------------------------
        if (processo.offsetMemoria == -1) {
            boolean ok = memory.alocarBlocos(processo)
            if (!ok) {
                // sem memória → processo volta pra fila
                //requeue(processo)

                return
            }
        }

        // -----------------------------
        // ALOCAÇÃO DE RECURSOS
        // -----------------------------
        if (!resources.tryAllocate(processo)) {
            // não conseguiu I/O → devolve para fila
            //requeue(processo)
            if (listaProcessosEmAtrasado.findAll { GerenciadorProcessos processos -> processos.processoId == processo.processoId}.size() == 0 )
                this.listaProcessosEmAtrasado.add(processo)
            return
        }
        println this.formatProcessCreation(processo)

        // inserindo processo na fila
        listaProcessos << processo

        if (processo.prioridade == 0) {
            fileTempoReal << processo
        }
        else {
            fileUsuario[processo.prioridade] << processo
        }
    }
    
    GerenciadorProcessos carregarProcesso() {
        if (!fileTempoReal.isEmpty()) {
            return fileTempoReal.remove(0)
        }

        for (int prioridade = 1; prioridade <= 5; prioridade++) {
            if (!fileUsuario[prioridade].isEmpty()) {
                return fileUsuario[prioridade].remove(0)
            }
        }
        return null
    }

    void balanceamentoDeProcessos() {
        listaProcessos.each { processo ->
            if (processo.tempoRestante > 0) {
                if (processo.prioridade > 1 && processo.tempoEspera >= 5) {
                    processo.prioridade--
                    processo.tempoEspera = 0
                } else {
                    processo.tempoEspera++
                }
            }
        }
    }
    
//    void demote(GerenciadorProcessos processo) {
//        if (processo.prioridade > 0 && processo.prioridade < 5) {
//            processo.prioridade++
//        }
//    }

    int getQuantum(GerenciadorProcessos processo) {
        if (processo.prioridade == 0) return Integer.MAX_VALUE
        return quantumTable[processo.prioridade]
    }

    boolean isDone(List<GerenciadorProcessos> all, int clock) {
        return all.every { it.tempoRestante <= 0 }
    }

    String formatProcessCreation(GerenciadorProcessos processo) {
        GerenciadorMemoria memoria = new GerenciadorMemoria()
        return String.format(
                'dispatcher =>\n' +
                '    PID: %-3d\n' +
                '    offset: %-3d\n' +
                '    blocks: %-3d\n' +
                '    priority: %-2d\n' +
                '    time: %-3d\n' +
                '    scanners: %-2d\n' +
                '    printers: %-2d\n' +
                '    modems: %-2d\n' +
                '    sata: %-2d\n',
                //'Process P%-3d created | arrival=%-3d prio=%-2d cpu=%-3d mem=%-3d printer=%-2d scanner=%-2d modem=%-2d sata=%-2d',
                processo.processoId,
                processo.offsetMemoria,
                processo.blocosDeMemoriaAlocados,
                processo.prioridade,
                processo.tempoProcessamento,
                processo.scannerAlocado ? 1 : 0,
                processo.impressoraId,
                processo.modemAlocado ? 1 : 0,
                processo.sataId
        )
    }

    void runProcess(
            GerenciadorProcessos processo,
            GerenciadorMemoria memory,
            GerenciadorRecursosAvancado resources,
            int clock
    ) {

        // troca de contexto
        int[]  contextoDeMemoria =  memory.memoria.findAll { int bloco -> bloco == processo.processoId}

        // Marca início
        processo.printStart()

        int quantum = getQuantum(processo)
        int used = 0

        // Executa até quantum expirar ou terminar
        while (used < quantum && processo.tempoRestante > 0 && contextoDeMemoria.size() == processo.blocosDeMemoriaAlocados) {
            processo.printInstruction(used + 1)
            processo.tempoRestante--
            used++
        }

        // TERMINOU?
        if (processo.tempoRestante <= 0) {
            processo.printEnd()
            memory.free(processo)
            resources.free(processo)
            listaProcessos = listaProcessos.findAll { GerenciadorProcessos processos -> processos.processoId != processo.processoId}
            return
        }

        // NÃO TERMINOU → rebaixa prioridade (se user)
//        demote(processo)

        // reaplica aging global
        balanceamentoDeProcessos()

        // devolve processo à fila
        requeue(processo)
    }

    // devolve processo para a fila correta
    void requeue(GerenciadorProcessos processo) {
        if (processo.prioridade == 0) {
            fileTempoReal << processo
        }
        else {
            fileUsuario[processo.prioridade] << processo
        }
    }

}
