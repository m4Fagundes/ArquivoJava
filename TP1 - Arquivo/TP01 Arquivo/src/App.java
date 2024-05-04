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
        fileTeste.Update(livro3);
        
        livroRead = fileTeste.read(5);

        System.out.println("O nome do livro é : " + livroRead.nome + livroRead.autor);


        fileTeste.idDireto.printHashMap();
        fileTeste.close();
        
    }
}
