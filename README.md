# so_projeto

dispatcher.groovy	-> Ponto de entrada, orquestra tudo, escalona, imprime logs
Process.groovy	-> Modelo do processo (dados e simulação básica)
Scheduler.groovy	-> Filas, prioridades, aging, quantum, MLFQ
MemoryManager.groovy	-> 1024 blocos, first-fit, RT/Usuário, offset
ResourceManager.groovy	-> Scanner, printer, modem, SATA (exclusão mútua)
FileSystemManager.groovy	-> Disco, arquivos, alocação contígua, operações
