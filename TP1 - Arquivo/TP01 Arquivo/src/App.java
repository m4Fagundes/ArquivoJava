import models.*;
import services.*;


public class App {
    public static void main(String[] args) throws Exception {

        Arquivo<Livro> fileTeste = new Arquivo<>(Livro.class.getConstructor());
        
        Livro livro = new Livro();

        livro.nome = "Cleam Code";
        livro.altor = "Robert C. Martins";

        Livro livro2 = new Livro();

        livro.nome = "Scrum a Arte de Fazer o Dobro em Metade do Tempo";
        livro.altor = "Jeff Sutherland";

        Livro livro3 = new Livro();

        livro3.nome = "Cleam Architecture";
        livro3.altor = "Robert C. Martin";

        Livro pessoa4 = new Livro();

        pessoa4.nome = "O Universo em uma Casca de Nós";
        pessoa4.altor = "Stephen Hawking";

        fileTeste.Create(livro);
        fileTeste.Create(livro2);
        fileTeste.delete(2);
        fileTeste.Create(livro2);
        fileTeste.Create(pessoa4);
        

        livro3.nome = "Como Programar Java";
        livro3.altor = "Paul Deitel";

        Livro livroRead = new Livro();

        fileTeste.Update(livro3);
        livroRead = fileTeste.read(1);

        fileTeste.Create(livroRead);
        System.out.println("O nome do livro é : " + livroRead.nome + livroRead.altor);


        fileTeste.close();
        
    }
}
