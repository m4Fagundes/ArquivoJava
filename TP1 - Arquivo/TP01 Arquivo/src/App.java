import java.text.SimpleDateFormat;
import java.util.Date;


import models.*;
import services.*;


public class App {
    public static void main(String[] args) throws Exception {

        Arquivo<Livro> fileTeste = new Arquivo<>(Livro.class.getConstructor());

        Livro livro = new Livro();

        livro.nome = "Clean Code";
        livro.autor = "Robert C. Martins";

        Livro livro2 = new Livro();

        livro2.nome = "Scrum a Arte de Fazer o Dobro em Metade do Tempo";
        livro2.autor = "Jeff Sutherland";

        Livro livro3 = new Livro();

        livro3.nome = "Clean Architecture";
        livro3.autor = "Robert C. Martin";

        Livro livro4 = new Livro();

        livro4.nome = "O Universo em uma Casca de Nós";
        livro4.autor = "Stephen Hawking";

        fileTeste.Create(livro);
        fileTeste.Create(livro2);
        fileTeste.delete(2);
        fileTeste.Create(livro3);
        fileTeste.Create(livro4);

        livro3.nome = "Como Programar Java";
        livro3.autor = "Paul Deitel";

        Livro livroRead = new Livro();
        //fileTeste.Update(livro3);

        livroRead = fileTeste.read(3);

        System.out.println("O nome do livro é : " + livroRead.nome +
        livroRead.autor);
        fileTeste.pesquisaPorNome("Clean Code");

        fileTeste.printHashMapProtected();
        fileTeste.printatNameHashMapProtected();
        fileTeste.pesquisaPorPalavra("o universo em");

        String pastaEntrada = "TP1 - Arquivo/TP01 Arquivo/src/DataBase";
        String pastaDeSaida = "TP1 - Arquivo/TP01 Arquivo/src/Backup";
        String pastaDeSaidaDescompactada = "TP1 - Arquivo/TP01 Arquivo/src/BackupDescompactado";


        //LZW.compactarPastaLZW(pastaEntrada, pastaDeSaida);
        //LZW.descompactarPastaLZW(pastaDeSaida, pastaDeSaidaDescompactada);

        // Cria um formato de data e hora para os backps
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String dataAtual = dateFormat.format(new Date());
        String pastaSaida = pastaDeSaida + "/" + dataAtual;

        // Compacta a pasta
        LZW.compactarPastaLZW(pastaEntrada, pastaSaida);
        // Descompacta a pasta
        LZW.descompactarPastaLZW(pastaDeSaida, pastaDeSaidaDescompactada);

    }
}