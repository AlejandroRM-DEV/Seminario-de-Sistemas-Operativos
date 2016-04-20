package application;

public enum Operacion {
	SUMA,
	RESTA,
	MULTIPLICACION,
	DIVISION,
	MODULO,
	RAIZ_CUADRADA,
	INDEFINIDA;

	public String simbolo(){
		switch (this) {
		case SUMA:
			return "+";
		case RESTA:
			return "-";
		case MULTIPLICACION:
			return "*";
		case DIVISION:
			return "/";
		case MODULO:
			return "+";
		case RAIZ_CUADRADA:
			return "\u221A";
		default: return "?";
		}
	}
}