package br.ufjf.avaliacao.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.text.TabableView;

import org.hibernate.HibernateException;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabs;
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
import br.ufjf.avaliacao.persistent.impl.SemestreDAO;
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
	private String titulo_questionario;
	private int tipo_questionario;
	private Tabbox tabbox;

	
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
		// a ideia é pegar todos os valores da pagina do zk, e ao final, quando pedir para salvar, faz a verificação dos valores se for o caso salva o uqestionario e as perguntas
		//algumas funções foram alterados para se adaptarem ao zul mas d
		
		session.setAttribute("indice_tab", 0);
		session.setAttribute("lista_de_objetos",new ArrayList<ArrayList<String>>());
		((List<List<String>>) session.getAttribute("lista_de_objetos")).add(new ArrayList<String>());
		session.setAttribute("spinnerInicio", new ArrayList<Integer>());
		session.setAttribute("spinnerFinal", new ArrayList<Integer>());
		session.setAttribute("titulos", new ArrayList<String>());
		session.setAttribute("tabs", new ArrayList<Tab>());
		session.setAttribute("ultimo_indice_acessado", 0);
		session.setAttribute("tipo_pergunta", new ArrayList<Integer>());
		session.setAttribute("tipoPergunta", 0);
		session.setAttribute("criando_tab_opcao",false);
		session.setAttribute("criando_tab_tipo_pergunta",true);
		session.setAttribute("index_tipo_pergunta",0);
		session.setAttribute("nova_alternativa",true);
		session.setAttribute("primeiro_listitem", new ArrayList<Listitem>());
		session.setAttribute("obrigatorio", new ArrayList<Boolean>());
		session.setAttribute("tabbox", new Tabbox());
		session.setAttribute("mudanca_perguntas",false);
		session.setAttribute("mudanca_titulo_pergunta",false);
		session.setAttribute("deletar_pergunta",false);
		session.setAttribute("indice_deletar_pergunta", -1);//deletar
		session.setAttribute("indice_mudanca_titulo_pergunta", -1);
		session.setAttribute("perguntas_deletadas", new ArrayList<Integer>());//deletar
		session.setAttribute("duplicar_pergunta", false);//duplicar
		session.setAttribute("indice_duplicar_pergunta", -1);//duplicar
		session.setAttribute("aux_duplicar_pergunta", 0);//duplicar
		session.setAttribute("obrigatorio_inicio", false);//duplicar
		session.setAttribute("mudar_combo", false);//duplicar
		session.setAttribute("chekbox", null);//duplicar
		
		titulo_questionario = "";
		tipo_questionario = -1;
		tabbox = new Tabbox();
		
		
		
		Window window = (Window) Executions.createComponents(
				"/criarQuestionario.zul", null, null);
		window.doModal();
		
	}
	
	public boolean questionarioValido() // valida todos os campos antes de salvar questionario
	{
		if(tipo_questionario==-1)//verifica o tipo de questionario
		{
			Messagebox.show("Escolha o tipo de questionário");
			return false;
		}
		GenericBusiness gb = new GenericBusiness();//verifica o titulo do questionario
		if(!gb.campoStrValido(titulo_questionario))
		{
			Messagebox.show("Preencha corretamente o titulo do questionário");
			return false;
		}
		for(int i=0;i<((List<String>)session.getAttribute("titulos")).size();i++)// verifica o titulo de cada pergunta
		{
			if(!gb.campoStrValido(((List<String>)session.getAttribute("titulos")).get(i)))
			{
				Messagebox.show("O titulo de alguma pergunta não está preenchido corretamente");
				return false;
			}
		}
		for(int i=0;i<((List<Integer>) session.getAttribute("tipo_pergunta")).size();i++)// verifica as opções
		{
			if(((List<Integer>) session.getAttribute("tipo_pergunta")).get(i)==1 
					|| ((List<Integer>) session.getAttribute("tipo_pergunta")).get(i)==2)
			{
				
					int quantidade_validos = 0;
					Listitem aux = ((List<Listitem>)session.getAttribute("primeiro_listitem")).get(i);
	
					while(aux.getNextSibling() != null)
					{
						String opcao = ((Textbox) aux.getChildren().get(0).getChildren().get(0)).getValue();
						if(gb.campoStrValido(opcao))
						{
							quantidade_validos++;
						}
						aux=(Listitem) aux.getNextSibling();
					}
					
					if(quantidade_validos == 0)
					{
						Messagebox.show("Existe alguma pergunta em que as opções não foram preenhidas corretamente");
						return false;
					}
			}
		}
		for(int i=0;i<((List<Integer>)session.getAttribute("spinnerInicio")).size();i++)//verifica os valores dos spinners
		{
			if(((List<Integer>) session.getAttribute("tipo_pergunta")).get(i)==3)
			if(((List<Integer>)session.getAttribute("spinnerInicio")).get(i)>=((List<Integer>)session.getAttribute("spinnerFinal")).get(i))
			{
				Messagebox.show("Existe alguma pergunta em que os spinners não foram preenhidos corretamente");
				return false;
			}
		}
		
		
		return true;
	}

	@Command
	public void salvarQuestionario()//salva o questionario e suas informações no banco de dados
	{
		
		if(questionarioValido())
		{
			Questionario questionario = new Questionario();
			questionario.setTipoQuestionario(tipo_questionario);
			questionario.setTituloQuestionario(titulo_questionario);
			questionario.setAtivo(false);
			questionario.setCurso(usuario.getCurso());
			QuestionarioDAO questionarioDAO = new QuestionarioDAO();
			questionarioDAO.salvar(questionario);
					
			while(((List<Boolean>) session.getAttribute("obrigatorio")).size()<((List<Integer>)session.getAttribute("tipo_pergunta")).size())
			{
				((List<Boolean>) session.getAttribute("obrigatorio")).add(false);
			}
			
			PerguntaDAO perguntaDAO = new PerguntaDAO();
			for(int i=0;i<((List<Integer>)session.getAttribute("tipo_pergunta")).size();i++)
			{
				if(!((List<Integer>) session.getAttribute("perguntas_deletadas")).contains(i))
				{	
					Pergunta pergunta = new Pergunta();
					pergunta.setQuestionario(questionario);
					pergunta.setTituloPergunta(((List<String>) session.getAttribute("titulos")).get(i));
					pergunta.setTipoPergunta(((List<Integer>)session.getAttribute("tipo_pergunta")).get(i));
					pergunta.setObrigatorio(((List<Boolean>)session.getAttribute("obrigatorio")).get(i));
					perguntaDAO.salvar(pergunta);
					if(((List<Integer>)session.getAttribute("tipo_pergunta")).get(i) == 3)
					{
						RespostaEspecificaDAO respostaDAO = new RespostaEspecificaDAO();
						for(int j=((List<Integer>)session.getAttribute("spinnerInicio")).get(i);j<=((List<Integer>)session.getAttribute("spinnerFinal")).get(i);j++)
						{
							RespostaEspecifica opcao = new RespostaEspecifica();
							opcao.setPergunta(pergunta);
							opcao.setRespostaEspecifica(String.valueOf(j));
							respostaDAO.salvar(opcao);
						}
					}
					
					else if(((List<Integer>)session.getAttribute("tipo_pergunta")).get(i) == 1 || ((List<Integer>)session.getAttribute("tipo_pergunta")).get(i) == 2)
					{
						RespostaEspecificaDAO respostaDAO = new RespostaEspecificaDAO();
						Listitem aux = ((List<Listitem>)session.getAttribute("primeiro_listitem")).get(i);
		
						while(aux.getNextSibling() != null)
						{
							RespostaEspecifica opcao = new RespostaEspecifica();
							opcao.setPergunta(pergunta);
							opcao.setRespostaEspecifica(((Textbox) aux.getChildren().get(0).getChildren().get(0)).getValue());
							respostaDAO.salvar(opcao);
							aux=(Listitem) aux.getNextSibling();
						}
					}
				}	
			}
			Messagebox.show("Questionário salvo", "Concluído",
					Messagebox.OK, Messagebox.INFORMATION,
					new EventListener<Event>() {
						@Override
						public void onEvent(Event event) throws Exception {
							Executions.sendRedirect(null);
						}
					});
		}
	
	}



	@Command // exclui questionario da lista
	public void excluirQuestionario(@BindingParam("questionario") Questionario questionario) {
		if(questionario.getPrazos().size()==0)
		{
			session.setAttribute("questionario",questionario);
			exclui();
		}
		else
		{
			Messagebox.show("O questionário não pode excluido pois possui prazos e alguem pode estar avaliando agora", "Concluido", Messagebox.OK,
					Messagebox.INFORMATION, new EventListener<Event>() {
						@Override
						public void onEvent(Event event) throws Exception {
							Executions.sendRedirect(null);
						}
					});
		}
	}



	@Command
	public void tituloQuestionario(@BindingParam("titulo") String titulo)
	{
		titulo_questionario = titulo;
	}
	
	@Command
	public void tipoQuestionario(@BindingParam("tipo") int tipo)
	{
		tipo_questionario = tipo;	
	}
	
	@Command
	public void novoListItem(@BindingParam("li") Listitem li) // para cada mudanca de algum valor de alguma linha no opcoes.zul aqui essa alteração tambem será computada
	{
		((List<Listitem>)session.getAttribute("primeiro_listitem")).add(li);
	}



	@Command
	public void tabInicial(@BindingParam("tab") Tab tab) 
	{ // seta a tab inicial, que a partir dela é possivel chegar a todas as outras
		((List<Tab>) session.getAttribute("tabs")).add(tab);
		session.setAttribute("inicial_tab", tab);
	}


	@Command //seta variaveis para dizer que é necessario duplicar os itens de uma pergunta
	public void duplicarPergunta(@BindingParam("index") String index) {
		int indice = Integer.parseInt(index);
		
		session.setAttribute("duplicar_pergunta", true);
		session.setAttribute("indice_duplicar_pergunta", indice);
		session.setAttribute("mudanca_perguntas",true);
	
		int i = ((List<Boolean>) session.getAttribute("obrigatorio")).size();
		for(;i<=((List<String>)session.getAttribute("titulos")).size();i++)
		{
			((List<Boolean>) session.getAttribute("obrigatorio")).add(false);
		}
		
	}


	@Command
	public void deletarPergunta(@BindingParam("index") String index)//deleta pergunta(apenas as informações relacionadas para a pergunta na hora de criar o questionario, não esta relacionado a banco de dados ou a edição de questionario)
	{
		int indice = Integer.parseInt(index);
	
		session.setAttribute("mudanca_perguntas",true);
		session.setAttribute("deletar_pergunta",true);
		session.setAttribute("indice_deletar_pergunta", indice);
		((List<Integer>) session.getAttribute("perguntas_deletadas")).add(indice);
	}



	@Command
	public void tipoPergunta(@BindingParam("div") Div div, //seta no zul e no questionario na hora da criação os componentes que devem ficar visiveis e guarda informações sobre as mudaas de tipo de questionario
			@BindingParam("div2") Div div2, @BindingParam("index") String index,
			@BindingParam("combo") Combobox combo) {
		
		int indice = Integer.parseInt(index);
		
		session.setAttribute("index_tipo_pergunta", indice);
				
		String tipo;
		int escolhido;
		
		if (combo.getSelectedIndex() == 0)
		{
			tipo = "Texto";
			escolhido = 0;
		}
		else if (combo.getSelectedIndex() == 1)
		{
			tipo = "Caixa de Seleção";
			escolhido = 1;
		}
		else if (combo.getSelectedIndex() == 2)
		{
			tipo = "Múltipla Escolha";
			escolhido = 2;
		}
		else
		{
			tipo = "Escala Numérica";	
			escolhido = 3;
		}
		
		combo.setText(tipo);
		
		((List<Integer>) session.getAttribute("tipo_pergunta")).set(indice, escolhido);
		
		if (combo.getSelectedIndex() == 0) {
			div2.setVisible(false);
			div.setVisible(false);
		} else {
			if (combo.getSelectedIndex() == 3) {
				div2.setVisible(false);
				div.setVisible(true);
	
			} else {
				div2.setVisible(true);
				div.setVisible(false);
			}
		}
	}




	
	public String getTituloPergunta()// retorna um valor para o zul pegar como titulo da pergunta, caso seja uma duplicação, o valor passado sera a da pergunta a ser duplicada
	{
		if((boolean) session.getAttribute("duplicar_pergunta"))// usado para Duplicar os valores
		{
			((List<String>) session.getAttribute("titulos")).add(((List<String>) session.getAttribute("titulos")).get((int) session.getAttribute("indice_duplicar_pergunta")));
			session.setAttribute("aux_duplicar_pergunta", 1 + (int) session.getAttribute("aux_duplicar_pergunta"));
			
			session.setAttribute("mudanca_perguntas",true);
			session.setAttribute("mudanca_titulo_pergunta",true);
			session.setAttribute("texto_mudanca_titulo_pergunta", ((List<String>) session.getAttribute("titulos")).get((int) session.getAttribute("indice_duplicar_pergunta")));
			session.setAttribute("indice_mudanca_titulo_pergunta", ((List<String>) session.getAttribute("titulos")).size() - 1);
			
			return ((List<String>) session.getAttribute("titulos")).get((int) session.getAttribute("indice_duplicar_pergunta"));
		}
	
			
		((List<String>) session.getAttribute("titulos")).add("");//add um novo espaço para fazer as operações
		return "";
	}

	@Command
	public void novoTitulo(@BindingParam("titulo") String titulo) { // seta o titulo direto no zul
		((List<String>) session.getAttribute("titulos")).set((int) session.getAttribute("indice_pergunta"), titulo);
		((List<Tab>) session.getAttribute("tabs")).get((int) session.getAttribute("indice_pergunta")).setLabel(titulo);
	}

	@Command
	public void atualizar(@BindingParam("txt") String txt){} // usado para forçar a atualização dos valores das opções
	
	@Command // salva nas informações a repeito do questionario as mudanças de titulo da pergunta, alem de setar variaveis para dizer para o zul mudar o titulo da tab
	public void mudancaTituloPergunta(@BindingParam("titulo") String titulo,@BindingParam("index") String index)// para cada mudança no titulo da pergunta, ela será salva em seu respectivo lugar
	{
		int indice = Integer.parseInt(index);
		((List<String>) session.getAttribute("titulos")).set(indice,titulo);
		
		GenericBusiness gb = new GenericBusiness();
		if(gb.campoStrValido(titulo) && titulo.length()!=0){
			session.setAttribute("mudanca_perguntas",true);
			session.setAttribute("mudanca_titulo_pergunta",true);
			session.setAttribute("texto_mudanca_titulo_pergunta", titulo);
			session.setAttribute("indice_mudanca_titulo_pergunta", indice);
		}
	
	}
	
	@Command
	public int getNovoIndex() { // informa qual é o proximo valor de indice(usado para cada frame saber a qual pergunta pertence)
		session.setAttribute("indice_tab", 1 + (int) session.getAttribute("indice_tab"));
		session.setAttribute("ultimo_indice_acessado",((int) session.getAttribute("indice_tab") - 1));
		return ((int) session.getAttribute("indice_tab") - 1);
	}
	

	@Command
	public int getIndex() {// retorna um idice que que é salvo no zul da pergunta como um label, para controlar a respeito de qual pergunta está havendo mudança
		return ((int) session.getAttribute("indice_tab")-1);
	}
	
	public List<Tab> getTabs() // retorna todas as tabs a partir da primeira
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
	public void obrigatorio(@BindingParam("check") Checkbox cbox,@BindingParam("index") String index)// verifica e salva a obrigatoriedade da pergunta
	{	
		int indice = Integer.parseInt(index);
		int i = ((List<Boolean>) session.getAttribute("obrigatorio")).size();
		for(;i<=((List<String>)session.getAttribute("titulos")).size();i++)
		{
			((List<Boolean>) session.getAttribute("obrigatorio")).add(false);
		}
		
		((List<Boolean>) session.getAttribute("obrigatorio")).set(indice,cbox.isChecked());
	}
	
	public boolean getObrigatorio_inicio()// da um valor padrão para obrigatoriedade
	{
		if((boolean) session.getAttribute("duplicar_pergunta"))// usado para Duplicar os valores
		{
			((List<Boolean>) session.getAttribute("obrigatorio")).set(-1 + ((List<String>)session.getAttribute("titulos")).size(),((List<Boolean>) session.getAttribute("obrigatorio")).get((int) session.getAttribute("indice_duplicar_pergunta")));
			
		}
		return false;
	}
	
	public int getTipoPergunta() {// retorna o tipo da pergunta, se for uma duplicação ela carrega as informações para repassar
		if(((boolean) session.getAttribute("criando_tab_tipo_pergunta"))==true)
		{
			if((boolean) session.getAttribute("duplicar_pergunta"))// usado para Duplicar os valores
			{
				session.setAttribute("mudar_combo", true);
			}
			((List<Integer>) session.getAttribute("tipo_pergunta")).add(new Integer(0));
			session.setAttribute("criando_tab_tipo_pergunta",false);
			return 0;
		}
		else
			return ((int) session.getAttribute("tipoPergunta"));
	}
	
	public void setTipoPergunta(int valor) {
		session.setAttribute("tipoPergunta", valor);
	}
	
	public Integer getSpinnerInicio() {// retorna o valor inicial do spinnerInicio, se for uma duplicação ela carrega as informações para repassar
		
		if((boolean) session.getAttribute("duplicar_pergunta"))// usado para Duplicar os valores, se está duplicando pega um valor do que ele está duplicando, se não ele seta o valor padrão
		{
			((List<Integer>) session.getAttribute("spinnerInicio")).add(((List<Integer>) session.getAttribute("spinnerInicio")).get((int)session.getAttribute("indice_duplicar_pergunta")));
			return ((List<Integer>) session.getAttribute("spinnerInicio")).get((int)session.getAttribute("indice_duplicar_pergunta"));
		}
		((List<Integer>) session.getAttribute("spinnerInicio")).add(0);
		return ((List<Integer>) session.getAttribute("spinnerInicio")).get(getIndex());
	}

	public Integer getSpinnerFinal() {// retorna o valor inicial do spinnerFinal, se for uma duplicação ela carrega as informações para repassar
		
		if((boolean) session.getAttribute("duplicar_pergunta"))// // usado para Duplicar os valores, se está duplicando pega um valor do que ele está duplicando, se não ele seta o valor padrão
		{
			((List<Integer>) session.getAttribute("spinnerFinal")).add(((List<Integer>) session.getAttribute("spinnerFinal")).get((int) session.getAttribute("indice_duplicar_pergunta")));
			session.setAttribute("duplicar_pergunta", false);
			return ((List<Integer>) session.getAttribute("spinnerFinal")).get((int)session.getAttribute("indice_duplicar_pergunta"));
		}
		
		((List<Integer>) session.getAttribute("spinnerFinal")).add(0);
	
		return ((List<Integer>) session.getAttribute("spinnerFinal")).get(getIndex());
	}

	@Command///REMOVER
	public void editarQuest(
			@BindingParam("questionario") Questionario questionario) {
		session.setAttribute("questionario", questionario);
		Window window = (Window) Executions.createComponents(
				"/editarQuestionario.zul", null, null);
		window.doModal();
	}

	@Command//exibi um questionario ja pronto, alem de outras informações e funçoes
	public void verQuest(@BindingParam("questionario") Questionario questionario) {
		session.setAttribute("questionario", questionario);
		Window window = (Window) Executions.createComponents(
				"/verQuestionario.zul", null, null);
		window.doModal();
	}

	
	@Command
	@NotifyChange({ "prazos", "prazo" })
	public void adicionaPrazo() {
		prazos.add(prazo);
		prazo = new PrazoQuestionario();
	}

	@Command
	public void adcPrazo(@BindingParam("questionario") Questionario questionario) {
		SemestreDAO semestreDAO = new SemestreDAO();
		if(semestreDAO.getSemestreAtualCurso(usuario.getCurso()) != null)
		{
			session.setAttribute("questionario", questionario);
			Window window = (Window) Executions.createComponents("/add-prazo.zul",
					null, null);
			window.doModal();
		}
		else
		{
			Messagebox.show("O sistema não tem um semestre referente a data atual, favor adicionar");
			Window window = (Window) Executions.createComponents(
					"/semestres.zul", null, null);
			window.doModal();
		}
	}

	@Command
	public void addPrazo(@BindingParam("window") Window w) {
		if (new QuestionariosBusiness().prazoValido(prazo)) {
			SemestreDAO semestreDAO = new SemestreDAO();
			if (validadaData(prazo) && semestreEscolhido!="") {
				prazo.setQuestionario((Questionario) session
						.getAttribute("questionario"));
				prazo.setSemestre(semestreDAO.getSemestreAtualCurso(usuario.getCurso()).getNomeSemestre());
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
					Messagebox.show("Não pode criar nessa data");
					return false;
				}
			}
		SemestreDAO semestreDAO = new SemestreDAO();
		Date dataFinal = semestreDAO.getSemestreAtualCurso(usuario.getCurso()).getDataFinalSemestre();
		if(dataFinal.before(prazo.getDataFinal()))
		{
			return false;
		}
			return true;
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

	@Command
	public void teste()
	{
		System.out.println("teste");
	}
	
	@Command
	public void exibirPergunta(@BindingParam("pergunta") Pergunta pergunta)
	{
		session.setAttribute("pergunta_exibida", pergunta);
		
		Window window = (Window) Executions.createComponents(
				"/exibirPergunta.zul", null, null);
		window.doModal();
		
	}
	
	public String getTituloPerguntaExibir()
	{
		return ((Pergunta) session.getAttribute("pergunta_exibida")).getTituloPergunta();
	}
	
	public int getSpinnerInicioExibir()
	{

		if(((Pergunta) session.getAttribute("pergunta_exibida")).getTipoPergunta()==3)
		{
			List<RespostaEspecifica> opcoes = ((Pergunta) session.getAttribute("pergunta_exibida")).getRespostasEspecificasBanco();
			int menor = Integer.parseInt(opcoes.get(0).getRespostaEspecifica());
			for(int i=0;i<opcoes.size();i++)
			{
				if(menor > Integer.parseInt(opcoes.get(i).getRespostaEspecifica()))
					menor = Integer.parseInt(opcoes.get(i).getRespostaEspecifica());
			}
			return menor;
		}
		return 0;
	}
	
	public int getSpinnerFinalExibir()
	{
		if(((Pergunta) session.getAttribute("pergunta_exibida")).getTipoPergunta()==3)
		{
			List<RespostaEspecifica> opcoes = ((Pergunta) session.getAttribute("pergunta_exibida")).getRespostasEspecificasBanco();

			int maior= Integer.parseInt(opcoes.get(0).getRespostaEspecifica());
			for(int i=0;i<opcoes.size();i++)
			{
				if(maior < Integer.parseInt(opcoes.get(i).getRespostaEspecifica()))
					maior = Integer.parseInt(opcoes.get(i).getRespostaEspecifica());
				
			}
			return maior;
		}
		return 0;
	}
	
	
	public String getTipoPerguntaExibir()
	{
		return ((Pergunta) session.getAttribute("pergunta_exibida")).getNomeTipoPergunta();
	}
	
	public boolean getObrigatorioExibir()
	{
		return ((Pergunta) session.getAttribute("pergunta_exibida")).isObrigatorio();
	}
	
	public List<RespostaEspecifica> getOpcoesExibir() //retorna todas as opçoes de uma pergunta em formato de string
	{
		return ((Pergunta) session.getAttribute("pergunta_exibida")).getRespostasEspecificasBanco();
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

	
	public List<Integer> getTiposQuestionario() {
		return tiposQuestionario;
	}



	public void setTiposQuestionario(List<Integer> tiposQuestionario) {
		this.tiposQuestionario = tiposQuestionario;
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




}
