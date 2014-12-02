package br.ufjf.avaliacao.library;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import br.ufjf.avaliacao.model.Curso;
import br.ufjf.avaliacao.persistent.impl.CursoDAO;

public class GetCurso {
	private static GetCurso instance = null;
	private HashMap<String, String> confs;

	private GetCurso() {
		try {
			InputStream inputStream = getClass().getResourceAsStream("cursos.txt");
			String arquivo = IOUtils.toString(inputStream);
			confs = new HashMap<String,String>();
			Pattern patternConf = Pattern.compile("(.*) = (.*)$", Pattern.MULTILINE);
			Matcher conf = patternConf.matcher(arquivo);
			while (conf.find()) {
				confs.put(conf.group(1), conf.group(2));
			}
	        inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Curso getCursoMatricula(String matricula)
	{
		String infoCurso = getInfoCurso(matricula);
		if(infoCurso!=null)
		{
			Curso curso = getConf(infoCurso);
			return curso;
		}
		return null;
	}
	
	public static String getInfoCurso(String matricula)
	{
		if(matricula!=null)
		if(matricula.length() >= 9)
		{	
			String codCurso = (String) matricula.subSequence(4, 6);
			String letraCurso = removerNumeros(matricula);
			if(letraCurso.length()==0)
				letraCurso="A";
		
			return codCurso + letraCurso;
		}
		return null;
	}
	
	private static String removerNumeros(String matricula)//a especializaÃƒÂ¯Ã‚Â¿Ã‚Â½ÃƒÂ¯Ã‚Â¿Ã‚Â½o do curso ÃƒÂ¯Ã‚Â¿Ã‚Â½ dada em caracteres ao final da matricula
	{
		String letras = "";
		for(int i=0;i<matricula.length();i++)
		{
			letras=letras+excessaoInteiro(matricula.charAt(i));
		}
		return letras;
	}
	
	private static String excessaoInteiro(char caracter)//retorna quando o caracter nao for inteiro
	{
		for(int i=0;i<10;i++)
		{
			if(caracter == (""+i).charAt(0))
				return "";
		}
		return ""+caracter;
	}
	
	public static Curso getConf(String key) {

			instance = new GetCurso();
		
			String infoCursoAux =  instance.confs.get(key);
			
			List<String> infoCurso = new ArrayList<String>();
			
			for (String retval: infoCursoAux.split(" - ", 5)){
				infoCurso.add(retval);
		    }
		
			Curso curso = new Curso();
			curso.setNomeCurso(infoCurso.get(1));
			curso.setTurno(infoCurso.get(2));
			curso.setModalidade(infoCurso.get(3));
			curso.setIdentificador(key);
			
				
			return curso;
		
	}
	
	
}
