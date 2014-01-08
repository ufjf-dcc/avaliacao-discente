package br.ufjf.avaliacao.business;

import org.zkoss.zul.Messagebox;

import br.ufjf.avaliacao.model.PrazoQuestionario;
import br.ufjf.avaliacao.model.Questionario;

public class QuestionariosBusiness extends GenericBusiness {

	public boolean prazoValido(PrazoQuestionario prazo) {
		if(prazo.getDataFinal()!=null && prazo.getDataInicial()!=null) {
			if(campoStrValido(prazo.getDataFinalFormatada()) && campoStrValido(prazo.getDataInicialFormatada())) {
				return true;
			}
		}
		return false;
	}
	public boolean tituloValido(Questionario questionario){
 		if(questionario.getTituloQuestionario()!=null && campoStrValido(questionario.getTituloQuestionario())){
 			return true;
 		}
 		Messagebox.show("O questionário não possui um titulo correto");
		return false;
	}
}
