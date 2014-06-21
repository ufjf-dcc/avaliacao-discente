package br.ufjf.avaliacao.controller;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.zkoss.bind.annotation.Init;

import br.ufjf.avaliacao.model.RespostaEspecifica;

public class RespostasEspecificasController extends GenericController {

	private List<RespostaEspecifica> respostas = new ArrayList<RespostaEspecifica>();

	@SuppressWarnings("unchecked")
	@Init
	public void init() throws HibernateException, Exception {
		testaPermissaoCoord();
		if (session.getAttribute("respostas") != null) {
			respostas = ((List<RespostaEspecifica>) session
					.getAttribute("respostas"));
		}
	}

	public List<RespostaEspecifica> getRespostas() {
		return respostas;
	}

	public void setRespostas(List<RespostaEspecifica> respostas) {
		this.respostas = respostas;
	}

}
