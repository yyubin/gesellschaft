package port;

import model.Sinner;
import java.util.Optional;

public interface SinnerRepository {

    Optional<Sinner> findById(Long id);

    Sinner save(Sinner sinner);
}
