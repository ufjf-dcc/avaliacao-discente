package br.ufjf.avaliacao.test;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.PieModel;

public class ResultadosController {

	
	PieModel model;
	
	@Init
	public void init() {
		model = GraficoInfoDTO.getModel();
	}
	
	@GlobalCommand("configChanged") 
    @NotifyChange("model")
    public void onConfigChanged(@BindingParam("model")PieModel model){
        this.model = model;
    }
	
	public PieModel getModel() {
        return model;
    }
}
