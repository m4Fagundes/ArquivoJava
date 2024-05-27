package services;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LZW {

    public static void compactarPastaLZW(String pastaEntrada, String pastaSaida) {
        File diretorio = new File(pastaEntrada);
        File[] arquivos = diretorio.listFiles();

        for (File arquivo : arquivos) {
            if (arquivo.isDirectory()) {
                compactarPastaLZW(arquivo.getAbsolutePath(), pastaSaida);
            } else {
                compactarArquivoLZW(arquivo.getAbsolutePath(), pastaSaida + "/" + arquivo.getName() + ".lzw");
            }
        }
    }

    public static void compactarArquivoLZW(String arquivoEntrada, String arquivoSaida) {
        try (FileInputStream fis = new FileInputStream(arquivoEntrada);
                DataOutputStream dos = new DataOutputStream(new FileOutputStream(arquivoSaida))) {

            Map<String, Integer> dicionario = new HashMap<>();
            for (int i = 0; i < 256; i++) {
                dicionario.put(String.valueOf((char) i), i);
            }

            int proximoCodigo = 256;
            StringBuilder palavra = new StringBuilder();
            List<Integer> codigoSaida = new ArrayList<>();

            int caractere;
            while ((caractere = fis.read()) != -1) {
                palavra.append((char) caractere);
                if (!dicionario.containsKey(palavra.toString())) {
                    dicionario.put(palavra.toString(), proximoCodigo++);
                    codigoSaida.add(dicionario.get(palavra.substring(0, palavra.length() - 1)));
                    palavra = new StringBuilder(palavra.substring(palavra.length() - 1));
                }
            }

            if (!palavra.toString().equals("")) {
                codigoSaida.add(dicionario.get(palavra.toString()));
            }

            for (Integer codigo : codigoSaida) {
                dos.writeShort(codigo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void descompactarPastaLZW(String pastaCompactada, String pastaSaida) {

        File diretorio = new File(pastaCompactada);
        File[] arquivos = diretorio.listFiles();

        for (File arquivo : arquivos) {
            if (arquivo.getName().endsWith(".lzw")) {
                descompactarArquivoLZW(arquivo.getAbsolutePath(), pastaSaida);
            }
        }
    }
    public static void descompactarArquivoLZW(String arquivoCompactado, String pastaSaida) {
        File pastaSaidaFile = new File(pastaSaida);
        if (!pastaSaidaFile.exists() || !pastaSaidaFile.isDirectory()) {
            pastaSaidaFile.mkdirs();
        }
    
        try (DataInputStream dis = new DataInputStream(new FileInputStream(arquivoCompactado));
             FileOutputStream fos = new FileOutputStream(pastaSaida + "/" + new File(arquivoCompactado).getName().replace(".lzw", ""))) {
    
            Map<Integer, String> dicionario = new HashMap<>();
            for (int i = 0; i < 256; i++) {
                dicionario.put(i, String.valueOf((char) i));
            }
    
            int proximoCodigo = 256;
            StringBuilder palavra = new StringBuilder();
            List<String> saidaDados = new ArrayList<>();
    
            int codigoAnterior = dis.readShort();
            palavra.append(dicionario.get(codigoAnterior));
            saidaDados.add(dicionario.get(codigoAnterior));
    
            int codigo;
            while (dis.available() > 0) {
                codigo = dis.readShort();
                String entrada;
                if (dicionario.containsKey(codigo)) {
                    entrada = dicionario.get(codigo);
                } else if (codigo == proximoCodigo) {
                    entrada = palavra.toString() + palavra.charAt(0);
                } else {
                    throw new IllegalArgumentException("Código não encontrado no dicionário.");
                }
    
                saidaDados.add(entrada);
                
                dicionario.put(proximoCodigo++, palavra.toString() + entrada.charAt(0));
                
                palavra = new StringBuilder(entrada);
            }
    
            for (String dados : saidaDados) {
                for (char c : dados.toCharArray()) {
                    fos.write(c);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }    
  }
