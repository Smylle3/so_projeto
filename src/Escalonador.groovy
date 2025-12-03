class Escalonador {
    List<GerenciadorProcessos> filaTempoReal
    Map<Integer, List<GerenciadorProcessos>> filaUsuario
    Map<Integer, Integer> tabelaQuantum

    List<GerenciadorProcessos> listaProcessos,listaProcessosEmAtrasado,listaProcessoEncerrados

    Escalonador() {
        this.filaTempoReal  = []
        this.filaUsuario = [
                1: [], 2: [], 3: [], 4: [], 5: []
        ]

        this.tabelaQuantum  = [
                1: 6, 2: 5, 3: 4, 4: 3, 5: 2
        ]
        this.listaProcessos  = []
        this.listaProcessosEmAtrasado = []
        this.listaProcessoEncerrados = []
    }
    
    void criarProcesso(GerenciadorProcessos processo, GerenciadorMemoria memoriaInstancia, GerenciadorRecursos recursosInstancia) {

        // ALOCAÇÃO DE MEMÓRIA
        if (processo.offsetMemoria == -1) {
            boolean ok = memoriaInstancia.alocarBlocos(processo)
            if (!ok) {
                // sem memória → processo volta pra fila
                return
            }
        }

        // ALOCAÇÃO DE RECURSOS
        if (!recursosInstancia.tentandoAlocar(processo)) {
            // não conseguiu I/O → devolve para fila
            if (listaProcessosEmAtrasado.findAll { GerenciadorProcessos processos -> processos.processoId == processo.processoId}.size() == 0 )
                this.listaProcessosEmAtrasado.add(processo)
            return
        }
        println this.exibirProcessoCriado(processo)


        listaProcessos << processo

        if (processo.prioridade == 0) {
            filaTempoReal << processo
        }
        else {
            filaUsuario[processo.prioridade] << processo
        }
    }
    
    GerenciadorProcessos carregarProcesso() {
        if(listaProcessos.isEmpty()){
            return null
        }

        if (!filaTempoReal.isEmpty()) {
            return filaTempoReal.remove(0)
        }

        for (int prioridade = 1; prioridade <= 5; prioridade++) {
            if (!filaUsuario[prioridade].isEmpty()) {
                return filaUsuario[prioridade].remove(0)
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
    


    int retornaTempoQuantum(GerenciadorProcessos processo) {
        if (processo.prioridade == 0) return 100
        return tabelaQuantum[processo.prioridade]
    }

    static boolean jaAcabou(List<GerenciadorProcessos> all) {
        return all.every { it.tempoRestante <= 0 }
    }

    static String exibirProcessoCriado(GerenciadorProcessos processo) {
        GerenciadorMemoria memoria = new GerenciadorMemoria()
        return String.format(
                '\ndispatcher =>\n' +
                '    PID: %-3d\n' +
                '    offset: %-3d\n' +
                '    blocks: %-3d\n' +
                '    priority: %-2d\n' +
                '    time: %-3d\n' +
                '    scanners: %-2d\n' +
                '    printers: %-2d\n' +
                '    modems: %-2d\n' +
                '    sata: %-2d\n',
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

    void executandoProcesso(
            GerenciadorProcessos processo,
            GerenciadorMemoria memoriaInstancia,
            GerenciadorRecursos recursosInstancia
    ) {

        // troca de contexto
        int[]  contextoDeMemoria =  memoriaInstancia.memoria.findAll { int bloco -> bloco == processo.processoId}

        // Marca início
        processo.printStart()

        int quantum = retornaTempoQuantum(processo)
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
            memoriaInstancia.liberarBlocos(processo)
            recursosInstancia.liberarRecursos(processo)
            listaProcessoEncerrados.add(listaProcessos.findAll { GerenciadorProcessos processos -> processos.processoId == processo.processoId}.remove(0))
            listaProcessos = listaProcessos.findAll { GerenciadorProcessos processos -> processos.processoId != processo.processoId}

            return
        }

        balanceamentoDeProcessos()

        retornaFila(processo)
    }

    // devolve processo para a fila correta
    void retornaFila(GerenciadorProcessos processo) {
        if (processo.prioridade == 0) {
            filaTempoReal << processo
        }
        else {
            filaUsuario[processo.prioridade] << processo
        }
    }

}
