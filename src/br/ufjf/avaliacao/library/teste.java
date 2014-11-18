package br.ufjf.avaliacao.library;


import br.ufjf.avaliacao.model.Curso;



public class teste {

	public static void main(String[] args) {
		
//		Curso curso = GetCurso.getCursoMatricula("");
//		System.out.println(curso.getNomeCurso());
//		System.out.println(curso.getModalidade());
//		System.out.println(curso.getTurno());
		
		System.out.println(removerNumeros(""));
		
	}


	private static String removerNumeros(String matricula)//a especialização do curso é dada em caracteres ao final da matricula
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
	
	
}
