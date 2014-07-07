package br.ufjf.avaliacao.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.HibernateException;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import br.ufjf.avaliacao.business.GenericBusiness;
import br.ufjf.avaliacao.business.QuestionariosBusiness;
import br.ufjf.avaliacao.model.Avaliacao;
import br.ufjf.avaliacao.model.Pergunta;
import br.ufjf.avaliacao.model.PrazoQuestionario;
import br.ufjf.avaliacao.model.Questionario;
import br.ufjf.avaliacao.model.RespostaEspecifica;
import br.ufjf.avaliacao.persistent.impl.AvaliacaoDAO;
import br.ufjf.avaliacao.persistent.impl.PerguntaDAO;
import br.ufjf.avaliacao.persistent.impl.PrazoQuestionarioDAO;
import br.ufjf.avaliacao.persistent.impl.QuestionarioDAO;
import br.ufjf.avaliacao.persistent.impl.RespostaEspecificaDAO;
import br.ufjf.avaliacao.persistent.impl.TurmaDAO;

public class QuestionariosController extends GenericController {

	private QuestionarioDAO questionarioDAO = new QuestionarioDAO();
	private List<Questionario> questionariosCoord = questionarioDAO
			.retornaQuestinariosCursoTipo(usuario.getCurso(), 0);
	private List<Questionario> questionariosProf = questionarioDAO
			.retornaQuestinariosCursoTipo(usuario.getCurso(), 1);
	private List<Questionario> questionariosAuto = questionarioDAO
			.retornaQuestinariosCursoTipo(usuario.getCurso(), 2);
	private List<Questionario> questionariosInfra = questionarioDAO
			.retornaQuestinariosCursoTipo(usuario.getCurso(), 3);
	private boolean ativo;
	private List<Pergunta> perguntasSessao = new ArrayList<Pergunta>();
	private Pergunta pergunta = new Pergunta();
	private PerguntaDAO perguntaDAO = new PerguntaDAO();
	private AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
	private Avaliacao avaliacao = new Avaliacao();
	private Questionario questionario = new Questionario();
	private Questionario questSessao = new Questionario();
	private List<Integer> tiposQuestionario = Arrays.asList(0, 1, 2, 3);
	private List<Integer> tiposPergunta = Arrays.asList(0, 1, 2, 3);
	private PrazoQuestionario prazo = new PrazoQuestionario();
	private List<PrazoQuestionario> prazos = new ArrayList<PrazoQuestionario>();
	private List<PrazoQuestionario> prazosSessao = new ArrayList<PrazoQuestionario>();
	private PrazoQuestionarioDAO prazoDAO = new PrazoQuestionarioDAO();
	private TurmaDAO turmaDAO = new TurmaDAO();
	private List<String> semestres = turmaDAO.getAllSemestres();
	private String semestreEscolhido;

	private List<RespostaEspecifica> respostas = new ArrayList<RespostaEspecifica>();
	private RespostaEspecifica resposta = new RespostaEspecifica();
	private RespostaEspecificaDAO respostaEspecificaDAO = new RespostaEspecificaDAO();

	private List<Pergunta> perguntas = new ArrayList<Pergunta>();


	
	@Init
	public void init() throws HibernateException, Exception {
		testaPermissaoCoord();
		if (((Questionario) session.getAttribute("questionario")) != null) 
		{
			questSessao = (Questionario) session.getAttribute("questionario");
			prazosSessao = questSessao.getPrazos();
			perguntasSessao = questSessao.getPerguntas();
		}
		
	}

	
	
	@Command
	public void criarQuest() { // seta novos parametros para um novo questionario e abre a janela de questionario

		session.setAttribute("indice_tab", 0);
		session.setAttribute("lista_de_objetos",new ArrayList<ArrayList<String>>());
		((List<List<String>>) session.getAttribute("lista_de_objetos")).add(new ArrayList<String>());
		session.setAttribute("spinnerInicio", new ArrayList<Integer>());
		session.setAttribute("spinnerFinal", new ArrayList<Integer>());
		session.setAttribute("titulos", new ArrayList<String>());
		session.setAttribute("tabs", new ArrayList<Tab>());
		session.setAttribute("ultimo_indice_lista", 0);
		session.setAttribute("tipo_pergunta", new ArrayList<String>());
		session.setAttribute("tipoPergunta", 0);

		
		Window window = (Window) Executions.createComponents(
				"/teste.zul", null, null);
		window.doModal();
		
	}

	@Command
	public void criarPergunta() // seta novos parametros para um nova pergunta do questionario setado acima
	{
		((List<Pergunta>) session.getAttribute("perguntas")).add(new Pergunta());
		((List<List<String>>) session.getAttribute("lista_de_objetos")).add(new ArrayList<String>());
	}
	
	@Command 
	public void addOpcao(@BindingParam("frame") Iframe frame,@BindingParam("index") String index)
	{	
		int indice = Integer.parseInt(index);
		session.setAttribute("ultimo_indice_lista", indice);
		frame.invalidate();
		((List<List<String>>) session.getAttribute("lista_de_objetos")).get(indice).add(nova_resposta);
	}
	
	@Command
	public void salvarMudancaOpcao(@BindingParam("opcao") String opcao, // para cada mudanca de algum valor de alguma linha no opcoes.zul aqui essa alteração tambem será computada
			@BindingParam("nova_opcao") String nova_opcao, @BindingParam("url") String url) {
		List<String> aux = new ArrayList<String>();
		aux = ((List<List<String>>) session.getAttribute("lista_de_objetos")).get(0);
		for(int i=0;i<aux.size();i++)
		{
			if(aux.get(i)==opcao)
			{
				aux.set(i, nova_opcao);
				break;
			}
		}
	}
	
	@Command
	public void teste(@BindingParam("index") String index)
	{
		
		System.out.println("spinnerInicio - "+((List<Integer>) session.getAttribute("spinnerInicio")).size());
		for(int i=0;i<((List<Integer>) session.getAttribute("spinnerInicio")).size();i++)
			System.out.println(i+" = "+((List<Integer>) session.getAttribute("spinnerInicio")).get(i));
		System.out.println("spinnerFinal - "+((List<Integer>) session.getAttribute("spinnerFinal")).size());
		for(int i=0;i<((List<Integer>) session.getAttribute("spinnerFinal")).size();i++)
			System.out.println(i+" = "+((List<Integer>) session.getAttribute("spinnerFinal")).get(i));
		System.out.println("titulos - "+((List<Integer>) session.getAttribute("titulos")).size());
		for(int i=0;i<((List<String>) session.getAttribute("titulos")).size();i++)
			System.out.println(i+" = "+((List<Integer>) session.getAttribute("titulos")).get(i));
		

	}
	@Command
	public void mudancaTituloPergunta(@BindingParam("titulo") String titulo,@BindingParam("index") String index)// para cada mudança no titulo da pergunta, ela será salva em seu respectivo lugar
	{
		int indice = Integer.parseInt(index);
		((List<String>) session.getAttribute("titulos")).set(indice,titulo);
	}
	
	public String getTituloPergunta()
	{
		((List<String>) session.getAttribute("titulos")).add("");//add um novo espaço para fazer as operações
		return "";
	}
	
	@Command
	public void tabInicial(@BindingParam("tab") Tab tab) { // seta a tab inicial, que a partir dela é possivel chegar a todas as outras
		((List<Tab>) session.getAttribute("tabs")).add(tab);
		session.setAttribute("inicial_tab", tab);
	}
	
	@Command
	public void novoTitulo(@BindingParam("titulo") String titulo) {
		((List<String>) session.getAttribute("titulos")).set((int) session.getAttribute("indice_pergunta"), titulo);
		((List<Tab>) session.getAttribute("tabs")).get((int) session.getAttribute("indice_pergunta")).setLabel(titulo);
	}
	
	public String getURL() { // rerna a url do frama, mais tarde sera usado como se fosse o metodo get para adquirir o indice da pergunta
		return "/opcoes.zul?"+((int)session.getAttribute("indice_tab")-1);
	}
	
	@Command
	public int getNovoIndex() { // informa qual é o proximo valor de indice(usado para cada frame saber a qual pergunta pertence)
		session.setAttribute("indice_tab", 1 + (int) session.getAttribute("indice_tab"));
		return ((int) session.getAttribute("indice_tab") - 1);
	}
	

	@Command
	public int getIndex() {
		return ((int) session.getAttribute("indice_tab")-1);
	}
	
	public List<Tab> getTabs() // retorna todas as tab a partir da primeira
	{
		List<Tab> aux = new ArrayList<Tab>();
		session.setAttribute("tab", ((Tab) session.getAttribute("inicial_tab")));
		while(((Tab) session.getAttribute("tab")) != null)
		{
			aux.add((Tab) session.getAttribute("tab"));
			session.setAttribute("tab", ((Tab) session.getAttribute("tab")).getNextSibling());
		}
		return aux;
	}
	
	@Command
	public void valorSpinnerInicio(@BindingParam("valor") int valor,@BindingParam("index") String index)// seta novos valores de spinner a cada vez que ele for modificado, o valor é guardado em seu devido lugar
	{	
		int indice = Integer.parseInt(index);
		((List<Integer>) session.getAttribute("spinnerInicio")).set(indice,valor);
	}
	
	@Command
	public void valorSpinnerFinal(@BindingParam("valor") int valor,@BindingParam("index") String index)// seta novos valores de spinner a cada vez que ele for modificado, o valor é guardado em seu devido lugar
	{	
		int indice = Integer.parseInt(index);
		((List<Integer>) session.getAttribute("spinnerFinal")).set(indice,valor);
	}
	

	@Command
	public void tipoPergunta(@BindingParam("textbox") Textbox text,
			@BindingParam("div") Div div, @BindingParam("button") Button b,
			@BindingParam("frame") Iframe frame, @BindingParam("index") String index,
			@BindingParam("combo") Combobox combo) {
		
		int indice = Integer.parseInt(index);
		
		String tipo;
		if (combo.getSelectedIndex() == 0)
			tipo = "Texto";
		else if (combo.getSelectedIndex() == 1)
			tipo = "Caixa de Seleção";
		else if (combo.getSelectedIndex() == 2)
			tipo = "Múltipla Escolha";
		else
			tipo = "Escala Numérica";
		
		combo.setText(tipo);
		
		if (combo.getSelectedIndex() == 0) {
			text.setDisabled(true);
			text.setVisible(true);
			div.setVisible(false);
			b.setDisabled(true);
			frame.setVisible(false);
		} else {
			if (combo.getSelectedIndex() == 3) {
				text.setVisible(false);
				div.setVisible(true);
				b.setDisabled(true);
				frame.setVisible(false);
			} else {
				text.setVisible(true);
				text.setDisabled(false);
				div.setVisible(false);
				b.setDisabled(false);
				frame.setVisible(true);
			}
		}
	}



	public int getTipoPergunta() {
		return ((int) session.getAttribute("tipoPergunta")) ;
	}
	
	public void setTipoPergunta(int valor) {
		session.setAttribute("tipoPergunta", valor);
	}
	
	@Command
	@NotifyChange("perguntas")
	public void duplicarPergunta(@BindingParam("pergunta") Pergunta p) {
		Pergunta pergunta = new Pergunta();
		pergunta.setTituloPergunta(p.getTituloPergunta());
		pergunta.setTipoPergunta(p.getTipoPergunta());
		if (p.getTipoPergunta() != 0) {
			List<RespostaEspecifica> respostas = new ArrayList<RespostaEspecifica>();
			for (RespostaEspecifica r : p.getRespostasEspecificas()) {
				RespostaEspecifica re = new RespostaEspecifica();
				re.setPergunta(pergunta);
				re.setRespostaEspecifica(r.getRespostaEspecifica());
				respostas.add(re);
			}
			pergunta.setRespostasEspecificas(respostas);
		}
		pergunta.setQuestionario(questionario);
		perguntas.add(pergunta);
	}

	@Command
	public void criarQuestionario() {
		if ((new GenericBusiness().campoStrValido(questionario
				.getTituloQuestionario()))
				&& (questionario.getTipoQuestionario() != null)) {
			if (!perguntas.isEmpty()) {
				questionario.setCurso(usuario.getCurso());
				if (questionarioDAO.salvar(questionario)) {
					if (perguntaDAO.salvarLista(perguntas)) {
						for (Pergunta p : perguntas) {
							if (p.getTipoPergunta() != 0) {
								respostaEspecificaDAO.salvarLista(p
										.getRespostasEspecificas());
							}
						}
						Messagebox.show("Questionário criado com sucesso",
								"Concluído", Messagebox.OK,
								Messagebox.INFORMATION,
								new EventListener<Event>() {
									@Override
									public void onEvent(Event event)
											throws Exception {
										Executions.sendRedirect(null);
									}
								});
					}
				}
			} else {
				Messagebox
						.show("Nenhuma pergunta adicionada ao questionário ainda. Impossível criar.");
			}
		}
	}

	@Command
	public void zerarQuestionario() {
		session.setAttribute("respostas", null);
	}

	@Command
	public void editarQuest(
			@BindingParam("questionario") Questionario questionario) {
		session.setAttribute("questionario", questionario);
		Window window = (Window) Executions.createComponents(
				"/editarQuestionario.zul", null, null);
		window.doModal();
	}

	@Command
	public void verQuest(@BindingParam("questionario") Questionario questionario) {
		session.setAttribute("questionario", questionario);
		Window window = (Window) Executions.createComponents(
				"/verQuestionario.zul", null, null);
		window.doModal();
	}

	@Command
	public void respostas() {
		Window w = (Window) Executions.createComponents(
				"/respostasEspecificas.zul", null, null);
		w.doModal();
	}

	@Command
	@NotifyChange({ "respostas", "resposta" })
	public void addRespostaEspecifica() {
		if (new GenericBusiness().campoStrValido(resposta
				.getRespostaEspecifica())) {
			resposta.setRespostaEspecifica(resposta.getRespostaEspecifica()
					.trim());
			resposta.setPergunta(pergunta);
			respostas.add(resposta);
			session.setAttribute("respostas", respostas);
			resposta = new RespostaEspecifica();
		}
	}

	/*
	 * Método privado para finalizar a criação de uma pergunta e coloca la no
	 * questionario. Verificações são feitas antes da chamada desse método.
	 */
	private void finalizar(Button b) {
		pergunta.setTituloPergunta(pergunta.getTituloPergunta().trim());
		pergunta.setQuestionario(questionario);
		perguntas.add(pergunta);
		if (pergunta.getTipoPergunta() == 3) {
			respostas = new ArrayList<RespostaEspecifica>();
			for (int i = ((List<Integer>) session.getAttribute("spinnerInicio")).get((int) session.getAttribute("indice_pergunta")); i <= ((List<Integer>) session.getAttribute("spinnerFinal")).get((int) session.getAttribute("indice_pergunta")); i++) {
				resposta = new RespostaEspecifica();
				resposta.setRespostaEspecifica(Integer.toString(i));
				resposta.setPergunta(pergunta);
				respostas.add(resposta);
			}
		}
		if (pergunta.getTipoPergunta() != 0) {
			pergunta.setRespostasEspecificas(respostas);
		}
		pergunta = new Pergunta();
		respostas = new ArrayList<RespostaEspecifica>();
		session.setAttribute("respostas", respostas);
		b.setDisabled(true);
		Button p = (Button) b.getPreviousSibling();
		p.setDisabled(true);
	}

	@Command
	@NotifyChange({ "perguntas", "pergunta" })
	public void addPergunta(@BindingParam("button") Button b) {
		if ((new GenericBusiness().campoStrValido(pergunta.getTituloPergunta()))
				&& (pergunta.getTipoPergunta() != null)) {
			if (pergunta.getTipoPergunta() != 3) {
				if (pergunta.getTipoPergunta() == 0) {
					finalizar(b);
				} else {
					if (!respostas.isEmpty()) {
						finalizar(b);
					} else {
						Messagebox
								.show("Nenhuma resposta cadastrada para essa pergunta.");
					}
				}
			} else {
				if (((List<Integer>) session.getAttribute("spinnerInicio")).get((int) session.getAttribute("indice_pergunta")) > ((List<Integer>) session.getAttribute("spinnerFinal")).get((int) session.getAttribute("indice_pergunta"))) {
					finalizar(b);
				} else {
					Messagebox.show("Escala inicial menor ou igual à final");
				}
			}
		}
	}

	@Command
	@NotifyChange({ "prazos", "prazo" })
	public void adicionaPrazo() {
		prazos.add(prazo);
		prazo = new PrazoQuestionario();
	}

	@Command
	public void adcPrazo(@BindingParam("questionario") Questionario questionario) {
		session.setAttribute("questionario", questionario);
		Window window = (Window) Executions.createComponents("/add-prazo.zul",
				null, null);
		window.doModal();
	}

	@Command
	public void addPrazo(@BindingParam("window") Window w) {
		if (new QuestionariosBusiness().prazoValido(prazo)) {
			if (validadaData(prazo) && semestreEscolhido!="") {
				prazo.setQuestionario((Questionario) session
						.getAttribute("questionario"));
				prazo.setSemestre(semestreEscolhido);
				prazoDAO.salvar(prazo);
				prazos.add(prazo);
				w.detach();
				Messagebox.show("Prazo Adicionado!");
			}
		} else {
			Messagebox.show("Data final e/ ou inicial invalida");
		}
		w.detach();
	}

	private boolean validadaData(PrazoQuestionario prazo) {
		if (prazo.getDataFinal().before(prazo.getDataInicial())) {
			Messagebox.show("Data final antes da data inicial");
			return false;
		}
		if (prazosSessao != null)
			for (int i = prazosSessao.size() - 1; i >= 0; i--) {
				boolean invalido = true;
				if (prazosSessao.get(i).getDataFinal()
						.before(prazo.getDataInicial()))
					invalido = false;
				if (prazosSessao.get(i).getDataInicial()
						.after(prazo.getDataInicial()))
					invalido = false;
				if (invalido) {
					Messagebox.show("N�o pode criar nessa data");
					return false;
				}
			}
		return true;
	}

	@Command
	public void excluiPrazo(@BindingParam("prazo") PrazoQuestionario prazo) { // deleta
																				// um
																				// prazo
																				// se
																				// for
																				// possivel
		if (avaliacaoDAO.prazoFoiUsado(prazo))
			Messagebox.show("Prazo nao pode ser excluido, ja esta em uso");

		else {
			prazoDAO.exclui(prazo); // exclui o prazo
			if (questionario.isAtivo()) {
				questionario.setAtivo(false);
				questionarioDAO.editar(questionario);
			}

			Messagebox.show("Prazo excluido", "Concluido", Messagebox.OK,
					Messagebox.INFORMATION, new EventListener<Event>() {
						@Override
						public void onEvent(Event event) throws Exception {
							Executions.sendRedirect(null);
						}
					});
		}

	}

	@Command
	@NotifyChange({ "perguntas", "pergunta" })
	public void excluiPergunta(@BindingParam("pergunta") Pergunta pergunta) {
		perguntas.remove(pergunta);
	}

	@Command
	public void exclui() {
		/*
		 * Exclusão do questionario funcionando. Faltam apenas fazer as
		 * verificicações para ver se não está em vigor o prazo e se o
		 * questionario já foi avaliado por alguém, consequentemente, suas
		 * perguntas, respostas, e respostas especificas estao todas ligados.
		 */
		questionario = (Questionario) session.getAttribute("questionario");
		perguntas = questionario.getPerguntas();
		prazos = questionario.getPrazos();

		for (Pergunta p : perguntas) {
			respostaEspecificaDAO.excluiLista(p.getRespostasEspecificasBanco());
		}
		if (perguntaDAO.excluiLista(perguntas)) {
			prazoDAO.excluiLista(prazos);
			if (questionarioDAO.exclui(questionario)) {
				Messagebox.show("Questionário Excluído", "Concluído",
						Messagebox.OK, Messagebox.INFORMATION,
						new EventListener<Event>() {
							@Override
							public void onEvent(Event event) throws Exception {
								Executions.sendRedirect(null);
							}
						});
				listaQuestionarios(questionario.getTipoQuestionario()).remove(
						questionario);
			}
		}
	}

	@Command
	public void salvarQuest() {
		if (perguntas.size() > 1) {
			if (questionarioDAO.editar(questionario))
				if (perguntaDAO.excluiLista(perguntasSessao)) {
					for (Pergunta pergunta : perguntas) {
						pergunta.setQuestionario(questionario);
					}

					if (perguntaDAO.salvarLista(perguntas)) {
						Messagebox.show("Questionário Salvo", "Concluído",
								Messagebox.OK, Messagebox.INFORMATION,
								new EventListener<Event>() {
									@Override
									public void onEvent(Event event)
											throws Exception {
										Executions.sendRedirect(null);
									}
								});
					}
				}
		}
	}

	@Command
	@NotifyChange({ "perguntas", "pergunta" })
	public void top(@BindingParam("pergunta") Pergunta pergunta) {
		for (int index = perguntas.indexOf(pergunta); index > 0; index--) {
			perguntas.set(index, perguntas.get(index - 1));
		}
		perguntas.set(0, pergunta);
	}

	@Command
	@NotifyChange({ "perguntas", "pergunta" })
	public void bottom(@BindingParam("pergunta") Pergunta pergunta) {
		for (int index = perguntas.indexOf(pergunta); index < perguntas.size() - 1; index++) {
			perguntas.set(index, perguntas.get(index + 1));
		}
		perguntas.set(perguntas.size() - 1, pergunta);
	}

	@Command
	@NotifyChange("perguntas")
	public void down(@BindingParam("pergunta") Pergunta pergunta) {
		int index = perguntas.indexOf(pergunta);
		Pergunta aux = perguntas.get(index + 1);
		perguntas.set(index + 1, pergunta);
		perguntas.set(index, aux);
	}

	@Command
	@NotifyChange("perguntas")
	public void up(@BindingParam("pergunta") Pergunta pergunta) {
		int index = perguntas.indexOf(pergunta);
		Pergunta aux = perguntas.get(index - 1);
		perguntas.set(index - 1, pergunta);
		perguntas.set(index, aux);
	}

	@Command
	@NotifyChange("questionario")
	public void ativa(@BindingParam("questionario") Questionario questionario) {
		if (!questionario.getPrazos().isEmpty()) {
			boolean ativo = true;
			for (Questionario q : listaQuestionarios(questionario
					.getTipoQuestionario())) {
				if (q.getIdQuestionario() == questionario.getIdQuestionario()) {
					if (!q.isAtivo())
						q.setAtivo(true);
					else
						q.setAtivo(false);
				} else
					q.setAtivo(false);

				questionarioDAO.editar(q);
				ativo = q.isAtivo();
			}

			if (ativo) {
				Messagebox.show("Ativado", "Concluido", Messagebox.OK,
						Messagebox.INFORMATION, new EventListener<Event>() {
							@Override
							public void onEvent(Event event) throws Exception {
								Executions.sendRedirect(null);
							}
						});
			} else {
				Messagebox.show("Desativado", "Concluido", Messagebox.OK,
						Messagebox.INFORMATION, new EventListener<Event>() {
							@Override
							public void onEvent(Event event) throws Exception {
								Executions.sendRedirect(null);
							}
						});
			}
		} else
			Messagebox.show("Questionário não possui prazo");
	}

	public List<Pergunta> getPerguntas() {
		return perguntas;
	}

	public void setPerguntas(List<Pergunta> perguntas) {
		this.perguntas = perguntas;
	}

	public Pergunta getPergunta() {
		return ((List<Pergunta>) session.getAttribute("perguntas")).get(getIndex());
	}

	public void setPergunta(Pergunta pergunta) {
		this.pergunta = pergunta;
	}

	public QuestionarioDAO getQuestionarioDAO() {
		return questionarioDAO;
	}

	public void setQuestionarioDAO(QuestionarioDAO questionarioDAO) {
		this.questionarioDAO = questionarioDAO;
	}

	public List<Questionario> listaQuestionarios(Integer tipoQuestionario) {
		if (tipoQuestionario == 0)
			return questionariosCoord;
		else if (tipoQuestionario == 1)
			return questionariosProf;
		else if (tipoQuestionario == 2)
			return questionariosAuto;
		else
			return questionariosInfra;
	}

	public Questionario getQuestionario() {
		return questionario;
	}

	public void setQuestionario(Questionario questionario) {
		this.questionario = questionario;
	}

	public List<Questionario> getQuestionariosCoord() {
		return questionariosCoord;
	}

	public void setQuestionariosCoord(List<Questionario> questionariosCoord) {
		this.questionariosCoord = questionariosCoord;
	}

	public List<Questionario> getQuestionariosProf() {
		return questionariosProf;
	}

	public void setQuestionariosProf(List<Questionario> questionariosProf) {
		this.questionariosProf = questionariosProf;
	}

	public List<Questionario> getQuestionariosAuto() {
		return questionariosAuto;
	}

	public void setQuestionariosAuto(List<Questionario> questionariosAuto) {
		this.questionariosAuto = questionariosAuto;
	}

	public List<Questionario> getQuestionariosInfra() {
		return questionariosInfra;
	}

	public void setQuestionariosInfra(List<Questionario> questionariosInfra) {
		this.questionariosInfra = questionariosInfra;
	}

	public boolean isAtivo() {
		return ativo;
	}

	@Command
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Avaliacao getAvaliacao() {
		return avaliacao;
	}

	public void setAvaliacao(Avaliacao avaliacao) {
		this.avaliacao = avaliacao;
	}
	
	public List<Integer> getTiposQuestionario() {
		return tiposQuestionario;
	}

	public void setTiposQuestionario(List<Integer> tiposQuestionario) {
		this.tiposQuestionario = tiposQuestionario;
	}

	public List<Pergunta> getPerguntasSessao() {
		return perguntasSessao;
	}

	public void setPerguntasSessao(List<Pergunta> perguntasSessao) {
		this.perguntasSessao = perguntasSessao;
	}

	public PrazoQuestionario getPrazo() {
		return prazo;
	}

	public void setPrazo(PrazoQuestionario prazo) {
		this.prazo = prazo;
	}

	public List<PrazoQuestionario> getPrazos() {
		return prazos;
	}

	public void setPrazos(List<PrazoQuestionario> prazos) {
		this.prazos = prazos;
	}

	
	public List<Integer> getTiposPergunta() {
		return tiposPergunta;
	}

	public void setTiposPergunta(List<Integer> tiposPergunta) {
		this.tiposPergunta = tiposPergunta;
	}

	public List<String> getSemestres() {
		return semestres;
	}

	public void setSemestres(List<String> semestres) {
		this.semestres = semestres;
	}

	public String getSemestreEscolhido() {
		return semestreEscolhido;
	}

	public void setSemestreEscolhido(String semestre) {
		this.semestreEscolhido = semestre;
	}

	public Integer getSpinnerInicio() {
		((List<Integer>) session.getAttribute("spinnerInicio")).add(0);
		return ((List<Integer>) session.getAttribute("spinnerInicio")).get(getIndex());
	}


	public Integer getSpinnerFinal() {
		((List<Integer>) session.getAttribute("spinnerFinal")).add(0);
		return ((List<Integer>) session.getAttribute("spinnerFinal")).get(getIndex());
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

	public Questionario getQuestSessao() {
		return questSessao;
	}

	public void setQuestSessao(Questionario questSessao) {
		this.questSessao = questSessao;
	}

	public List<PrazoQuestionario> getPrazosSessao() {
		return prazosSessao;
	}

	public void setPrazosSessao(List<PrazoQuestionario> prazosSessao) {
		this.prazosSessao = prazosSessao;
	}

	public List<String> getLista_opcoes() {
		List<String> aux = new ArrayList<String>();
		return aux;
	}


}
