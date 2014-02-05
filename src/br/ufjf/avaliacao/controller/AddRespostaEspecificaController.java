package br.ufjf.avaliacao.controller;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import br.ufjf.avaliacao.business.AddRespostaEspecificaBusiness;
import br.ufjf.avaliacao.model.Pergunta;
import br.ufjf.avaliacao.model.RespostaEspecifica;
import br.ufjf.avaliacao.persistent.impl.RespostaEspecificaDAO;

public class AddRespostaEspecificaController extends GenericController {

	List<RespostaEspecifica> respostas = new ArrayList<RespostaEspecifica>();
	RespostaEspecifica resposta = new RespostaEspecifica();

	@Command
	@NotifyChange({ "respostas", "resposta" })
	public void adicionarResposta() {
		if (new AddRespostaEspecificaBusiness().campoStrValido(resposta
				.getRespostaEspecifica())) {
			resposta.setRespostaEspecifica(resposta.getRespostaEspecifica()
					.trim());
			respostas.add(resposta);
			resposta = new RespostaEspecifica();
		}
	}

	@Command
	@NotifyChange("respostas")
	public void excluirResposta(@BindingParam("resposta") RespostaEspecifica r) {
		respostas.remove(r);
	}

	@Command
	public void terminar(@BindingParam("window") final Window window) {
		if (!respostas.isEmpty()) {
			for (RespostaEspecifica r : respostas) {
				r.setPergunta((Pergunta) session
						.getAttribute("perguntaEspecifica"));
			}
			if (new RespostaEspecificaDAO().salvarLista(respostas)) {
				Messagebox.show("Respostas adicionadas", "Concluído",
						Messagebox.OK, Messagebox.INFORMATION,
						new EventListener<Event>() {
							@Override
							public void onEvent(Event event) throws Exception {
								window.detach();
							}
						});
			}
		} else {
			Messagebox.show("Nenhuma resposta adicionada ainda.");
		}
	}

	@Command
	@NotifyChange("respostas")
	public void down(@BindingParam("resposta") RespostaEspecifica resposta) {
		int index = respostas.indexOf(resposta);
		RespostaEspecifica aux = respostas.get(index + 1);
		respostas.set(index + 1, resposta);
		respostas.set(index, aux);
	}

	@Command
	@NotifyChange("respostas")
	public void up(@BindingParam("resposta") RespostaEspecifica resposta) {
		int index = respostas.indexOf(resposta);
		RespostaEspecifica aux = respostas.get(index - 1);
		respostas.set(index - 1, resposta);
		respostas.set(index, aux);
	}

	public List<RespostaEspecifica> getRespostas() {
		return respostas;
	}

	public void setRespostas(List<RespostaEspecifica> respostas) {
		this.respostas = respostas;
	}

	public RespostaEspecifica getResposta() {
		return resposta;
	}

	public void setResposta(RespostaEspecifica resposta) {
		this.resposta = resposta;
	}

}
