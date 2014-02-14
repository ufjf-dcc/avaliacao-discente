package br.ufjf.avaliacao.business;

import br.ufjf.avaliacao.model.RespostaEspecifica;

public class AddRespostaEspecificaBusiness extends GenericBusiness {

	public RespostaEspecifica validaRespostaEspe(RespostaEspecifica r) {
		if (campoStrValido(r.getRespostaEspecifica())) {
			r.setRespostaEspecifica(r.getRespostaEspecifica().trim());
			return r;
		}
		return null;
	}
}
