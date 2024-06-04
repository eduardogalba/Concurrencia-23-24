package controlacceso;

public interface ControlAcceso {

    public void entrar (int nSala) throws IllegalArgumentException;

    public void salir (int nSala) throws IllegalArgumentException;
}
