# AliasFinder
Projeto desenvolvido para a Disciplina de Manutenção e Evolução de Software


# Funcionalidades
Este código é responsável por verificar se existem Alias em projetos do Github seguindo os passos abaixo:
1 - Faz o download dos commits de um repositório utilizando as credenciais de uma conta do Github. 
2 - Cria uma lista com nome e email dos autores de cada commit. 
3 - Filtra retirando as instâncias repetidas.
4 - Aplica a heurística para encontrar Alias.
5 - Devolve um arquivo "AliasList" na pasta raiz onde o código foi executado.


# Heurística
Essa foi implementada partindo de princípios mais conservadores, considerando Alias quando duas contas possuem ou nome exatamente iguais ou e-mails exatamente iguais.

# Exemplos de Uso
Para executar é necessário informar três argumentos

User      Password     Project_Name_On_GitHub

Exemplos práticos testados durante o desenvolvimento

User2     Password2    hubot

.
