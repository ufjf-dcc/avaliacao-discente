package br.ufjf.avaliacao.test;

import org.zkoss.zul.PieModel;
import org.zkoss.zul.SimplePieModel;

public class GraficoInfoDTO {
	
	public static PieModel getModel(){
        PieModel model = new SimplePieModel();
        model.setValue("Ótimo", new Double(220));
        model.setValue("Bom", new Double(240));
        model.setValue("Regular", new Double(110));
        model.setValue("Ruim", new Double(410));
        model.setValue("Péssimo", new Double(210));
        return model;
    }
	
	public static PieModel getModel2(){
        PieModel model = new SimplePieModel();
        model.setValue("1", new Double(34));
        model.setValue("2", new Double(40));
        model.setValue("3", new Double(23));
        model.setValue("4", new Double(4));
        model.setValue("5", new Double(30));
        return model;
    }
}
