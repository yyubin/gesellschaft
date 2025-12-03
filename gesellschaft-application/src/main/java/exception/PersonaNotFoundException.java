package exception;

public class PersonaNotFoundException extends RuntimeException {

    public PersonaNotFoundException(Long id) {
        super("Persona not found with id: " + id);
    }
}
