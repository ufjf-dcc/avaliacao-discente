package br.ufjf.avaliacao.business;

public class GenericBusiness {

	//Verifica a validade de um campo string, não deixando valores nulos nem espaços em branco 
		public boolean campoStrValido(String campo) {
			if(campo==null)
				return false;
			else if (campo.trim().length()==0)
				return false;
			else return true;
		}
}
