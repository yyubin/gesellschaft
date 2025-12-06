package port;

import model.Sinner;
import java.util.List;
import java.util.Optional;

public interface SinnerRepository {

    Optional<Sinner> findById(Long id);

    List<Sinner> findAll();

    Sinner save(Sinner sinner);
}
