class GerenciadorArquivos {

    int tamanhoDoDisco
    int[] disco

    Map<String, Map> arquivos = [:]

    void carregaEstadoInicial(File f) {
        def linhasTxt = f.readLines()

        tamanhoDoDisco = linhasTxt[0] as int
        disco = new int[tamanhoDoDisco].collect { 0 }

        int quantidadeOcupada = linhasTxt[1] as int

        (0..<quantidadeOcupada).each { idx ->
            def partes = linhasTxt[idx + 2].split(',').collect { it.trim() }

            def nome = partes[0]
            def inicio = partes[1] as int
            def tamanho = partes[2] as int

            arquivos[nome] = [start: inicio, size: tamanho, owner: -1]

            (0..<tamanho).each { i ->
                disco[inicio + i] = nome.charAt(0) as int
            }
        }
    }

    void executarOperacoes(File arquivo, List<GerenciadorProcessos> processos) {
        def linhas = arquivo.readLines()
        int quantidadeOcupada = linhas[1] as int
        def operacoes = linhas.drop(quantidadeOcupada + 2)
        println "\nSistema de arquivos =>\n"
        operacoes.eachWithIndex { linha, indice ->
            def partes = linha.split(',').collect { it.trim() }

            int pid = partes[0] as int
            int tipoOperacao = partes[1] as int
            String nomeArquivo = partes[2]

            GerenciadorProcessos processo = processos.find { it.processoId == pid }

            if (!processo) {
                println "Operação ${indice + 1} => Falha"
                println "O processo ${pid} não existe."
                return
            }

            if (tipoOperacao == 0) {
                int tamanho = partes[3] as int
                criarArquivo(processo, nomeArquivo, tamanho, indice + 1)
            }
            else {
                deletarArquivo(processo, nomeArquivo, indice + 1)
            }
        }
    }

    void criarArquivo(GerenciadorProcessos processo, String nome, int tamanho, int numeroOp) {
        int inicio = firstFit(tamanho)

        if (inicio < 0) {
            println "Operação ${numeroOp} => Falha"
            println "O processo ${processo.processoId} não pode criar o arquivo ${nome} (falta de espaço)."
            return
        }

        arquivos[nome] = [start: inicio, size: tamanho, owner: processo.processoId]

        (0..<tamanho).each { i ->
            disco[inicio + i] = nome.charAt(0) as int
        }

        println "Operação ${numeroOp} => Sucesso"
        println "O processo ${processo.processoId} criou o arquivo ${nome} (blocos ${inicio} a ${inicio + tamanho - 1})."
    }

    void deletarArquivo(GerenciadorProcessos processo, String nome, int numeroOp) {
        if (!arquivos.containsKey(nome)) {
            println "Operação ${numeroOp} => Falha"
            println "O arquivo ${nome} não existe."
            return
        }

        def meta = arquivos[nome]
        boolean permitido = (processo.prioridade == 0) || (meta.owner == processo.processoId)

        if (!permitido) {
            println "Operação ${numeroOp} => Falha"
            println "O processo ${processo.processoId} não pode deletar o arquivo ${nome}."
            return
        }

        (0..<meta.size).each { i ->
            disco[meta.start + i] = 0
        }

        arquivos.remove(nome)

        println "Operação ${numeroOp} => Sucesso"
        println "O processo ${processo.processoId} deletou o arquivo ${nome}."
    }

    int firstFit(int tamanho) {
        for (int i = 0; i <= tamanhoDoDisco - tamanho; i++) {
            boolean livre = (0..<tamanho).every { bloco -> disco[i + bloco] == 0 }
            if (livre) return i
        }
        return -1
    }

    void imprimirMapaDoDisco() {
        println '\nMapa de ocupação do disco:\n'
        disco.each { print("____")}
        print("_\n")
        disco.each { it == 0 ? print("|   "): print("| "+(char)it+" ") }
        print("|\n")
        disco.each { print("----")}
        print("-\n")
    }

}
