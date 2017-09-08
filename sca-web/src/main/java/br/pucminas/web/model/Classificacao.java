package br.pucminas.web.model;

public enum Classificacao {

	OB(0, "Obrigatória"),
	OP(1, "Optativa"),
	EL(4, "Eletiva"),
	OC(2, "Optativa Complementar"),
	EO(3, "Estágio Obrigatório");

	private final int code;
	private final String description;

	private Classificacao(int code, String description) {
		this.code = code;
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public int getCode() {
		return code;
	}

	@Override
	public String toString() {
		return code + ": " + description;
	}
}