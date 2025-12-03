# Projeto de Sistemas Operacionais - Grupo 5

## Instalação das dependências

Para executar o projeto será necessário instalar o Java, a partir da versão 17,
e também Groovy, a partir da versão 2.5.0

## Executar o programa

Para compilar o projeto, basta rodar na pasta raiz:

    groovyc -d out src/*.groovy

E para executar:

   ` groovy -cp src src/Dispatcher.groovy processes.txt files.txt`
