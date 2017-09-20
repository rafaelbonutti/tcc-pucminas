# SCA - Sistema de Controle Acadêmico

Trabalho de Conclusão de Curso

## Prova de Conceito: ##
> http://ec2-54-233-147-179.sa-east-1.compute.amazonaws.com:8080/index.jsf

## Instruções para execução: ##

1 - baixar o código fonte
  
    git pull https://github.com/rafaelbonutti/tcc-pucminas.git
  
2 - compilar e gerar o uberjar
  
     mvn clean install
  
3 - executar o Consul

    consul agent -dev
  
4 - executar as aplicações
 
     java -jar target/sca-web-swarm.jar
     
  
     java -jar target/curso-service-swarm.jar
     
 
     java -jar target/disciplina-service-swarm.jar
     
