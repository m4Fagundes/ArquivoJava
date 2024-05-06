package services;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.text.Normalizer;
import models.*;

/**
 * Classe genérica para manipulação de registros de arquivo que implementam a
 * interface Registro.
 * Utiliza serialização e deserialização de objetos do tipo T em um array de
 * bytes para armazenamento em arquivo.
 * 
 * @param <T> Tipo do registro que estende a interface Registro.
 */
public class Arquivo<T extends Registro> {

  protected RandomAccessFile arquivo; // Objeto para leitura e escrita no arquivo.
  protected RandomAccessFile arquivoIndice;
  protected Constructor<T> construtor; // Construtor do tipo T, usado para criar instâncias de T.
  protected ArquivoIndexado idDireto; // Objeto da classe HashMap
  protected IndexacaoNome hashIndiretaNome;
  protected ListaInvertida invertedList;
  final protected int TAM_CABECALHO = 4; // Tamanho fixo do cabeçalho do arquivo.

  /**
   * Constrói um arquivo que manipula registros do tipo T.
   * O arquivo é criado ou aberto como leitura e escrita ('rw').
   * Um cabeçalho de arquivo é inicializado se o arquivo estiver vazio.
   * 
   * @param c Construtor da classe T.
   */
  public Arquivo(Constructor<T> c) throws Exception {
    this.construtor = c;
    this.arquivo = new RandomAccessFile("pessoas.db", "rw");
    idDireto = new ArquivoIndexado();
    hashIndiretaNome = new IndexacaoNome();
    invertedList = new ListaInvertida();

    if (arquivo.length() < TAM_CABECALHO) {
      arquivo.seek(0);
      arquivo.writeInt(0);
    }
  }

  /**
   * Cria um novo registro no arquivo, utilizando índices para otimizar a busca.
   * Salva o novo ID no objeto e no arquivo, e escreve o objeto serializado,
   * reutilizando espaço de registros deletados se possível.
   * 
   * @param obj Instância de T para ser armazenada no arquivo.
   */
  public void Create(T obj) throws Exception {
    arquivo.seek(0);
    int ultimoID = arquivo.readInt();
    ultimoID++;
    arquivo.seek(0);
    arquivo.writeInt(ultimoID);
    obj.setID(ultimoID);

    byte[] registroOBJ = obj.toByteArray();
    short tamanhoRegistroNovo = (short) registroOBJ.length;
    boolean espaçoEncontrado = false;
    Long offset = arquivo.length();

    arquivo.seek(TAM_CABECALHO);

    while (arquivo.getFilePointer() < arquivo.length()) {
      long posiçãoLapide = arquivo.getFilePointer();
      byte valorLapide = arquivo.readByte();
      short tamanhoRegistroAtual = arquivo.readShort();

      if (valorLapide == '*' && tamanhoRegistroAtual >= tamanhoRegistroNovo) {
        arquivo.seek(posiçãoLapide);
        arquivo.writeByte(' ');
        arquivo.writeShort(tamanhoRegistroNovo);
        arquivo.write(registroOBJ);
        espaçoEncontrado = true;
        offset = posiçãoLapide;
        break;
      } else {
        arquivo.skipBytes(tamanhoRegistroAtual);
      }
    }

    if (!espaçoEncontrado) {
      arquivo.seek(arquivo.length());
      arquivo.writeByte(' ');
      arquivo.writeShort(tamanhoRegistroNovo);
      arquivo.write(registroOBJ);
    }

    idDireto.index.put(ultimoID, offset);
    idDireto.salvarHashMap();
    invertedList.inserir(obj.getNome(), obj.getID());
    hashIndiretaNome.hash.put(obj.getNome(), obj.getID());
    hashIndiretaNome.salvarHashMapFinal(obj.getNome(), obj.getID());
  }

  /**
   * Apaga um registro pelo ID, marcando seu espaço como deletado (lapide '*')
   * e remove o ID do índice.
   *
   * @param id ID do registro a ser deletado.
   */
  public void delete(int id) {
    try {
      Long offset = idDireto.index.get(id);
      if (offset == null) {
        System.out.println("Registro com ID " + id + " não encontrado.");
        return;
      }
      arquivo.seek(offset);
      arquivo.writeByte('*');
      // TODO salvar a string do obj e depois mandar ela como parametro para o metodo
      // de remocao
      idDireto.index.remove(id);
      idDireto.salvarHashMap();
      // TODO Pegar o objeto com esse id correspondente para remover pelo nome
      hashIndiretaNome.hash.remove(id);
      hashIndiretaNome.salvarHashMap();

    } catch (IOException e) {
      System.out.println("Erro de I/O ao deletar registro: " + e.getMessage());
    }
  }

  /**
   * Atualiza um registro no arquivo substituindo o antigo por um novo
   * se o espaço for suficiente, ou movendo para o final do arquivo se necessário.
   * Atualiza o índice para refletir a mudança.
   *
   * @param obj Registro a ser atualizado.
   */
  public void Update(T obj) throws Exception {
    Long enderecoOBJ = idDireto.index.get(obj.getID());
    if (enderecoOBJ == null) {
      System.out.println("Objeto não encontrado para atualização.");
      return;
    }

    arquivo.seek(enderecoOBJ);
    arquivo.readByte();
    short tamanhoAtual = arquivo.readShort();
    byte[] novoRegistro = obj.toByteArray();
    short novoTamanho = (short) novoRegistro.length;

    if (tamanhoAtual >= novoTamanho) {
      arquivo.seek(enderecoOBJ);
      arquivo.writeByte(' ');
      arquivo.writeShort(novoTamanho);
      arquivo.write(novoRegistro);
    } else {
      arquivo.seek(enderecoOBJ);
      arquivo.writeByte('*');
      idDireto.index.remove(obj.getID());
      arquivo.seek(arquivo.length());
      long novoEndereco = arquivo.getFilePointer();
      arquivo.writeByte(' ');
      arquivo.writeShort(novoTamanho);
      arquivo.write(novoRegistro);
      idDireto.index.put(obj.getID(), novoEndereco);
      idDireto.salvarHashMap();
    }
  }

  /**
   * Lê e deserializa um registro pelo ID usando índices.
   * Retorna nulo se o registro não for encontrado ou estiver marcado como
   * deletado.
   * 
   * @param id ID do registro a ser lido.
   * @return Uma instância de T se encontrado, nulo caso contrário.
   */
  public T read(int id) throws Exception {
    Long enderecoLeitura = idDireto.index.get(id);
    if (enderecoLeitura == null) {
      System.out.println("Objeto não encontrado para leitura.");
      return null;
    }

    arquivo.seek(enderecoLeitura);
    byte lapide = arquivo.readByte();
    if (lapide == ' ') {
      short tamanhoRegistro = arquivo.readShort();
      byte[] registro = new byte[tamanhoRegistro];
      arquivo.readFully(registro);
      T obj = construtor.newInstance();
      obj.fromByteArray(registro);
      return obj;
    } else {
      System.out.println("O registro não está presente no acervo.");
      return null;
    }
  }

  public void pesquisaPorNome(String nome) throws Exception {
    int id = hashIndiretaNome.hash.get(nome);
    T obj = read(id);
    if (obj != null) {
      System.out.println("O livro " + obj.getNome() + " está presente no banco de dados");
    } else {
      System.out.println("O livro não esta poresente no banco de dados");
    }
  }

  /*
   * Funcao para remover acentos de uma sting e deixar ela mais comum
   * para a busca com o atributo nome
   */

  public static String removerAcentos(String input) {
    if (input == null) {
      return null;
    }
    String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
    return normalized.replaceAll("\\p{M}", "");
  }

  // TODO Metodo de pesquisa com erro ou persistencia da linked list com erro
  public void pesquisaPorPalavra(String palavra) throws Exception {
    Long offset = invertedList.indice.get(palavra);
    if (offset == null) {
        System.out.println("Registro com essa palavra não encontrado.");
        return;
    }
    
    System.out.println("Este é o endereço: " + offset);

    invertedList.arquivoListas.seek(offset);
    int quantidadeValores = invertedList.arquivoListas.readInt();
    System.out.println("Este é o tamanho da LinkedList: " + quantidadeValores);

    for(int i = 0; i < quantidadeValores; i++) {
        int ID = invertedList.arquivoListas.readInt();
        System.out.println("Este é o ID: " + ID);
        Long endereco = idDireto.index.get(ID);
        if (endereco != null) {
            arquivo.seek(endereco);
            byte lapide = arquivo.readByte();
            if (lapide == ' ') {
                short tamanhoRegistro = arquivo.readShort();
                byte[] registro = new byte[tamanhoRegistro];
                arquivo.readFully(registro);
                T obj = construtor.newInstance();
                obj.fromByteArray(registro);
    
                System.out.println(obj.getNome());
            } else {
                System.out.println("O registro não está presente no acervo.");
            }
        } else {
            System.out.println("Endereço não encontrado para o ID: " + ID);
        }
    }
}

  /**
   * Fecha o arquivo e garante que todas as modificações sejam salvas.
   * Deve ser chamado ao finalizar o uso do arquivo.
   */
  public void close() {
    try {
      idDireto.salvarHashMap();
      if (arquivo != null) {
        arquivo.close();
      }
    } catch (IOException e) {
      System.out.println("Erro ao fechar os arquivos: " + e.getMessage());
    }
  }

  // Funcao para chamar funcao protected no Main
  public void printHashMapProtected() {
    idDireto.printHashMap();
  }

  public void printatNameHashMapProtected() {
    hashIndiretaNome.printHashMap();
  }
}
