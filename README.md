Banco de Dados em Java

Este projeto implementa um banco de dados manual em Java, desenvolvido como parte das atividades da disciplina de Algoritmos e Estruturas de Dados III. O banco de dados armazena informações de maneira inteligente, utilizando métodos Create, Update e Delete, e inclui o reaproveitamento de espaço para otimização do armazenamento.
Funcionalidades

-    Criar novos registros
-    Atualizar registros existentes
-    Excluir registros
-    Reaproveitar espaço liberado por registros excluídos

Desenvolvimento

O projeto foi desenvolvido de forma totalmente autoral, baseado apenas no conhecimento lógico adquirido nas aulas da disciplina. O código do professor não foi utilizado, demonstrando a aplicação prática dos conceitos aprendidos.

Respondendo as Perguntas

O que você considerou como perda aceitável para o reuso de espaços vazios, isto é, quais são os critérios para a gestão dos espaços vazios?
  - Se o espaço tiver marcado como lapide toda vez que um novo registro e colocado no arquivo, e verificado se em algum dos espaços vazios e possível de ser armazenado.

O código do CRUD com arquivos de tipos genéricos está funcionando corretamente?
  - Sim o CRUD funciona corretamente para o tipo de dados genérico.

O CRUD tem um índice direto implementado com a tabela hash extensível?
  - Não.... ainda

A operação de inclusão busca o espaço vazio mais adequado para o novo registro antes de acrescentá-lo ao fim do arquivo?
  - A operação de inclusão busca um espaço valido para que o registro seja colocado.

A operação de alteração busca o espaço vazio mais adequado para o registro quando ele cresce de tamanho antes de acrescentá-lo ao fim do arquivo?
  - Apenas armazena no final do arquivo.

As operações de alteração (quando for o caso) e de exclusão estão gerenciando os espaços vazios para que possam ser reaproveitados?
  - Sim, ambas quando marcado lapide no registro, criam-se espaços para armazenamentos posteriores.

O trabalho está funcionando corretamente?
  - Sim, o trabalho esta funcionando perfeitamente, reaproveitando espaços vazios como foi solicitado na atividade.
  
O trabalho está completo?
  - Sim, o trabalho atende ao exigido no enunciado.
  
O trabalho é original e não a cópia de um trabalho de um colega?
  - Sim o trabalho e 100% original=, feito baseado apenas nos conhecimentos adquiridos na diciplina.

Criadores/Colaboradores

Matheus Fagundes
Luca Lourenço
