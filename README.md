# Projeto de Sistemas Operacionais - Grupo 5

## Instalação das dependências

Para executar o projeto será necessário instalar o Java, a partir da versão 17,
e também Groovy, a partir da versão 2.5.0

## Executar o programa

Para compilar o projeto, basta rodar na pasta raiz:

    groovyc -d out src/*.groovy

E para executar:

   ` groovy -cp src src/Dispatcher.groovy processes.txt files.txt`

Também tem um arquivo de teste de stress que foi utilizado e passado com sucesso, testando os principais casos abordados pela aplicação
a versão utilizada com o arquivo testeDeStress.txt, existe uma versao do teste de stress que detalha
o que cada entrada testa. AVISO!!!! Caso deseje executar esse teste, é necessário um com computador
com um processador de 6 núcleos e 12 threads com clock minimo de 2,3 GHz e 6GB por precaução.
No nosso ambiente de teste utilizamos um AMD Ryzen 5 5500U e uma maquina com 12 GB de RAM, durante a
execução foram utilizados 10 threads na média de 87% de uso de cada uma.
Caso queria utilizar esse aconselhamos a execução do teste de stress por partes para não gerar um alto uso da CPU
