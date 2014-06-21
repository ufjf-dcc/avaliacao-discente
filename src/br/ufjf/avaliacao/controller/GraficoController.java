package br.ufjf.avaliacao.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.zkoss.zul.ChartModel;
import org.zkoss.zul.PieModel;

import br.ufjf.avaliacao.model.RespostaEspecifica;
import br.ufjf.avaliacao.model.Grafico;
import br.ufjf.avaliacao.model.Pergunta;
import br.ufjf.avaliacao.model.Turma;
import br.ufjf.avaliacao.model.Usuario;

public class GraficoController extends GenericController {

	private List<RespostaEspecifica> respostas;
	private String type;
	private String url;

	public List<RespostaEspecifica> getRespostas() {
		return (List<RespostaEspecifica>) session.getAttribute("respostas");
	}

	public void setRespostas(List<RespostaEspecifica> respostas) {
		this.respostas = respostas;
	}

	public String getType() {
		return (String) session.getAttribute("type");
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return (String) ((Grafico) session.getAttribute("grafico")).getURL();
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
