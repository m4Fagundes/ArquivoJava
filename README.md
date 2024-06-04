Integrantes do Grupo

    Matheus Fagundes
    Luca Lourenço
    Pedro Rodrigues

D# Relatório de Projeto - Backup com LZW

## Descrição do Projeto

O projeto consiste em implementar um sistema de backup de arquivos utilizando o algoritmo LZW para compactação e descompactação. O sistema permite ao usuário escolher qual versão de backup recuperar e automatiza o processo de compactação e descompactação.

## Implementações

### Classe LZW

A classe LZW contém os métodos essenciais para compactação e descompactação de arquivos:

- **`compactarPastaLZW(pastaEntrada, pastaSaidaBase)`:** Este método percorre recursivamente uma pasta de entrada e compacta todos os arquivos utilizando o algoritmo LZW.
- **`compactarArquivoLZW(arquivoEntrada, arquivoSaida)`:** Este método compacta um arquivo individual utilizando o algoritmo LZW.
- **`descompactarPastaLZW(pastaCompactada, pastaSaida)`:** Descompacta uma pasta de arquivos previamente compactada com o algoritmo LZW.
- **`descompactarArquivoLZW(arquivoCompactado, pastaSaida)`:** Descompacta um arquivo individual previamente compactado com o algoritmo LZW.

### Outras Funcionalidades

- **`escolherPastaParaDescompactar(caminhoBackup)`:** Permite ao usuário escolher dinamicamente qual pasta de backup descompactar, listando todas as pastas disponíveis.

## Experiência do Grupo

O grupo conseguiu implementar com sucesso o sistema de backup com LZW, incluindo a escolha dinâmica de pastas para descompactação.
Além disso, implementamos uma verificação adicional no processo de descompactação para garantir a integridade dos arquivos recuperados.

### Checklist

- [x] Há uma rotina de compactação usando o algoritmo LZW para fazer backup dos arquivos?
- [x] Há uma rotina de descompactação usando o algoritmo LZW para recuperação dos arquivos?
- [x] O usuário pode escolher a versão a recuperar?
- [ ] Qual foi a taxa de compressão alcançada por esse backup?
- [x] O trabalho está funcionando corretamente?
- [x] O trabalho está completo?
- [x] O trabalho é original e não a cópia de um trabalho de um colega?

