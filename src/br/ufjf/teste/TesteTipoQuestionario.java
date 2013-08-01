package br.ufjf.teste;

import br.ufjf.model.TipoQuestionario;
import br.ufjf.persistent.impl.TipoQuestionarioDAO;

public class TesteTipoQuestionario {
	
	public static void main(String[] args) {
	TipoQuestionario tipoQuestionario = new TipoQuestionario();
	TipoQuestionarioDAO tipoQuestionarioDAO = new TipoQuestionarioDAO();
	tipoQuestionario.setTipoQuestionario("a");
	tipoQuestionarioDAO.salvar(tipoQuestionario);
	}
} 

