package br.ufjf.avaliacao.business;

import br.ufjf.avaliacao.model.PrazoQuestionario;

public class QuestionariosBusiness extends GenericBusiness {

	public boolean prazoValido(PrazoQuestionario prazo) {
		if(prazo.getDataFinal()!=null && prazo.getDataInicial()!=null) {
			if(campoStrValido(prazo.getDataFinalFormatada()) && campoStrValido(prazo.getDataInicialFormatada())) {
				return true;
			}
		}
		return false;
	}
}
