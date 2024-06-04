package accesopuente;

public interface  AccesoPuente {

    public void solicitarEntrada(int l);

    public void notificarSalida(int l) throws IllegalArgumentException;
}
